/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.infrastructure.lga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.lga.LgaCriteria;
import de.symeda.sormas.api.infrastructure.lga.LgaDto;
import de.symeda.sormas.api.infrastructure.lga.LgaFacade;
import de.symeda.sormas.api.infrastructure.lga.LgaIndexDto;
import de.symeda.sormas.api.infrastructure.lga.LgaReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.DefaultInfrastructureCache;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "LgaFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class LgaFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Lga, LgaDto, LgaIndexDto, LgaReferenceDto, LgaService, LgaCriteria>
	implements LgaFacade {

	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private AreaService areaService;
	@EJB
	private CountryService countryService;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private DefaultInfrastructureCache defaultInfrastructureCache;

	public LgaFacadeEjb() {
	}

	@Inject
	protected LgaFacadeEjb(LgaService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Lga.class,
			LgaDto.class,
			service,
			featureConfiguration,
			Validations.importRegionAlreadyExists, // TODO: Add LGA-specific validation strings
			Strings.messageRegionArchivingNotPossible, // TODO: Add LGA-specific message strings
			null);
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getAllActiveByServerCountry() {
		CountryReferenceDto serverCountry = countryFacade.getServerCountry();

		return getAllActiveByPredicate((cb, root) -> {
			if (serverCountry != null) {
				Path<Object> countryUuid = root.join(Lga.COUNTRY, JoinType.LEFT).get(AbstractDomainObject.UUID);
				return CriteriaBuilderHelper.or(cb, cb.isNull(countryUuid), cb.equal(countryUuid, serverCountry.getUuid()));
			}

			return null;
		});
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getAllActiveByCountry(String countryUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Lga.COUNTRY).get(AbstractDomainObject.UUID), countryUuid));
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getAllActiveByArea(String areaUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Lga.AREA).get(AbstractDomainObject.UUID), areaUuid));
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getAllActiveAsReference() {
		return toRefDtos(service.getAllActive(Lga.NAME, true).stream());
	}

	@Override
	public List<LgaIndexDto> getIndexList(LgaCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Lga> cq = cb.createQuery(Lga.class);
		Root<Lga> lga = cq.from(Lga.class);
		Join<Lga, Area> area = lga.join(Lga.AREA, JoinType.LEFT);
		Join<Lga, Country> country = lga.join(Lga.COUNTRY, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, lga);
		}
		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Lga.NAME:
				case Lga.EPID_CODE:
				case Lga.EXTERNAL_ID:
					expression = cb.lower(lga.get(sortProperty.propertyName));
					break;
				case Lga.GROWTH_RATE:
				case Lga.DEFAULT_INFRASTRUCTURE:
					expression = lga.get(sortProperty.propertyName);
					break;
				case Lga.AREA:
					expression = cb.lower(area.get(Area.NAME));
					break;
				case LgaIndexDto.COUNTRY:
					expression = cb.lower(country.get(Country.DEFAULT_NAME));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(cb.lower(lga.get(Lga.NAME))));
		}

		cq.select(lga);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	public Page<LgaIndexDto> getIndexPage(LgaCriteria lgaCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<LgaIndexDto> lgaIndexList = getIndexList(lgaCriteria, offset, size, sortProperties);
		long totalElementCount = count(lgaCriteria);
		return new Page<>(lgaIndexList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public LgaReferenceDto getLgaReferenceById(int id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public List<String> getNamesByIds(List<Long> lgaIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Lga> root = cq.from(Lga.class);

		Predicate filter = root.get(AbstractDomainObject.ID).in(lgaIds);
		cq.where(filter);
		cq.select(root.get(Lga.NAME));
		return em.createQuery(cq).getResultList();
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> lgaUuids) {
		return service.isUsedInInfrastructureData(lgaUuids, Region.LGA, Region.class);
	}

	public static LgaReferenceDto toReferenceDto(Lga entity) {
		if (entity == null) {
			return null;
		}
		return new LgaReferenceDto(entity.getUuid(), entity.getName(), entity.getExternalID());
	}

	public LgaDto toDto(Lga entity) {
		if (entity == null) {
			return null;
		}
		LgaDto dto = new LgaDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setCentrallyManaged(entity.isCentrallyManaged());
		dto.setDefaultInfrastructure(entity.isDefaultInfrastructure());

		return dto;
	}

	@Override
	protected LgaReferenceDto toRefDto(Lga lga) {
		return toReferenceDto(lga);
	}

	public LgaIndexDto toIndexDto(Lga entity) {
		if (entity == null) {
			return null;
		}
		LgaIndexDto dto = new LgaIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		// TODO: Add LGA population support when PopulationData supports LGA
		dto.setPopulation(null);
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setDefaultInfrastructure(entity.isDefaultInfrastructure());

		return dto;
	}

	@Override
	protected List<Lga> findDuplicates(LgaDto dto, boolean includeArchived) {
		return service.getByName(dto.getName(), includeArchived);
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getReferencesByName(String name, boolean includeArchivedEntities) {
		return toRefDtos(service.getByName(name, includeArchivedEntities).stream());
	}

	@PermitAll
	public List<LgaDto> getByName(String name, boolean includeArchivedEntities) {
		return toDtos(service.getByName(name, includeArchivedEntities).stream());
	}

	@Override
	@PermitAll
	public List<LgaReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {
		return toRefDtos(service.getByExternalId(externalId, includeArchivedEntities).stream());
	}

	private List<LgaReferenceDto> getAllActiveByPredicate(BiFunction<CriteriaBuilder, Root<Lga>, Predicate> buildPredicate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Lga> cq = cb.createQuery(Lga.class);
		Root<Lga> root = cq.from(Lga.class);

		Predicate basicFilter = service.createBasicFilter(cb, root);
		cq.where(CriteriaBuilderHelper.and(cb, basicFilter, buildPredicate.apply(cb, root)));

		cq.orderBy(cb.asc(root.get(Lga.NAME)));

		return toRefDtos(em.createQuery(cq).getResultList().stream());
	}

	@Override
	protected Lga fillOrBuildEntity(@NotNull LgaDto source, Lga target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		target = DtoHelper.fillOrBuildEntity(source, target, Lga::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setCentrallyManaged(source.isCentrallyManaged());
		target.setDefaultInfrastructure(source.isDefaultInfrastructure());

		return target;
	}

	@Override
	protected boolean checkDefaultRemovalAllowed(LgaDto dto) {
		// Check if any region uses this LGA as default
		return true; // TODO: Implement check if needed
	}

	@Override
	protected Lga getDefaultInfrastructure() {
		return defaultInfrastructureCache.getDefaultLga();
	}

	@Override
	protected void resetDefaultInfrastructure() {
		defaultInfrastructureCache.resetDefaultLga();
	}

	@LocalBean
	@Stateless
	public static class LgaFacadeEjbLocal extends LgaFacadeEjb {

		public LgaFacadeEjbLocal() {
		}

		@Inject
		protected LgaFacadeEjbLocal(LgaService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}


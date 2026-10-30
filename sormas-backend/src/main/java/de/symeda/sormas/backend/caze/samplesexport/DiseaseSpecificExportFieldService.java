/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificCaseExportSchema;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless
@LocalBean
public class DiseaseSpecificExportFieldService extends BaseAdoService<DiseaseSpecificExportField> {

	private static final String SEED_RESOURCE_DIR = "/disease-specific-export-fields/";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public DiseaseSpecificExportFieldService() {
		super(DiseaseSpecificExportField.class);
	}

	public List<DiseaseSpecificExportField> getByDisease(Disease disease) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseSpecificExportField> cq = cb.createQuery(getElementClass());
		Root<DiseaseSpecificExportField> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(DiseaseSpecificExportField.DISEASE), disease));
		cq.orderBy(cb.asc(from.get(DiseaseSpecificExportField.DISPLAY_ORDER)), cb.asc(from.get(DiseaseSpecificExportField.PROPERTY_PATH)));
		return em.createQuery(cq).getResultList();
	}

	public long countByDisease(Disease disease) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<DiseaseSpecificExportField> from = cq.from(getElementClass());
		cq.select(cb.count(from));
		cq.where(cb.equal(from.get(DiseaseSpecificExportField.DISEASE), disease));
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getActivePropertyPaths(Disease disease) {
		return getByDisease(disease).stream().filter(DiseaseSpecificExportField::isActive).map(DiseaseSpecificExportField::getPropertyPath).collect(Collectors.toList());
	}

	public void seedFromCsvResources() {
		String locale = configFacade.getCountryLocale();
		for (Disease disease : DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES) {
			if (countByDisease(disease) == 0) {
				List<String[]> csvRows = readSeedCsv(disease);
				if (csvRows.isEmpty()) {
					persistSchemaAsActive(disease, locale);
				} else {
					importSeedRows(disease, csvRows);
				}
			}
			appendMissingUnionCatalogPaths(disease, locale);
		}
	}

	private void persistSchemaAsActive(Disease disease, String locale) {
		for (DiseaseSpecificCaseExportSchema.Column column : DiseaseSpecificCaseExportSchema.build(disease, locale)) {
			persistField(disease, column.getPropertyPath(), column.getDisplayOrder(), true, column.getSchemaBlock());
		}
	}

	private void importSeedRows(Disease disease, List<String[]> csvRows) {
		for (String[] row : csvRows) {
			if (row.length < 4 || "disease".equalsIgnoreCase(row[0])) {
				continue;
			}
			String propertyPath = row[1];
			String block = propertyPath.contains(".")
				? propertyPath.substring(0, propertyPath.indexOf('.'))
				: DiseaseSpecificCaseExportSchema.BLOCK_CASE;
			persistField(disease, propertyPath, Integer.parseInt(row[2]), Boolean.parseBoolean(row[3]), block);
		}
	}

	private void appendMissingUnionCatalogPaths(Disease disease, String locale) {
		Set<String> existing = getByDisease(disease).stream().map(DiseaseSpecificExportField::getPropertyPath).collect(Collectors.toCollection(LinkedHashSet::new));
		int order = existing.size() + 1;
		for (String path : DiseaseSpecificCaseExportSchema.unionCatalogPaths(locale)) {
			if (existing.contains(path)) {
				continue;
			}
			String block = path.contains(".") ? path.substring(0, path.indexOf('.')) : DiseaseSpecificCaseExportSchema.BLOCK_CASE;
			persistField(disease, path, order++, false, block);
		}
	}

	private void persistField(Disease disease, String propertyPath, int displayOrder, boolean active, String schemaBlock) {
		DiseaseSpecificExportField field = new DiseaseSpecificExportField();
		field.setDisease(disease);
		field.setPropertyPath(propertyPath);
		field.setDisplayOrder(displayOrder);
		field.setActive(active);
		field.setSchemaBlock(schemaBlock);
		ensurePersisted(field);
	}

	private List<String[]> readSeedCsv(Disease disease) {
		String resource = SEED_RESOURCE_DIR + disease.name() + ".csv";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			if (in == null) {
				return new ArrayList<>();
			}
			try (CSVReader reader = CSVUtils.createCSVReader(new InputStreamReader(in, StandardCharsets.UTF_8), ',')) {
				return reader.readAll();
			}
		} catch (IOException | CsvException e) {
			logger.warn("Could not read seed CSV {}", resource, e);
			return new ArrayList<>();
		}
	}

	public List<String> importCsvAndUpdateActive(byte[] csvContent) {
		List<String> unknownPaths = new ArrayList<>();
		try (CSVReader reader = CSVUtils.createCSVReader(new InputStreamReader(new java.io.ByteArrayInputStream(csvContent), StandardCharsets.UTF_8), ',')) {
			List<String[]> rows = reader.readAll();
			for (String[] row : rows) {
				if (row.length < 4 || "disease".equalsIgnoreCase(row[0])) {
					continue;
				}
				Disease disease = Disease.valueOf(row[0]);
				String propertyPath = row[1];
				boolean active = Boolean.parseBoolean(row[3]);
				List<DiseaseSpecificExportField> matches = getByDisease(disease).stream()
					.filter(f -> f.getPropertyPath().equals(propertyPath))
					.collect(Collectors.toList());
				if (matches.isEmpty()) {
					unknownPaths.add(propertyPath);
					continue;
				}
				DiseaseSpecificExportField field = matches.get(0);
				field.setActive(active);
				if (row.length > 2 && StringUtils.isNotBlank(row[2])) {
					field.setDisplayOrder(Integer.parseInt(row[2]));
				}
				ensurePersisted(field);
			}
		} catch (IOException | CsvException e) {
			throw new RuntimeException(e);
		}
		return unknownPaths;
	}
}

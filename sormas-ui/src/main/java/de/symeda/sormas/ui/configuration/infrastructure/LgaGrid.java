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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.lga.LgaCriteria;
import de.symeda.sormas.api.infrastructure.lga.LgaIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class LgaGrid extends FilteredGrid<LgaIndexDto, LgaCriteria> {

	private static final long serialVersionUID = 6289713952342575369L;

	public LgaGrid(LgaCriteria criteria) {

		super(LgaIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(LgaView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		String[] columns = new String[] {
			LgaIndexDto.NAME };
		if (FacadeProvider.getFeatureConfigurationFacade().isCountryEnabled()) {
			columns = ArrayUtils.add(columns, LgaIndexDto.COUNTRY);
		}
		columns = ArrayUtils.addAll(
			columns,
			LgaIndexDto.AREA,
			LgaIndexDto.EPID_CODE,
			LgaIndexDto.EXTERNAL_ID,
			LgaIndexDto.POPULATION,
			LgaIndexDto.GROWTH_RATE);
		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			columns = ArrayUtils.add(columns, LgaIndexDto.DEFAULT_INFRASTRUCTURE);
		}
		setColumns(columns);

		getColumn(LgaIndexDto.POPULATION).setSortable(false);

		if (UiUtil.disabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			removeColumn(LgaIndexDto.AREA);
		}

		if (UiUtil.permitted(FeatureType.EDIT_INFRASTRUCTURE_DATA, UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editLga(e.getUuid()));
		}

		Optional.ofNullable(((Column<LgaIndexDto, Boolean>) getColumn(LgaIndexDto.DEFAULT_INFRASTRUCTURE)))
			.ifPresent(c -> c.setRenderer(new BooleanRenderer()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(LgaIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		if (ViewModelProviders.of(LgaView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getLgaFacade()::getIndexList, FacadeProvider.getLgaFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getLgaFacade()::getIndexList);
	}
}


/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureDtoWithDefault;
import de.symeda.sormas.api.infrastructure.lga.LgaDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class LgaEditForm extends AbstractEditForm<LgaDto> {

	private static final long serialVersionUID = -1;

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRowLocs(LgaDto.NAME, LgaDto.EPID_CODE) + 
					fluidRowLocs(LgaDto.COUNTRY) + 
					fluidRowLocs(LgaDto.AREA) +
					fluidRowLocs(LgaDto.EXTERNAL_ID) +
					fluidRowLocs(InfrastructureDtoWithDefault.DEFAULT_INFRASTRUCTURE);
	//@formatter:on

	private final Boolean create;

	public LgaEditForm(boolean create) {

		super(
			LgaDto.class,
			LgaDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withFeatureTypes(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations()),
			UiFieldAccessCheckers.getNoop());
		this.create = create;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {
		if (create == null) {
			return;
		}

		addField(LgaDto.NAME, TextField.class);
		addField(LgaDto.EPID_CODE, TextField.class);
		ComboBox country = addInfrastructureField(LgaDto.COUNTRY);
		ComboBox area = addInfrastructureField(LgaDto.AREA);
		addField(LgaDto.EXTERNAL_ID, TextField.class);

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			addField(InfrastructureDtoWithDefault.DEFAULT_INFRASTRUCTURE, CheckBox.class);
		}

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, LgaDto.NAME, LgaDto.EPID_CODE);

		country.addItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());

		area.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		FieldHelper.setVisibleWhen(
			country,
			Collections.singletonList(area),
			Arrays.asList(null, FacadeProvider.getCountryFacade().getServerCountry()),
			true);
	}

	@Override
	public void setValue(LgaDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		getField(LgaDto.COUNTRY).setReadOnly(newFieldValue.getCountry() != null);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}


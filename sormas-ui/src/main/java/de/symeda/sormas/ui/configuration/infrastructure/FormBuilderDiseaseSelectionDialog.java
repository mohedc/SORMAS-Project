/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.ui.utils.ComboBoxHelper;

public class FormBuilderDiseaseSelectionDialog extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private ComboBox diseaseComboBox;
	private List<Disease> availableDiseases;

	public FormBuilderDiseaseSelectionDialog(FormType formType) {
		setSpacing(true);
		setMargin(true);
		setWidth(100, Unit.PERCENTAGE);

		// Get existing forms for this formType to filter out diseases
		List<FormBuilderDto> existingForms = FacadeProvider.getFormBuilderFacade().getByFormType(formType, true);
		Set<Disease> existingDiseases = existingForms.stream()
			.map(FormBuilderDto::getDisease)
			.filter(disease -> disease != null)
			.collect(Collectors.toSet());

		// Filter available diseases
		availableDiseases = new ArrayList<>();
		for (Disease disease : Disease.values()) {
			if (!existingDiseases.contains(disease)) {
				availableDiseases.add(disease);
			}
		}

		// Add label explaining the dialog
		Label infoLabel = new Label(I18nProperties.getString(Strings.infoSelectDiseaseForDuplicate));
		infoLabel.setWidth(100, Unit.PERCENTAGE);
		addComponent(infoLabel);

		// Create disease ComboBox
		diseaseComboBox = ComboBoxHelper.createComboBoxV7();
		diseaseComboBox.setCaption(I18nProperties.getPrefixCaption(FormBuilderDto.I18N_PREFIX, FormBuilderDto.DISEASE));
		diseaseComboBox.setWidth(100, Unit.PERCENTAGE);
		diseaseComboBox.setNullSelectionAllowed(false);
		diseaseComboBox.setRequired(true);

		// Add available diseases to ComboBox
		if (!availableDiseases.isEmpty()) {
			diseaseComboBox.addItems(availableDiseases);
		}

		addComponent(diseaseComboBox);
	}

	public Disease getSelectedDisease() {
		return (Disease) diseaseComboBox.getValue();
	}

	public boolean hasAvailableDiseases() {
		return !availableDiseases.isEmpty();
	}

	public List<Disease> getAvailableDiseases() {
		return new ArrayList<>(availableDiseases);
	}

	public ComboBox getDiseaseComboBox() {
		return diseaseComboBox;
	}
}

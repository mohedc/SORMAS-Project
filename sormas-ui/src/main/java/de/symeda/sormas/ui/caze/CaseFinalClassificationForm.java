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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.FeatureTypeFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class CaseFinalClassificationForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String FINAL_CLASSIFICATION_HEADING_LOC = "finalClassificationHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
			fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION);
	//@formatter:on

	private ComboBox finalClassificationField;
	private Disease disease;

	public CaseFinalClassificationForm(
		String caseUuid,
		Disease disease,
		ViewMode viewMode,
		boolean isPseudonymized,
		boolean inJurisdiction) {

		super(
			CaseDataDto.class,
			CaseDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease)
				.add(new OutbreakFieldVisibilityChecker(viewMode))
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale()))
				.add(new UserRightFieldVisibilityChecker(UiUtil::permitted))
				.add(new FeatureTypeFieldVisibilityChecker(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations())),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));

		this.disease = disease;

		addFields();
	}

	@Override
	protected void addFields() {

		Label finalClassificationHeadingLabel = new Label(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, "finalClassificationHeading"));
		finalClassificationHeadingLabel.addStyleName(H3);
		finalClassificationHeadingLabel.addStyleName(VSPACE_3);
		getContent().addComponent(finalClassificationHeadingLabel, FINAL_CLASSIFICATION_HEADING_LOC);

		finalClassificationField = addField(CaseDataDto.FINAL_CLASSIFICATION, ComboBox.class);
		finalClassificationField.setNullSelectionAllowed(true);
		finalClassificationField.setItemCaptionMode(ComboBox.ItemCaptionMode.ID_TOSTRING);

		FieldHelper.updateEnumData(finalClassificationField, Arrays.asList(FinalClassification.LAB_CONFIRMED, FinalClassification.CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE, FinalClassification.CLINICAL, FinalClassification.DISCARDED, FinalClassification.PENDING_LAB_RESULTS));

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}

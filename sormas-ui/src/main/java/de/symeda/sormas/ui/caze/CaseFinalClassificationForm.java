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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.DateField;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.FeatureTypeFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.*;
import org.jetbrains.annotations.NotNull;

public class CaseFinalClassificationForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String FINAL_CLASSIFICATION_HEADING_LOC = "finalClassificationHeadingLoc";
	private static final String ADDITIONAL_HEADING_LOC = "additionalHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
			fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION);

	private static final String YELLOW_FEVER_HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
			fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION, "") +
			fluidRowLocs(CaseDataDto.CLASSIFICATION_COMMENT, "");

	private static final String MEASLES_HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
			fluidRowLocs(CaseDataDto.MEASLES_COMMUNITY_INVESTIGATION, "") +
			fluidRowLocs(CaseDataDto.MEASLES_INVESTIGATION_RESULTS, "") +
			fluidRowLocs(CaseDataDto.SOURCE_OF_INFECTION_IDENTIFIED, "") +
			fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION, "");

	private static final String AFP_HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
					fluidRowLocs(6,CaseDataDto.IMMUNOCOMPROMISED_STATUS_SUSPECTED) +
					fluidRowLocs(CaseDataDto.DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE, CaseDataDto.DATE_DIFFERENTIATION_RECEIVED_EPI) +
					fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION, CaseDataDto.VDPV_CLASSIFICATION, CaseDataDto.SERO_CLASSIFICATION);

	private static final String IDSR_HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
					fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION) +
					loc(ADDITIONAL_HEADING_LOC) +
					fluidRowLocs(CaseDataDto.DATE_REGION_RECEIVES_LAB_RESULTS,CaseDataDto.REGION_LAB_RESULTS_RECEIVED) +
					fluidRowLocs(CaseDataDto.DATE_LAB_RESULTS_SENT_HEALTH_FACILITY_REGION, CaseDataDto.DATE_LAB_RESULTS_RECEIVED_HEALTH_FACILITY);

	private static final String CONGENITAL_RUBELLA_HTML_LAYOUT =
			loc(FINAL_CLASSIFICATION_HEADING_LOC) +
					fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION, "") +
					fluidRowLocs(CaseDataDto.FINAL_CLASSIFICATION_DISCARDED, "") +
					fluidRowLocs(CaseDataDto.CLASSIFICATION_DATE, CaseDataDto.CLASSIFICATION_BY_ORIGIN) +
					fluidRowLocs(CaseDataDto.INVESTIGATOR_NAME, CaseDataDto.INVESTIGATOR_TEL);

	//@formatter:on

	private static final List<Disease> DISEASES_REQUIRING_CONFIRMATION = Arrays.asList(
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
		Disease.NEONATAL_TETANUS,
		Disease.CONGENITAL_RUBELLA,
			Disease.AFP
	);

	private ComboBox finalClassificationField;
	private ComboBox classificationByOriginField;
	private Disease disease;
	private ComboBox regionCombo;
	private FinalClassification previousFinalClassification;
	private boolean isInitializing = true;
	private String caseUuid;
	private DateField dateCaptured;

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
		this.caseUuid = caseUuid;

		addFields();
	}

	@Override
	public void setValue(CaseDataDto newFieldValue) throws com.vaadin.v7.data.Property.ReadOnlyException, com.vaadin.v7.data.util.converter.Converter.ConversionException {
		isInitializing = true;
		try {
			super.setValue(newFieldValue);
		} finally {
			// Reset flag after initialization is complete
			isInitializing = false;
		}
	}

	@Override
	protected void addFields() {

		Label finalClassificationHeadingLabel = new Label(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, "finalClassificationHeading"));
		finalClassificationHeadingLabel.addStyleName(H3);
		finalClassificationHeadingLabel.addStyleName(VSPACE_3);
		getContent().addComponent(finalClassificationHeadingLabel, FINAL_CLASSIFICATION_HEADING_LOC);

		createLabel(I18nProperties.getString(Strings.additionalHeading), H3, ADDITIONAL_HEADING_LOC);

		addField(CaseDataDto.IMMUNOCOMPROMISED_STATUS_SUSPECTED, NullableOptionGroup.class);
		addField(CaseDataDto.DATE_REGION_RECEIVES_LAB_RESULTS, DateField.class);
		regionCombo = addInfrastructureField(CaseDataDto.REGION_LAB_RESULTS_RECEIVED);
		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		addField(CaseDataDto.DATE_LAB_RESULTS_SENT_HEALTH_FACILITY_REGION, DateField.class);
		addField(CaseDataDto.DATE_LAB_RESULTS_RECEIVED_HEALTH_FACILITY, DateField.class);
		addField(CaseDataDto.CLASSIFICATION_DATE, DateField.class);
		addField(CaseDataDto.INVESTIGATOR_NAME, TextField.class);
		addField(CaseDataDto.INVESTIGATOR_TEL, TextField.class);
		finalClassificationField = addField(CaseDataDto.FINAL_CLASSIFICATION, ComboBox.class);
		addField(CaseDataDto.VDPV_CLASSIFICATION, ComboBox.class);
		addField(CaseDataDto.SERO_CLASSIFICATION, ComboBox.class);
		dateCaptured = addDateField(CaseDataDto.DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE, DateField.class, 7);
		addField(CaseDataDto.DATE_DIFFERENTIATION_RECEIVED_EPI, DateField.class);
		finalClassificationField.setNullSelectionAllowed(true);
		finalClassificationField.setItemCaptionMode(ComboBox.ItemCaptionMode.ID_TOSTRING);
		TextArea classificationCommentField = addField(CaseDataDto.CLASSIFICATION_COMMENT, TextArea.class);
		classificationCommentField.setRows(4);
		TextArea finalClassificationDiscardedField = addField(CaseDataDto.FINAL_CLASSIFICATION_DISCARDED, TextArea.class);
		finalClassificationDiscardedField.setRows(4);
		finalClassificationDiscardedField.setCaption("Please specify:");
		addField(CaseDataDto.MEASLES_COMMUNITY_INVESTIGATION, NullableOptionGroup.class);
		TextArea measlesInvestigationResults = addField(CaseDataDto.MEASLES_INVESTIGATION_RESULTS, TextArea.class);
		measlesInvestigationResults.setRows(4);
			addField(CaseDataDto.SOURCE_OF_INFECTION_IDENTIFIED, NullableOptionGroup.class);
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.MEASLES_INVESTIGATION_RESULTS,
				CaseDataDto.MEASLES_COMMUNITY_INVESTIGATION,
				Collections.singletonList(YesNo.YES),
				true);
		classificationByOriginField = addField(CaseDataDto.CLASSIFICATION_BY_ORIGIN, ComboBox.class);
		classificationByOriginField.setNullSelectionAllowed(true);
		classificationByOriginField.setItemCaptionMode(ComboBox.ItemCaptionMode.ID_TOSTRING);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.FINAL_CLASSIFICATION_DISCARDED,
			CaseDataDto.FINAL_CLASSIFICATION,
			Collections.singletonList(FinalClassification.DISCARDED),
			true);

		List<FinalClassification> values = getFinalClassifications();

		FieldHelper.updateEnumData(finalClassificationField, values);
		
		// Update ClassificationByOrigin enum data
		if (disease == Disease.CONGENITAL_RUBELLA) {
			FieldHelper.updateEnumData(classificationByOriginField, Arrays.asList(
					de.symeda.sormas.api.caze.ClassificationByOrigin.values()));
		}

		// Add value change listener for confirmation dialog
		finalClassificationField.addValueChangeListener(event -> {
			// Skip if form is being initialized
			if (isInitializing) {
				return;
			}
			
			FinalClassification selectedValue = (FinalClassification) event.getProperty().getValue();
			
			// Check if the selected value is LAB_CONFIRMED or CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE
			// and the disease requires confirmation
			if (selectedValue != null
					&& (FinalClassification.LAB_CONFIRMED.equals(selectedValue)
					|| FinalClassification.CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE.equals(selectedValue)
					|| FinalClassification.CONFIRMED_POLIO.equals(selectedValue))
					&& DISEASES_REQUIRING_CONFIRMATION.contains(disease)) {
				
				// Store the previous value from the form DTO to revert if user clicks No
				CaseDataDto caseDataDto = getValue();
				if (caseDataDto != null) {
					previousFinalClassification = caseDataDto.getFinalClassification();
				} else {
					previousFinalClassification = null;
				}
				
				// Show confirmation dialog
				Label messageLabel = new Label("The final classification of the case selected will set the case classification to confirmed. Do you want to proceed?");
				VaadinUiUtil.showConfirmationPopup(
					"",
					messageLabel,
					"Yes",
					"No",
					640,
					confirmed -> {
						if (confirmed) {
							// User clicked Yes - set case classification to CONFIRMED
							CaseDataDto dto = getValue();
							if (dto != null) {
								dto.setCaseClassification(CaseClassification.CONFIRMED);
							}
						} else {
							// User clicked No - revert to previous value
							finalClassificationField.setValue(previousFinalClassification);
						}
					});
			}
		});

		// Set field visibility based on disease
		if (disease != Disease.CONGENITAL_RUBELLA) {
			setVisible(false, 
				CaseDataDto.FINAL_CLASSIFICATION_DISCARDED,
				CaseDataDto.CLASSIFICATION_DATE,
				CaseDataDto.CLASSIFICATION_BY_ORIGIN,
				CaseDataDto.INVESTIGATOR_NAME,
				CaseDataDto.INVESTIGATOR_TEL);
		}
		if (disease != Disease.AFP && disease != Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			setVisible(false,
					CaseDataDto.IMMUNOCOMPROMISED_STATUS_SUSPECTED,
					CaseDataDto.DATE_REGION_RECEIVES_LAB_RESULTS,
					CaseDataDto.REGION_LAB_RESULTS_RECEIVED,
					CaseDataDto.DATE_LAB_RESULTS_SENT_HEALTH_FACILITY_REGION,
					CaseDataDto.DATE_LAB_RESULTS_RECEIVED_HEALTH_FACILITY);
		}
		if (disease != Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			getContent().getComponent(ADDITIONAL_HEADING_LOC).setVisible(false);
		}

	}

	@NotNull
	private List<FinalClassification> getFinalClassifications() {
		if (Disease.AFP.equals(disease)) {
			return Arrays.asList(
					FinalClassification.CONFIRMED_POLIO,
					FinalClassification.COMPATIBLE,
					FinalClassification.DISCARDED,
					FinalClassification.NOT_AN_AFP_CASE
			);
		}
		if (Disease.CSM.equals(disease)) {
			return Arrays.asList(
				FinalClassification.LAB_CONFIRMED,
				FinalClassification.PROBABLE,
				FinalClassification.SUSPECTED
			);
		}
		return Arrays.asList(
				FinalClassification.LAB_CONFIRMED,
				FinalClassification.CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE,
				FinalClassification.CLINICAL,
				FinalClassification.DISCARDED,
				FinalClassification.PENDING_LAB_RESULTS
		);
	}

	@Override
	protected String createHtmlLayout() {
		if (disease == Disease.AFP) {
			return AFP_HTML_LAYOUT;
		}
		if(disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS){
			return IDSR_HTML_LAYOUT;
		}
		if(disease == Disease.CONGENITAL_RUBELLA){
			return CONGENITAL_RUBELLA_HTML_LAYOUT;
		}
		if (disease == Disease.MEASLES) {
			return MEASLES_HTML_LAYOUT;
		}
		if (disease == Disease.YELLOW_FEVER) {
			return YELLOW_FEVER_HTML_LAYOUT;
		}
		return HTML_LAYOUT;
	}

	private boolean hasPositiveMeaslesPathogenTest() {
		if (caseUuid == null) {
			return false;
		}
		List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(caseUuid));
		for (SampleDto sample : samples) {
			if (sample.isDeleted()) {
				continue;
			}
			for (PathogenTestDto test : FacadeProvider.getPathogenTestFacade().getAllBySample(sample.toReference())) {
				if (test.isDeleted()) {
					continue;
				}
				if (test.getTestedDisease() == Disease.MEASLES && test.getTestResult() == PathogenTestResultType.POSITIVE) {
					return true;
				}
			}
		}
		return false;
	}

}

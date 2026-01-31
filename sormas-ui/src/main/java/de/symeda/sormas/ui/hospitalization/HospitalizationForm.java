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

package de.symeda.sormas.ui.hospitalization;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import de.symeda.sormas.api.Disease;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateComparator;
import de.symeda.sormas.api.utils.InpatOutpat;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class HospitalizationForm extends AbstractEditForm<HospitalizationDto> {

	private static final long serialVersionUID = 1L;

	private static final String HOSPITALIZATION_HEADING_LOC = "hospitalizationHeadingLoc";
	private static final String PREVIOUS_HOSPITALIZATIONS_HEADING_LOC = "previousHospitalizationsHeadingLoc";
	private static final String HEALTH_FACILITY = Captions.CaseHospitalization_healthFacility;
	private static final String HEALTH_FACILITY_DEPARTMENT = Captions.CaseData_department;
	private static final String HOSPITAL_NAME_DETAIL = " ( %s )";
	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) +
			fluidRowLocs(HospitalizationDto.HOSPITAL_RECORD_NUMBER,  HospitalizationDto.SELECT_INPATIENT_OUTPATIENT) +
			fluidRowLocs(HEALTH_FACILITY, HEALTH_FACILITY_DEPARTMENT) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE, HospitalizationDto.LEFT_AGAINST_ADVICE, "") +
			fluidRowLocs(HospitalizationDto.HOSPITALIZATION_REASON, HospitalizationDto.OTHER_HOSPITALIZATION_REASON) +
					fluidRowLocs(3, HospitalizationDto.INTENSIVE_CARE_UNIT, 3,
							HospitalizationDto.INTENSIVE_CARE_UNIT_START,
							3,
							HospitalizationDto.INTENSIVE_CARE_UNIT_END)
					+ fluidRowLocs(HospitalizationDto.ISOLATED, HospitalizationDto.ISOLATION_DATE, "")
					+ fluidRowLocs(HospitalizationDto.DESCRIPTION) +
			loc(PREVIOUS_HOSPITALIZATIONS_HEADING_LOC) +
			fluidRowLocs(HospitalizationDto.HOSPITALIZED_PREVIOUSLY) +
			fluidRowLocs(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS);

	// Disease-specific layouts
	private static final String MEASLES_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(6, HospitalizationDto.SELECT_INPATIENT_OUTPATIENT) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE) +
			fluidRowLocs(6, HospitalizationDto.SEEN_AT_HEALTH_FACILITY) +
			fluidRowLocs(6, HospitalizationDto.DATE_FIRST_SEEN_AT_HEALTH_FACILITY);

	private static final String YELLOW_FEVER_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(HEALTH_FACILITY, HEALTH_FACILITY_DEPARTMENT) +
			fluidRowLocs(6, HospitalizationDto.SELECT_INPATIENT_OUTPATIENT) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE);

	private static final String RUBELLA_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(6, HospitalizationDto.SELECT_INPATIENT_OUTPATIENT) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE);

	private static final String MENINGITIS_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(HEALTH_FACILITY, HEALTH_FACILITY_DEPARTMENT) +
			fluidRowLocs(HospitalizationDto.HOSPITAL_RECORD_NUMBER, HospitalizationDto.SERIAL_NUMBER_IN_CONSULTATION_REGISTER) +
			fluidRowLocs(HospitalizationDto.DATE_HEALTH_REGION_NOTIFIED, HospitalizationDto.DATE_OF_CONSULTATION_AT_HEALTH_FACILITY) +
			fluidRowLocs(HospitalizationDto.SELECT_INPATIENT_OUTPATIENT, "") +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE);

	private static final String AFP_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
					fluidRowLocs(HEALTH_FACILITY, HospitalizationDto.HOSPITAL_RECORD_NUMBER) +
					fluidRowLocs(HospitalizationDto.SELECT_INPATIENT_OUTPATIENT, HospitalizationDto.ADMISSION_DATE);

	private static final String NNT_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
					fluidRowLocs(HEALTH_FACILITY, HospitalizationDto.HOSPITAL_RECORD_NUMBER) +
					fluidRowLocs(HospitalizationDto.SELECT_INPATIENT_OUTPATIENT, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) +
					fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.ADDRESS);

	private final CaseDataDto caze;
	private final ViewMode viewMode;
	private NullableOptionGroup intensiveCareUnit;
	private DateField intensiveCareUnitStart;
	private DateField intensiveCareUnitEnd;
	private NullableOptionGroup seenAtHealthFacility;
	private DateField dateFirstSeenAtHealthFacility;
	//@formatter:on

	public HospitalizationForm(CaseDataDto caze, ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction, boolean isEditAllowed) {

		super(
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale())
				.add(new OutbreakFieldVisibilityChecker(viewMode)),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.caze = caze;
		this.viewMode = viewMode;
		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		if (caze == null || viewMode == null) {
			return;
		}

		Disease disease = caze.getDisease();

		Label hospitalizationHeadingLabel = new Label(I18nProperties.getString(Strings.headingHospitalization));
		hospitalizationHeadingLabel.addStyleName(H3);
		getContent().addComponent(hospitalizationHeadingLabel, HOSPITALIZATION_HEADING_LOC);

		Label previousHospitalizationsHeadingLabel = new Label(I18nProperties.getString(Strings.headingPreviousHospitalizations));
		previousHospitalizationsHeadingLabel.addStyleName(H3);
		getContent().addComponent(previousHospitalizationsHeadingLabel, PREVIOUS_HOSPITALIZATIONS_HEADING_LOC);

		addField(HospitalizationDto.ADDRESS, TextField.class);
		TextField facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, TextField.class);
		FacilityReferenceDto healthFacility = caze.getHealthFacility();
		facilityField.setValue(getHospitalName(healthFacility, caze));
		facilityField.setReadOnly(true);

		if (!StringUtils.isEmpty(caze.getDepartment())) {
			TextField facilityDepartmentField = addCustomField(HEALTH_FACILITY_DEPARTMENT, String.class, TextField.class);
			String healthFacilityDepartment = caze.getDepartment();
			facilityDepartmentField.setValue(healthFacilityDepartment);
			facilityDepartmentField.setReadOnly(true);
		}

		final NullableOptionGroup admittedToHealthFacilityField = addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, NullableOptionGroup.class);
		final DateField admissionDateField = addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		final DateField dischargeDateField = addDateField(HospitalizationDto.DISCHARGE_DATE, DateField.class, 7);
		intensiveCareUnit = addField(HospitalizationDto.INTENSIVE_CARE_UNIT, NullableOptionGroup.class);
		intensiveCareUnitStart = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_START, DateField.class);
		intensiveCareUnitStart.setVisible(false);
		intensiveCareUnitEnd = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_END, DateField.class);
		intensiveCareUnitEnd.setVisible(false);
		FieldHelper
			.setVisibleWhen(intensiveCareUnit, Arrays.asList(intensiveCareUnitStart, intensiveCareUnitEnd), Arrays.asList(YesNoUnknown.YES), true);
		final Field isolationDateField = addField(HospitalizationDto.ISOLATION_DATE);
		final TextArea descriptionField = addField(HospitalizationDto.DESCRIPTION, TextArea.class);
		descriptionField.setRows(4);
		final NullableOptionGroup isolatedField = addField(HospitalizationDto.ISOLATED, NullableOptionGroup.class);
		final NullableOptionGroup leftAgainstAdviceField = addField(HospitalizationDto.LEFT_AGAINST_ADVICE, NullableOptionGroup.class);

		final ComboBox hospitalizationReason = addField(HospitalizationDto.HOSPITALIZATION_REASON);
		final TextField otherHospitalizationReason = addField(HospitalizationDto.OTHER_HOSPITALIZATION_REASON, TextField.class);
		NullableOptionGroup hospitalizedPreviouslyField = addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, NullableOptionGroup.class);
		CssStyles.style(hospitalizedPreviouslyField, CssStyles.ERROR_COLOR_PRIMARY);
		PreviousHospitalizationsField previousHospitalizationsField =
			addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);

		// For measles, ADMITTED_TO_HEALTH_FACILITY is not in the layout, so skip the setEnabledWhen logic
		// For measles, admission and discharge dates should be enabled based on SELECT_INPATIENT_OUTPATIENT
		// For yellow fever and congenital rubella, same logic applies
		if (caze.getDisease() != Disease.MEASLES && caze.getDisease() != Disease.YELLOW_FEVER && caze.getDisease() != Disease.CONGENITAL_RUBELLA) {
			FieldHelper.setEnabledWhen(
				admittedToHealthFacilityField,
				Arrays.asList(YesNoUnknown.YES, YesNoUnknown.NO, YesNoUnknown.UNKNOWN),
				Arrays.asList(
					facilityField,
					admissionDateField,
					dischargeDateField,
					intensiveCareUnit,
					intensiveCareUnitStart,
					intensiveCareUnitEnd,
					isolationDateField,
					descriptionField,
					isolatedField,
					leftAgainstAdviceField,
					hospitalizationReason,
					otherHospitalizationReason),
				false);
		}

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (isVisibleAllowed(HospitalizationDto.ISOLATION_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				HospitalizationDto.ISOLATION_DATE,
				HospitalizationDto.ISOLATED,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}
		if (isVisibleAllowed(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				HospitalizationDto.PREVIOUS_HOSPITALIZATIONS,
				HospitalizationDto.HOSPITALIZED_PREVIOUSLY,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			HospitalizationDto.OTHER_HOSPITALIZATION_REASON,
			HospitalizationDto.HOSPITALIZATION_REASON,
			Collections.singletonList(HospitalizationReasonType.OTHER),
			true);

		// Validations
		// Add a visual-only validator to check if symptomonsetdate<admissiondate, as saving should be possible either way
		admissionDateField.addValueChangeListener(event -> {
			if (caze.getSymptoms().getOnsetDate() != null
				&& DateComparator.getDateInstance().compare(admissionDateField.getValue(), caze.getSymptoms().getOnsetDate()) < 0) {
				admissionDateField.setComponentError(new ErrorMessage() {

					@Override
					public ErrorLevel getErrorLevel() {
						return ErrorLevel.INFO;
					}

					@Override
					public String getFormattedHtmlMessage() {
						return I18nProperties.getValidationError(
							Validations.afterDateSoft,
							admissionDateField.getCaption(),
							I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
					}
				});
			} else {
				// remove all invalidity-indicators and re-evaluate field
				admissionDateField.setComponentError(null);
				admissionDateField.markAsDirty();
			}
			// re-evaluate validity of dischargeDate (necessary because discharge has to be after admission)
			dischargeDateField.markAsDirty();
		});
		admissionDateField.addValidator(
			new DateComparisonValidator(
				admissionDateField,
				dischargeDateField,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, admissionDateField.getCaption(), dischargeDateField.getCaption())));
		dischargeDateField.addValidator(
			new DateComparisonValidator(
				dischargeDateField,
				admissionDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, dischargeDateField.getCaption(), admissionDateField.getCaption())));
		dischargeDateField.addValueChangeListener(event -> admissionDateField.markAsDirty()); // re-evaluate admission date for consistent validation of all fields
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				admissionDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitStart.getCaption(), admissionDateField.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				intensiveCareUnitEnd,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), intensiveCareUnitEnd.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				intensiveCareUnitStart,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitEnd.getCaption(), intensiveCareUnitStart.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				dischargeDateField,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitEnd.getCaption(), dischargeDateField.getCaption())));
		intensiveCareUnitStart.addValueChangeListener(event -> intensiveCareUnitEnd.markAsDirty());
		intensiveCareUnitEnd.addValueChangeListener(event -> intensiveCareUnitStart.markAsDirty());
		hospitalizedPreviouslyField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
		previousHospitalizationsField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));

		TextField hospitalRecordNumber = addField(HospitalizationDto.HOSPITAL_RECORD_NUMBER, TextField.class);
		NullableOptionGroup selectInpatientOutpatient = addField(HospitalizationDto.SELECT_INPATIENT_OUTPATIENT, NullableOptionGroup.class);
		addField(HospitalizationDto.SERIAL_NUMBER_IN_CONSULTATION_REGISTER, TextField.class);
		addDateField(HospitalizationDto.DATE_OF_CONSULTATION_AT_HEALTH_FACILITY, DateField.class, 7);
		addDateField(HospitalizationDto.DATE_HEALTH_REGION_NOTIFIED, DateField.class, 7);
		addField(HospitalizationDto.SEEN_AT_HEALTH_FACILITY, NullableOptionGroup.class);
		addDateField(HospitalizationDto.DATE_FIRST_SEEN_AT_HEALTH_FACILITY, DateField.class, 7);
		addDateField(HospitalizationDto.DATE_HEALTH_FACILITY_NOTIFIED_DISTRICT, DateField.class, 7);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			HospitalizationDto.DATE_FIRST_SEEN_AT_HEALTH_FACILITY,
			HospitalizationDto.SEEN_AT_HEALTH_FACILITY,
			Arrays.asList(YesNoUnknown.YES),
			true);

		// Show dateHealthFacilityNotifiedDistrict only for Yellow Fever
		Field<?> dateHealthFacilityNotifiedDistrictField = getField(HospitalizationDto.DATE_HEALTH_FACILITY_NOTIFIED_DISTRICT);
		if (dateHealthFacilityNotifiedDistrictField != null) {
			dateHealthFacilityNotifiedDistrictField.setVisible(caze != null && caze.getDisease() == Disease.YELLOW_FEVER);
		}

		
		// if SELECT_INPATIENT_OUTPATIENT is not null then show ADMISSION_DATE and DISCHARGE_DATE
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			HospitalizationDto.ADMISSION_DATE,
			HospitalizationDto.SELECT_INPATIENT_OUTPATIENT,
			Arrays.asList(InpatOutpat.INPATIENT, InpatOutpat.OUTPATIENT),
			true);

		FieldHelper.setVisibleWhen(
				getFieldGroup(),
				HospitalizationDto.ADMISSION_DATE,
				HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY,
				Arrays.asList(YesNoUnknown.YES),
				true);


		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			HospitalizationDto.DISCHARGE_DATE,
			HospitalizationDto.SELECT_INPATIENT_OUTPATIENT,
			Arrays.asList(InpatOutpat.INPATIENT, InpatOutpat.OUTPATIENT),
			true);


		if (disease == Disease.AFP){
			FieldHelper.setVisibleWhen(selectInpatientOutpatient, Arrays.asList(admissionDateField), Arrays.asList(YesNoUnknown.YES),true);
			FieldHelper
					.setVisibleWhen(intensiveCareUnit, Arrays.asList(intensiveCareUnitStart, intensiveCareUnitEnd), Arrays.asList(YesNoUnknown.YES), true);
		}
	}

	private void updatePrevHospHint(NullableOptionGroup hospitalizedPreviouslyField, PreviousHospitalizationsField previousHospitalizationsField) {

		YesNoUnknown value = (YesNoUnknown) hospitalizedPreviouslyField.getNullableValue();
		Collection<PreviousHospitalizationDto> previousHospitalizations = previousHospitalizationsField.getValue();
		if (UiUtil.permitted(UserRight.CASE_EDIT)
			&& value == YesNoUnknown.YES
			&& (previousHospitalizations == null || previousHospitalizations.size() == 0)) {
			hospitalizedPreviouslyField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			hospitalizedPreviouslyField.setComponentError(null);
		}
		if (Objects.nonNull(previousHospitalizationsField.getValue())) {
			hospitalizedPreviouslyField.setEnabled(previousHospitalizationsField.isEmpty());
		} else {
			hospitalizedPreviouslyField.setEnabled(true);
		}

	}

	@Override
	protected String createHtmlLayout() {

		Disease disease = caze != null ? caze.getDisease() : null;

		if (caze != null && caze.getDisease() == Disease.MEASLES) {
			return MEASLES_LAYOUT;
		}
		if (caze != null && caze.getDisease() == Disease.YELLOW_FEVER) {
			return YELLOW_FEVER_LAYOUT;
		}
		if (caze != null && caze.getDisease() == Disease.CONGENITAL_RUBELLA) {
			return RUBELLA_LAYOUT;
		}
		if (caze != null && caze.getDisease() == Disease.CSM) {
			return MENINGITIS_LAYOUT;
		}
		if (disease == Disease.AFP) return AFP_LAYOUT;
		if (disease == Disease.NEONATAL_TETANUS) return NNT_LAYOUT;
		return HTML_LAYOUT;
	}

	private String getHospitalName(FacilityReferenceDto healthFacility, CaseDataDto caze) {
		final boolean noneFacility = healthFacility == null || healthFacility.getUuid().equalsIgnoreCase(FacilityDto.NONE_FACILITY_UUID);
		if (noneFacility || !FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			return null;
		}
		StringBuilder hospitalName = new StringBuilder();
		hospitalName.append(healthFacility.buildCaption());
		if (caze.getHealthFacilityDetails() != null && caze.getHealthFacilityDetails().trim().length() > 0) {
			hospitalName.append(String.format(HOSPITAL_NAME_DETAIL, caze.getHealthFacilityDetails()));
		}
		return hospitalName.toString();
	}
}

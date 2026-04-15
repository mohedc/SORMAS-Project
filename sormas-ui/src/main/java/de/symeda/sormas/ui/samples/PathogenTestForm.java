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
package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.*;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.*;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.InjectionSite;
import de.symeda.sormas.api.utils.YesNo;
import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.AgglutinationPositiveResult;
import de.symeda.sormas.api.sample.AgglutinationTestResult;
import de.symeda.sormas.api.sample.GramStainResult;
import de.symeda.sormas.api.sample.LaboratoryType;
import de.symeda.sormas.api.sample.MacroscopicExamination;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OptionGroupWithCaption;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class PathogenTestForm extends AbstractEditForm<PathogenTestDto> {

	private static final long serialVersionUID = -1218707278398543154L;

	private static final String PATHOGEN_TEST_HEADING_LOC = "pathogenTestHeadingLoc";

	private static final String PRESCRIBER_HEADING_LOC = "prescriberHeading";
	protected static final String STOOL_SPECIMEN_RESULTS_HEADLINE_LOC = "stoolSpecimenResultsLoc";
	protected static final String FOLLOW_UP_EXAMINATION_HEADLINE_LOC = "followUpExaminationLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.REPORT_DATE, PathogenTestDto.VIA_LIMS) +
			fluidRowLocs(PathogenTestDto.EXTERNAL_ID, PathogenTestDto.EXTERNAL_ORDER_ID) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.PCR_TEST_SPECIFICATION, "") +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) +
			fluidRowLocs(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(6,PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 2,PathogenTestDto.PRELIMINARY) +
			fluidRowLocs(PathogenTestDto.RIFAMPICIN_RESISTANT, PathogenTestDto.ISONIAZID_RESISTANT, "", "") +
			fluidRowLocs(PathogenTestDto.TEST_SCALE, "") +
			fluidRowLocs(PathogenTestDto.STRAIN_CALL_STATUS, "") +
			fluidRowLocs(PathogenTestDto.SPECIE, "") +
			fluidRowLocs(PathogenTestDto.PATTERN_PROFILE, "") +
			fluidRowLocs(PathogenTestDto.DRUG_SUSCEPTIBILITY) +
			fluidRowLocs(4,PathogenTestDto.SEROTYPE, 4,PathogenTestDto.SEROTYPING_METHOD, 4,PathogenTestDto.SERO_TYPING_METHOD_TEXT) +
			fluidRowLocs(6,PathogenTestDto.SERO_GROUP_SPECIFICATION , 6, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT) +
			fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			fluidRowLocs(PathogenTestDto.CQ_VALUE, "") +
			fluidRowLocs(PathogenTestDto.CT_VALUE_E, PathogenTestDto.CT_VALUE_N) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_RDRP, PathogenTestDto.CT_VALUE_S) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_ORF_1, PathogenTestDto.CT_VALUE_RDRP_S) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT) +
			fluidRowLocs(PRESCRIBER_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE, "") +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_FIRST_NAME, PathogenTestDto.PRESCRIBER_LAST_NAME) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_PHONE_NUMBER, "") +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_ADDRESS, PathogenTestDto.PRESCRIBER_POSTAL_CODE) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_CITY, PathogenTestDto.PRESCRIBER_COUNTRY) +
			fluidRowLocs(PathogenTestDto.DELETION_REASON) +
			fluidRowLocs(PathogenTestDto.OTHER_DELETION_REASON);

	private static final String MEASLES_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(7, PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE, 5, PathogenTestDto.DATE_DISTRICT_RECEIVED_LAB_RESULTS) +
			fluidRowLocs(PathogenTestDto.DATE_INDIRECT_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE, PathogenTestDto.DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE) +
			fluidRowLocs(PathogenTestDto.PERFORM_RUBELLA_TEST, PathogenTestDto.COMMUNITY_INVESTIGATION) +
			fluidRowLocs(PathogenTestDto.INVESTIGATION_RESULTS, PathogenTestDto.SOURCE_OF_INFECTION_IDENTIFIED) +
			fluidRowLocs(4, PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT, PathogenTestDto.DATE_RESULTS_SENT_TO_DISTRICT);

	private static final String YELLOW_FEVER_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs(5, PathogenTestDto.LAB_DETAILS, 7, PathogenTestDto.DATE_RESULTS_SENT_TO_DISTRICT) +
			fluidRowLocs(4, PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 4, PathogenTestDto.VIRUS_ISOLATED) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT, "");

	private static final String MENINGITIS_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs(PathogenTestDto.LAB_DETAILS, "") +
			fluidRowLocs(PathogenTestDto.MACROSCOPIC_EXAMINATION, "") +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.CELL_COUNT_NORMAL, PathogenTestDto.CELL_COUNT_ABNORMAL) +
			fluidRowLocs(PathogenTestDto.WBC_COUNT_POLYCYTES_PERCENT, PathogenTestDto.WBC_COUNT_MONOCYTES_PERCENT) +
			fluidRowLocs(PathogenTestDto.GRAM_STAIN_RESULT, "") +
			fluidRowLocs(PathogenTestDto.AGGLUTINATION_RESULT, PathogenTestDto.AGGLUTINATION_POSITIVE_RESULTS) +
			fluidRowLocs(PathogenTestDto.AGGLUTINATION_OTHER_MICROORGANISM, "") +
			fluidRowLocs(PathogenTestDto.DATE_RESULTS_SENT_TO_DISTRICT, "") +
			fluidRowLocs(PathogenTestDto.DATE_RESULTS_SENT_TO_REGION, PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE) +
			fluidRowLocs(PathogenTestDto.REFERENCE_LABORATORY, PathogenTestDto.DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY) +
			fluidRowLocs(PathogenTestDto.OTHER_TESTS_PENDING, PathogenTestDto.OTHER_TESTS_PENDING_SPECIFY) +
			fluidRowLocs(4, PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 4, "") +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT, "");

	private static final String IDSR_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
					fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
					fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
					fluidRowLocs(PathogenTestDto.TEST_DATE_TIME) +
					fluidRowLocs(PathogenTestDto.LAB, PathogenTestDto.LAB_DETAILS) +
					fluidRowLocs(PathogenTestDto.VIRAL_DETECTION, PathogenTestDto.VIRAL_DETECTION_TEST_TYPE) +
					fluidRowLocs(PathogenTestDto.VIRAL_DETECTION_RESULTS, PathogenTestDto.TEST_RESULT_VERIFIED) +
					fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_TEXT) +
					fluidRowLocs(6, PathogenTestDto.DATE_LAB_RESULTS_SENT_DIVISION) +
					fluidRowLocs(6, PathogenTestDto.NAME_LAB_TECHNICIAN_SEND_RESULTS);

	private static final String AFP_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
					fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
					fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
					fluidRowLocs(PathogenTestDto.TEST_DATE_TIME) +
					fluidRowLocs(PathogenTestDto.LAB, PathogenTestDto.LAB_DETAILS) +
					loc(STOOL_SPECIMEN_RESULTS_HEADLINE_LOC) +
					fluidRowLocs(6,PathogenTestDto.DATE_COMBINED_CELL_CULTURE_RESULTS) +
					fluidRowLocs(PathogenTestDto.DATE_RESULTS_SENT_TO_NATIONAL_EPI, PathogenTestDto.DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE) +
					fluidRowLocs(PathogenTestDto.DATE_SENT_FROM_IC_NATIONAL_REG_LAB, PathogenTestDto.DATE_DIFFERENTIATION_SENT_EPI) +
					fluidRowLocs(6,PathogenTestDto.DATE_DIFFERENTIATION_RECEIVED_EPI) +
					fluidRowLocs(PathogenTestDto.DATE_ISOLATE_SENT_SEQUENCING, PathogenTestDto.DATE_SEQ_RESULTS_SENT_PROGRAM) +
					fluidRowLocs(PathogenTestDto.W1, PathogenTestDto.W2, PathogenTestDto.W3) +
					fluidRowLocs(PathogenTestDto.SL1, PathogenTestDto.SL2, PathogenTestDto.SL3) +
					fluidRowLocs(PathogenTestDto.SABIN_TYPE1, PathogenTestDto.SABIN_TYPE2, PathogenTestDto.SABIN_TYPE3) +
					fluidRowLocs(PathogenTestDto.NPENT, PathogenTestDto.NEV) +
					fluidRowLocs(6,PathogenTestDto.FINAL_CELL_CULTURE_RESULTS) +
					fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED) +
					fluidRowLocs(6, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
					loc(FOLLOW_UP_EXAMINATION_HEADLINE_LOC) +
					fluidRowLocs(PathogenTestDto.DATE_FOLLOWUP_EXAM, PathogenTestDto.RESIDUAL_ANALYSIS, PathogenTestDto.RESULT_EXAM);


	private static final String CONGENITAL_RUBELLA_HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.VIRUS_DETECTION_GENOTYPE) +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(4, PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 4, "") +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT, "");

	//@formatter:on

	private SampleDto sample;
	private EnvironmentSampleDto environmentSample;
	private AbstractSampleForm sampleForm;
	private final int caseSampleCount;
	private final boolean create;
	private Label pathogenTestHeadingLabel;
	private ComboBox testTypeField;
	private ComboBox diseaseField;
	private ComboBox testResultField;
	private DrugSusceptibilityForm drugSusceptibilityField;
	private TextField testTypeTextField;
	private ComboBox pcrTestSpecification;
	private Disease disease;
	private TextField typingIdField;
	private NullableOptionGroup viralDetectionField;
	private ComboBox viralDetectionTestTypeField;
	private ComboBox viralDetectionResultsField;
	private DateField dateLabResultsSentDivisionField ;
	private TextField nameLabTechnicianSendResultsField;
	private Disease caseDisease;
	private DateField dateCaptured;

	// List of tests that are used for serogrouping
	List<PathogenTestType> seroGrpTests = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING);

	public PathogenTestForm(
		AbstractSampleForm sampleForm,
		boolean create,
		int caseSampleCount,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Disease disease) {
		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sampleForm = sampleForm;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(SampleDto sample, boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {

		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sample = sample;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(EnvironmentSampleDto sample, boolean create, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {

		this(create, 0, isPseudonymized, inJurisdiction, disease);
		this.environmentSample = sample;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		super(
			PathogenTestDto.class,
			PathogenTestDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(create || inJurisdiction, !create && isPseudonymized),
			disease);// Jurisdiction doesn't matter for creation forms  // Pseudonymization doesn't matter for creation forms

		this.caseSampleCount = caseSampleCount;
		this.create = create;
		setWidth(900, Unit.PIXELS);
	}

	private static void setCqValueVisibility(TextField cqValueField, PathogenTestType testType, PathogenTestResultType testResultType) {
		if (((testType == PathogenTestType.PCR_RT_PCR && testResultType == PathogenTestResultType.POSITIVE))
			|| testType == PathogenTestType.CQ_VALUE_DETECTION) {
			cqValueField.setVisible(true);
		} else {
			cqValueField.setVisible(false);
			cqValueField.clear();
		}
	}

	private void updateDrugSusceptibilityFieldSpecifications(PathogenTestType testType, Disease disease) {
		if (disease != null) { // Drug susceptibility is applicable only diseass not for environment
			if ((FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG))) {
				boolean wasReadOnly = testResultField.isReadOnly();

				if (disease == Disease.TUBERCULOSIS && testType != null) {
					if (Arrays.asList(PathogenTestType.BEIJINGGENOTYPING, PathogenTestType.MIRU_PATTERN_CODE, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY).contains(testType)) {
						if (wasReadOnly) {
							testResultField.setReadOnly(false);
						}
						testResultField.setValue(PathogenTestResultType.NOT_APPLICABLE);
						if (wasReadOnly) {
							testResultField.setReadOnly(true);
						}
					} else if (testType == PathogenTestType.SPOLIGOTYPING) {
						if (wasReadOnly) {
							testResultField.setReadOnly(false);
						}
						testResultField.setValue(PathogenTestResultType.POSITIVE);
						if (wasReadOnly) {
							testResultField.setReadOnly(true);
						}
					} else if (wasReadOnly) {
						// Field was read-only but no longer meets conditions for auto-set values
						testResultField.setReadOnly(false);
						testResultField.setValue(null);
					}
				} else if (wasReadOnly) {
					// Disease is not TB or testType is null, but field was read-only
					testResultField.setReadOnly(false);
					testResultField.setValue(null);
				}

				drugSusceptibilityField.updateFieldsVisibility(disease, testType);
			} else {
				if (disease != Disease.TUBERCULOSIS && (DiseaseHelper.checkDiseaseIsInvasiveBacterialDiseases(disease) && testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY)) { // for non lux tb no drug susceptibility
					drugSusceptibilityField.updateFieldsVisibility(disease, testType);
					drugSusceptibilityField.setVisible(true);
				} else {
					if (drugSusceptibilityField != null) {
						drugSusceptibilityField.setVisible(false);
						drugSusceptibilityField.updateFieldsVisibility(disease, testType);
					}
				}
			}
		}
	}

	private Date getSampleDate() {
		if (sample != null) {
			return sample.getSampleDateTime();
		}
		if (sampleForm != null) {
			return (Date) sampleForm.getField(SampleDto.SAMPLE_DATE_TIME).getValue();
		}
		if (environmentSample != null) {
			return environmentSample.getSampleDateTime();
		}
		return null;
	}

	private SamplePurpose getSamplePurpose() {
		if (sample != null) {
			return sample.getSamplePurpose();
		}
		if (sampleForm != null) {
			return (SamplePurpose) sampleForm.getField(SampleDto.SAMPLE_PURPOSE).getValue();
		}
		return null;
	}

	private LaboratoryType getLaboratoryType() {
		if (sample != null) {
			return sample.getLaboratoryType();
		}
		if (sampleForm != null) {
			return (LaboratoryType) sampleForm.getField(SampleDto.LABORATORY_TYPE).getValue();
		}
		return null;
	}

	@Override
	protected String createHtmlLayout() {
		if (disease == Disease.MEASLES) {
			return MEASLES_HTML_LAYOUT;
		}
		if (disease == Disease.YELLOW_FEVER) {
			return YELLOW_FEVER_HTML_LAYOUT;
		}
		if (disease == Disease.CSM) {
			return MENINGITIS_HTML_LAYOUT;
		}
		if (disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			return IDSR_HTML_LAYOUT;
		}
		if (disease == Disease.AFP) {
			return AFP_HTML_LAYOUT;
		}
		if (disease == Disease.CONGENITAL_RUBELLA) {
			return CONGENITAL_RUBELLA_HTML_LAYOUT;
		}
		return HTML_LAYOUT;
	}

	@Override
	public void setHeading(String heading) {
		pathogenTestHeadingLabel.setValue(heading);
	}

	@Override
	public void setValue(PathogenTestDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		pcrTestSpecification.setValue(newFieldValue.getPcrTestSpecification());
		testTypeTextField.setValue(newFieldValue.getTestTypeText());
		typingIdField.setValue(newFieldValue.getTypingId());
	}

	@Override
	protected void addFields() {

		CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sample.getAssociatedCase().getUuid());
		caseDisease = caseDataDto.getDisease();

		pathogenTestHeadingLabel = new Label();
		pathogenTestHeadingLabel.addStyleName(H3);
		getContent().addComponent(pathogenTestHeadingLabel, PATHOGEN_TEST_HEADING_LOC);

		Label stoolSpecimenResults = new Label(I18nProperties.getString(Strings.headingStoolSpecimenResults));
		CssStyles.style(stoolSpecimenResults, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(stoolSpecimenResults, STOOL_SPECIMEN_RESULTS_HEADLINE_LOC);

		Label followUpExamination = new Label(I18nProperties.getString(Strings.headingFollowUpExamination));
		CssStyles.style(followUpExamination, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(followUpExamination, FOLLOW_UP_EXAMINATION_HEADLINE_LOC);

		addDateField(PathogenTestDto.REPORT_DATE, DateField.class, 0);
		addField(PathogenTestDto.VIA_LIMS);
		addField(PathogenTestDto.EXTERNAL_ID);
		addField(PathogenTestDto.EXTERNAL_ORDER_ID);
		testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		testTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		testTypeField.setImmediate(true);
		TextField seroTypingMethodText = addField(PathogenTestDto.SERO_TYPING_METHOD_TEXT);
		seroTypingMethodText.setVisible(false);
		pcrTestSpecification = addField(PathogenTestDto.PCR_TEST_SPECIFICATION, ComboBox.class);
		testTypeTextField = addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		FieldHelper.addSoftRequiredStyle(testTypeTextField);
		DateTimeField testDateField = addField(PathogenTestDto.TEST_DATE_TIME, DateTimeField.class);
		testDateField.addValidator(
			new DateComparisonValidator(
				testDateField,
				this::getSampleDate,
				false,
				false,
				I18nProperties.getValidationError(
					Validations.afterDateWithDate,
					testDateField.getCaption(),
					I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME),
					DateFormatHelper.formatDate(getSampleDate()))));
		ComboBox lab = addInfrastructureField(PathogenTestDto.LAB);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		TextField labDetails = addField(PathogenTestDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		typingIdField = addField(PathogenTestDto.TYPING_ID, TextField.class);
		typingIdField.setVisible(false);

		addField(PathogenTestDto.DATE_COMBINED_CELL_CULTURE_RESULTS, DateField.class);
		addField(PathogenTestDto.DATE_RESULTS_SENT_TO_NATIONAL_EPI, DateField.class);
		addField(PathogenTestDto.DATE_SENT_FROM_IC_NATIONAL_REG_LAB, DateField.class);
		addField(PathogenTestDto.DATE_DIFFERENTIATION_SENT_EPI, DateField.class);
		addField(PathogenTestDto.DATE_DIFFERENTIATION_RECEIVED_EPI, DateField.class);
		addField(PathogenTestDto.DATE_ISOLATE_SENT_SEQUENCING, DateField.class);
		addField(PathogenTestDto.DATE_SEQ_RESULTS_SENT_PROGRAM, DateField.class);
		addField(PathogenTestDto.W1, OptionGroup.class);
		addField(PathogenTestDto.W2, OptionGroup.class);
		addField(PathogenTestDto.W3, OptionGroup.class);
		addField(PathogenTestDto.SL1, OptionGroup.class);
		addField(PathogenTestDto.SL2, OptionGroup.class);
		addField(PathogenTestDto.SL3, OptionGroup.class);
		addField(PathogenTestDto.SABIN_TYPE1, OptionGroup.class);
		addField(PathogenTestDto.SABIN_TYPE2, OptionGroup.class);
		addField(PathogenTestDto.SABIN_TYPE3, OptionGroup.class);
		addField(PathogenTestDto.NPENT, NullableOptionGroup.class);
		addField(PathogenTestDto.NEV, NullableOptionGroup.class);
		ComboBox finalCellCultureResults = addField(PathogenTestDto.FINAL_CELL_CULTURE_RESULTS, ComboBox.class);
		List<PathogenTestResultType> cellResults = Arrays.asList(PathogenTestResultType.SUSPECTED_POLIOVIRUS, PathogenTestResultType.NEGATIVE, PathogenTestResultType.NPENT, PathogenTestResultType.SUSPECT_POLIOVIRUS_NPENT);
		FieldHelper.updateEnumData(finalCellCultureResults, cellResults);
		addField(PathogenTestDto.DATE_FOLLOWUP_EXAM, DateField.class);
		NullableOptionGroup residualAnalysis = addField(PathogenTestDto.RESIDUAL_ANALYSIS, NullableOptionGroup.class);

		List<InjectionSite> paralysisSite = Arrays.asList(InjectionSite.LEFT_ARM, InjectionSite.LEFT_LEG, InjectionSite.RIGHT_ARM, InjectionSite.RIGHT_LEG);
		FieldHelper.updateEnumData(residualAnalysis, paralysisSite);
		addField(PathogenTestDto.RESULT_EXAM, ComboBox.class);

		// Tested Desease or Tested Pathogen, depending on sample type
		diseaseField = addDiseaseField(PathogenTestDto.TESTED_DISEASE, true, create, false);
		addField(PathogenTestDto.TESTED_DISEASE_DETAILS, TextField.class);
		ComboBox diseaseVariantField = addCustomizableEnumField(PathogenTestDto.TESTED_DISEASE_VARIANT);
		diseaseVariantField.setNullSelectionAllowed(true);
		TextField diseaseVariantDetailsField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		ComboBox testedPathogenField = addCustomizableEnumField(PathogenTestDto.TESTED_PATHOGEN);
		TextField testedPathogenDetailsField = addField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, TextField.class);
		testedPathogenDetailsField.setVisible(false);
		FieldHelper.updateItems(testedPathogenField, FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, null));
		testedPathogenField.addValueChangeListener(e -> {
			Pathogen pathogen = (Pathogen) e.getProperty().getValue();
			if (pathogen != null && pathogen.isHasDetails()) {
				testedPathogenDetailsField.setVisible(true);
			} else {
				testedPathogenDetailsField.clear();
				testedPathogenDetailsField.setVisible(false);
			}
		});

		if (environmentSample == null) {
			diseaseField.setVisible(true);
			diseaseField.setRequired(true);

			testedPathogenField.setVisible(false);
			testedPathogenField.setRequired(false);
		} else {
			diseaseField.setVisible(false);
			diseaseField.setRequired(false);

			testedPathogenField.setVisible(true);
			testedPathogenField.setRequired(true);
		}

		testResultField = addField(PathogenTestDto.TEST_RESULT, ComboBox.class);
		testResultField.removeItem(PathogenTestResultType.NOT_DONE);

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			testResultField.removeItem(PathogenTestResultType.NOT_APPLICABLE);
		}
		TextField seroTypeTF = addField(PathogenTestDto.SEROTYPE, TextField.class);

		NullableOptionGroup rifampicinResistantField = addField(PathogenTestDto.RIFAMPICIN_RESISTANT, NullableOptionGroup.class);
		rifampicinResistantField.setVisible(false);

		NullableOptionGroup isoniazidResistantField = addField(PathogenTestDto.ISONIAZID_RESISTANT, NullableOptionGroup.class);
		isoniazidResistantField.setVisible(false);

		ComboBox testScaleField = addField(PathogenTestDto.TEST_SCALE, ComboBox.class);
		testScaleField.setVisible(false);

		ComboBox strainCallStatusField = addField(PathogenTestDto.STRAIN_CALL_STATUS, ComboBox.class);
		strainCallStatusField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		strainCallStatusField.setVisible(false);

		ComboBox specieField = addField(PathogenTestDto.SPECIE, ComboBox.class);
		specieField.setVisible(false);

		TextField patternProfileField = addField(PathogenTestDto.PATTERN_PROFILE, TextField.class);
		patternProfileField.setVisible(false);

			drugSusceptibilityField = (DrugSusceptibilityForm) addField(
					PathogenTestDto.DRUG_SUSCEPTIBILITY,
					new DrugSusceptibilityForm(
							FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
							UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale())));
			drugSusceptibilityField.setCaption(null);
			drugSusceptibilityField.setVisible(false);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			//tuberculosis-pcr test specification
			Map<Object, List<Object>> tuberculosisPcrDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR));
					put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.RIFAMPICIN_RESISTANT, tuberculosisPcrDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.RIFAMPICIN_RESISTANT, tuberculosisPcrDependencies);
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.ISONIAZID_RESISTANT, tuberculosisPcrDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.ISONIAZID_RESISTANT, tuberculosisPcrDependencies);

			//tuberculosis-microscopy test specification
			Map<Object, List<Object>> tuberculosisMicroscopyDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MICROSCOPY));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TEST_SCALE, tuberculosisMicroscopyDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.TEST_SCALE, tuberculosisMicroscopyDependencies);

			//tuberculosis-beijinggenotyping test specification
			Map<Object, List<Object>> tuberculosisBeijingDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.BEIJINGGENOTYPING));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.STRAIN_CALL_STATUS, tuberculosisBeijingDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.STRAIN_CALL_STATUS, tuberculosisBeijingDependencies);

			//tuberculosis-spoligotyping test specification
			Map<Object, List<Object>> tuberculosisSpoligotypingDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.SPOLIGOTYPING));
					put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SPECIE, tuberculosisSpoligotypingDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.SPECIE, tuberculosisSpoligotypingDependencies);

			//tuberculosis-miru-code test specification
			Map<Object, List<Object>> tuberculosisMiruCodeDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies);

			//tuberculosis-antibiotic test specification
			Map<Object, List<Object>> tuberculosisAntibioticDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.DRUG_SUSCEPTIBILITY, tuberculosisAntibioticDependencies, true);

			//test result - read only
			Map<Object, List<Object>> tuberculosisTestResultReadOnlyDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
					put(
						PathogenTestDto.TEST_TYPE,
						Arrays.asList(
							PathogenTestType.BEIJINGGENOTYPING,
							PathogenTestType.SPOLIGOTYPING,
							PathogenTestType.MIRU_PATTERN_CODE,
							PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
				}
			};
			FieldHelper.setReadOnlyWhen(getFieldGroup(), PathogenTestDto.TEST_RESULT, tuberculosisTestResultReadOnlyDependencies, true, false);
		} else if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
		&& DiseaseHelper.checkDiseaseIsInvasiveBacterialDiseases(disease)) {
			//invasive-antibiotic test specification
			Map<Object, List<Object>> invasiveAntibioticDependencies = new HashMap<>() {
				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.DRUG_SUSCEPTIBILITY, invasiveAntibioticDependencies, true);
		}

		seroTypeTF.setVisible(false);
		ComboBox seroTypeMetCB = addField(PathogenTestDto.SEROTYPING_METHOD, ComboBox.class);
		seroTypeMetCB.setVisible(false);
		ComboBox seroGrpSepcCB = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION, ComboBox.class);
		seroGrpSepcCB.setVisible(false);
		TextField seroGrpSpecTxt = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, TextField.class);
		TextField cqValueField = addField(FieldConfiguration.withConversionError(PathogenTestDto.CQ_VALUE, Validations.onlyNumbersAllowed));
		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			cqValueField.setVisible(false);
		}

		addFields(
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_E, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_N, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_RDRP, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_S, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_ORF_1, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_RDRP_S, Validations.onlyNumbersAllowed));

		setVisibleClear(
			false,
			PathogenTestDto.CQ_VALUE,
			PathogenTestDto.CT_VALUE_E,
			PathogenTestDto.CT_VALUE_N,
			PathogenTestDto.CT_VALUE_RDRP,
			PathogenTestDto.CT_VALUE_S,
			PathogenTestDto.CT_VALUE_ORF_1,
			PathogenTestDto.CT_VALUE_RDRP_S);
		NullableOptionGroup testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, NullableOptionGroup.class);
		testResultVerifiedField.setRequired(true);
		addField(PathogenTestDto.PRELIMINARY).addStyleName(CssStyles.VSPACE_4);
		CheckBox fourFoldIncrease = addField(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
		CssStyles.style(fourFoldIncrease, VSPACE_3, VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);

		addField(PathogenTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(6);
		TextField virusDetectionGenotypeField = addField(PathogenTestDto.VIRUS_DETECTION_GENOTYPE, TextField.class);
		virusDetectionGenotypeField.setVisible(false);
		addField(PathogenTestDto.VIRUS_ISOLATED, NullableOptionGroup.class);

		// Measles-specific fields (hidden by default, shown in configureMeaslesFields)
		addDateField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISTRICT, DateField.class, 7);
		addDateField(PathogenTestDto.DATE_DISTRICT_RECEIVED_LAB_RESULTS, DateField.class, 7);
		addDateField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE, DateField.class, 7);
		addDateField(PathogenTestDto.DATE_INDIRECT_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE, DateField.class, 7);
		dateCaptured = addDateField(PathogenTestDto.DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE, DateField.class, 7);
		addField(PathogenTestDto.COMMUNITY_INVESTIGATION, CheckBox.class);
		addField(PathogenTestDto.PERFORM_RUBELLA_TEST, CheckBox.class);
		TextArea investigationResultsField = addField(PathogenTestDto.INVESTIGATION_RESULTS, TextArea.class);
		investigationResultsField.setRows(4);
		addField(PathogenTestDto.SOURCE_OF_INFECTION_IDENTIFIED, TextField.class);
		ComboBox finalClassificationField = addField(PathogenTestDto.FINAL_CLASSIFICATION, ComboBox.class);
		finalClassificationField.addItems(FinalClassification.values());
		finalClassificationField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		// Meningitis-specific fields (hidden by default, shown in configureMeningitisFields)
		ComboBox macroscopicExaminationField = addField(PathogenTestDto.MACROSCOPIC_EXAMINATION, ComboBox.class);
		macroscopicExaminationField.setVisible(false);
		NullableOptionGroup cellCountNormalField = addField(PathogenTestDto.CELL_COUNT_NORMAL, NullableOptionGroup.class);
		cellCountNormalField.setVisible(false);
		NullableOptionGroup cellCountAbnormalField = addField(PathogenTestDto.CELL_COUNT_ABNORMAL, NullableOptionGroup.class);
		cellCountAbnormalField.setVisible(false);
		TextField wbcCountPolycytesField = addField(PathogenTestDto.WBC_COUNT_POLYCYTES_PERCENT, TextField.class);
		wbcCountPolycytesField.setVisible(false);
		TextField wbcCountMonocytesField = addField(PathogenTestDto.WBC_COUNT_MONOCYTES_PERCENT, TextField.class);
		wbcCountMonocytesField.setVisible(false);
		ComboBox gramStainResultField = addField(PathogenTestDto.GRAM_STAIN_RESULT, ComboBox.class);
		gramStainResultField.setVisible(false);
		NullableOptionGroup agglutinationResultField = addField(PathogenTestDto.AGGLUTINATION_RESULT, NullableOptionGroup.class);
		agglutinationResultField.setVisible(false);
		// For agglutination positive results, we'll use checkboxes
		// Note: This will need special handling as it's a Set
		ComboBox agglutinationPositiveResultsField = addField(PathogenTestDto.AGGLUTINATION_POSITIVE_RESULTS, ComboBox.class);
		agglutinationPositiveResultsField.setVisible(false);
		TextField agglutinationOtherMicroorganismField = addField(PathogenTestDto.AGGLUTINATION_OTHER_MICROORGANISM, TextField.class);
		agglutinationOtherMicroorganismField.setVisible(false);
		addDateField(PathogenTestDto.DATE_RESULTS_SENT_TO_REGION, DateField.class, 7);
		addDateField(PathogenTestDto.DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY, DateField.class, 7);
		ComboBox referenceLaboratoryField = addInfrastructureField(PathogenTestDto.REFERENCE_LABORATORY);
		referenceLaboratoryField.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		referenceLaboratoryField.setVisible(false);
		NullableOptionGroup otherTestsPendingField = addField(PathogenTestDto.OTHER_TESTS_PENDING, NullableOptionGroup.class);
		otherTestsPendingField.setVisible(false);
		TextField otherTestsPendingSpecifyField = addField(PathogenTestDto.OTHER_TESTS_PENDING_SPECIFY, TextField.class);
		otherTestsPendingSpecifyField.setVisible(false);

		addFields(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE, PathogenTestDto.PRESCRIBER_FIRST_NAME, PathogenTestDto.PRESCRIBER_LAST_NAME);
		TextField proscriberPhoneField = addField(PathogenTestDto.PRESCRIBER_PHONE_NUMBER, TextField.class);
		proscriberPhoneField.addValidator(
			new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, proscriberPhoneField.getCaption())));

		addFields(PathogenTestDto.PRESCRIBER_ADDRESS, PathogenTestDto.PRESCRIBER_POSTAL_CODE, PathogenTestDto.PRESCRIBER_CITY);
		ComboBox prescriberCountrField = addInfrastructureField(PathogenTestDto.PRESCRIBER_COUNTRY);
		FieldHelper.updateItems(prescriberCountrField, FacadeProvider.getCountryFacade().getAllActiveAsReference());

		addField(PathogenTestDto.DELETION_REASON);
		addField(PathogenTestDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, PathogenTestDto.DELETION_REASON, PathogenTestDto.OTHER_DELETION_REASON);
		viralDetectionField = addField(PathogenTestDto.VIRAL_DETECTION, NullableOptionGroup.class);
		viralDetectionTestTypeField = addField(PathogenTestDto.VIRAL_DETECTION_TEST_TYPE, ComboBox.class);
		viralDetectionResultsField = addField(PathogenTestDto.VIRAL_DETECTION_RESULTS, ComboBox.class);
		dateLabResultsSentDivisionField = addField(PathogenTestDto.DATE_LAB_RESULTS_SENT_DIVISION, DateField.class);
		nameLabTechnicianSendResultsField = addField(PathogenTestDto.NAME_LAB_TECHNICIAN_SEND_RESULTS, TextField.class);

		initializeAccessAndAllowedAccesses();
		initializeVisibilitiesAndAllowedVisibilities();

		pcrTestSpecification.setVisible(false);

		if (isVisibleAllowed(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE)) {
			Label prescriberHeadingLabel = new Label(I18nProperties.getCaption(Captions.PathogenTest_prescriber));
			prescriberHeadingLabel.addStyleName(H3);
			getContent().addComponent(prescriberHeadingLabel, PRESCRIBER_HEADING_LOC);
		}

		Map<Object, List<Object>> pcrTestSpecificationVisibilityDependencies = new HashMap<>() {

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CORONAVIRUS));
				put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PCR_TEST_SPECIFICATION, pcrTestSpecificationVisibilityDependencies, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TEST_TYPE_TEXT,
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TESTED_DISEASE_DETAILS,
			PathogenTestDto.TESTED_DISEASE,
			Arrays.asList(Disease.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TYPING_ID,
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.DNA_MICROARRAY, PathogenTestType.SEQUENCING),
			true);

		Map<Object, List<Object>> serotypeVisibilityDependencies = new HashMap<Object, List<Object>>() {

			private static final long serialVersionUID = 1967952323596082247L;

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CSM));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SEROTYPE, serotypeVisibilityDependencies, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_TYPING_METHOD_TEXT,
			PathogenTestDto.SEROTYPING_METHOD,
			SerotypingMethod.OTHER,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT,
			PathogenTestDto.SERO_GROUP_SPECIFICATION,
			SeroGroupSpecification.OTHER,
			true);

		Consumer<Disease> updateDiseaseVariantField = disease -> {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField.setVisible(
				disease != null && isVisibleAllowed(PathogenTestDto.TESTED_DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		};

		updateDiseaseVariantField.accept((Disease) diseaseField.getValue());

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			if (caseDisease != null && (caseDisease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS || caseDisease == Disease.AFP)) {
				return;
			}
			Disease latestDisease = (Disease) valueChangeEvent.getProperty().getValue();
			// If the disease changed, test type field should be updated with its respective test types
			if (latestDisease != disease) {
				testTypeField.clear();
			}
			disease = latestDisease;
			updateDiseaseVariantField.accept(disease);

			FieldHelper.updateItems(
				testTypeField,
				Arrays.asList(PathogenTestType.values()),
				FieldVisibilityCheckers.withDisease(disease),
				PathogenTestType.class);

			if (Disease.MEASLES.equals(caseDisease)) {
				applyMeaslesCaseTestTypeRestriction(disease);
			}

			// Configure measles-specific fields if disease is measles
			if (disease == Disease.MEASLES) {
				configureMeaslesFields();
			}
			// Configure yellow fever-specific fields if disease is yellow fever
			if (disease == Disease.YELLOW_FEVER) {
				configureYellowFeverFields();
			}
			// Configure meningitis-specific fields if disease is CSM
			if (disease == Disease.CSM) {
				configureMeningitisFields();
			}
			// Configure congenital rubella-specific fields if disease is congenital rubella
			if (disease == Disease.CONGENITAL_RUBELLA) {
				configureCongenitalRubellaFields();
			}

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				FieldHelper.updateItems(
					strainCallStatusField,
					Arrays.asList(PathogenStrainCallStatus.values()),
					FieldVisibilityCheckers.withDisease(disease),
					PathogenStrainCallStatus.class);

				updateDrugSusceptibilityFieldSpecifications((PathogenTestType) testTypeField.getValue(), disease);
			}
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			if (testType != null) {
				if (testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY) {
					fourFoldIncrease.setVisible(true);
					fourFoldIncrease.setEnabled(caseSampleCount >= 2);
				} else {
					fourFoldIncrease.setVisible(false);
					fourFoldIncrease.setEnabled(false);
				}
				// If disease is IMI or IPI and test type is serogrouping, then test result is set to positive and not editable
				if (seroGrpTests.contains(testType)) {
					testResultField.setValue(PathogenTestResultType.POSITIVE);
				} else {
					testResultField.clear();
				}

				updateDrugSusceptibilityFieldSpecifications(testType, (Disease) diseaseField.getValue());

				seroTypeMetCB.setVisible(disease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION && PathogenTestType.SEROGROUPING.equals(testType));
				seroTypeTF.setVisible(disease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION && seroGrpTests.contains(testType));
				seroGrpSepcCB.setVisible(disease == Disease.INVASIVE_MENINGOCOCCAL_INFECTION && seroGrpTests.contains(testType));
				// for enabling the test result, finding configured country and disease
				boolean isLuxTbAntiSus = FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
						&& Disease.TUBERCULOSIS.equals((Disease) diseaseField.getValue()) && PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY.equals(testType);
				if(isLuxTbAntiSus){
					seroGrpTests = new ArrayList<>(seroGrpTests);
					seroGrpTests.add(testType);
				}
				// for all serogrouping tests and isLuxTbAntiSus the test result field should be disabled
				testResultField.setEnabled(!seroGrpTests.contains(testType));
				setVisibleClear(
					PathogenTestType.PCR_RT_PCR == testType,
					PathogenTestDto.CQ_VALUE,
					PathogenTestDto.CT_VALUE_E,
					PathogenTestDto.CT_VALUE_N,
					PathogenTestDto.CT_VALUE_RDRP,
					PathogenTestDto.CT_VALUE_S,
					PathogenTestDto.CT_VALUE_ORF_1,
					PathogenTestDto.CT_VALUE_RDRP_S);
				// If the disease is IMI or IPI and the test type is antibiotic susceptibility,
				// then a test result is set to positive and disabled
				if (DiseaseHelper.checkDiseaseIsInvasiveBacterialDiseases((Disease) diseaseField.getValue()) && testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
					testResultField.setValue(PathogenTestResultType.POSITIVE);
					testResultField.setEnabled(false);
				}

			} else {
				setVisibleClear(
					testTypeField.getValue() != null,
					PathogenTestDto.SEROTYPE,
					PathogenTestDto.SEROTYPING_METHOD,
					PathogenTestDto.SERO_GROUP_SPECIFICATION);
				testResultField.clear();
				testResultField.setEnabled(true);
			}

				// If disease is IMI or IPI and test type is antibiotic susceptibility, then test result is set to positive
				if ((diseaseField.getValue() == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION || diseaseField.getValue() == Disease.INVASIVE_MENINGOCOCCAL_INFECTION)
				&& testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
					testResultField.setValue(PathogenTestResultType.POSITIVE);
				}
		});
		lab.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null
				&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
				labDetails.setVisible(true);
				labDetails.setRequired(isEditableAllowed(labDetails));
			} else {
				labDetails.setVisible(false);
				labDetails.setRequired(false);
				labDetails.clear();
			}
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			setCqValueVisibility(cqValueField, testType, (PathogenTestResultType) testResultField.getValue());
			// Reconfigure congenital rubella fields when test type changes
			if (disease == Disease.CONGENITAL_RUBELLA) {
				configureCongenitalRubellaFields();
			}
		});

		testResultField.addValueChangeListener(e -> {
			PathogenTestResultType testResult = (PathogenTestResultType) e.getProperty().getValue();
			setCqValueVisibility(cqValueField, (PathogenTestType) testTypeField.getValue(), testResult);
			// Reconfigure congenital rubella fields when test result changes
			if (disease == Disease.CONGENITAL_RUBELLA) {
				configureCongenitalRubellaFields();
			}
		});

		if (SamplePurpose.INTERNAL.equals(getSamplePurpose())) { // this only works for already saved samples
			setRequired(true, PathogenTestDto.LAB);
		}
		setRequired(true, PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_RESULT);

		// Measles-specific configuration (called after all other visibility logic)
		if (disease == Disease.MEASLES) {
			configureMeaslesFields();
		}
		if (Disease.MEASLES.equals(caseDisease)) {
			applyMeaslesCaseTestTypeRestriction((Disease) diseaseField.getValue());
		}
		// Yellow fever-specific configuration (called after all other visibility logic)
		if (disease == Disease.YELLOW_FEVER) {
			configureYellowFeverFields();
		}
		// Meningitis-specific configuration (called after all other visibility logic)
		if (disease == Disease.CSM) {
			configureMeningitisFields();
		}

		if (disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS){
			handleIDSR();
		}
		
		if(disease == Disease.AFP){
			handleAFP();
		}
		// Congenital rubella-specific configuration (called after all other visibility logic)
		if (disease == Disease.CONGENITAL_RUBELLA) {
			configureCongenitalRubellaFields();
		}
	}

	/**
	 * When the case disease is measles, limits test type options by tested disease (Measles / Rubella / Dengue).
	 */
	private void applyMeaslesCaseTestTypeRestriction(Disease testedDisease) {
		if (!Disease.MEASLES.equals(caseDisease) || testedDisease == null) {
			return;
		}

		PathogenTestType previous = (PathogenTestType) testTypeField.getValue();

		if (testedDisease == Disease.MEASLES) {
			List<PathogenTestType> items =
				Arrays.asList(PathogenTestType.INDIRECT_IGM_SEROLOGY, PathogenTestType.CAPTURED_IGM_SEROLOGY);
			testTypeField.removeAllItems();
			testTypeField.addItems(items);
			if (previous != null && items.contains(previous)) {
				testTypeField.setValue(previous);
			} else {
				testTypeField.setValue(null);
			}
			return;
		}

		if (testedDisease == Disease.RUBELLA || testedDisease == Disease.DENGUE) {
			testTypeField.removeAllItems();
			testTypeField.addItem(PathogenTestType.IGM_SERUM_ANTIBODY);
			testTypeField.setItemCaption(PathogenTestType.IGM_SERUM_ANTIBODY, "IgM");
			if (PathogenTestType.IGM_SERUM_ANTIBODY.equals(previous)) {
				testTypeField.setValue(previous);
			} else {
				testTypeField.setValue(null);
			}
		}
	}

	/**
	 * Configures fields specifically for measles pathogen tests
	 */
	protected void configureMeaslesFields() {
		// Show investigation results when community investigation is yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.INVESTIGATION_RESULTS),
			PathogenTestDto.COMMUNITY_INVESTIGATION,
			Arrays.asList(true),
			true);

		// Hide tested disease details if not OTHER
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TESTED_DISEASE_DETAILS,
			PathogenTestDto.TESTED_DISEASE,
			Arrays.asList(Disease.OTHER),
			true);

		// Filter tested disease field to only show MEASLES, DENGUE, and RUBELLA (excluding CONGENITAL_RUBELLA)
		// Only filter if case disease is MEASLES
		if (disease == Disease.MEASLES) {
			// list of possible tested diseases for measles
			List<Disease> possibleTestedDiseases = Arrays.asList(Disease.MEASLES, Disease.DENGUE, Disease.RUBELLA);

			// Get tested disease field and remove all items that are not in the possibleTestedDiseases list
			ComboBox testedDiseaseField = (ComboBox) getField(PathogenTestDto.TESTED_DISEASE);
			Object currentValue = testedDiseaseField.getValue();
			
			// Get all item IDs and remove those not in the allowed list
			@SuppressWarnings("unchecked")
			Collection<Object> itemIds = (Collection<Object>) testedDiseaseField.getItemIds();
			List<Object> itemsToRemove = itemIds.stream()
				.filter(item -> !possibleTestedDiseases.contains(item))
				.collect(Collectors.toList());
			
			for (Object item : itemsToRemove) {
				testedDiseaseField.removeItem(item);
			}
		
			
			// Restore the current value if it's still valid
			if (currentValue != null && possibleTestedDiseases.contains(currentValue)) {
				testedDiseaseField.setValue(currentValue);
			} else if (currentValue != null) {
				testedDiseaseField.setValue(null);
			}
		}



	}

	/**
	 * Configures fields specifically for yellow fever pathogen tests
	 */
	protected void configureYellowFeverFields() {
		// Show investigation results when community investigation is yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.INVESTIGATION_RESULTS),
			PathogenTestDto.COMMUNITY_INVESTIGATION,
			Arrays.asList(true),
			true);

		// Hide tested disease details if not OTHER
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TESTED_DISEASE_DETAILS,
			PathogenTestDto.TESTED_DISEASE,
			Arrays.asList(Disease.OTHER),
			true);
	}

	/**
	 * Configures fields specifically for meningitis pathogen tests
	 */
	protected void configureMeningitisFields() {
		// Show macroscopic examination
		getField(PathogenTestDto.MACROSCOPIC_EXAMINATION).setVisible(true);
		ComboBox macroscopicExaminationField = (ComboBox) getField(PathogenTestDto.MACROSCOPIC_EXAMINATION);
		macroscopicExaminationField.addItems(MacroscopicExamination.values());
		macroscopicExaminationField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		// Show cell count fields when test type is CELL_COUNT
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.CELL_COUNT_NORMAL, PathogenTestDto.CELL_COUNT_ABNORMAL),
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.CELL_COUNT),
			true);

		// Show WBC count fields when test type is WBC_COUNT
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.WBC_COUNT_POLYCYTES_PERCENT, PathogenTestDto.WBC_COUNT_MONOCYTES_PERCENT),
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.WBC_COUNT),
			true);

		// Show Gram stain result when test type is GRAM_STAIN
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.GRAM_STAIN_RESULT,
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.GRAM_STAIN),
			true);
		ComboBox gramStainResultField = (ComboBox) getField(PathogenTestDto.GRAM_STAIN_RESULT);
		gramStainResultField.addItems(GramStainResult.values());
		gramStainResultField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		// Show agglutination fields when test type is LATEX_AGGLUTINATION or SLIDE_AGGLUTINATION
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.AGGLUTINATION_RESULT),
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.AGGLUTINATION_TEST),
			true);
		NullableOptionGroup agglutinationResultField = (NullableOptionGroup) getField(PathogenTestDto.AGGLUTINATION_RESULT);
		agglutinationResultField.addItems(AgglutinationTestResult.values());
		agglutinationResultField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		ComboBox agglutinationPositiveResultsField = (ComboBox) getField(PathogenTestDto.AGGLUTINATION_POSITIVE_RESULTS);
		agglutinationPositiveResultsField.addItems(AgglutinationPositiveResult.values());
		agglutinationPositiveResultsField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		// Show agglutination positive results when agglutination result is POSITIVE
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.AGGLUTINATION_POSITIVE_RESULTS),
			PathogenTestDto.AGGLUTINATION_RESULT,
			Arrays.asList(AgglutinationTestResult.POSITIVE),
			true);

		// Show agglutination other microorganism when agglutination positive results is OTHER_MICROORGANISMS
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.AGGLUTINATION_OTHER_MICROORGANISM,
			PathogenTestDto.AGGLUTINATION_POSITIVE_RESULTS,
			Arrays.asList(AgglutinationPositiveResult.OTHER_MICROORGANISMS),
			true);

		// Show other tests pending specify when other tests pending is Yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.OTHER_TESTS_PENDING_SPECIFY,
			PathogenTestDto.OTHER_TESTS_PENDING,
			Arrays.asList(true),
			true);

		// Show other tests pending field
		getField(PathogenTestDto.OTHER_TESTS_PENDING).setVisible(true);

		// DATE_RESULTS_SENT_TO_DISTRICT is always visible (already in layout)
		getField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISTRICT).setVisible(true);

		// Set conditional visibility for date fields based on laboratory type
		LaboratoryType laboratoryType = getLaboratoryType();
		if (laboratoryType == LaboratoryType.REGIONAL_LABORATORY) {
			// For Regional Laboratory: Show regional lab specific fields
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REGION).setVisible(true);
			getField(PathogenTestDto.DATE_DISTRICT_RECEIVED_LAB_RESULTS).setVisible(true);
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY).setVisible(true);
			getField(PathogenTestDto.REFERENCE_LABORATORY).setVisible(true);
			// Hide reference lab specific field
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE).setVisible(false);
		} else if (laboratoryType == LaboratoryType.REFERENCE_LABORATORY) {
			// For Reference Laboratory: Show reference lab specific field
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE).setVisible(true);
			// Hide regional lab specific fields
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REGION).setVisible(false);
			getField(PathogenTestDto.DATE_DISTRICT_RECEIVED_LAB_RESULTS).setVisible(false);
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY).setVisible(false);
			getField(PathogenTestDto.REFERENCE_LABORATORY).setVisible(true);
		} else {
			// If laboratory type is not set or is HEALTH_LABORATORY, hide all conditional fields
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REGION).setVisible(false);
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE).setVisible(false);
			getField(PathogenTestDto.DATE_DISTRICT_RECEIVED_LAB_RESULTS).setVisible(false);
			getField(PathogenTestDto.DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY).setVisible(false);
			getField(PathogenTestDto.REFERENCE_LABORATORY).setVisible(false);
		}
	}

	/**
	 * Configures fields specifically for congenital rubella pathogen tests
	 */
	protected void configureCongenitalRubellaFields() {
		// Hide tested disease details if not OTHER
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TESTED_DISEASE_DETAILS,
			PathogenTestDto.TESTED_DISEASE,
			Arrays.asList(Disease.OTHER),
			true);

		// Show genotype field only when PCR test type is selected and result is positive
		Map<Object, List<Object>> genotypeVisibilityDependencies = new HashMap<>() {
			{
				put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR));
//				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.VIRUS_DETECTION_GENOTYPE, genotypeVisibilityDependencies, true);
	}

	private void handleIDSR() {
		Disease suspectedDisease = sample.getSuspectedDisease();
		if (suspectedDisease == null) return;

		for (Disease currentDisease : Disease.values()) {
			if (currentDisease == suspectedDisease) {
				diseaseField.removeAllItems();
				FieldHelper.updateEnumData(diseaseField, Collections.singleton(currentDisease));
				break;
			}
		}

		List<PathogenTestType> idsrTestTypes = Arrays.asList(
				PathogenTestType.P_FALICIPARUM,
				PathogenTestType.P_VIVAX,
				PathogenTestType.SHIGELLA,
				PathogenTestType.CULTURE,
				PathogenTestType.LATEX,
				PathogenTestType.GRAM_STAIN,
				PathogenTestType.OTHER
		);

		testTypeField.removeAllItems();
		testTypeField.addItems(idsrTestTypes);
		testTypeField.setValue(null);

		List<PathogenTestResultType> validValues = Arrays.asList(PathogenTestResultType.POSITIVE, PathogenTestResultType.NEGATIVE, PathogenTestResultType.PENDING);
		FieldHelper.updateEnumData(viralDetectionResultsField, validValues);

		FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(PathogenTestDto.VIRAL_DETECTION_TEST_TYPE),
				PathogenTestDto.VIRAL_DETECTION,
				Arrays.asList(YesNo.YES),
				true
		);

		testResultField.removeItem(PathogenTestResultType.INDETERMINATE);
	}

	private void handleAFP() {

		List<PathogenTestType> afpTestTypes = Arrays.asList(
				PathogenTestType.WILD_POLIOVIRUS,
				PathogenTestType.VDPV,
				PathogenTestType.SABIN_STRAIN,
				PathogenTestType.NON_POLIO_ENTEROVIRUS
		);

		testTypeField.removeAllItems();
		testTypeField.addItems(afpTestTypes);
		testTypeField.setValue(null);

		List<PathogenTestResultType> validValues = Arrays.asList(PathogenTestResultType.INDETERMINATE, PathogenTestResultType.PENDING, PathogenTestResultType.POSITIVE, PathogenTestResultType.NEGATIVE);
		FieldHelper.updateEnumData(testResultField, validValues);

		dateCaptured.setCaption("Date results received at National EPI");
	}


}

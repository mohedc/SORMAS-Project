package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.HSPACE_RIGHT_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IdsrType;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.*;
import de.symeda.sormas.api.sample.LpAspect;
import de.symeda.sormas.api.sample.LpPackaging;
import de.symeda.sormas.api.sample.Packaging;
import de.symeda.sormas.api.sample.LaboratoryType;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.InjectionSite;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;

public abstract class AbstractSampleForm extends AbstractEditForm<SampleDto> {

	private static final long serialVersionUID = -2323128076462668517L;

	protected static final String PATHOGEN_TESTING_INFO_LOC = "pathogenTestingInfoLoc";
	protected static final String ADDITIONAL_TESTING_INFO_LOC = "additionalTestingInfoLoc";
	protected static final String PATHOGEN_TESTING_READ_HEADLINE_LOC = "pathogenTestingReadHeadlineLoc";
	protected static final String ADDITIONAL_TESTING_READ_HEADLINE_LOC = "additionalTestingReadHeadlineLoc";
	protected static final String REQUESTED_PATHOGEN_TESTS_READ_LOC = "requestedPathogenTestsReadLoc";
	protected static final String REQUESTED_ADDITIONAL_TESTS_READ_LOC = "requestedAdditionalTestsReadLoc";
	protected static final String REPORT_INFO_LABEL_LOC = "reportInfoLabelLoc";
	protected static final String REFERRED_FROM_BUTTON_LOC = "referredFromButtonLoc";
	protected static final String STOOL_SPECIMEN_COLLECTION_HEADLINE_LOC = "stoolSpecimenCollectionLoc";
	protected static final String ELISA_IGM_HEADLINE_LOC = "elisaIgmHeadlineLoc";
	protected static final String PCR_HEADLINE_LOC = "pcrHeadlineLoc";
	protected static final String PRNT_HEADLINE_LOC = "prntHeadlineLoc";
	public ComboBox sampleMaterialComboBox;

	//@formatter:off
    protected static final String SAMPLE_COMMON_HTML_LAYOUT =
            fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3,SampleDto.REPORTING_USER, 1, "") +
                    fluidRowLocs(SampleDto.SAMPLE_PURPOSE) +
                    fluidRowLocs(SampleDto.OUTSIDE_COUNTRY_NAME) +
                    fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL) +
                    fluidRowLocs("", SampleDto.SAMPLE_MATERIAL_TEXT) +
                    fluidRowLocs(SampleDto.SAMPLING_REASON, SampleDto.SAMPLING_REASON_DETAILS) +
                    fluidRowLocs(SampleDto.SAMPLE_SOURCE, "") +
                    fluidRowLocs(SampleDto.FIELD_SAMPLE_ID, REFERRED_FROM_BUTTON_LOC) +
                    fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +

                    locCss(VSPACE_TOP_3, SampleDto.PATHOGEN_TESTING_REQUESTED) +
                    loc(PATHOGEN_TESTING_READ_HEADLINE_LOC) +
                    loc(PATHOGEN_TESTING_INFO_LOC) +
                    loc(SampleDto.REQUESTED_PATHOGEN_TESTS) +
                    loc(SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS) +
                    loc(REQUESTED_PATHOGEN_TESTS_READ_LOC) +

                    locCss(VSPACE_TOP_3, SampleDto.ADDITIONAL_TESTING_REQUESTED) +
                    loc(ADDITIONAL_TESTING_READ_HEADLINE_LOC) +
                    loc(ADDITIONAL_TESTING_INFO_LOC) +
                    loc(SampleDto.REQUESTED_ADDITIONAL_TESTS) +
                    loc(SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS) +
                    loc(REQUESTED_ADDITIONAL_TESTS_READ_LOC) +

                    locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
                    fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
                    fluidRowLocs(SampleDto.SENT_TO_IP_DAKAR) +
                    loc(ELISA_IGM_HEADLINE_LOC) +
                    fluidRowLocs(SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE) +
                    loc(PCR_HEADLINE_LOC) +
                    fluidRowLocs(SampleDto.PCR, SampleDto.PCR_DATE) +
                    loc(PRNT_HEADLINE_LOC) +
                    fluidRowLocs(SampleDto.PRNT, SampleDto.PRNT_DATE) +

                    locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
                    fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +

                    fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON) +
                    fluidRowLocs(SampleDto.COMMENT) +
                    fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT) +
					fluidRowLocs(CaseDataDto.DELETION_REASON) +
					fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON) +
					fluidRowLocs(SampleDto.IDSR_DIAGNOSIS) +
					fluidRowLocs(SampleDto.IDSR_DIAGNOSIS_DETAILS);

    protected static final String MEASLES_HTML_LAYOUT =
			fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3,SampleDto.REPORTING_USER, 1, "") +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.FIELD_SAMPLE_ID) +
                    fluidRowLocs(SampleDto.SAMPLE_DATE_TIME) +
                    fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
                    fluidRowLocs(SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION) +
                    fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT) +
					 locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
					fluidRowLocs(SampleDto.DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB, SampleDto.DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB) +
                    locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB) +
					fluidRowLocs(SampleDto.DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB, SampleDto.PATHOGEN_TEST_RESULT);

    protected static final String YELLOW_FEVER_HTML_LAYOUT =
			fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3,SampleDto.REPORTING_USER, 1, "") +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.FIELD_SAMPLE_ID) +
                    fluidRowLocs(SampleDto.SAMPLE_DATE_TIME) +
                    fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
                    fluidRowLocs(SampleDto.LAB_SAMPLE_ID, "") +
                    fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT) +
					 locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
//					fluidRowLocs(SampleDto.DATE_RESULTS_SENT_TO_REFERRING_CLINICIAN, "") +
					// fluidRowLocs(SampleDto.DISPATCHED_TO_REGIONAL_COLDROOM_DATE, SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE) +
					// fluidRowLocs(6, SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE) +
					locCss(VSPACE_TOP_3, "") +
					fluidRowLocs(6, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON) +
					fluidRowLocs(SampleDto.SENT_TO_IP_DAKAR) +
					loc(ELISA_IGM_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE) +
					loc(PCR_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.PCR, SampleDto.PCR_DATE) +
					loc(PRNT_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.PRNT, SampleDto.PRNT_INPUT_VALUE, SampleDto.PRNT_DATE) +
					fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);

	protected static final String MENINGITIS_HTML_LAYOUT =
			fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3, SampleDto.REPORTING_USER, 1, "") +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.FIELD_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT) +
					fluidRowLocs(SampleDto.LABORATORY_TYPE, "") +
					fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
					fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, "") +
//					fluidRowLocs(SampleDto.DATE_FORM_CSF_DISPATCHED_TO_HEALTH_DISTRICT, SampleDto.DATE_HEALTH_FACILITY_NOTIFY_REGION) +
//					locCss(VSPACE_TOP_3, SampleDto.CSF_SAMPLE_COLLECTED) +
//					locCss(VSPACE_TOP_3, SampleDto.LUMBAR_PUNCTURE_PERFORMED) +
//					fluidRowLocs(SampleDto.DATE_OF_LP, SampleDto.CSF_APPEARANCE_AT_COLLECTION) +
//					fluidRowLocs(SampleDto.LP_NOT_DONE_REASON, SampleDto.LP_NOT_DONE_REASON_OTHER) +
//					fluidRowLocs(SampleDto.LP_PACKAGING, SampleDto.LP_PACKAGING_OTHER) +
					fluidRowLocs(SampleDto.CSF_APPEARANCE_AT_COLLECTION, SampleDto.TIME_OF_INOCULATION_INTO_TRANSPORT_MEDIA) +
					locCss(VSPACE_TOP_3, "") +
					fluidRowLocs(SampleDto.SHIPPED, "") +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
					fluidRowLocs(SampleDto.SAMPLES_NOT_SENT_REASON, "") +
					fluidRowLocs(SampleDto.SAMPLE_CONTAINER_USED, SampleDto.SAMPLE_CONTAINER_USED_OTHER) +
//					locCss(VSPACE_TOP_3, SampleDto.MENINGITIS_RDT_PERFORMED) +
//					fluidRowLocs(SampleDto.MENINGITIS_RDT_RESULT, "") +
//					locCss(VSPACE_TOP_3, SampleDto.WAS_SPECIMEN_TAKEN) +
//					fluidRowLocs(SampleDto.DATE_SPECIMEN_SENT_TO_LABORATORY_TYPE) +
					locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SAMPLE_CONTAINER_RECEIVED, SampleDto.SAMPLE_CONTAINER_RECEIVED_OTHER) +
					fluidRowLocs(SampleDto.CSF_APPEARANCE_AT_RECEPTION, SampleDto.SPECIMEN_CONDITION) +
					fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);

	protected static final String AFP_HTML_LAYOUT =
			loc(STOOL_SPECIMEN_COLLECTION_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.UUID, SampleDto.FIELD_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.OUTSIDE_COUNTRY_NAME) +
					fluidRowLocs(6, SampleDto.SAMPLE_MATERIAL) +
					fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, "") +
					fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
					fluidRowLocs(SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.DATE_FIRST_SPECIMEN, SampleDto.DATE_SECOND_SPECIMEN) +

					locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
					fluidRowLocs(6,SampleDto.DATE_SPECIMEN_SENT_NATIONAL_LEVEL) +
					fluidRowLocs(6,SampleDto.DATE_SPECIMEN_SENT_INTERCOUNTY_NATLAB) +

					locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SENT_TO_IP_DAKAR) +
					loc(ELISA_IGM_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE) +
					loc(PCR_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.PCR, SampleDto.PCR_DATE) +
					loc(PRNT_HEADLINE_LOC) +
					fluidRowLocs(SampleDto.PRNT, SampleDto.PRNT_INPUT_VALUE) +
					fluidRowLocs(6, SampleDto.PRNT_DATE) +
					fluidRowLocs(6, SampleDto.DATE_SPECIMEN_RECEIVED_NATIONAL_LEVEL) +
					fluidRowLocs(SampleDto.DATE_SPECIMEN_RECEIVED_INTERCOUNTY_NATLAB, SampleDto.STATUS_SPECIMEN_RECEPTION_AT_LAB) +
					fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);

	protected static final String CONGENITAL_RUBELLA_HTML_LAYOUT =
			fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3, SampleDto.REPORTING_USER, 1, "") +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.FIELD_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SAMPLE_DATE_TIME) +
					fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT) +
					fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
					locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
					locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SPECIMEN_CONDITION) +
					fluidRowLocs(SampleDto.COMMENT) +
					fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);

	protected static final String IDSR_HTML_LAYOUT =
			fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3, SampleDto.REPORTING_USER, 1, "") +
					fluidRowLocs(SampleDto.SAMPLE_PURPOSE, SampleDto.FIELD_SAMPLE_ID) +
					fluidRowLocs(SampleDto.SAMPLE_DATE_TIME) +
					fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +
					fluidRowLocs(SampleDto.SUSPECTED_DISEASE) +
					fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT) +
					locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
					fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
					locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
					fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
					fluidRowLocs(6, SampleDto.SPECIMEN_CONDITION) +
					fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);

	//@formatter:on

	protected AbstractSampleForm(Class<SampleDto> type, String propertyI18nPrefix, Disease disease, UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			type,
			propertyI18nPrefix,
			true,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			fieldAccessCheckers,
				disease);
	}

	protected void addCommonFields() {

		final NullableOptionGroup samplePurpose = addField(SampleDto.SAMPLE_PURPOSE, NullableOptionGroup.class);
		TextField outsideCountryField = addField(SampleDto.OUTSIDE_COUNTRY_NAME, TextField.class);
		outsideCountryField.setVisible(false);
		addField(SampleDto.UUID).setReadOnly(true);
		 samplePurpose.addValueChangeListener(e -> updateRequestedTestFields());
		addField(SampleDto.LAB_SAMPLE_ID, TextField.class);
		final DateTimeField sampleDateField = addField(SampleDto.SAMPLE_DATE_TIME, DateTimeField.class);
		sampleDateField.setDateCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME));
		sampleDateField.setTimeCaption(I18nProperties.getCaption(Captions.Sample_sampleDateTimeTime));
		sampleDateField.setCaption(null);
		sampleDateField.setInvalidCommitted(false);
		sampleMaterialComboBox = addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		addField(SampleDto.SAMPLE_SOURCE, ComboBox.class);
		addField(SampleDto.FIELD_SAMPLE_ID, TextField.class);
		addDateField(SampleDto.SHIPMENT_DATE, DateField.class, 7);
		addDateField(SampleDto.DATE_RESULTS_SENT_TO_REFERRING_CLINICIAN, DateField.class, 7);
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);
		addField(SampleDto.SENT_TO_IP_DAKAR, NullableOptionGroup.class);
		
		// IP Dakar test result fields
		NullableOptionGroup elisaIgmField = addField(SampleDto.ELISA_IGM, NullableOptionGroup.class);
		FieldHelper.updateEnumData(
			elisaIgmField,
			Arrays.asList(
				SimpleTestResultType.POSITIVE,
				SimpleTestResultType.NEGATIVE,
				SimpleTestResultType.EQUIVOCAL,
				SimpleTestResultType.INDETERMINATE));
		addDateField(SampleDto.ELISA_IGM_DATE, DateField.class, 7);
		NullableOptionGroup pcrField = addField(SampleDto.PCR, NullableOptionGroup.class);
		FieldHelper.updateEnumData(
			pcrField,
			Arrays.asList(PathogenTestResultType.POSITIVE, PathogenTestResultType.NEGATIVE, PathogenTestResultType.NOT_TESTED));
		addDateField(SampleDto.PCR_DATE, DateField.class, 7);
		NullableOptionGroup prntField = addField(SampleDto.PRNT, NullableOptionGroup.class);
		FieldHelper.updateEnumData(
			prntField,
			Arrays.asList(PathogenTestResultType.POSITIVE, PathogenTestResultType.NEGATIVE, PathogenTestResultType.NOT_TESTED));
		TextField prntInputValueField = addField(SampleDto.PRNT_INPUT_VALUE, TextField.class);
		addDateField(SampleDto.PRNT_DATE, DateField.class, 7);
		
		// Add subtitle labels for IP Dakar test results
		Label elisaIgmHeading = new Label("Elisa IgM");
		CssStyles.style(elisaIgmHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(elisaIgmHeading, ELISA_IGM_HEADLINE_LOC);
		
		Label pcrHeading = new Label("PCR");
		CssStyles.style(pcrHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(pcrHeading, PCR_HEADLINE_LOC);
		
		Label prntHeading = new Label("PRNT");
		CssStyles.style(prntHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(prntHeading, PRNT_HEADLINE_LOC);
		
		addDateField(SampleDto.DISPATCHED_TO_REGIONAL_COLDROOM_DATE, DateField.class, 7);
		addDateField(SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE, DateField.class, 7);
		addDateField(SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE, DateField.class, 7);
		addField(SampleDto.RECEIVED_DATE, DateField.class);
		final ComboBox lab = addInfrastructureField(SampleDto.LAB);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		final TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		lab.addValueChangeListener(event -> updateLabDetailsVisibility(labDetails, event));

		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		TextArea comment = addField(SampleDto.COMMENT, TextArea.class);
		comment.setRows(4);
		comment.setDescription(
			I18nProperties.getPrefixDescription(SampleDto.I18N_PREFIX, SampleDto.COMMENT, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		addField(SampleDto.SHIPPED, CheckBox.class);
		addField(SampleDto.RECEIVED, CheckBox.class);

		ComboBox testResultField = addField(SampleDto.PATHOGEN_TEST_RESULT, ComboBox.class);
		testResultField.removeItem(PathogenTestResultType.NOT_DONE);

		addFields(SampleDto.SAMPLING_REASON, SampleDto.SAMPLING_REASON_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.SAMPLING_REASON_DETAILS,
			SampleDto.SAMPLING_REASON,
			Collections.singletonList(SamplingReason.OTHER_REASON),
			true);

		addField(SampleDto.DELETION_REASON);
		addField(SampleDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, SampleDto.DELETION_REASON, SampleDto.OTHER_DELETION_REASON);

		ComboBox idsrDiagnosisField = addField(SampleDto.IDSR_DIAGNOSIS, ComboBox.class);
		idsrDiagnosisField.setNullSelectionAllowed(true);
		idsrDiagnosisField.setVisible(false);
		TextField idsrDiagnosisDetailsField = addField(SampleDto.IDSR_DIAGNOSIS_DETAILS, TextField.class);
		idsrDiagnosisDetailsField.setVisible(false);
		idsrDiagnosisDetailsField.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.IDSR_DIAGNOSIS_DETAILS));

		// Measles-specific fields (hidden by default, shown in configureMeaslesFields)
		addDateField(SampleDto.DATE_FORM_SENT_TO_HIGHER_LEVEL, DateField.class, 7);
		addField(SampleDto.NAME_CONTACT_PERSON_COMPLETING_FORM, TextField.class);
		addDateField(SampleDto.DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB, DateField.class, 7);
		addDateField(SampleDto.DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB, DateField.class, 7);
		addDateField(SampleDto.DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB, DateField.class, 7);
		addDateField(SampleDto.DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB, DateField.class, 7);

		// Meningitis-specific fields (hidden by default, shown in configureMeningitisFields)
		addField(SampleDto.BARCODE, TextField.class);
		addDateField(SampleDto.DATE_FORM_CSF_DISPATCHED_TO_HEALTH_DISTRICT, DateField.class, 7);
		addDateField(SampleDto.DATE_HEALTH_FACILITY_NOTIFY_REGION, DateField.class, 7);
		addField(SampleDto.CSF_SAMPLE_COLLECTED, NullableOptionGroup.class);
		addField(SampleDto.LUMBAR_PUNCTURE_PERFORMED, NullableOptionGroup.class);
		addDateField(SampleDto.DATE_OF_LP, DateField.class, 7);
		OptionGroup csfAppearanceAtCollectionField = addField(SampleDto.CSF_APPEARANCE_AT_COLLECTION, OptionGroup.class);
		CssStyles.style(csfAppearanceAtCollectionField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		csfAppearanceAtCollectionField.setMultiSelect(true);
		csfAppearanceAtCollectionField.addItems(Arrays.asList(CsfAppearance.values()));
		addField(SampleDto.LP_NOT_DONE_REASON, ComboBox.class);
		addField(SampleDto.LP_NOT_DONE_REASON_OTHER, TextField.class);
		addField(SampleDto.LP_ASPECT, ComboBox.class);
		addField(SampleDto.LP_PACKAGING, ComboBox.class);
		addField(SampleDto.LP_PACKAGING_OTHER, TextField.class);
		DateField timeOfInoculationIntoTransportMedia = addDateField(SampleDto.TIME_OF_INOCULATION_INTO_TRANSPORT_MEDIA, DateField.class, 7);
		timeOfInoculationIntoTransportMedia.setResolution(Resolution.MINUTE);
		timeOfInoculationIntoTransportMedia.setDateFormat("HH:mm");
		addField(SampleDto.DATE_TIME_SAMPLE_SENT_TO_LAB, DateTimeField.class);
		addField(SampleDto.SAMPLES_NOT_SENT_REASON, TextField.class);
		addField(SampleDto.SAMPLE_CONTAINER_USED, ComboBox.class);
		addField(SampleDto.SAMPLE_CONTAINER_USED_OTHER, TextField.class);
		addField(SampleDto.MENINGITIS_RDT_PERFORMED, NullableOptionGroup.class);
		addField(SampleDto.MENINGITIS_RDT_RESULT, ComboBox.class);
		NullableOptionGroup wasSpecimenTakenField = addField(SampleDto.WAS_SPECIMEN_TAKEN, NullableOptionGroup.class);
		wasSpecimenTakenField.setValue(YesNo.YES);
		wasSpecimenTakenField.setReadOnly(true);
		addField(SampleDto.LABORATORY_TYPE, ComboBox.class);
		addField(SampleDto.LABORATORY_NAME, TextField.class);
		addDateField(SampleDto.DATE_SPECIMEN_SENT_TO_LABORATORY_TYPE, DateField.class, 7);
		addField(SampleDto.LAB_NUMBER, TextField.class);
		addField(SampleDto.SAMPLE_CONTAINER_RECEIVED, ComboBox.class);
		addField(SampleDto.SAMPLE_CONTAINER_RECEIVED_OTHER, TextField.class);
		addField(SampleDto.SAMPLE_CONDITION_AT_RECEPTION, ComboBox.class);
		addField(SampleDto.CSF_APPEARANCE_AT_RECEPTION, ComboBox.class);
		addField(SampleDto.PACKAGING, ComboBox.class);
		addField(SampleDto.PACKAGING_OTHER, TextField.class);
		addField(SampleDto.SUSPECTED_DISEASE, ComboBox.class);
		addField(SampleDto.DATE_FIRST_SPECIMEN, DateField.class);
		addField(SampleDto.DATE_SECOND_SPECIMEN, DateField.class);
		addField(SampleDto.DATE_SPECIMEN_SENT_NATIONAL_LEVEL, DateField.class);
		addField(SampleDto.DATE_SPECIMEN_RECEIVED_NATIONAL_LEVEL, DateField.class);
		addField(SampleDto.DATE_SPECIMEN_SENT_INTERCOUNTY_NATLAB, DateField.class);
		addField(SampleDto.DATE_SPECIMEN_RECEIVED_INTERCOUNTY_NATLAB, DateField.class);
		addField(SampleDto.STATUS_SPECIMEN_RECEPTION_AT_LAB, OptionGroup.class);

	}

	protected void defaultValueChangeListener() {

		final NullableOptionGroup samplePurposeField = (NullableOptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
		final Field<?> receivedField = getField(SampleDto.RECEIVED);
		final Field<?> shippedField = getField(SampleDto.SHIPPED);

		samplePurposeField.setRequired(true);

		// Rule: If a sample is not shipped, it must not be received
		// Disable received checkbox when shipped is not checked
		updateReceivedFieldBasedOnShipped(shippedField, receivedField);
		shippedField.addValueChangeListener(e -> updateReceivedFieldBasedOnShipped(shippedField, receivedField));

		Disease disease = null;
		final CaseReferenceDto associatedCase = getValue().getAssociatedCase();
		if (associatedCase != null && UiUtil.permitted(UserRight.CASE_VIEW)) {
			disease = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid()).getDisease();
		} else {
			final ContactReferenceDto associatedContact = getValue().getAssociatedContact();
			if (associatedContact != null && UiUtil.permitted(UserRight.CONTACT_VIEW)) {
				disease = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid()).getDisease();
			}
		}

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.DATE_SPECIMEN_RECEIVED_NATIONAL_LEVEL, SampleDto.DATE_SPECIMEN_RECEIVED_INTERCOUNTY_NATLAB,
			SampleDto.STATUS_SPECIMEN_RECEPTION_AT_LAB),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.SPECIMEN_CONDITION, SampleDto.DATE_SPECIMEN_RECEIVED_NATIONAL_LEVEL, SampleDto.DATE_SPECIMEN_RECEIVED_INTERCOUNTY_NATLAB,
			SampleDto.STATUS_SPECIMEN_RECEPTION_AT_LAB),
			true);

		if (disease != Disease.NEW_INFLUENZA) {
			getField(SampleDto.SAMPLE_SOURCE).setVisible(false);
		}

		// IDSR diagnosis visibility
		Field<?> idsrDiagnosisField = getField(SampleDto.IDSR_DIAGNOSIS);
		if (disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			idsrDiagnosisField.setVisible(true);
		} else {
			idsrDiagnosisField.setVisible(false);
		}

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.IDSR_DIAGNOSIS_DETAILS),
			SampleDto.IDSR_DIAGNOSIS,
			Arrays.asList(IdsrType.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			SampleDto.IDSR_DIAGNOSIS,
			Arrays.asList(SampleDto.IDSR_DIAGNOSIS_DETAILS),
			Arrays.asList(IdsrType.OTHER));

		UserReferenceDto reportingUser = getValue().getReportingUser();
		if (UiUtil.permitted(UserRight.SAMPLE_EDIT_NOT_OWNED) || (reportingUser != null && UiUtil.getUserUuid().equals(reportingUser.getUuid()))) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS, SampleDto.DATE_SPECIMEN_SENT_NATIONAL_LEVEL, SampleDto.DATE_SPECIMEN_SENT_INTERCOUNTY_NATLAB),
				SampleDto.SHIPPED,
				Arrays.asList(true),
				true);
			FieldHelper.setEnabledWhen(
				getFieldGroup(),
				shippedField,
				Arrays.asList(true),
				Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS),
				true);
//			FieldHelper.setRequiredWhen(
//				getFieldGroup(),
//				SampleDto.SAMPLE_PURPOSE,
//				Arrays.asList(SampleDto.LAB),
//				Arrays.asList(SamplePurpose.EXTERNAL, null));
			setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL);
		} else {
			getField(SampleDto.SAMPLE_DATE_TIME).setEnabled(false);
			getField(SampleDto.SAMPLE_MATERIAL).setEnabled(false);
			getField(SampleDto.SAMPLE_MATERIAL_TEXT).setEnabled(false);
			getField(SampleDto.LAB).setEnabled(false);
			shippedField.setEnabled(false);
			getField(SampleDto.SHIPMENT_DATE).setEnabled(false);
			getField(SampleDto.SHIPMENT_DETAILS).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_SENT_NATIONAL_LEVEL).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_SENT_INTERCOUNTY_NATLAB).setEnabled(false);
			getField(SampleDto.DISPATCHED_TO_REGIONAL_COLDROOM_DATE).setEnabled(false);
			getField(SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE).setEnabled(false);
			getField(SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB).setEnabled(false);
			getField(SampleDto.DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB).setEnabled(false);
			getField(SampleDto.SAMPLE_SOURCE).setEnabled(false);
		}

		StringBuilder reportInfoText = new StringBuilder().append(I18nProperties.getString(Strings.reportedOn))
			.append(" ")
			.append(DateFormatHelper.formatLocalDateTime(getValue().getReportDateTime()));
		if (reportingUser != null) {
			reportInfoText.append(" ").append(I18nProperties.getString(Strings.by)).append(" ");
		}
		Label reportInfoLabel = new Label(reportInfoText.toString());
		reportInfoLabel.setEnabled(false);
		getContent().addComponent(reportInfoLabel, REPORT_INFO_LABEL_LOC);
		UserField reportingUserField = addField(SampleDto.REPORTING_USER, UserField.class);
		reportingUserField.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		reportingUserField.setReadOnly(true);

		// Measles-specific configuration (called after all other visibility logic)
		if (disease == Disease.MEASLES) {
			configureMeaslesFields();
		}
		// IP Dakar test result fields visibility - show when SENT_TO_IP_DAKAR is Yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE,
				SampleDto.PCR, SampleDto.PCR_DATE,
				SampleDto.PRNT, SampleDto.PRNT_DATE),
			SampleDto.SENT_TO_IP_DAKAR,
			Arrays.asList(YesNo.YES),
			true);
		
		// PRNT Input Value visibility - show only when PRNT is POSITIVE
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.PRNT_INPUT_VALUE,
			SampleDto.PRNT,
			Arrays.asList(PathogenTestResultType.POSITIVE),
			true);
		
		// Show subtitle labels when SENT_TO_IP_DAKAR is Yes
		Field<?> sentToIpDakarField = getField(SampleDto.SENT_TO_IP_DAKAR);
		if (sentToIpDakarField != null) {
			sentToIpDakarField.addValueChangeListener(e -> {
				boolean isVisible = YesNo.YES.equals(e.getProperty().getValue());
				if (getContent().getComponent(ELISA_IGM_HEADLINE_LOC) != null) {
					getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
				}
				if (getContent().getComponent(PCR_HEADLINE_LOC) != null) {
					getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
				}
				if (getContent().getComponent(PRNT_HEADLINE_LOC) != null) {
					getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
				}
			});
			// Initialize visibility
			boolean isVisible = YesNo.YES.equals(sentToIpDakarField.getValue());
			if (getContent().getComponent(ELISA_IGM_HEADLINE_LOC) != null) {
				getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
			}
			if (getContent().getComponent(PCR_HEADLINE_LOC) != null) {
				getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
			}
			if (getContent().getComponent(PRNT_HEADLINE_LOC) != null) {
				getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
			}
		}

		// Yellow fever-specific configuration (called after all other visibility logic)
		if (disease == Disease.YELLOW_FEVER) {
			configureYellowFeverFields();
		}

		if(disease == Disease.AFP){
			handleAFP();
		}

		if(disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS){
			handleIDSR();
		}

		// Meningitis-specific configuration (called after all other visibility logic)
		if (disease == Disease.CSM) {
			configureMeningitisFields();
		}

		// Congenital rubella-specific configuration (called after all other visibility logic)
		if (disease == Disease.CONGENITAL_RUBELLA) {
			configureCongenitalRubellaFields();
		}

		// Has to run last so that it cannot be overruled by any disease-specific field configuration
		applyReceivalRightRestrictions();
	}

	/**
	 * Receiving a sample and recording what the laboratory finds on arrival is reserved for laboratory personnel; see
	 * {@link UserRight#SAMPLE_EDIT_RECEIVAL}. Mirrors the restriction {@code EnvironmentSampleEditForm} applies for environment samples:
	 * the fields are disabled rather than hidden, so that everybody can still read what the laboratory entered.
	 */
	protected void applyReceivalRightRestrictions() {

		if (UiUtil.permitted(UserRight.SAMPLE_EDIT_RECEIVAL)) {
			return;
		}

		for (String propertyId : SampleDto.RECEIVAL_PROPERTIES) {
			Field<?> field = getField(propertyId);
			if (field != null) {
				field.setEnabled(false);
			}
		}
	}

	protected void updateLabDetailsVisibility(TextField labDetails, Property.ValueChangeEvent event) {
		if (event.getProperty().getValue() != null
			&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
			labDetails.setVisible(true);
			labDetails.setRequired(isEditableAllowed(labDetails));
		} else {
			labDetails.setVisible(false);
			labDetails.setRequired(false);
			labDetails.clear();
		}
	}

	protected void addValidators() {
		// Validators
		final DateTimeField sampleDateField = getField(SampleDto.SAMPLE_DATE_TIME);
		final DateField shipmentDate = getField(SampleDto.SHIPMENT_DATE);
		final DateField receivedDate = getField(SampleDto.RECEIVED_DATE);

		sampleDateField.addValidator(
			new DateComparisonValidator(
				sampleDateField,
				shipmentDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), shipmentDate.getCaption())));
		sampleDateField.addValidator(
			new DateComparisonValidator(
				sampleDateField,
				receivedDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), receivedDate.getCaption())));
		shipmentDate.addValidator(
			new DateComparisonValidator(
				shipmentDate,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, shipmentDate.getCaption(), sampleDateField.getCaption())));
		shipmentDate.addValidator(
			new DateComparisonValidator(
				shipmentDate,
				receivedDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, shipmentDate.getCaption(), receivedDate.getCaption())));
		receivedDate.addValidator(
			new DateComparisonValidator(
				receivedDate,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), sampleDateField.getCaption())));
		receivedDate.addValidator(
			new DateComparisonValidator(
				receivedDate,
				shipmentDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), shipmentDate.getCaption())));

		List<AbstractField<Date>> validatedFields = Arrays.asList(sampleDateField, shipmentDate, receivedDate);
		validatedFields.forEach(field -> field.addValueChangeListener(r -> {
			validatedFields.forEach(otherField -> {
				otherField.setValidationVisible(!otherField.isValid());
			});
		}));
	}

	protected void setVisibilities() {

		FieldHelper
			.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), false);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.NO_TEST_POSSIBLE_REASON,
			SampleDto.SPECIMEN_CONDITION,
			Arrays.asList(SpecimenCondition.NOT_ADEQUATE, SpecimenCondition.BAD_SAMPLE_HAEMOLYSED),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			SampleDto.SAMPLE_MATERIAL,
			Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT),
			Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			SampleDto.SPECIMEN_CONDITION,
			Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON),
			Arrays.asList(SpecimenCondition.NOT_ADEQUATE, SpecimenCondition.BAD_SAMPLE_HAEMOLYSED));
//		FieldHelper.setVisibleWhen(
//			getFieldGroup(),
//			Arrays.asList(SampleDto.LAB, SampleDto.SHIPPED, SampleDto.RECEIVED),
//			SampleDto.SAMPLE_PURPOSE,
//			Arrays.asList(SamplePurpose.EXTERNAL, null),
//			true);
	}

	protected void initializeRequestedTestFields() {

		// Information texts for users that can edit the requested tests
		Label requestedPathogenInfoLabel = new Label(I18nProperties.getString(Strings.infoSamplePathogenTesting));
		getContent().addComponent(requestedPathogenInfoLabel, PATHOGEN_TESTING_INFO_LOC);
		Label requestedAdditionalInfoLabel = new Label(I18nProperties.getString(Strings.infoSampleAdditionalTesting));
		getContent().addComponent(requestedAdditionalInfoLabel, ADDITIONAL_TESTING_INFO_LOC);

		// Yes/No fields for requesting pathogen/additional tests
		CheckBox pathogenTestingRequestedField = addField(SampleDto.PATHOGEN_TESTING_REQUESTED, CheckBox.class);
		pathogenTestingRequestedField.setWidthUndefined();
		pathogenTestingRequestedField.addValueChangeListener(e -> updateRequestedTestFields());

		CheckBox additionalTestingRequestedField = addField(SampleDto.ADDITIONAL_TESTING_REQUESTED, CheckBox.class);
		additionalTestingRequestedField.setWidthUndefined();
		additionalTestingRequestedField.addValueChangeListener(e -> updateRequestedTestFields());

		// CheckBox groups to select the requested pathogen/additional tests
		OptionGroup requestedPathogenTestsField = addField(SampleDto.REQUESTED_PATHOGEN_TESTS, OptionGroup.class);
		CssStyles.style(requestedPathogenTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedPathogenTestsField.setMultiSelect(true);
		requestedPathogenTestsField.addItems(
			Arrays.stream(PathogenTestType.values())
				.filter(c -> fieldVisibilityCheckers.isVisible(PathogenTestType.class, c.name()))
				.collect(Collectors.toList()));
		requestedPathogenTestsField.removeItem(PathogenTestType.OTHER);
		requestedPathogenTestsField.setCaption(null);

		OptionGroup requestedAdditionalTestsField = addField(SampleDto.REQUESTED_ADDITIONAL_TESTS, OptionGroup.class);
		CssStyles.style(requestedAdditionalTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedAdditionalTestsField.setMultiSelect(true);
		requestedAdditionalTestsField.addItems((Object[]) AdditionalTestType.values());
		requestedAdditionalTestsField.setCaption(null);

		// Text fields to type in other tests
		TextField requestedOtherPathogenTests = addField(SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS, TextField.class);
		TextField requestedOtherAdditionalTests = addField(SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS, TextField.class);

		// header for read view
		Label pathogenTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedPathogenTests));
		CssStyles.style(pathogenTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(pathogenTestsHeading, PATHOGEN_TESTING_READ_HEADLINE_LOC);

		Label additionalTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedAdditionalTests));
		CssStyles.style(additionalTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(additionalTestsHeading, ADDITIONAL_TESTING_READ_HEADLINE_LOC);

		updateRequestedTestFields();
	}

	/**
	 * Updates the received field based on the shipped field value.
	 * If sample is not shipped, received must be disabled and cleared.
	 */
	private void updateReceivedFieldBasedOnShipped(Field<?> shippedField, Field<?> receivedField) {
		boolean canEditReceival = UiUtil.permitted(UserRight.SAMPLE_EDIT_RECEIVAL);
		boolean isShipped = Boolean.TRUE.equals(shippedField.getValue());
		receivedField.setEnabled(isShipped && canEditReceival);
		// Users without the receival right must not implicitly reset an already recorded receival, the server would reject the save
		if (!isShipped && canEditReceival) {
			receivedField.clear();
		}
	}

	private void updateRequestedTestFields() {

		boolean showRequestFields = getField(SampleDto.SAMPLE_PURPOSE).getValue() != SamplePurpose.INTERNAL;
		UserReferenceDto reportingUser = getValue() != null ? getValue().getReportingUser() : null;
		boolean canEditRequest = showRequestFields
			&& (UiUtil.permitted(UserRight.SAMPLE_EDIT_NOT_OWNED) || reportingUser != null && UiUtil.getUserUuid().equals(reportingUser.getUuid()));
		boolean canOnlyReadRequests = !canEditRequest && showRequestFields;
		boolean canUseAdditionalTests = UiUtil.permitted(FeatureType.ADDITIONAL_TESTS, UserRight.ADDITIONAL_TEST_VIEW);

		Field<?> pathogenTestingField = getField(SampleDto.PATHOGEN_TESTING_REQUESTED);
		pathogenTestingField.setVisible(canEditRequest);
		if (!showRequestFields) {
			pathogenTestingField.clear();
		}

		Field<?> additionalTestingField = getField(SampleDto.ADDITIONAL_TESTING_REQUESTED);
		additionalTestingField.setVisible(canEditRequest && canUseAdditionalTests);
		if (!showRequestFields) {
			additionalTestingField.clear();
		}

		boolean pathogenTestsRequested = Boolean.TRUE.equals(pathogenTestingField.getValue());
		setVisible(pathogenTestsRequested, SampleDto.REQUESTED_PATHOGEN_TESTS, SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS);
		getContent().getComponent(PATHOGEN_TESTING_INFO_LOC).setVisible(pathogenTestsRequested);

		boolean additionalTestsRequested = Boolean.TRUE.equals(additionalTestingField.getValue());
		setVisible(additionalTestsRequested, SampleDto.REQUESTED_ADDITIONAL_TESTS, SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS);
		getContent().getComponent(ADDITIONAL_TESTING_INFO_LOC).setVisible(additionalTestsRequested);

		getContent().getComponent(PATHOGEN_TESTING_READ_HEADLINE_LOC).setVisible(canOnlyReadRequests);
		getContent().getComponent(ADDITIONAL_TESTING_READ_HEADLINE_LOC).setVisible(canOnlyReadRequests && canUseAdditionalTests);

		if (getValue() != null && canOnlyReadRequests) {
			CssLayout requestedPathogenTestsLayout = new CssLayout();
			CssStyles.style(requestedPathogenTestsLayout, VSPACE_3);
			for (PathogenTestType testType : getValue().getRequestedPathogenTests()) {
				Label testLabel = new Label(testType.toString());
				testLabel.setWidthUndefined();
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, VSPACE_4, HSPACE_RIGHT_4);
				requestedPathogenTestsLayout.addComponent(testLabel);
			}
			getContent().addComponent(requestedPathogenTestsLayout, REQUESTED_PATHOGEN_TESTS_READ_LOC);
		} else {
			getContent().removeComponent(REQUESTED_PATHOGEN_TESTS_READ_LOC);
		}

		if (getValue() != null && canOnlyReadRequests && canUseAdditionalTests) {
			CssLayout requestedAdditionalTestsLayout = new CssLayout();
			CssStyles.style(requestedAdditionalTestsLayout, VSPACE_3);
			for (AdditionalTestType testType : getValue().getRequestedAdditionalTests()) {
				Label testLabel = new Label(testType.toString());
				testLabel.setWidthUndefined();
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, VSPACE_4, HSPACE_RIGHT_4);
				requestedAdditionalTestsLayout.addComponent(testLabel);
			}
			getContent().addComponent(requestedAdditionalTestsLayout, REQUESTED_ADDITIONAL_TESTS_READ_LOC);
		} else {
			getContent().removeComponent(REQUESTED_ADDITIONAL_TESTS_READ_LOC);
		}
	}

	/**
	 * Configures fields specifically for measles samples
	 */
	protected void configureMeaslesFields() {
		// Filter sample material options for measles: Blood, throat swab, urine, other
		// Note: "gingival fluid" is not available in SampleMaterial enum, using available options
		// Instead of removing all items, check each item and remove only those that don't match

//		getField(SampleDto.SHIPMENT_DATE).setVisible(true);
		Field<?> shippedField = getField(SampleDto.SHIPPED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			shippedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB,
				SampleDto.DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
			
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			shippedField,
			Arrays.asList(true),
			Arrays.asList(
				SampleDto.DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB,
				SampleDto.DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB),
			true);
		Field<?> receivedField = getField(SampleDto.RECEIVED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB,
				SampleDto.DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(
				SampleDto.DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB,
				SampleDto.DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB),
			true);

		// Show measles-specific fields
	}

	/**
	 * Configures fields specifically for yellow fever samples
	 */
	protected void configureYellowFeverFields() {
		// SampleDto.PATHOGEN_TEST_RESULT readOnly
		getField(SampleDto.PATHOGEN_TEST_RESULT).setReadOnly(true);
//		getField(SampleDto.SHIPMENT_DATE).setVisible(true);
		getField(SampleDto.DATE_RESULTS_SENT_TO_REFERRING_CLINICIAN).setVisible(true);
		Field<?> shippedField = getField(SampleDto.SHIPPED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			shippedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.DISPATCHED_TO_REGIONAL_COLDROOM_DATE,
				SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE,
				SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			shippedField,
			Arrays.asList(true),
			Arrays.asList(
				SampleDto.DISPATCHED_TO_REGIONAL_COLDROOM_DATE,
				SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE,
				SampleDto.DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE),
			true);

		// Show IP Dakar test result fields when SENT_TO_IP_DAKAR is Yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE,
				SampleDto.PCR, SampleDto.PCR_DATE,
				SampleDto.PRNT, SampleDto.PRNT_DATE),
			SampleDto.SENT_TO_IP_DAKAR,
			Arrays.asList(YesNo.YES),
			true);
		
		// Show subtitle labels when SENT_TO_IP_DAKAR is Yes
		Field<?> sentToIpDakarField = getField(SampleDto.SENT_TO_IP_DAKAR);
		if (sentToIpDakarField != null) {
			sentToIpDakarField.addValueChangeListener(e -> {
				boolean isVisible = YesNo.YES.equals(e.getProperty().getValue());
				getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
				getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
				getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
			});
			// Initialize visibility
			boolean isVisible = YesNo.YES.equals(sentToIpDakarField.getValue());
			getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
			getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
			getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
		}

		// Show yellow fever-specific fields
	}

	/**
	 * Configures fields specifically for congenital rubella samples
	 */
	protected void configureCongenitalRubellaFields() {
		// Filter sample material options for congenital rubella: Serum, Throat swab, Urine, CSF, Other
		FieldHelper.updateEnumData(sampleMaterialComboBox, Arrays.asList(SampleMaterial.SERUM, SampleMaterial.THROAT_SWAB, SampleMaterial.URINE, SampleMaterial.CSF, SampleMaterial.OTHER));

		// Set PATHOGEN_TEST_RESULT as read-only
		getField(SampleDto.PATHOGEN_TEST_RESULT).setReadOnly(true);

		// Configure visibility for shipped/received fields
//		getField(SampleDto.SHIPMENT_DATE).setVisible(true);clear
		Field<?> shippedField = getField(SampleDto.SHIPPED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			shippedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.SHIPMENT_DETAILS),
			true);

		Field<?> receivedField = getField(SampleDto.RECEIVED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.RECEIVED_DATE),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.RECEIVED_DATE),
			true);
	}

	/**
	 * Configures fields specifically for meningitis samples
	 */
	protected void configureMeningitisFields() {
		NullableOptionGroup wasSpecimenTakenField = (NullableOptionGroup) getField(SampleDto.WAS_SPECIMEN_TAKEN);
		wasSpecimenTakenField.setValue(YesNo.YES);
		wasSpecimenTakenField.setReadOnly(true);

		FieldHelper.updateEnumData(
			sampleMaterialComboBox,
			Arrays.asList(SampleMaterial.CSF, SampleMaterial.BLOOD, SampleMaterial.THROAT_SWAB, SampleMaterial.OTHER));
		// getField(SampleDto.SAMPLE_MATERIAL).setVisible(false);
		// getField(SampleDto.SAMPLE_MATERIAL_TEXT).setVisible(false);

		// Show meningitis-specific fields
		getField(SampleDto.BARCODE).setVisible(true);
		getField(SampleDto.DATE_FORM_CSF_DISPATCHED_TO_HEALTH_DISTRICT).setVisible(true);
		getField(SampleDto.DATE_HEALTH_FACILITY_NOTIFY_REGION).setVisible(true);
		getField(SampleDto.CSF_SAMPLE_COLLECTED).setVisible(true);
		getField(SampleDto.LUMBAR_PUNCTURE_PERFORMED).setVisible(true);
		getField(SampleDto.WAS_SPECIMEN_TAKEN).setVisible(true);
		getField(SampleDto.PACKAGING).setVisible(true);
		getField(SampleDto.MENINGITIS_RDT_PERFORMED).setVisible(true);

		// LP fields visibility - shown when lumbarPuncturePerformed = YES
		Field<?> lumbarPunctureField = getField(SampleDto.LUMBAR_PUNCTURE_PERFORMED);
		// FieldHelper.setVisibleWhen(
		// 	getFieldGroup(),
		// 	Arrays.asList(
		// 		SampleDto.DATE_OF_LP,
		// 		SampleDto.CSF_APPEARANCE_AT_COLLECTION,
		// 		SampleDto.LP_PACKAGING,
		// 		SampleDto.TIME_OF_INOCULATION_INTO_TRANSPORT_MEDIA),
		// 	SampleDto.LUMBAR_PUNCTURE_PERFORMED,
		// 	Arrays.asList(YesNo.YES),
		// 	true);
		// FieldHelper.setEnabledWhen(
		// 	getFieldGroup(),
		// 	lumbarPunctureField,
		// 	Arrays.asList(YesNo.YES),
		// 	Arrays.asList(
		// 		SampleDto.DATE_OF_LP,
		// 		SampleDto.CSF_APPEARANCE_AT_COLLECTION,
		// 		SampleDto.LP_PACKAGING,
		// 		SampleDto.TIME_OF_INOCULATION_INTO_TRANSPORT_MEDIA),
		// 	true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.LP_NOT_DONE_REASON),
			SampleDto.LUMBAR_PUNCTURE_PERFORMED,
			Arrays.asList(YesNo.NO),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.LP_NOT_DONE_REASON_OTHER,
			SampleDto.LP_NOT_DONE_REASON,
			Arrays.asList(LpNotDoneReason.OTHER),
			true);

		// LP Packaging Other visibility
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.LP_PACKAGING_OTHER,
			SampleDto.LP_PACKAGING,
			Arrays.asList(LpPackaging.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.SAMPLES_NOT_SENT_REASON),
			SampleDto.SHIPPED,
			Arrays.asList(false),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.SAMPLE_CONTAINER_USED),
			SampleDto.SHIPPED,
			Arrays.asList(true),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.SAMPLE_CONTAINER_USED_OTHER,
			SampleDto.SAMPLE_CONTAINER_USED,
			Arrays.asList(SampleContainerType.OTHER),
			true);

		// Laboratory fields visibility - shown when wasSpecimenTaken = YES
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.LABORATORY_TYPE, SampleDto.LAB, SampleDto.DATE_SPECIMEN_SENT_TO_LABORATORY_TYPE),
			SampleDto.WAS_SPECIMEN_TAKEN,
			Arrays.asList(YesNo.YES),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			wasSpecimenTakenField,
			Arrays.asList(YesNo.YES),
			Arrays.asList(SampleDto.LABORATORY_TYPE, SampleDto.LABORATORY_NAME, SampleDto.DATE_SPECIMEN_SENT_TO_LABORATORY_TYPE),
			true);

		// Received block only when shipped is checked
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(SampleDto.RECEIVED), SampleDto.SHIPPED, Arrays.asList(true), true);
		Field<?> receivedField = getField(SampleDto.RECEIVED);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				SampleDto.RECEIVED_DATE,
				SampleDto.LAB_NUMBER,
				SampleDto.LAB_SAMPLE_ID,
				SampleDto.SAMPLE_CONTAINER_RECEIVED,
				SampleDto.SAMPLE_CONDITION_AT_RECEPTION,
				SampleDto.CSF_APPEARANCE_AT_RECEPTION),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(
				SampleDto.RECEIVED_DATE,
				SampleDto.LAB_NUMBER,
				SampleDto.LAB_SAMPLE_ID,
				SampleDto.SAMPLE_CONTAINER_RECEIVED,
				SampleDto.SAMPLE_CONDITION_AT_RECEPTION,
				SampleDto.CSF_APPEARANCE_AT_RECEPTION),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.SAMPLE_CONTAINER_RECEIVED_OTHER,
			SampleDto.SAMPLE_CONTAINER_RECEIVED,
			Arrays.asList(SampleContainerType.OTHER),
			true);

		// Lab number equals Lab sample ID
		TextField labNumberField = (TextField) getField(SampleDto.LAB_NUMBER);
		TextField labSampleIdField = (TextField) getField(SampleDto.LAB_SAMPLE_ID);
		labNumberField.addValueChangeListener(e -> {
			if (!Objects.equals(labSampleIdField.getValue(), e.getProperty().getValue())) {
				labSampleIdField.setValue((String) e.getProperty().getValue());
			}
		});
		labSampleIdField.addValueChangeListener(e -> {
			if (!Objects.equals(labNumberField.getValue(), e.getProperty().getValue())) {
				labNumberField.setValue((String) e.getProperty().getValue());
			}
		});

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.MENINGITIS_RDT_RESULT),
			SampleDto.MENINGITIS_RDT_PERFORMED,
			Arrays.asList(YesNo.YES),
			true);

		// Update LAB field caption based on LABORATORY_TYPE selection
		ComboBox laboratoryTypeField = (ComboBox) getField(SampleDto.LABORATORY_TYPE);
		ComboBox labField = (ComboBox) getField(SampleDto.LAB);
		String defaultLabCaption = I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.LAB);
		
		// Initialize caption if LABORATORY_TYPE already has a value
		if (laboratoryTypeField != null && labField != null) {
			LaboratoryType currentLaboratoryType = (LaboratoryType) laboratoryTypeField.getValue();
			if (currentLaboratoryType != null) {
				labField.setCaption("Name of " + currentLaboratoryType.toString());
			}
			
			// Add value change listener
			laboratoryTypeField.addValueChangeListener(e -> {
				LaboratoryType selectedType = (LaboratoryType) e.getProperty().getValue();
				if (selectedType != null) {
					labField.setCaption("Name of " + selectedType.toString());
				} else {
					labField.setCaption(defaultLabCaption);
				}
			});
		}

		// Packaging Other visibility
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.PACKAGING_OTHER,
			SampleDto.PACKAGING,
			Arrays.asList(Packaging.OTHER),
			true);
	}

	private void handleAFP() {

		Label stoolSpecimenCollection = new Label(I18nProperties.getString(Strings.headingStoolSpecimenCollection));
		CssStyles.style(stoolSpecimenCollection, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(stoolSpecimenCollection, STOOL_SPECIMEN_COLLECTION_HEADLINE_LOC);

		setRequired(false, SampleDto.SAMPLE_PURPOSE, SampleDto.SAMPLE_MATERIAL);
		FieldHelper.updateEnumData(sampleMaterialComboBox, Arrays.asList(SampleMaterial.STOOL));
		sampleMaterialComboBox.setValue(SampleMaterial.STOOL);
		sampleMaterialComboBox.setEnabled(false);

		NullableOptionGroup pcrField = (NullableOptionGroup) getField(SampleDto.PCR);
		NullableOptionGroup prntField = (NullableOptionGroup) getField(SampleDto.PRNT);

		FieldHelper.updateEnumData(
				pcrField,
				Arrays.asList(
						PathogenTestResultType.POSITIVE,
						PathogenTestResultType.NEGATIVE,
						PathogenTestResultType.NOT_TESTED
				)
		);

		FieldHelper.updateEnumData(
				prntField,
				Arrays.asList(
						PathogenTestResultType.POSITIVE,
						PathogenTestResultType.NEGATIVE,
						PathogenTestResultType.NOT_TESTED
				)
		);

		NullableOptionGroup samplePurposeField = (NullableOptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
		boolean isNationalUser = canSeeOutsideCountryLabTesting();

		FieldHelper.updateEnumData(samplePurposeField,isNationalUser
						? Arrays.asList(SamplePurpose.EXTERNAL, SamplePurpose.INTERNAL, SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING)
						: Arrays.asList(SamplePurpose.EXTERNAL, SamplePurpose.INTERNAL));

		if (!isNationalUser && getSelectedSamplePurpose(samplePurposeField) == SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING) {
			samplePurposeField.clear();
		}

		ComboBox labField = (ComboBox) getField(SampleDto.LAB);
		TextField labDetailsField = (TextField) getField(SampleDto.LAB_DETAILS);
		TextField outsideCountryField = (TextField) getField(SampleDto.OUTSIDE_COUNTRY_NAME);
		samplePurposeField.addValueChangeListener(e -> handleSamplePurposeChange(
				getSelectedSamplePurpose(samplePurposeField),
				labField,
				labDetailsField,
				outsideCountryField,
				isNationalUser
		));

		handleSamplePurposeChange(
				getSelectedSamplePurpose(samplePurposeField),
				labField,
				labDetailsField,
				outsideCountryField,
				isNationalUser
		);

		FieldHelper.setVisibleWhen(
				getFieldGroup(),
				SampleDto.SENT_TO_IP_DAKAR,
				SampleDto.RECEIVED,
				Arrays.asList(true),
				true);

		FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(
						SampleDto.ELISA_IGM, SampleDto.ELISA_IGM_DATE,
						SampleDto.PCR, SampleDto.PCR_DATE,
						SampleDto.PRNT, SampleDto.PRNT_DATE),
				SampleDto.SENT_TO_IP_DAKAR,
				Arrays.asList(YesNo.YES),
				true);

		// Show subtitle labels when SENT_TO_IP_DAKAR is Yes
		Field<?> sentToIpDakarField = getField(SampleDto.SENT_TO_IP_DAKAR);
		if (sentToIpDakarField != null) {
			sentToIpDakarField.addValueChangeListener(e -> {
				boolean isVisible = YesNo.YES.equals(e.getProperty().getValue());
				getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
				getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
				getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
			});
			// Initialize visibility
			boolean isVisible = YesNo.YES.equals(sentToIpDakarField.getValue());
			getContent().getComponent(ELISA_IGM_HEADLINE_LOC).setVisible(isVisible);
			getContent().getComponent(PCR_HEADLINE_LOC).setVisible(isVisible);
			getContent().getComponent(PRNT_HEADLINE_LOC).setVisible(isVisible);
		}

	}

	private boolean canSeeOutsideCountryLabTesting() {
		UserProvider userProvider = UserProvider.getCurrent();
		return userProvider != null
				&& userProvider.getUserRoles().stream()
				.anyMatch(r -> r.getLinkedDefaultUserRole() == DefaultUserRole.NATIONAL_USER);
	}

	private SamplePurpose getSelectedSamplePurpose(NullableOptionGroup samplePurposeField) {
		Object value = samplePurposeField.getValue();
		if (value instanceof SamplePurpose) {
			return (SamplePurpose) value;
		}
		if (value instanceof Collection) {
			return ((Collection<?>) value).stream()
					.filter(SamplePurpose.class::isInstance)
					.map(SamplePurpose.class::cast)
					.findFirst()
					.orElse(null);
		}
		return null;
	}

	private void handleSamplePurposeChange(
			SamplePurpose value,
			ComboBox labField,
			TextField labDetailsField,
			TextField outsideCountryField,
			boolean isNationalUser) {
		if (isNationalUser && value == SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING) {
			outsideCountryField.setVisible(true);
			outsideCountryField.setRequired(true);
			labDetailsField.setVisible(true);
			labDetailsField.setRequired(true);
			Object otherFacility = labField.getItemIds().stream()
					.filter(f -> f instanceof FacilityReferenceDto)
					.filter(f -> FacilityDto.OTHER_FACILITY_UUID.equals(((FacilityReferenceDto) f).getUuid()))
					.findFirst()
					.orElse(null);
			labField.setValue(otherFacility);
			return;
		}
		outsideCountryField.setVisible(false);
		outsideCountryField.setRequired(false);
		labDetailsField.setVisible(true);
		labDetailsField.setRequired(false);
	}

	private void handleIDSR() {

		List<SampleMaterial> validValues = Arrays.asList(SampleMaterial.STOOL, SampleMaterial.BLOOD, SampleMaterial.CSF, SampleMaterial.OTHER);
		FieldHelper.updateEnumData(sampleMaterialComboBox, validValues);

	}

	@Override
	protected String createHtmlLayout() {
		Disease disease = getCaseDisease();
		switch (disease) {
			case MEASLES:
				return MEASLES_HTML_LAYOUT;
			case YELLOW_FEVER:
				return YELLOW_FEVER_HTML_LAYOUT;
			case AFP:
				return AFP_HTML_LAYOUT;
			case CSM:
				return MENINGITIS_HTML_LAYOUT;
			case CONGENITAL_RUBELLA:
				return CONGENITAL_RUBELLA_HTML_LAYOUT;
			case IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS:
				return IDSR_HTML_LAYOUT;
			default:
				return SAMPLE_COMMON_HTML_LAYOUT;
		}
	}
}

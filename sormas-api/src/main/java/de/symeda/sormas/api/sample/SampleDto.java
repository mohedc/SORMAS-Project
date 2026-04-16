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
package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IdsrType;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.*;

@DependingOnFeatureType(featureType = FeatureType.SAMPLES_LAB)
public class SampleDto extends SormasToSormasShareableDto implements IsSample {

	private static final long serialVersionUID = -6975445672442728938L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 6210;

	public static final String I18N_PREFIX = "Sample";

	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String FIELD_SAMPLE_ID = "fieldSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String SAMPLE_PURPOSE = "samplePurpose";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String SENT_TO_IP_DAKAR = "sentToIpDakar";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String REFERRED_TO = "referredTo";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String PATHOGEN_TESTING_REQUESTED = "pathogenTestingRequested";
	public static final String ADDITIONAL_TESTING_REQUESTED = "additionalTestingRequested";
	public static final String REQUESTED_PATHOGEN_TESTS = "requestedPathogenTests";
	public static final String REQUESTED_ADDITIONAL_TESTS = "requestedAdditionalTests";
	public static final String PATHOGEN_TEST_RESULT = "pathogenTestResult";
	public static final String REQUESTED_OTHER_PATHOGEN_TESTS = "requestedOtherPathogenTests";
	public static final String REQUESTED_OTHER_ADDITIONAL_TESTS = "requestedOtherAdditionalTests";
	public static final String SAMPLING_REASON = "samplingReason";
	public static final String SAMPLING_REASON_DETAILS = "samplingReasonDetails";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";
	public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";
	public static final String IDSR_DIAGNOSIS_DETAILS = "idsrDiagnosisDetails";
	public static final String DATE_FORM_SENT_TO_HIGHER_LEVEL = "dateFormSentToHigherLevel";
	public static final String NAME_CONTACT_PERSON_COMPLETING_FORM = "nameContactPersonCompletingForm";
	public static final String DISPATCHED_TO_REGIONAL_COLDROOM_DATE = "dispatchedToRegionalColdroomDate";
	public static final String DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE = "dispatchedToNationalLabByCourierDate";
	public static final String DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE = "dispatchedToNationalLabByRegionDistrictDate";
	public static final String DATE_FIRST_SPECIMEN = "dateFirstSpecimen";
	public static final String DATE_SECOND_SPECIMEN = "dateSecondSpecimen";
	public static final String DATE_SPECIMEN_SENT_NATIONAL_LEVEL = "dateSpecimenSentNationalLevel";
	public static final String DATE_SPECIMEN_RECEIVED_NATIONAL_LEVEL = "dateSpecimenReceivedNationalLevel";
	public static final String DATE_SPECIMEN_SENT_INTERCOUNTY_NATLAB = "dateSpecimenSentInter";
	public static final String DATE_SPECIMEN_RECEIVED_INTERCOUNTY_NATLAB = "dateSpecimenReceivedInter";
	public static final String STATUS_SPECIMEN_RECEPTION_AT_LAB = "statusSpecimenReceptionAtLab";
	public static final String DATE_SPECIMEN_SENT_FROM_FIELD_TO_NATIONAL_LAB = "dateSpecimenSentFromFieldToNationalLab";
	public static final String DATE_SPECIMEN_SENT_TO_REGIONAL_REFERENCE_LAB = "dateSpecimenSentToRegionalReferenceLab";
	public static final String DATE_SPECIMEN_RECEIVED_AT_NATIONAL_LAB = "dateSpecimenReceivedAtNationalLab";
	public static final String DATE_SPECIMEN_RECEIVED_AT_REGIONAL_REFERENCE_LAB = "dateSpecimenReceivedAtRegionalReferenceLab";
	public static final String DATE_FORM_CSF_DISPATCHED_TO_HEALTH_DISTRICT = "dateFormCsfDispatchedToHealthDistrict";
	public static final String DATE_HEALTH_FACILITY_NOTIFY_REGION = "dateHealthFacilityNotifyRegion";
	public static final String LUMBAR_PUNCTURE_PERFORMED = "lumbarPuncturePerformed";
	public static final String DATE_OF_LP = "dateOfLp";
	public static final String LP_ASPECT = "lpAspect";
	public static final String LP_PACKAGING = "lpPackaging";
	public static final String LP_PACKAGING_OTHER = "lpPackagingOther";
	public static final String WAS_SPECIMEN_TAKEN = "wasSpecimenTaken";
	public static final String LABORATORY_TYPE = "laboratoryType";
	public static final String LABORATORY_NAME = "laboratoryName";
	public static final String DATE_SPECIMEN_SENT_TO_LABORATORY_TYPE = "dateSpecimenSentToLaboratoryType";
	public static final String PACKAGING = "packaging";
	public static final String PACKAGING_OTHER = "packagingOther";
	public static final String ELISA_IGM = "elisaIgm";
	public static final String ELISA_IGM_DATE = "elisaIgmDate";
	public static final String PCR = "pcr";
	public static final String PCR_DATE = "pcrDate";
	public static final String PRNT = "prnt";
	public static final String PRNT_INPUT_VALUE = "prntInputValue";
	public static final String PRNT_DATE = "prntDate";
	public static final String DATE_RESULTS_SENT_TO_REFERRING_CLINICIAN = "dateResultsSentToReferringClinician";
	public static final String SUSPECTED_DISEASE = "suspectedDisease";

	private CaseReferenceDto associatedCase;
	private ContactReferenceDto associatedContact;
	private EventParticipantReferenceDto associatedEventParticipant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labSampleID;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String fieldSampleID;
	@NotNull(message = Validations.requiredField)
	private Date sampleDateTime;
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	@SensitiveData
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@SensitiveData
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;

	private Float reportLatLonAccuracy;

	@NotNull(message = Validations.requiredField)
	private SampleMaterial sampleMaterial;
//	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String sampleMaterialText;
	@NotNull(message = Validations.requiredField)
	private SamplePurpose samplePurpose;

	private FacilityReferenceDto lab;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labDetails;
	private Date shipmentDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String shipmentDetails;
	private YesNo sentToIpDakar;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String noTestPossibleReason;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String comment;
	private SampleSource sampleSource;
	private SampleReferenceDto referredTo;
	private boolean shipped;
	private boolean received;
	private PathogenTestResultType pathogenTestResult;

	private Boolean pathogenTestingRequested;
	private Boolean additionalTestingRequested;
	private Set<PathogenTestType> requestedPathogenTests;
	private Set<AdditionalTestType> requestedAdditionalTests;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String requestedOtherPathogenTests;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String requestedOtherAdditionalTests;

	private SamplingReason samplingReason;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String samplingReasonDetails;

	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;
	private IdsrType idsrDiagnosis;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String idsrDiagnosisDetails;
	private Date dateFormSentToHigherLevel;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String nameContactPersonCompletingForm;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date dispatchedToRegionalColdroomDate;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date dispatchedToNationalLabByCourierDate;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date dispatchedToNationalLabByRegionDistrictDate;
	private Date dateFirstSpecimen;
	private Date dateSecondSpecimen;
	private Date dateSpecimenSentNationalLevel;
	private Date dateSpecimenReceivedNationalLevel;
	private Date dateSpecimenSentInter;
	private Date dateSpecimenReceivedInter;
	private SpecimenCondition statusSpecimenReceptionAtLab;
	@Diseases(value = {
		Disease.MEASLES })
	private Date dateSpecimenSentFromFieldToNationalLab;
	@Diseases(value = {
		Disease.MEASLES })
	private Date dateSpecimenSentToRegionalReferenceLab;
	@Diseases(value = {
		Disease.MEASLES })
	private Date dateSpecimenReceivedAtNationalLab;
	@Diseases(value = {
		Disease.MEASLES })
	private Date dateSpecimenReceivedAtRegionalReferenceLab;
	@Diseases(value = {
		Disease.CSM })
	private Date dateFormCsfDispatchedToHealthDistrict;
	@Diseases(value = {
		Disease.CSM })
	private Date dateHealthFacilityNotifyRegion;
	@Diseases(value = {
		Disease.CSM })
	private YesNo lumbarPuncturePerformed;
	@Diseases(value = {
		Disease.CSM })
	private Date dateOfLp;
	@Diseases(value = {
		Disease.CSM })
	private LpAspect lpAspect;
	@Diseases(value = {
		Disease.CSM })
	private LpPackaging lpPackaging;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Diseases(value = {
		Disease.CSM })
	private String lpPackagingOther;
	@Diseases(value = {
		Disease.CSM })
	private YesNo wasSpecimenTaken;
	@Diseases(value = {
		Disease.CSM })
	private LaboratoryType laboratoryType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Diseases(value = {
		Disease.CSM })
	private String laboratoryName;
	@Diseases(value = {
		Disease.CSM })
	private Date dateSpecimenSentToLaboratoryType;
	@Diseases(value = {
		Disease.CSM })
	private Packaging packaging;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Diseases(value = {
		Disease.CSM })
	private String packagingOther;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private SimpleTestResultType elisaIgm;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date elisaIgmDate;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private PathogenTestResultType pcr;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date pcrDate;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private PathogenTestResultType prnt;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String prntInputValue;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date prntDate;
	@Diseases(value = {
		Disease.YELLOW_FEVER })
	private Date dateResultsSentToReferringClinician;
	@Diseases(value = {
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
	private Disease suspectedDisease;

	@ImportIgnore
	public CaseReferenceDto getAssociatedCase() {
		return associatedCase;
	}

	public void setAssociatedCase(CaseReferenceDto associatedCase) {
		this.associatedCase = associatedCase;
	}

	@ImportIgnore
	public ContactReferenceDto getAssociatedContact() {
		return associatedContact;
	}

	public void setAssociatedContact(ContactReferenceDto associatedContact) {
		this.associatedContact = associatedContact;
	}

	@ImportIgnore
	public EventParticipantReferenceDto getAssociatedEventParticipant() {
		return associatedEventParticipant;
	}

	public void setAssociatedEventParticipant(EventParticipantReferenceDto associatedEventParticipant) {
		this.associatedEventParticipant = associatedEventParticipant;
	}

	public String getLabSampleID() {
		return labSampleID;
	}

	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}

	public String getFieldSampleID() {
		return fieldSampleID;
	}

	public void setFieldSampleID(String fieldSampleID) {
		this.fieldSampleID = fieldSampleID;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	public FacilityReferenceDto getLab() {
		return lab;
	}

	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	public Date getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	public String getShipmentDetails() {
		return shipmentDetails;
	}

	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}

	public YesNo getSentToIpDakar() {
		return sentToIpDakar;
	}

	public void setSentToIpDakar(YesNo sentToIpDakar) {
		this.sentToIpDakar = sentToIpDakar;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}

	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public SampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	@ImportIgnore
	public SampleReferenceDto getReferredTo() {
		return referredTo;
	}

	public void setReferredTo(SampleReferenceDto referredTo) {
		this.referredTo = referredTo;
	}

	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	@ImportIgnore
	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	@ImportIgnore
	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	@ImportIgnore
	public Set<PathogenTestType> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	@ImportIgnore
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;
	}

	@ImportIgnore
	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	@ImportIgnore
	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
	}

	public SamplingReason getSamplingReason() {
		return samplingReason;
	}

	public void setSamplingReason(SamplingReason samplingReason) {
		this.samplingReason = samplingReason;
	}

	public String getSamplingReasonDetails() {
		return samplingReasonDetails;
	}

	public void setSamplingReasonDetails(String samplingReasonDetails) {
		this.samplingReasonDetails = samplingReasonDetails;
	}

	public static SampleDto build(UserReferenceDto userRef, CaseReferenceDto caseRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedCase(caseRef);
		sampleDto.setWasSpecimenTaken(YesNo.YES);
		return sampleDto;
	}

	public static SampleDto build(UserReferenceDto userRef, EventParticipantReferenceDto eventParticipantRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedEventParticipant(eventParticipantRef);
		sampleDto.setWasSpecimenTaken(YesNo.YES);

		return sampleDto;
	}

	public static SampleDto build(UserReferenceDto userRef, ContactReferenceDto contactRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedContact(contactRef);
		sampleDto.setWasSpecimenTaken(YesNo.YES);

		return sampleDto;
	}

	private static SampleDto getSampleDto(UserReferenceDto userRef) {

		SampleDto sample = new SampleDto();
		sample.setUuid(DataHelper.createUuid());

		sample.setReportingUser(userRef);
		sample.setReportDateTime(new Date());
		sample.setPathogenTestResult(PathogenTestResultType.PENDING);

		return sample;
	}

	public static SampleDto buildReferralDto(UserReferenceDto userRef, SampleDto referredSample) {

		final SampleDto sample;
		final CaseReferenceDto associatedCase = referredSample.getAssociatedCase();
		final ContactReferenceDto associatedContact = referredSample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = referredSample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			sample = build(userRef, associatedCase);
		} else if (associatedContact != null) {
			sample = build(userRef, associatedContact);
		} else {
			sample = build(userRef, associatedEventParticipant);
		}
		migrateAttributesOfPhysicalSample(referredSample, sample);

		return sample;
	}

	/**
	 * The physical sample is neither the source, nor the target. This method is about migrating the attributes that belong to the real
	 * (physical) sample out there in the labs.
	 * Source and target should both refer to the physical sample, but have different values for some attributes. For example, the
	 * specimenCondition may be different in source and target.
	 * In one lab (source), the specimenCondition may be ADEQUATE. But then during transport to another lab (target) the specimenCondition
	 * can change to NOT_ADEQUATE.
	 *
	 * In contrast, the attributes of the physical sample don't change (e.g. samplingReason) and thus should be migrated when a sample
	 * referral is created in SORMAS.
	 */
	private static void migrateAttributesOfPhysicalSample(SampleDto source, SampleDto target) {
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSampleSource(source.getSampleSource());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSamplingReason(source.getSamplingReason());
		target.setSamplingReasonDetails(source.getSamplingReasonDetails());
		target.setSamplePurpose(source.getSamplePurpose());
	}

	@ImportIgnore
	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	@ImportIgnore
	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	@ImportIgnore
	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(
			getUuid(),
			sampleMaterial,
			associatedCase != null ? associatedCase.getUuid() : null,
			associatedContact != null ? associatedContact.getUuid() : null,
			associatedEventParticipant != null ? associatedEventParticipant.getUuid() : null);
	}

	@Override
	public SampleDto clone() throws CloneNotSupportedException {
		return (SampleDto) super.clone();
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	public IdsrType getIdsrDiagnosis() {
		return idsrDiagnosis;
	}

	public void setIdsrDiagnosis(IdsrType idsrDiagnosis) {
		this.idsrDiagnosis = idsrDiagnosis;
	}

	public String getIdsrDiagnosisDetails() {
		return idsrDiagnosisDetails;
	}

	public void setIdsrDiagnosisDetails(String idsrDiagnosisDetails) {
		this.idsrDiagnosisDetails = idsrDiagnosisDetails;
	}

	public Date getDateFormSentToHigherLevel() {
		return dateFormSentToHigherLevel;
	}

	public void setDateFormSentToHigherLevel(Date dateFormSentToHigherLevel) {
		this.dateFormSentToHigherLevel = dateFormSentToHigherLevel;
	}

	public String getNameContactPersonCompletingForm() {
		return nameContactPersonCompletingForm;
	}

	public void setNameContactPersonCompletingForm(String nameContactPersonCompletingForm) {
		this.nameContactPersonCompletingForm = nameContactPersonCompletingForm;
	}

	public Date getDispatchedToRegionalColdroomDate() {
		return dispatchedToRegionalColdroomDate;
	}

	public void setDispatchedToRegionalColdroomDate(Date dispatchedToRegionalColdroomDate) {
		this.dispatchedToRegionalColdroomDate = dispatchedToRegionalColdroomDate;
	}

	public Date getDispatchedToNationalLabByCourierDate() {
		return dispatchedToNationalLabByCourierDate;
	}

	public void setDispatchedToNationalLabByCourierDate(Date dispatchedToNationalLabByCourierDate) {
		this.dispatchedToNationalLabByCourierDate = dispatchedToNationalLabByCourierDate;
	}

	public Date getDispatchedToNationalLabByRegionDistrictDate() {
		return dispatchedToNationalLabByRegionDistrictDate;
	}

	public void setDispatchedToNationalLabByRegionDistrictDate(Date dispatchedToNationalLabByRegionDistrictDate) {
		this.dispatchedToNationalLabByRegionDistrictDate = dispatchedToNationalLabByRegionDistrictDate;
	}

	public Date getDateFirstSpecimen() {
		return dateFirstSpecimen;
	}

	public void setDateFirstSpecimen(Date dateFirstSpecimen) {
		this.dateFirstSpecimen = dateFirstSpecimen;
	}

	public Date getDateSecondSpecimen() {
		return dateSecondSpecimen;
	}

	public void setDateSecondSpecimen(Date dateSecondSpecimen) {
		this.dateSecondSpecimen = dateSecondSpecimen;
	}

	public Date getDateSpecimenSentNationalLevel() {
		return dateSpecimenSentNationalLevel;
	}

	public void setDateSpecimenSentNationalLevel(Date dateSpecimenSentNationalLevel) {
		this.dateSpecimenSentNationalLevel = dateSpecimenSentNationalLevel;
	}

	public Date getDateSpecimenReceivedNationalLevel() {
		return dateSpecimenReceivedNationalLevel;
	}

	public void setDateSpecimenReceivedNationalLevel(Date dateSpecimenReceivedNationalLevel) {
		this.dateSpecimenReceivedNationalLevel = dateSpecimenReceivedNationalLevel;
	}

	public Date getDateSpecimenSentInter() {
		return dateSpecimenSentInter;
	}

	public void setDateSpecimenSentInter(Date dateSpecimenSentInter) {
		this.dateSpecimenSentInter = dateSpecimenSentInter;
	}

	public Date getDateSpecimenReceivedInter() {
		return dateSpecimenReceivedInter;
	}

	public void setDateSpecimenReceivedInter(Date dateSpecimenReceivedInter) {
		this.dateSpecimenReceivedInter = dateSpecimenReceivedInter;
	}

	public SpecimenCondition getStatusSpecimenReceptionAtLab() {
		return statusSpecimenReceptionAtLab;
	}

	public void setStatusSpecimenReceptionAtLab(SpecimenCondition statusSpecimenReceptionAtLab) {
		this.statusSpecimenReceptionAtLab = statusSpecimenReceptionAtLab;
	}

	public Date getDateSpecimenSentFromFieldToNationalLab() {
		return dateSpecimenSentFromFieldToNationalLab;
	}

	public void setDateSpecimenSentFromFieldToNationalLab(Date dateSpecimenSentFromFieldToNationalLab) {
		this.dateSpecimenSentFromFieldToNationalLab = dateSpecimenSentFromFieldToNationalLab;
	}

	public Date getDateSpecimenSentToRegionalReferenceLab() {
		return dateSpecimenSentToRegionalReferenceLab;
	}

	public void setDateSpecimenSentToRegionalReferenceLab(Date dateSpecimenSentToRegionalReferenceLab) {
		this.dateSpecimenSentToRegionalReferenceLab = dateSpecimenSentToRegionalReferenceLab;
	}

	public Date getDateSpecimenReceivedAtNationalLab() {
		return dateSpecimenReceivedAtNationalLab;
	}

	public void setDateSpecimenReceivedAtNationalLab(Date dateSpecimenReceivedAtNationalLab) {
		this.dateSpecimenReceivedAtNationalLab = dateSpecimenReceivedAtNationalLab;
	}

	public Date getDateSpecimenReceivedAtRegionalReferenceLab() {
		return dateSpecimenReceivedAtRegionalReferenceLab;
	}

	public void setDateSpecimenReceivedAtRegionalReferenceLab(Date dateSpecimenReceivedAtRegionalReferenceLab) {
		this.dateSpecimenReceivedAtRegionalReferenceLab = dateSpecimenReceivedAtRegionalReferenceLab;
	}

	public Date getDateFormCsfDispatchedToHealthDistrict() {
		return dateFormCsfDispatchedToHealthDistrict;
	}

	public void setDateFormCsfDispatchedToHealthDistrict(Date dateFormCsfDispatchedToHealthDistrict) {
		this.dateFormCsfDispatchedToHealthDistrict = dateFormCsfDispatchedToHealthDistrict;
	}

	public Date getDateHealthFacilityNotifyRegion() {
		return dateHealthFacilityNotifyRegion;
	}

	public void setDateHealthFacilityNotifyRegion(Date dateHealthFacilityNotifyRegion) {
		this.dateHealthFacilityNotifyRegion = dateHealthFacilityNotifyRegion;
	}

	public YesNo getLumbarPuncturePerformed() {
		return lumbarPuncturePerformed;
	}

	public void setLumbarPuncturePerformed(YesNo lumbarPuncturePerformed) {
		this.lumbarPuncturePerformed = lumbarPuncturePerformed;
	}

	public Date getDateOfLp() {
		return dateOfLp;
	}

	public void setDateOfLp(Date dateOfLp) {
		this.dateOfLp = dateOfLp;
	}

	public LpAspect getLpAspect() {
		return lpAspect;
	}

	public void setLpAspect(LpAspect lpAspect) {
		this.lpAspect = lpAspect;
	}

	public LpPackaging getLpPackaging() {
		return lpPackaging;
	}

	public void setLpPackaging(LpPackaging lpPackaging) {
		this.lpPackaging = lpPackaging;
	}

	public String getLpPackagingOther() {
		return lpPackagingOther;
	}

	public void setLpPackagingOther(String lpPackagingOther) {
		this.lpPackagingOther = lpPackagingOther;
	}

	public YesNo getWasSpecimenTaken() {
		return wasSpecimenTaken;
	}

	public void setWasSpecimenTaken(YesNo wasSpecimenTaken) {
		this.wasSpecimenTaken = wasSpecimenTaken;
	}

	public LaboratoryType getLaboratoryType() {
		return laboratoryType;
	}

	public void setLaboratoryType(LaboratoryType laboratoryType) {
		this.laboratoryType = laboratoryType;
	}

	public String getLaboratoryName() {
		return laboratoryName;
	}

	public void setLaboratoryName(String laboratoryName) {
		this.laboratoryName = laboratoryName;
	}

	public Date getDateSpecimenSentToLaboratoryType() {
		return dateSpecimenSentToLaboratoryType;
	}

	public void setDateSpecimenSentToLaboratoryType(Date dateSpecimenSentToLaboratoryType) {
		this.dateSpecimenSentToLaboratoryType = dateSpecimenSentToLaboratoryType;
	}

	public Packaging getPackaging() {
		return packaging;
	}

	public void setPackaging(Packaging packaging) {
		this.packaging = packaging;
	}

	public String getPackagingOther() {
		return packagingOther;
	}

	public void setPackagingOther(String packagingOther) {
		this.packagingOther = packagingOther;
	}

	public SimpleTestResultType getElisaIgm() {
		return elisaIgm;
	}

	public void setElisaIgm(SimpleTestResultType elisaIgm) {
		this.elisaIgm = elisaIgm;
	}

	public Date getElisaIgmDate() {
		return elisaIgmDate;
	}

	public void setElisaIgmDate(Date elisaIgmDate) {
		this.elisaIgmDate = elisaIgmDate;
	}

	public PathogenTestResultType getPcr() {
		return pcr;
	}

	public void setPcr(PathogenTestResultType pcr) {
		this.pcr = pcr;
	}

	public Date getPcrDate() {
		return pcrDate;
	}

	public void setPcrDate(Date pcrDate) {
		this.pcrDate = pcrDate;
	}

	public PathogenTestResultType getPrnt() {
		return prnt;
	}

	public void setPrnt(PathogenTestResultType prnt) {
		this.prnt = prnt;
	}

	public String getPrntInputValue() {
		return prntInputValue;
	}

	public void setPrntInputValue(String prntInputValue) {
		this.prntInputValue = prntInputValue;
	}

	public Date getPrntDate() {
		return prntDate;
	}

	public void setPrntDate(Date prntDate) {
		this.prntDate = prntDate;
	}

	public Date getDateResultsSentToReferringClinician() {
		return dateResultsSentToReferringClinician;
	}

	public void setDateResultsSentToReferringClinician(Date dateResultsSentToReferringClinician) {
		this.dateResultsSentToReferringClinician = dateResultsSentToReferringClinician;
	}

	public Disease getSuspectedDisease() {
		return suspectedDisease;
	}

	public void setSuspectedDisease(Disease suspectedDisease) {
		this.suspectedDisease = suspectedDisease;
	}
}

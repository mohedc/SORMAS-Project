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
package de.symeda.sormas.backend.sample;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.*;
import de.symeda.sormas.api.utils.*;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.IdsrType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;

@Entity(name = "samples")
public class Sample extends DeletableAdo implements IsSample, SormasToSormasShareable {

	private static final long serialVersionUID = -7196712070188634978L;

	public static final String TABLE_NAME = "samples";

	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String ASSOCIATED_CONTACT = "associatedContact";
	public static final String ASSOCIATED_EVENT_PARTICIPANT = "associatedEventParticipant";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String FIELD_SAMPLE_ID = "fieldSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_PURPOSE = "samplePurpose";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String SENT_TO_IP_DAKAR = "sentToIpDakar";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String REFERRED_TO = "referredTo";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String PATHOGEN_TESTING_REQUESTED = "pathogenTestingRequested";
	public static final String ADDITIONAL_TESTING_REQUESTED = "additionalTestingRequested";
	public static final String ADDITIONAL_TESTS = "additionalTests";
	public static final String PATHOGEN_TEST_RESULT = "pathogenTestResult";
	public static final String PATHOGEN_TEST_RESULT_CHANGE_DATE = "pathogenTestResultChangeDate";
	public static final String REQUESTED_PATHOGEN_TESTS_STRING = "requestedPathogenTestsString";
	public static final String REQUESTED_ADDITIONAL_TESTS_STRING = "requestedAdditionalTestsString";
	public static final String REQUESTED_OTHER_PATHOGEN_TESTS = "requestedOtherPathogenTests";
	public static final String REQUESTED_OTHER_ADDITIONAL_TESTS = "requestedOtherAdditionalTests";
	public static final String PATHOGENTESTS = "pathogenTests";
	public static final String SAMPLING_REASON = "samplingReason";
	public static final String SAMPLING_REASON_DETAILS = "samplingReasonDetails";
	public static final String SORMAS_TO_SORMAS_ORIGIN_INFO = "sormasToSormasOriginInfo";
	public static final String SORMAS_TO_SORMAS_SHARES = "sormasToSormasShares";
	public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";
	public static final String IDSR_DIAGNOSIS_DETAILS = "idsrDiagnosisDetails";
	public static final String DATE_FORM_SENT_TO_HIGHER_LEVEL = "dateFormSentToHigherLevel";
	public static final String NAME_CONTACT_PERSON_COMPLETING_FORM = "nameContactPersonCompletingForm";
	public static final String DISPATCHED_TO_REGIONAL_COLDROOM_DATE = "dispatchedToRegionalColdroomDate";
	public static final String DISPATCHED_TO_NATIONAL_LAB_BY_COURIER_DATE = "dispatchedToNationalLabByCourierDate";
	public static final String DISPATCHED_TO_NATIONAL_LAB_BY_REGION_DISTRICT_DATE = "dispatchedToNationalLabByRegionDistrictDate";
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
	public static final String BARCODE = "barcode";
	public static final String CSF_SAMPLE_COLLECTED = "csfSampleCollected";
	public static final String LP_NOT_DONE_REASON = "lpNotDoneReason";
	public static final String LP_NOT_DONE_REASON_OTHER = "lpNotDoneReasonOther";
	public static final String TIME_OF_INOCULATION_INTO_TRANSPORT_MEDIA = "timeOfInoculationIntoTransportMedia";
	public static final String SAMPLES_SENT_TO_LABORATORY = "samplesSentToLaboratory";
	public static final String SAMPLES_NOT_SENT_REASON = "samplesNotSentReason";
	public static final String DISPATCHED = "dispatched";
	public static final String DATE_TIME_SAMPLE_SENT_TO_LAB = "dateTimeSampleSentToLab";
	public static final String SAMPLE_CONTAINER_USED = "sampleContainerUsed";
	public static final String SAMPLE_CONTAINER_USED_OTHER = "sampleContainerUsedOther";
	public static final String MENINGITIS_RDT_PERFORMED = "meningitisRdtPerformed";
	public static final String MENINGITIS_RDT_RESULT = "meningitisRdtResult";
	public static final String LAB_NUMBER = "labNumber";
	public static final String SAMPLE_CONTAINER_RECEIVED = "sampleContainerReceived";
	public static final String SAMPLE_CONTAINER_RECEIVED_OTHER = "sampleContainerReceivedOther";
	public static final String SAMPLE_CONDITION_AT_RECEPTION = "sampleConditionAtReception";
	public static final String CSF_APPEARANCE_AT_COLLECTION = "csfAppearanceAtCollection";
	public static final String CSF_APPEARANCE_AT_RECEPTION = "csfAppearanceAtReception";
	public static final String DATE_RESULTS_SENT_TO_REFERRING_CLINICIAN = "dateResultsSentToReferringClinician";

	private Case associatedCase;
	private Contact associatedContact;
	private EventParticipant associatedEventParticipant;
	private String labSampleID;
	private String fieldSampleID;
	private Date sampleDateTime;

	private Date reportDateTime;
	private User reportingUser;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;

	private SampleMaterial sampleMaterial;
	private SamplePurpose samplePurpose;
	private String sampleMaterialText;
	private Facility lab;
	private String labDetails;
	private Date shipmentDate;
	private String shipmentDetails;
	private YesNo sentToIpDakar;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private SampleSource sampleSource;
	private Sample referredTo;
	private boolean shipped;
	private boolean received;
	private PathogenTestResultType pathogenTestResult;
	private Date pathogenTestResultChangeDate;

	private Boolean pathogenTestingRequested;
	private Boolean additionalTestingRequested;
	private Set<PathogenTestType> requestedPathogenTests;
	private Set<AdditionalTestType> requestedAdditionalTests;
	private String requestedOtherPathogenTests;
	private String requestedOtherAdditionalTests;
	private String requestedPathogenTestsString;
	private String requestedAdditionalTestsString;
	private SamplingReason samplingReason;
	private String samplingReasonDetails;
	private IdsrType idsrDiagnosis;
	private String idsrDiagnosisDetails;
	private Date dateFormSentToHigherLevel;
	private String nameContactPersonCompletingForm;
	private Date dispatchedToRegionalColdroomDate;
	private Date dispatchedToNationalLabByCourierDate;
	private Date dispatchedToNationalLabByRegionDistrictDate;
	private Date dateFirstSpecimen;
	private Date dateSecondSpecimen;
	private Date dateSpecimenSentNationalLevel;
	private Date dateSpecimenReceivedNationalLevel;
	private Date dateSpecimenSentInter;
	private Date dateSpecimenReceivedInter;
	private SpecimenCondition statusSpecimenReceptionAtLab;
	private Date dateSpecimenSentFromFieldToNationalLab;
	private Date dateSpecimenSentToRegionalReferenceLab;
	private Date dateSpecimenReceivedAtNationalLab;
	private Date dateSpecimenReceivedAtRegionalReferenceLab;
	private Date dateFormCsfDispatchedToHealthDistrict;
	private Date dateHealthFacilityNotifyRegion;
	private YesNo lumbarPuncturePerformed;
	private Date dateOfLp;
	private LpAspect lpAspect;
	private LpPackaging lpPackaging;
	private String lpPackagingOther;
	private YesNo wasSpecimenTaken;
	private LaboratoryType laboratoryType;
	private String laboratoryName;
	private Date dateSpecimenSentToLaboratoryType;
	private Packaging packaging;
	private String packagingOther;
	private String barcode;
	private YesNo csfSampleCollected;
	private LpNotDoneReason lpNotDoneReason;
	private String lpNotDoneReasonOther;
	private Date timeOfInoculationIntoTransportMedia;
	private YesNo samplesSentToLaboratory;
	private String samplesNotSentReason;
	private boolean dispatched;
	private Date dateTimeSampleSentToLab;
	private SampleContainerType sampleContainerUsed;
	private String sampleContainerUsedOther;
	private YesNo meningitisRdtPerformed;
	private MeningitisRdtResult meningitisRdtResult;
	private String labNumber;
	private SampleContainerType sampleContainerReceived;
	private String sampleContainerReceivedOther;
	private SpecimenCondition sampleConditionAtReception;
	private CsfAppearance csfAppearanceAtCollection;
	private CsfAppearance csfAppearanceAtReception;
	private SimpleTestResultType elisaIgm;
	private Date elisaIgmDate;
	private PathogenTestResultType pcr;
	private Date pcrDate;
	private PathogenTestResultType prnt;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String prntInputValue;
	private Date prntDate;
	private Date dateResultsSentToReferringClinician;
	private Disease suspectedDisease;
	private List<PathogenTest> pathogenTests;
	private List<AdditionalTest> additionalTests;

	private SormasToSormasOriginInfo sormasToSormasOriginInfo;
	private List<SormasToSormasShareInfo> sormasToSormasShares = new ArrayList<>(0);

	private List<SampleReport> sampleReports = new ArrayList<>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Case getAssociatedCase() {
		return associatedCase;
	}

	public void setAssociatedCase(Case associatedCase) {
		this.associatedCase = associatedCase;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Contact getAssociatedContact() {
		return associatedContact;
	}

	public void setAssociatedContact(Contact associatedContact) {
		this.associatedContact = associatedContact;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public EventParticipant getAssociatedEventParticipant() {
		return associatedEventParticipant;
	}

	public void setAssociatedEventParticipant(EventParticipant associatedEventParticipant) {
		this.associatedEventParticipant = associatedEventParticipant;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabSampleID() {
		return labSampleID;
	}

	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getFieldSampleID() {
		return fieldSampleID;
	}

	public void setFieldSampleID(String fieldSampleID) {
		this.fieldSampleID = fieldSampleID;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn
	public Facility getLab() {
		return lab;
	}

	public void setLab(Facility lab) {
		this.lab = lab;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getShipmentDetails() {
		return shipmentDetails;
	}

	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getSentToIpDakar() {
		return sentToIpDakar;
	}

	public void setSentToIpDakar(YesNo sentToIpDakar) {
		this.sentToIpDakar = sentToIpDakar;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}

	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}

	@OneToMany(mappedBy = PathogenTest.SAMPLE, fetch = FetchType.LAZY)
	public List<PathogenTest> getPathogenTests() {
		return pathogenTests;
	}

	public void setPathogenTests(List<PathogenTest> pathogenTests) {
		this.pathogenTests = pathogenTests;
	}

	@OneToMany(mappedBy = AdditionalTest.SAMPLE, fetch = FetchType.LAZY)
	public List<AdditionalTest> getAdditionalTests() {
		return additionalTests;
	}

	public void setAdditionalTests(List<AdditionalTest> additionalTests) {
		this.additionalTests = additionalTests;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Enumerated(EnumType.STRING)
	public SampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	@OneToOne(cascade = {})
	@JoinColumn(nullable = true)
	public Sample getReferredTo() {
		return referredTo;
	}

	public void setReferredTo(Sample referredTo) {
		this.referredTo = referredTo;
	}

	@Column
	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	@Column
	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPathogenTestResultChangeDate() {
		return pathogenTestResultChangeDate;
	}

	public void setPathogenTestResultChangeDate(Date pathogenTestResultChangeDate) {
		this.pathogenTestResultChangeDate = pathogenTestResultChangeDate;
	}

	@Column
	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	@Column
	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	@Transient
	public Set<PathogenTestType> getRequestedPathogenTests() {
		if (requestedPathogenTests == null) {
			if (StringUtils.isEmpty(requestedPathogenTestsString)) {
				requestedPathogenTests = new HashSet<>();
			} else {
				requestedPathogenTests =
					Arrays.stream(requestedPathogenTestsString.split(",")).map(PathogenTestType::valueOf).collect(Collectors.toSet());
			}
		}
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;

		if (this.requestedPathogenTests == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		requestedPathogenTests.stream().forEach(t -> {
			sb.append(t.name());
			sb.append(",");
		});
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		requestedPathogenTestsString = sb.toString();
	}

	@Transient
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		if (requestedAdditionalTests == null) {
			if (StringUtils.isEmpty(requestedAdditionalTestsString)) {
				requestedAdditionalTests = new HashSet<>();
			} else {
				requestedAdditionalTests =
					Arrays.stream(requestedAdditionalTestsString.split(",")).map(AdditionalTestType::valueOf).collect(Collectors.toSet());
			}
		}
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;

		if (this.requestedAdditionalTests == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		requestedAdditionalTests.stream().forEach(t -> {
			sb.append(t.name());
			sb.append(",");
		});
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		requestedAdditionalTestsString = sb.toString();
	}

	public String getRequestedPathogenTestsString() {
		return requestedPathogenTestsString;
	}

	public void setRequestedPathogenTestsString(String requestedPathogenTestsString) {
		this.requestedPathogenTestsString = requestedPathogenTestsString;
		requestedPathogenTests = null;
	}

	public String getRequestedAdditionalTestsString() {
		return requestedAdditionalTestsString;
	}

	public void setRequestedAdditionalTestsString(String requestedAdditionalTestsString) {
		this.requestedAdditionalTestsString = requestedAdditionalTestsString;
		requestedAdditionalTests = null;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
	}

	@Enumerated(EnumType.STRING)
	public SamplingReason getSamplingReason() {
		return samplingReason;
	}

	public void setSamplingReason(SamplingReason samplingReason) {
		this.samplingReason = samplingReason;
	}

	@Column(columnDefinition = "text")
	public String getSamplingReasonDetails() {
		return samplingReasonDetails;
	}

	public void setSamplingReasonDetails(String samplingReasonDetails) {
		this.samplingReasonDetails = samplingReasonDetails;
	}

	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(
			getUuid(),
			getSampleMaterial(),
			getAssociatedCase() != null ? getAssociatedCase().getUuid() : null,
			getAssociatedContact() != null ? getAssociatedContact().getUuid() : null,
			getAssociatedEventParticipant() != null ? getAssociatedEventParticipant().getUuid() : null);
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	@Override
	@ManyToOne(cascade = {
		CascadeType.PERSIST,
		CascadeType.MERGE,
		CascadeType.DETACH,
		CascadeType.REFRESH })
	public SormasToSormasOriginInfo getSormasToSormasOriginInfo() {
		return sormasToSormasOriginInfo;
	}

	@Override
	public void setSormasToSormasOriginInfo(SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		this.sormasToSormasOriginInfo = sormasToSormasOriginInfo;
	}

	@OneToMany(mappedBy = SormasToSormasShareInfo.SAMPLE, fetch = FetchType.LAZY)
	public List<SormasToSormasShareInfo> getSormasToSormasShares() {
		return sormasToSormasShares;
	}

	public void setSormasToSormasShares(List<SormasToSormasShareInfo> sormasToSormasShares) {
		this.sormasToSormasShares = sormasToSormasShares;
	}

	@OneToMany(mappedBy = SampleReport.SAMPLE, fetch = FetchType.LAZY)
	public List<SampleReport> getSampleReports() {
		return sampleReports;
	}

	public void setSampleReports(List<SampleReport> externalMessages) {
		this.sampleReports = externalMessages;
	}

	@Enumerated(EnumType.STRING)
	public IdsrType getIdsrDiagnosis() {
		return idsrDiagnosis;
	}

	public void setIdsrDiagnosis(IdsrType idsrDiagnosis) {
		this.idsrDiagnosis = idsrDiagnosis;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getIdsrDiagnosisDetails() {
		return idsrDiagnosisDetails;
	}

	public void setIdsrDiagnosisDetails(String idsrDiagnosisDetails) {
		this.idsrDiagnosisDetails = idsrDiagnosisDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateFormSentToHigherLevel() {
		return dateFormSentToHigherLevel;
	}

	public void setDateFormSentToHigherLevel(Date dateFormSentToHigherLevel) {
		this.dateFormSentToHigherLevel = dateFormSentToHigherLevel;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getNameContactPersonCompletingForm() {
		return nameContactPersonCompletingForm;
	}

	public void setNameContactPersonCompletingForm(String nameContactPersonCompletingForm) {
		this.nameContactPersonCompletingForm = nameContactPersonCompletingForm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDispatchedToRegionalColdroomDate() {
		return dispatchedToRegionalColdroomDate;
	}

	public void setDispatchedToRegionalColdroomDate(Date dispatchedToRegionalColdroomDate) {
		this.dispatchedToRegionalColdroomDate = dispatchedToRegionalColdroomDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDispatchedToNationalLabByCourierDate() {
		return dispatchedToNationalLabByCourierDate;
	}

	public void setDispatchedToNationalLabByCourierDate(Date dispatchedToNationalLabByCourierDate) {
		this.dispatchedToNationalLabByCourierDate = dispatchedToNationalLabByCourierDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDispatchedToNationalLabByRegionDistrictDate() {
		return dispatchedToNationalLabByRegionDistrictDate;
	}

	public void setDispatchedToNationalLabByRegionDistrictDate(Date dispatchedToNationalLabByRegionDistrictDate) {
		this.dispatchedToNationalLabByRegionDistrictDate = dispatchedToNationalLabByRegionDistrictDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateFirstSpecimen() {
		return dateFirstSpecimen;
	}

	public void setDateFirstSpecimen(Date dateFirstSpecimen) {
		this.dateFirstSpecimen = dateFirstSpecimen;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSecondSpecimen() {
		return dateSecondSpecimen;
	}

	public void setDateSecondSpecimen(Date dateSecondSpecimen) {
		this.dateSecondSpecimen = dateSecondSpecimen;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenSentNationalLevel() {
		return dateSpecimenSentNationalLevel;
	}

	public void setDateSpecimenSentNationalLevel(Date dateSpecimenSentNationalLevel) {
		this.dateSpecimenSentNationalLevel = dateSpecimenSentNationalLevel;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenReceivedNationalLevel() {
		return dateSpecimenReceivedNationalLevel;
	}

	public void setDateSpecimenReceivedNationalLevel(Date dateSpecimenReceivedNationalLevel) {
		this.dateSpecimenReceivedNationalLevel = dateSpecimenReceivedNationalLevel;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenSentInter() {
		return dateSpecimenSentInter;
	}

	public void setDateSpecimenSentInter(Date dateSpecimenSentInter) {
		this.dateSpecimenSentInter = dateSpecimenSentInter;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenReceivedInter() {
		return dateSpecimenReceivedInter;
	}

	public void setDateSpecimenReceivedInter(Date dateSpecimenReceivedInter) {
		this.dateSpecimenReceivedInter = dateSpecimenReceivedInter;
	}
	@Enumerated(EnumType.STRING)
	public SpecimenCondition getStatusSpecimenReceptionAtLab() {
		return statusSpecimenReceptionAtLab;
	}

	public void setStatusSpecimenReceptionAtLab(SpecimenCondition statusSpecimenReceptionAtLab) {
		this.statusSpecimenReceptionAtLab = statusSpecimenReceptionAtLab;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenSentFromFieldToNationalLab() {
		return dateSpecimenSentFromFieldToNationalLab;
	}

	public void setDateSpecimenSentFromFieldToNationalLab(Date dateSpecimenSentFromFieldToNationalLab) {
		this.dateSpecimenSentFromFieldToNationalLab = dateSpecimenSentFromFieldToNationalLab;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenSentToRegionalReferenceLab() {
		return dateSpecimenSentToRegionalReferenceLab;
	}

	public void setDateSpecimenSentToRegionalReferenceLab(Date dateSpecimenSentToRegionalReferenceLab) {
		this.dateSpecimenSentToRegionalReferenceLab = dateSpecimenSentToRegionalReferenceLab;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenReceivedAtNationalLab() {
		return dateSpecimenReceivedAtNationalLab;
	}

	public void setDateSpecimenReceivedAtNationalLab(Date dateSpecimenReceivedAtNationalLab) {
		this.dateSpecimenReceivedAtNationalLab = dateSpecimenReceivedAtNationalLab;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenReceivedAtRegionalReferenceLab() {
		return dateSpecimenReceivedAtRegionalReferenceLab;
	}

	public void setDateSpecimenReceivedAtRegionalReferenceLab(Date dateSpecimenReceivedAtRegionalReferenceLab) {
		this.dateSpecimenReceivedAtRegionalReferenceLab = dateSpecimenReceivedAtRegionalReferenceLab;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateFormCsfDispatchedToHealthDistrict() {
		return dateFormCsfDispatchedToHealthDistrict;
	}

	public void setDateFormCsfDispatchedToHealthDistrict(Date dateFormCsfDispatchedToHealthDistrict) {
		this.dateFormCsfDispatchedToHealthDistrict = dateFormCsfDispatchedToHealthDistrict;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateHealthFacilityNotifyRegion() {
		return dateHealthFacilityNotifyRegion;
	}

	public void setDateHealthFacilityNotifyRegion(Date dateHealthFacilityNotifyRegion) {
		this.dateHealthFacilityNotifyRegion = dateHealthFacilityNotifyRegion;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getLumbarPuncturePerformed() {
		return lumbarPuncturePerformed;
	}

	public void setLumbarPuncturePerformed(YesNo lumbarPuncturePerformed) {
		this.lumbarPuncturePerformed = lumbarPuncturePerformed;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfLp() {
		return dateOfLp;
	}

	public void setDateOfLp(Date dateOfLp) {
		this.dateOfLp = dateOfLp;
	}

	@Enumerated(EnumType.STRING)
	public LpAspect getLpAspect() {
		return lpAspect;
	}

	public void setLpAspect(LpAspect lpAspect) {
		this.lpAspect = lpAspect;
	}

	@Enumerated(EnumType.STRING)
	public LpPackaging getLpPackaging() {
		return lpPackaging;
	}

	public void setLpPackaging(LpPackaging lpPackaging) {
		this.lpPackaging = lpPackaging;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLpPackagingOther() {
		return lpPackagingOther;
	}

	public void setLpPackagingOther(String lpPackagingOther) {
		this.lpPackagingOther = lpPackagingOther;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getWasSpecimenTaken() {
		return wasSpecimenTaken;
	}

	public void setWasSpecimenTaken(YesNo wasSpecimenTaken) {
		this.wasSpecimenTaken = wasSpecimenTaken;
	}

	@Enumerated(EnumType.STRING)
	public LaboratoryType getLaboratoryType() {
		return laboratoryType;
	}

	public void setLaboratoryType(LaboratoryType laboratoryType) {
		this.laboratoryType = laboratoryType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLaboratoryName() {
		return laboratoryName;
	}

	public void setLaboratoryName(String laboratoryName) {
		this.laboratoryName = laboratoryName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSpecimenSentToLaboratoryType() {
		return dateSpecimenSentToLaboratoryType;
	}

	public void setDateSpecimenSentToLaboratoryType(Date dateSpecimenSentToLaboratoryType) {
		this.dateSpecimenSentToLaboratoryType = dateSpecimenSentToLaboratoryType;
	}

	@Enumerated(EnumType.STRING)
	public Packaging getPackaging() {
		return packaging;
	}

	public void setPackaging(Packaging packaging) {
		this.packaging = packaging;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPackagingOther() {
		return packagingOther;
	}

	public void setPackagingOther(String packagingOther) {
		this.packagingOther = packagingOther;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getCsfSampleCollected() {
		return csfSampleCollected;
	}

	public void setCsfSampleCollected(YesNo csfSampleCollected) {
		this.csfSampleCollected = csfSampleCollected;
	}

	@Enumerated(EnumType.STRING)
	public LpNotDoneReason getLpNotDoneReason() {
		return lpNotDoneReason;
	}

	public void setLpNotDoneReason(LpNotDoneReason lpNotDoneReason) {
		this.lpNotDoneReason = lpNotDoneReason;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLpNotDoneReasonOther() {
		return lpNotDoneReasonOther;
	}

	public void setLpNotDoneReasonOther(String lpNotDoneReasonOther) {
		this.lpNotDoneReasonOther = lpNotDoneReasonOther;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimeOfInoculationIntoTransportMedia() {
		return timeOfInoculationIntoTransportMedia;
	}

	public void setTimeOfInoculationIntoTransportMedia(Date timeOfInoculationIntoTransportMedia) {
		this.timeOfInoculationIntoTransportMedia = timeOfInoculationIntoTransportMedia;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getSamplesSentToLaboratory() {
		return samplesSentToLaboratory;
	}

	public void setSamplesSentToLaboratory(YesNo samplesSentToLaboratory) {
		this.samplesSentToLaboratory = samplesSentToLaboratory;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSamplesNotSentReason() {
		return samplesNotSentReason;
	}

	public void setSamplesNotSentReason(String samplesNotSentReason) {
		this.samplesNotSentReason = samplesNotSentReason;
	}

	@Column
	public boolean isDispatched() {
		return dispatched;
	}

	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateTimeSampleSentToLab() {
		return dateTimeSampleSentToLab;
	}

	public void setDateTimeSampleSentToLab(Date dateTimeSampleSentToLab) {
		this.dateTimeSampleSentToLab = dateTimeSampleSentToLab;
	}

	@Enumerated(EnumType.STRING)
	public SampleContainerType getSampleContainerUsed() {
		return sampleContainerUsed;
	}

	public void setSampleContainerUsed(SampleContainerType sampleContainerUsed) {
		this.sampleContainerUsed = sampleContainerUsed;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSampleContainerUsedOther() {
		return sampleContainerUsedOther;
	}

	public void setSampleContainerUsedOther(String sampleContainerUsedOther) {
		this.sampleContainerUsedOther = sampleContainerUsedOther;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getMeningitisRdtPerformed() {
		return meningitisRdtPerformed;
	}

	public void setMeningitisRdtPerformed(YesNo meningitisRdtPerformed) {
		this.meningitisRdtPerformed = meningitisRdtPerformed;
	}

	@Enumerated(EnumType.STRING)
	public MeningitisRdtResult getMeningitisRdtResult() {
		return meningitisRdtResult;
	}

	public void setMeningitisRdtResult(MeningitisRdtResult meningitisRdtResult) {
		this.meningitisRdtResult = meningitisRdtResult;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabNumber() {
		return labNumber;
	}

	public void setLabNumber(String labNumber) {
		this.labNumber = labNumber;
	}

	@Enumerated(EnumType.STRING)
	public SampleContainerType getSampleContainerReceived() {
		return sampleContainerReceived;
	}

	public void setSampleContainerReceived(SampleContainerType sampleContainerReceived) {
		this.sampleContainerReceived = sampleContainerReceived;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSampleContainerReceivedOther() {
		return sampleContainerReceivedOther;
	}

	public void setSampleContainerReceivedOther(String sampleContainerReceivedOther) {
		this.sampleContainerReceivedOther = sampleContainerReceivedOther;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSampleConditionAtReception() {
		return sampleConditionAtReception;
	}

	public void setSampleConditionAtReception(SpecimenCondition sampleConditionAtReception) {
		this.sampleConditionAtReception = sampleConditionAtReception;
	}

	@Enumerated(EnumType.STRING)
	public CsfAppearance getCsfAppearanceAtCollection() {
		return csfAppearanceAtCollection;
	}

	public void setCsfAppearanceAtCollection(CsfAppearance csfAppearanceAtCollection) {
		this.csfAppearanceAtCollection = csfAppearanceAtCollection;
	}

	@Enumerated(EnumType.STRING)
	public CsfAppearance getCsfAppearanceAtReception() {
		return csfAppearanceAtReception;
	}

	public void setCsfAppearanceAtReception(CsfAppearance csfAppearanceAtReception) {
		this.csfAppearanceAtReception = csfAppearanceAtReception;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public SimpleTestResultType getElisaIgm() {
		return elisaIgm;
	}

	public void setElisaIgm(SimpleTestResultType elisaIgm) {
		this.elisaIgm = elisaIgm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column
	public Date getElisaIgmDate() {
		return elisaIgmDate;
	}

	public void setElisaIgmDate(Date elisaIgmDate) {
		this.elisaIgmDate = elisaIgmDate;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public PathogenTestResultType getPcr() {
		return pcr;
	}

	public void setPcr(PathogenTestResultType pcr) {
		this.pcr = pcr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column
	public Date getPcrDate() {
		return pcrDate;
	}

	public void setPcrDate(Date pcrDate) {
		this.pcrDate = pcrDate;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public PathogenTestResultType getPrnt() {
		return prnt;
	}

	public void setPrnt(PathogenTestResultType prnt) {
		this.prnt = prnt;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPrntInputValue() {
		return prntInputValue;
	}

	public void setPrntInputValue(String prntInputValue) {
		this.prntInputValue = prntInputValue;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column
	public Date getPrntDate() {
		return prntDate;
	}

	public void setPrntDate(Date prntDate) {
		this.prntDate = prntDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column
	public Date getDateResultsSentToReferringClinician() {
		return dateResultsSentToReferringClinician;
	}

	public void setDateResultsSentToReferringClinician(Date dateResultsSentToReferringClinician) {
		this.dateResultsSentToReferringClinician = dateResultsSentToReferringClinician;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public Disease getSuspectedDisease() {
		return suspectedDisease;
	}

	public void setSuspectedDisease(Disease suspectedDisease) {
		this.suspectedDisease = suspectedDisease;
	}
}

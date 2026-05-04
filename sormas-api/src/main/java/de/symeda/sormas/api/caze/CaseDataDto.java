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

package de.symeda.sormas.api.caze;

import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_FRANCE;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_GERMANY;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_LUXEMBOURG;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_SWITZERLAND;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.afpimmunization.AfpImmunizationDto;
import de.symeda.sormas.api.caze.caseimport.MotherVaccinationStatus;
import de.symeda.sormas.api.response.ResponseDto;
import de.symeda.sormas.api.utils.*;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class CaseDataDto extends SormasToSormasShareableDto implements IsCase {

	private static final long serialVersionUID = 5007131477733638086L;
	private static final long MILLISECONDS_30_DAYS = 30L * 24L * 60L * 60L * 1000L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 123458;

	public static final String I18N_PREFIX = "CaseData";

	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String CASE_IDENTIFICATION_SOURCE = "caseIdentificationSource";
	public static final String SCREENING_TYPE = "screeningType";
	public static final String CLASSIFICATION_USER = "classificationUser";
	public static final String CLASSIFICATION_DATE = "classificationDate";
	public static final String CLASSIFICATION_COMMENT = "classificationComment";
	public static final String FINAL_CLASSIFICATION_DISCARDED = "finalClassificationDiscarded";
	public static final String CLASSIFIED_BY = "classifiedBy";
	public static final String CLINICAL_CONFIRMATION = "clinicalConfirmation";
	public static final String EPIDEMIOLOGICAL_CONFIRMATION = "epidemiologicalConfirmation";
	public static final String LABORATORY_DIAGNOSTIC_CONFIRMATION = "laboratoryDiagnosticConfirmation";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String PLAGUE_TYPE = "plagueType";
	public static final String DENGUE_FEVER_TYPE = "dengueFeverType";
	public static final String RABIES_TYPE = "rabiesType";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String DISTRICT_LEVEL_DATE = "districtLevelDate";
	public static final String REGION_LEVEL_DATE = "regionLevelDate";
	public static final String NATIONAL_LEVEL_DATE = "nationalLevelDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String SYMPTOMS = "symptoms";
	public static final String HOSPITALIZATION = "hospitalization";
	public static final String EPI_DATA = "epiData";
	public static final String THERAPY = "therapy";
	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String MATERNAL_HISTORY = "maternalHistory";
	public static final String PORT_HEALTH_INFO = "portHealthInfo";
	public static final String HEALTH_CONDITIONS = "healthConditions";
	public static final String AFP_IMMUNIZATION = "afpImmunization";
	public static final String RESPONSE = "response";
	public static final String PREGNANT = "pregnant";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String VACCINATED = "vaccinated";
	public static final String ROUTINE_VACCINATION_TYPE = "routineVaccinationType";
	public static final String VACCINATION_RECORD_TYPE = "vaccinationRecordType";
	public static final String NUMBER_OF_VACCINATION_DOSES = "numberOfVaccinationDoses";
	public static final String LAST_VACCINATION_DATE = "lastVaccinationDate";
	public static final String SMALLPOX_VACCINATION_SCAR = "smallpoxVaccinationScar";
	public static final String SMALLPOX_VACCINATION_RECEIVED = "smallpoxVaccinationReceived";
	public static final String SMALLPOX_LAST_VACCINATION_DATE = "smallpoxLastVaccinationDate";
	public static final String AT_LEAST_ONE_YELLOW_FEVER_DOSE = "atLeastOneYellowFeverDose";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String REPORT_LAT_LON_ACCURACY = "reportLatLonAccuracy";
	public static final String OUTCOME = "outcome";
	public static final String OUTCOME_DATE = "outcomeDate";
	public static final String SEQUELAE = "sequelae";
	public static final String SEQUELAE_DETAILS = "sequelaeDetails";
	public static final String CLINICIAN_NAME = "clinicianName";
	public static final String CLINICIAN_PHONE = "clinicianPhone";
	public static final String CLINICIAN_EMAIL = "clinicianEmail";
	public static final String NOTIFYING_CLINIC = "notifyingClinic";
	public static final String NOTIFYING_CLINIC_DETAILS = "notifyingClinicDetails";
	public static final String CASE_ORIGIN = "caseOrigin";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String POINT_OF_ENTRY_DETAILS = "pointOfEntryDetails";
	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	public static final String EXTERNAL_ID = "externalID";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String CASE_REFERENCE_NUMBER = "caseReferenceNumber";
	public static final String SHARED_TO_COUNTRY = "sharedToCountry";
	public static final String NOSOCOMIAL_OUTBREAK = "nosocomialOutbreak";
	public static final String INFECTION_SETTING = "infectionSetting";
	public static final String QUARANTINE = "quarantine";
	public static final String QUARANTINE_TYPE_DETAILS = "quarantineTypeDetails";
	public static final String QUARANTINE_FROM = "quarantineFrom";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String QUARANTINE_HELP_NEEDED = "quarantineHelpNeeded";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_ORDERED_VERBALLY_DATE = "quarantineOrderedVerballyDate";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE = "quarantineOrderedOfficialDocumentDate";
	public static final String QUARANTINE_HOME_POSSIBLE = "quarantineHomePossible";
	public static final String QUARANTINE_HOME_POSSIBLE_COMMENT = "quarantineHomePossibleComment";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED = "quarantineHomeSupplyEnsured";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT = "quarantineHomeSupplyEnsuredComment";
	public static final String QUARANTINE_EXTENDED = "quarantineExtended";
	public static final String QUARANTINE_REDUCED = "quarantineReduced";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT = "quarantineOfficialOrderSent";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT_DATE = "quarantineOfficialOrderSentDate";
	public static final String POSTPARTUM = "postpartum";
	public static final String TRIMESTER = "trimester";
	public static final String OVERWRITE_FOLLOW_UP_UNTIL = "overwriteFollowUpUntil";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String VISITS = "visits";
	public static final String FACILITY_TYPE = "facilityType";

	public static final String CASE_ID_ISM = "caseIdIsm";
	public static final String CONTACT_TRACING_FIRST_CONTACT_TYPE = "contactTracingFirstContactType";
	public static final String CONTACT_TRACING_FIRST_CONTACT_DATE = "contactTracingFirstContactDate";
	public static final String WAS_IN_QUARANTINE_BEFORE_ISOLATION = "wasInQuarantineBeforeIsolation";
	public static final String QUARANTINE_REASON_BEFORE_ISOLATION = "quarantineReasonBeforeIsolation";
	public static final String QUARANTINE_REASON_BEFORE_ISOLATION_DETAILS = "quarantineReasonBeforeIsolationDetails";
	public static final String END_OF_ISOLATION_REASON = "endOfIsolationReason";
	public static final String END_OF_ISOLATION_REASON_DETAILS = "endOfIsolationReasonDetails";

	public static final String PROHIBITION_TO_WORK = "prohibitionToWork";
	public static final String PROHIBITION_TO_WORK_FROM = "prohibitionToWorkFrom";
	public static final String PROHIBITION_TO_WORK_UNTIL = "prohibitionToWorkUntil";

	public static final String RE_INFECTION = "reInfection";
	public static final String PREVIOUS_INFECTION_DATE = "previousInfectionDate";
	public static final String REINFECTION_STATUS = "reinfectionStatus";
	public static final String REINFECTION_DETAILS = "reinfectionDetails";

	public static final String BLOOD_ORGAN_OR_TISSUE_DONATED = "bloodOrganOrTissueDonated";

	public static final String NOT_A_CASE_REASON_NEGATIVE_TEST = "notACaseReasonNegativeTest";
	public static final String NOT_A_CASE_REASON_PHYSICIAN_INFORMATION = "notACaseReasonPhysicianInformation";
	public static final String NOT_A_CASE_REASON_DIFFERENT_PATHOGEN = "notACaseReasonDifferentPathogen";
	public static final String NOT_A_CASE_REASON_OTHER = "notACaseReasonOther";
	public static final String NOT_A_CASE_REASON_DETAILS = "notACaseReasonDetails";
	public static final String FOLLOW_UP_STATUS_CHANGE_DATE = "followUpStatusChangeDate";
	public static final String FOLLOW_UP_STATUS_CHANGE_USER = "followUpStatusChangeUser";
	public static final String DONT_SHARE_WITH_REPORTING_TOOL = "dontShareWithReportingTool";
	public static final String CASE_REFERENCE_DEFINITION = "caseReferenceDefinition";
	public static final String PREVIOUS_QUARANTINE_TO = "previousQuarantineTo";
	public static final String QUARANTINE_CHANGE_COMMENT = "quarantineChangeComment";

	public static final String EXTERNAL_DATA = "externalData";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";
	public static final String POST_MORTEM = "postMortem";
	public static final String DEPARTMENT = "department";

	public static final String NOTIFIER = "notifier";
	public static final String RADIOGRAPHY_COMPATIBILITY = "radiographyCompatibility";
	public static final String OTHER_DIAGNOSTIC_CRITERIA = "otherDiagnosticCriteria";
    public static final String IDSR_DIAGNOSIS = "idsrDiagnosis";
    public static final String IDSR_DIAGNOSIS_DETAILS = "idsrDiagnosisDetails";
	public static final String NOTIFIED_BY = "notifiedBy";
	public static final String NOTIFIED_BY_TEXT = "notifiedByText";
	public static final String NOTIFIED_BY_DETAILS = "notifiedByDetails";
	public static final String DATE_OF_NOTIFICATION = "dateOfNotification";
	public static final String DATE_OF_INVESTIGATION = "dateOfInvestigation";
	public static final String DIVISION = "division";
	public static final String COMPOUND_OWNER = "compoundOwner";
	public static final String NATIONALITY = "nationality";
	public static final String MOTHER_VACCINATED_WITH_TT = "motherVaccinatedWithTT";
	public static final String MOTHER_HAVE_CARD = "motherHaveCard";
	public static final String MOTHER_NUMBER_OF_DOSES = "motherNumberOfDoses";
	public static final String MOTHER_VACCINATION_STATUS = "motherVaccinationStatus";
	public static final String MOTHER_TT_DATE_ONE = "motherTTDateOne";
	public static final String MOTHER_TT_DATE_TWO = "motherTTDateTwo";
	public static final String MOTHER_TT_DATE_THREE = "motherTTDateThree";
	public static final String MOTHER_TT_DATE_FOUR = "motherTTDateFour";
	public static final String MOTHER_TT_DATE_FIVE = "motherTTDateFive";
	public static final String MOTHER_LAST_DOSE_DATE = "motherLastDoseDate";
	public static final String INVESTIGATOR_NAME = "investigatorName";
	public static final String INVESTIGATOR_TITLE = "investigatorTitle";
	public static final String INVESTIGATOR_UNIT = "investigatorUnit";
	public static final String INVESTIGATOR_ADDRESS = "investigatorAddress";
	public static final String INVESTIGATOR_TEL = "investigatorTel";
	public static final String INVESTIGATOR_EMAIL = "investigatorEmail";
	public static final String DATE_RECEIVED_AT_DISTRICT_LEVEL = "dateReceivedAtDistrictLevel";
	public static final String SOURCE_OF_INFECTION_IDENTIFIED = "sourceOfInfectionIdentified";
	public static final String MEASLES_COMMUNITY_INVESTIGATION = "measlesCommunityInvestigation";
	public static final String MEASLES_INVESTIGATION_RESULTS = "measlesInvestigationResults";
	public static final String MOTHER_GIVEN_PROTECTIVE_DOSE_TT = "motherGivenProtectiveDoseTT";
	public static final String MOTHER_GIVEN_PROTECTIVE_DOSE_TT_DATE = "motherGivenProtectiveDoseTTDate";
	public static final String SUPPLEMENTAL_IMMUNIZATION = "supplementalImmunization";
	public static final String SUPPLEMENTAL_IMMUNIZATION_DETAILS = "supplementalImmunizationDetails";
	public static final String FINAL_CLASSIFICATION = "finalClassification";
	public static final String FOLLOW_UP_EXAMINATION = "followUpExamination";
	public static final String VDPV_CLASSIFICATION = "vdpvClassification";
	public static final String SERO_CLASSIFICATION = "seroClassification";
	public static final String DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE = "dateCapturedResultsReceivedAtNationalEPIOffice";
	public static final String DATE_DIFFERENTIATION_RECEIVED_EPI = "dateDifferentiationReceivedEpi";
	public static final String IMMUNOCOMPROMISED_STATUS_SUSPECTED = "immunocompromisedStatusSuspected";
	public static final String DATE_REGION_RECEIVES_LAB_RESULTS = "dateRegionReceivesLabResults";
	public static final String DATE_LAB_RESULTS_SENT_HEALTH_FACILITY_REGION = "dateLabResultsSentHealthFacilityRegion";
	public static final String DATE_LAB_RESULTS_RECEIVED_HEALTH_FACILITY = "dateLabResultsReceivedAtHealthFacility";
	public static final String DATE_FORM_SENT_TO_REGION = "dateFormSentToRegion";
	public static final String PERSON_FULLNAME = "personFullName";
	public static final String PERSON_TELEPHONE = "personTelephone";
	public static final String PERSON_DESIGNATION = "personDesignation";
	public static final String DISTRICT_NOTIFICATION_DATE = "districtNotificationDate";
	public static final String DATE_FORM_SENT_TO_DISTRICT = "dateFormSentToDistrict";
	public static final String DATE_FORM_RECEIVED_AT_DISTRICT = "dateFormReceivedAtDistrict";
	public static final String DATE_FORM_RECEIVED_AT_REGION = "dateFormReceivedAtRegion";
	public static final String DATE_FORM_SENT_TO_NATIONAL = "dateFormSentToNational";
	public static final String DATE_FORM_RECEIVED_AT_NATIONAL = "dateFormReceivedAtNational";
	public static final String ARRIVAL_AT_REGIONAL_PUBLIC_HEALTH_OFFICE_DATE = "arrivalAtRegionalPublicHealthOfficeDate";
	public static final String ARRIVAL_AT_NATIONAL_LEVEL_DATE = "arrivalAtNationalLevelDate";
	public static final String VACCINE_TYPE = "vaccineType";
	public static final String HEALTH_WORKER_COMPLETING_FORM = "healthWorkerCompletingForm";
	public static final String MENAC = "menac";
	public static final String MENAC_DATE = "menacDate";
	public static final String MENACW = "menacw";
	public static final String MENACW_DATE = "menacwDate";
	public static final String MENACWY = "menacwy";
	public static final String MENACWY_DATE = "menacwyDate";
	public static final String MENA_CONJUNATE = "menaConjunate";
	public static final String MENA_CONJUNATE_DATE = "menaConjunateDate";
	public static final String PCVI3_I = "pcvi3I";
	public static final String PCVI3_I_DATE = "pcvi3IDate";
	public static final String PCVI3_2 = "pcvi3_2";
	public static final String PCVI3_2_DATE = "pcvi3_2Date";
	public static final String PCV13_3 = "pcv13_3";
	public static final String PCV13_3_DATE = "pcv13_3Date";
	public static final String HIB_I = "hibI";
	public static final String HIB_I_DATE = "hibIDate";
	public static final String HIB_2 = "hib2";
	public static final String HIB_2_DATE = "hib2Date";
	public static final String HIB_3 = "hib3";
	public static final String HIB_3_DATE = "hib3Date";
	public static final String CLASSIFICATION_BY_ORIGIN = "classificationByOrigin";
	public static final String REGION_LAB_RESULTS_RECEIVED = "regionLabResultsReceived";


    // Fields are declared in the order they should appear in the import template
	@Outbreaks
	@NotNull(message = Validations.validDisease)
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	@Outbreaks
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	@Outbreaks
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseVariantDetails;
	@Diseases({
		Disease.PLAGUE })
	@Outbreaks
	private PlagueType plagueType;
	@Diseases({
		Disease.DENGUE })
	@Outbreaks
	private DengueFeverType dengueFeverType;
	@Diseases({
		Disease.RABIES })
	@Outbreaks
	private RabiesType rabiesType;
	@NotNull(message = Validations.validPerson)
	@EmbeddedPersonalData
	private PersonReferenceDto person;
	@Outbreaks
	@HideForCountries(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String epidNumber;
	@Outbreaks
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	@Outbreaks
	private UserReferenceDto reportingUser;
	@HideForCountries(countries = {
		COUNTRY_CODE_FRANCE,
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND,
		COUNTRY_CODE_LUXEMBOURG })
	private Date regionLevelDate;
	@HideForCountries(countries = {
		COUNTRY_CODE_FRANCE,
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND,
		COUNTRY_CODE_LUXEMBOURG })
	private Date nationalLevelDate;
	@Outbreaks
	@HideForCountries(countries = {
		COUNTRY_CODE_FRANCE,
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND,
		COUNTRY_CODE_LUXEMBOURG })
	private Date districtLevelDate;
	@Outbreaks
	@Diseases(value = Disease.RESPIRATORY_SYNCYTIAL_VIRUS, hide = true)
	private CaseClassification caseClassification;
	@HideForCountriesExcept
	private CaseIdentificationSource caseIdentificationSource;
	@HideForCountriesExcept
	private ScreeningType screeningType;
	@Outbreaks
	@Diseases(value = Disease.RESPIRATORY_SYNCYTIAL_VIRUS, hide = true)
	private UserReferenceDto classificationUser;
	@Outbreaks
	@Diseases(value = Disease.RESPIRATORY_SYNCYTIAL_VIRUS, hide = true)
	private Date classificationDate;
	@Outbreaks
	@Diseases(value = Disease.RESPIRATORY_SYNCYTIAL_VIRUS, hide = true)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String classificationComment;
	@Outbreaks
	@Diseases({ Disease.CONGENITAL_RUBELLA })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String finalClassificationDiscarded;

	private YesNoUnknown clinicalConfirmation;
	private YesNoUnknown epidemiologicalConfirmation;
	private YesNoUnknown laboratoryDiagnosticConfirmation;

	@Outbreaks
	private InvestigationStatus investigationStatus;
	@Outbreaks
	private Date investigatedDate;
	@Outbreaks
	private CaseOutcome outcome;
	@Outbreaks
	private Date outcomeDate;
	private YesNoUnknown sequelae;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String sequelaeDetails;
	@NotNull(message = Validations.validResponsibleRegion)
	private RegionReferenceDto responsibleRegion;
	@NotNull(message = Validations.validResponsibleDistrict)
	private DistrictReferenceDto responsibleDistrict;
	@Outbreaks
	@PersonalData
	@SensitiveData
	private CommunityReferenceDto responsibleCommunity;

	@Outbreaks
	private RegionReferenceDto region;
	@Outbreaks
	private DistrictReferenceDto district;
	@Outbreaks
	@PersonalData
	@SensitiveData
	private CommunityReferenceDto community;
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	private FacilityType facilityType;
	@Outbreaks
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	private FacilityReferenceDto healthFacility;
	@Outbreaks
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String healthFacilityDetails;

	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@SensitiveData
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	private HealthConditionsDto healthConditions;
	private YesNoUnknown pregnant;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.OTHER })
	@Outbreaks
	private VaccinationStatus vaccinationStatus;
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM})
	private VaccinationStatus vaccinated;
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM})
	private RoutineVaccinationType routineVaccinationType;
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private VaccinationRecordType vaccinationRecordType;
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private Integer numberOfVaccinationDoses;
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private Date lastVaccinationDate;
	@Diseases({
		Disease.YELLOW_FEVER})
	private YesNoUnknown atLeastOneYellowFeverDose;
	@Diseases({
		Disease.MONKEYPOX })
	private YesNoUnknown smallpoxVaccinationScar;
	@Diseases({
		Disease.MONKEYPOX })
	private YesNoUnknown smallpoxVaccinationReceived;
	@Diseases({
		Disease.MONKEYPOX })
	private Date smallpoxLastVaccinationDate;
	@Outbreaks
	@SensitiveData
	private UserReferenceDto surveillanceOfficer;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@DependingOnUserRight(UserRight.CASE_CLINICIAN_VIEW)
	private String clinicianName;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@DependingOnUserRight(UserRight.CASE_CLINICIAN_VIEW)
	private String clinicianPhone;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@DependingOnUserRight(UserRight.CASE_CLINICIAN_VIEW)
	private String clinicianEmail;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private HospitalWardType notifyingClinic;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String notifyingClinicDetails;
	@Deprecated
	@SensitiveData
	private UserReferenceDto caseOfficer;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;
	private Float reportLatLonAccuracy;
	@Valid
	private HospitalizationDto hospitalization;
	@Valid
	private AfpImmunizationDto afpImmunization;
	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private SymptomsDto symptoms;
	@Valid
	private EpiDataDto epiData;
	@Valid
	private TherapyDto therapy;
	@Valid
	private ClinicalCourseDto clinicalCourse;
	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private MaternalHistoryDto maternalHistory;
	@Size(max = 32, message = Validations.textTooLong)
	private String creationVersion;
	@SensitiveData
	@Valid
	private PortHealthInfoDto portHealthInfo;
	private CaseOrigin caseOrigin;
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	private PointOfEntryReferenceDto pointOfEntry;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String pointOfEntryDetails;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String additionalDetails;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalID;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalToken;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String internalToken;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@DependingOnFeatureType(featureType = FeatureType.SELF_REPORTING)
	private String caseReferenceNumber;
	private boolean sharedToCountry;
	@HideForCountriesExcept
	private boolean nosocomialOutbreak;
	@HideForCountriesExcept
	private InfectionSetting infectionSetting;
	private QuarantineType quarantine;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHelpNeeded;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOrderedVerbally;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOrderedOfficialDocument;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOrderedVerballyDate;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOrderedOfficialDocumentDate;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomePossible;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHomePossibleComment;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@HideForCountriesExcept
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineHomeSupplyEnsuredComment;
	private boolean quarantineExtended;
	private boolean quarantineReduced;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private boolean quarantineOfficialOrderSent;
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	private Date quarantineOfficialOrderSentDate;
	@SensitiveData
	private YesNoUnknown postpartum;
	@SensitiveData
	private Trimester trimester;
	private FollowUpStatus followUpStatus;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String followUpComment;
	private Date followUpUntil;
	private boolean overwriteFollowUpUntil;

	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private Integer caseIdIsm;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private ContactTracingContactType contactTracingFirstContactType;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private Date contactTracingFirstContactDate;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private YesNoUnknown wasInQuarantineBeforeIsolation;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private QuarantineReason quarantineReasonBeforeIsolation;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String quarantineReasonBeforeIsolationDetails;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	private EndOfIsolationReason endOfIsolationReason;
	@HideForCountriesExcept(countries = COUNTRY_CODE_SWITZERLAND)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String endOfIsolationReasonDetails;

	@HideForCountriesExcept
	private YesNoUnknown prohibitionToWork;
	@HideForCountriesExcept
	private Date prohibitionToWorkFrom;
	@HideForCountriesExcept
	private Date prohibitionToWorkUntil;

	@Diseases({
		Disease.CORONAVIRUS })
	@HideForCountriesExcept
	private YesNoUnknown reInfection;
	@Diseases({
		Disease.CORONAVIRUS })
	@HideForCountriesExcept
	private Date previousInfectionDate;
	@Diseases({
		Disease.CORONAVIRUS })
	@HideForCountriesExcept
	private ReinfectionStatus reinfectionStatus;
	@Diseases({
		Disease.CORONAVIRUS })
	@HideForCountriesExcept
	private Map<ReinfectionDetail, Boolean> reinfectionDetails;

	@HideForCountriesExcept
	private YesNoUnknown bloodOrganOrTissueDonated;

	@HideForCountriesExcept
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	private boolean notACaseReasonNegativeTest;

	@HideForCountriesExcept
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	private boolean notACaseReasonPhysicianInformation;

	@HideForCountriesExcept
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	private boolean notACaseReasonDifferentPathogen;

	@HideForCountriesExcept
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	private boolean notACaseReasonOther;

	@HideForCountriesExcept
	@SensitiveData
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String notACaseReasonDetails;

	private Date followUpStatusChangeDate;
	private UserReferenceDto followUpStatusChangeUser;

	private boolean dontShareWithReportingTool;

	@HideForCountriesExcept
	private CaseReferenceDefinition caseReferenceDefinition;

	private Date previousQuarantineTo;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String quarantineChangeComment;

	private Map<String, String> externalData;
	private boolean deleted;
	private DeletionReason deletionReason;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;
	@HideForCountriesExcept(countries = COUNTRY_CODE_LUXEMBOURG)
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	private boolean postMortem;
	@HideForCountriesExcept(countries = COUNTRY_CODE_LUXEMBOURG)
	private String department;

	private NotifierReferenceDto notifier;
	private RadiographyCompatibility radiographyCompatibility;
	private String otherDiagnosticCriteria;
    @Diseases({
            Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
    @Outbreaks
    private IdsrType idsrDiagnosis;
    @Diseases({
            Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
    @Outbreaks
    @Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
    private String idsrDiagnosisDetails;

	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
			Disease.CSM,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private NotifiedBy notifiedBy;

	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.AFP})
	private String notifiedByText;
	@Diseases({
			Disease.MEASLES,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String notifiedByDetails;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
			Disease.CSM,
			Disease.CONGENITAL_RUBELLA})
	private Date dateOfNotification;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
			Disease.CSM})
	private Date dateOfInvestigation;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES})
	private String division;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES})
	private String compoundOwner;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.CSM})
	private String nationality;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private YesNoUnknown motherVaccinatedWithTT;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private YesNoUnknown motherHaveCard;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private String motherNumberOfDoses;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private MotherVaccinationStatus motherVaccinationStatus;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherTTDateOne;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherTTDateTwo;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherTTDateThree;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherTTDateFour;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherTTDateFive;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherLastDoseDate;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String investigatorName;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP})
	private String investigatorTitle;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String investigatorUnit;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.AFP})
	private String investigatorAddress;
	@Diseases({
			Disease.NEONATAL_TETANUS,
			Disease.MEASLES,
			Disease.YELLOW_FEVER,
			Disease.AFP,
	Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String investigatorTel;
	@Diseases({
			Disease.MEASLES,
			Disease.YELLOW_FEVER})
	private String investigatorEmail;
	@Diseases({
			Disease.MEASLES})
	private Date dateReceivedAtDistrictLevel;
	@Diseases({
			Disease.MEASLES})
	private YesNo sourceOfInfectionIdentified;
	@Diseases({
			Disease.MEASLES})
	private YesNo measlesCommunityInvestigation;
	@Diseases({
			Disease.MEASLES})
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String measlesInvestigationResults;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private YesNoUnknown motherGivenProtectiveDoseTT;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private Date motherGivenProtectiveDoseTTDate;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	private YesNoUnknown supplementalImmunization;
	@Diseases({
			Disease.NEONATAL_TETANUS})
	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String supplementalImmunizationDetails;
	@Diseases({
			Disease.CSM})
	private Date arrivalAtRegionalPublicHealthOfficeDate;
	@Diseases({
			Disease.CSM})
	private Date arrivalAtNationalLevelDate;
	@Diseases({
			Disease.CSM})
	private VaccineType vaccineType;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown menac;
	@Diseases({
			Disease.CSM})
	private Date menacDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown menacw;
	@Diseases({
			Disease.CSM})
	private Date menacwDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown menacwy;
	@Diseases({
			Disease.CSM})
	private Date menacwyDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown menaConjunate;
	@Diseases({
			Disease.CSM})
	private Date menaConjunateDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown pcvi3I;
	@Diseases({
			Disease.CSM})
	private Date pcvi3IDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown pcvi3_2;
	@Diseases({
			Disease.CSM})
	private Date pcvi3_2Date;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown pcv13_3;
	@Diseases({
			Disease.CSM})
	private Date pcv13_3Date;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown hibI;
	@Diseases({
			Disease.CSM})
	private Date hibIDate;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown hib2;
	@Diseases({
			Disease.CSM})
	private Date hib2Date;
	@Diseases({
			Disease.CSM})
	private YesNoUnknown hib3;
	@Diseases({
			Disease.CSM})
	private Date hib3Date;
	@Diseases({
			Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String healthWorkerCompletingForm;

	private FinalClassification finalClassification;
	private Vdpv vdpvClassification;
	private SeroType seroClassification;
	@Diseases({
			Disease.CONGENITAL_RUBELLA })
	private ClassificationByOrigin classificationByOrigin;
	private YesNoUnknown immunocompromisedStatusSuspected;
	private Date dateRegionReceivesLabResults;
	private Date dateLabResultsSentHealthFacilityRegion;
	private Date dateLabResultsReceivedAtHealthFacility;
	@Diseases({
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
			Disease.CSM})
	private Date dateFormSentToRegion;

	@Diseases({
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
			Disease.CSM})
	private String personFullName;
	@Diseases({
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
			Disease.CSM})
	private String personTelephone;
	@Diseases({
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String personDesignation;
	@Diseases({
			Disease.CSM})
	private Date districtNotificationDate;
	@Diseases({
			Disease.CSM})
	private Date dateFormSentToDistrict;
	@Diseases({
			Disease.CSM})
	private Date dateFormReceivedAtDistrict;
	@Diseases({
			Disease.CSM})
	private Date dateFormReceivedAtRegion;
	@Diseases({
			Disease.CSM})
	private Date dateFormSentToNational;
	@Diseases({
			Disease.CSM})
	private Date dateFormReceivedAtNational;

	@Diseases({
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private RegionReferenceDto regionLabResultsReceived;

	@Valid
	private ResponseDto response;

	@Diseases(value = {Disease.AFP})
	private Date dateCapturedResultsReceivedAtNationalEPIOffice;
	@Diseases(value = {Disease.AFP})
	private Date dateDifferentiationReceivedEpi;

	public static CaseDataDto build(PersonReferenceDto person, Disease disease) {
		return build(person, disease, HealthConditionsDto.build());
	}

	public static CaseDataDto build(PersonReferenceDto person, Disease disease, HealthConditionsDto healthConditions) {
		CaseDataDto caze = new CaseDataDto();
		caze.setUuid(DataHelper.createUuid());
		caze.setPerson(person);
		caze.setHospitalization(HospitalizationDto.build());
		caze.setAfpImmunization(AfpImmunizationDto.build());
		caze.setEpiData(EpiDataDto.build());
		caze.setSymptoms(SymptomsDto.build());
		caze.setTherapy(TherapyDto.build());
		caze.setHealthConditions(healthConditions);
		caze.setClinicalCourse(ClinicalCourseDto.build());
		caze.setResponse(ResponseDto.build());
		caze.setMaternalHistory(MaternalHistoryDto.build());
		caze.setPortHealthInfo(PortHealthInfoDto.build());
		caze.setDisease(disease);
		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze.setCaseClassification(CaseClassification.SUSPECT);
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		caze.setNotifiedBy(NotifiedBy.OTHER);
		// TODO This is a workaround for transferring the followup comment while converting a contact to a case. This can be removed if the followup for cases is implemented in the mobile app
		caze.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
		return caze;
	}

	/**
	 * 
	 * @param contact
	 *            leads to the returned case
	 * @return dto that contains the contacts information. If the contact has one exposure, this marked as the probable infection
	 *         environment.
	 */
	public static CaseDataDto buildFromContact(ContactDto contact) {

		HealthConditionsDto healthConditionsClone = null;
		try {
			healthConditionsClone = (HealthConditionsDto) contact.getHealthConditions().clone();
			healthConditionsClone.setUuid(DataHelper.createUuid());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		CaseDataDto cazeData = CaseDataDto.build(contact.getPerson(), contact.getDisease(), healthConditionsClone);
		copyEpiData(contact, cazeData);
		List<ExposureDto> exposures = cazeData.getEpiData().getExposures();
		if (exposures.size() == 1) {
			exposures.get(0).setProbableInfectionEnvironment(true);
			exposures.get(0).setContactToCase(contact.toReference());
		}
		return cazeData;
	}

	public static CaseDataDto buildFromUnrelatedContact(ContactDto contact, Disease disease) {

		CaseDataDto cazeData = CaseDataDto.build(contact.getPerson(), disease);
		copyEpiData(contact, cazeData);
		return cazeData;
	}

	private static void copyEpiData(ContactDto contact, CaseDataDto cazeData) {
		try {
			EpiDataDto epiDataClone = contact.getEpiData().clone();
			epiDataClone.setUuid(cazeData.getEpiData().getUuid());
			for (ActivityAsCaseDto activityAsCase : epiDataClone.getActivitiesAsCase()) {
				activityAsCase.setUuid(DataHelper.createUuid());
				activityAsCase.getLocation().setUuid(DataHelper.createUuid());
			}
			for (ExposureDto exposure : epiDataClone.getExposures()) {
				exposure.setUuid(DataHelper.createUuid());
				exposure.getLocation().setUuid(DataHelper.createUuid());
			}
			cazeData.setEpiData(epiDataClone);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		cazeData.setFollowUpComment(contact.getFollowUpComment());
	}

	public static CaseDataDto buildFromEventParticipant(EventParticipantDto eventParticipant, PersonDto person, Disease eventDisease) {

		CaseDataDto caseData = CaseDataDto.build(eventParticipant.getPerson().toReference(), eventDisease);

		updateCaseOutcome(caseData, person, eventDisease, eventParticipant.getCreationDate());

		return caseData;
	}

	public static CaseDataDto buildFromTravelEntry(TravelEntryDto travelEntry, PersonDto person) {

		CaseDataDto caseData = CaseDataDto.build(person.toReference(), travelEntry.getDisease());

		caseData.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caseData.setDiseaseVariant(travelEntry.getDiseaseVariant());
		caseData.setDiseaseDetails(travelEntry.getDiseaseVariantDetails());
		caseData.setResponsibleRegion(travelEntry.getResponsibleRegion());
		caseData.setResponsibleDistrict(travelEntry.getResponsibleDistrict());
		caseData.setResponsibleCommunity(travelEntry.getResponsibleCommunity());
		caseData.setPointOfEntry(travelEntry.getPointOfEntry());
		caseData.setPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
		caseData.setReportDate(travelEntry.getReportDate());

		updateCaseOutcome(caseData, person, travelEntry.getDisease(), travelEntry.getReportDate());

		return caseData;
	}

	private static void updateCaseOutcome(CaseDataDto caseData, PersonDto person, Disease disease, Date creationDate) {
		if (person.getPresentCondition() != null
			&& person.getPresentCondition().isDeceased()
			&& disease == person.getCauseOfDeathDisease()
			&& person.getDeathDate() != null
			&& Math.abs(person.getDeathDate().getTime() - creationDate.getTime()) <= MILLISECONDS_30_DAYS) {
			caseData.setOutcome(CaseOutcome.DECEASED);
			caseData.setOutcomeDate(person.getDeathDate());
		}
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), getPerson().getFirstName(), getPerson().getLastName());
	}

	/**
	 * Returns true if the case is an original point of entry case and has not yet
	 * been assigned a health facility.
	 */
	public boolean checkIsUnreferredPortHealthCase() {
		return caseOrigin == CaseOrigin.POINT_OF_ENTRY && healthFacility == null;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto personDto) {
		this.person = personDto;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public CaseIdentificationSource getCaseIdentificationSource() {
		return caseIdentificationSource;
	}

	public void setCaseIdentificationSource(CaseIdentificationSource caseIdentificationSource) {
		this.caseIdentificationSource = caseIdentificationSource;
	}

	public ScreeningType getScreeningType() {
		return screeningType;
	}

	public void setScreeningType(ScreeningType screeningType) {
		this.screeningType = screeningType;
	}

	public UserReferenceDto getClassificationUser() {
		return classificationUser;
	}

	public void setClassificationUser(UserReferenceDto classificationUser) {
		this.classificationUser = classificationUser;
	}

	public Date getClassificationDate() {
		return classificationDate;
	}

	public void setClassificationDate(Date classificationDate) {
		this.classificationDate = classificationDate;
	}

	public String getClassificationComment() {
		return classificationComment;
	}

	public void setClassificationComment(String classificationComment) {
		this.classificationComment = classificationComment;
	}

	public String getFinalClassificationDiscarded() {
		return finalClassificationDiscarded;
	}

	public void setFinalClassificationDiscarded(String finalClassificationDiscarded) {
		this.finalClassificationDiscarded = finalClassificationDiscarded;
	}

	public YesNoUnknown getClinicalConfirmation() {
		return clinicalConfirmation;
	}

	public void setClinicalConfirmation(YesNoUnknown clinicalConfirmation) {
		this.clinicalConfirmation = clinicalConfirmation;
	}

	public YesNoUnknown getEpidemiologicalConfirmation() {
		return epidemiologicalConfirmation;
	}

	public void setEpidemiologicalConfirmation(YesNoUnknown epidemiologicalConfirmation) {
		this.epidemiologicalConfirmation = epidemiologicalConfirmation;
	}

	public YesNoUnknown getLaboratoryDiagnosticConfirmation() {
		return laboratoryDiagnosticConfirmation;
	}

	public void setLaboratoryDiagnosticConfirmation(YesNoUnknown laboratoryDiagnosticConfirmation) {
		this.laboratoryDiagnosticConfirmation = laboratoryDiagnosticConfirmation;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public PlagueType getPlagueType() {
		return plagueType;
	}

	public void setPlagueType(PlagueType plagueType) {
		this.plagueType = plagueType;
	}

	public DengueFeverType getDengueFeverType() {
		return dengueFeverType;
	}

	public void setDengueFeverType(DengueFeverType dengueFeverType) {
		this.dengueFeverType = dengueFeverType;
	}

	public RabiesType getRabiesType() {
		return rabiesType;
	}

	public void setRabiesType(RabiesType rabiesType) {
		this.rabiesType = rabiesType;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public Date getInvestigatedDate() {
		return investigatedDate;
	}

	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	public Date getRegionLevelDate() {
		return regionLevelDate;
	}

	public void setRegionLevelDate(Date regionLevelDate) {
		this.regionLevelDate = regionLevelDate;
	}

	public Date getNationalLevelDate() {
		return nationalLevelDate;
	}

	public void setNationalLevelDate(Date nationalLevelDate) {
		this.nationalLevelDate = nationalLevelDate;
	}

	public Date getDistrictLevelDate() {
		return districtLevelDate;
	}

	public void setDistrictLevelDate(Date districtLevelDate) {
		this.districtLevelDate = districtLevelDate;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public String getClinicianName() {
		return clinicianName;
	}

	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	public String getClinicianPhone() {
		return clinicianPhone;
	}

	public void setClinicianPhone(String clinicianPhone) {
		this.clinicianPhone = clinicianPhone;
	}

	public String getClinicianEmail() {
		return clinicianEmail;
	}

	public void setClinicianEmail(String clinicianEmail) {
		this.clinicianEmail = clinicianEmail;
	}

	@Deprecated
	public UserReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	@Deprecated
	public void setCaseOfficer(UserReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public RegionReferenceDto getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(RegionReferenceDto responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public DistrictReferenceDto getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(DistrictReferenceDto responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public CommunityReferenceDto getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(CommunityReferenceDto responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public HospitalizationDto getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(HospitalizationDto hospitalization) {
		this.hospitalization = hospitalization;
	}
	public AfpImmunizationDto getAfpImmunization() {
		return afpImmunization;
	}
	public void setAfpImmunization(AfpImmunizationDto afpImmunization) {
		this.afpImmunization = afpImmunization;
	}

	public EpiDataDto getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiDataDto epiData) {
		this.epiData = epiData;
	}

	public TherapyDto getTherapy() {
		return therapy;
	}

	public void setTherapy(TherapyDto therapy) {
		this.therapy = therapy;
	}

	public ClinicalCourseDto getClinicalCourse() {
		return clinicalCourse;
	}

	public void setClinicalCourse(ClinicalCourseDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public MaternalHistoryDto getMaternalHistory() {
		return maternalHistory;
	}

	public void setMaternalHistory(MaternalHistoryDto maternalHistory) {
		this.maternalHistory = maternalHistory;
	}

	public ResponseDto getResponse() {
		return response;
	}

	public void setResponse(ResponseDto response) {
		this.response = response;
	}


	public PortHealthInfoDto getPortHealthInfo() {
		return portHealthInfo;
	}

	public void setPortHealthInfo(PortHealthInfoDto portHealthInfo) {
		this.portHealthInfo = portHealthInfo;
	}

	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public VaccinationStatus getVaccinated() {
		return vaccinated;
	}

	public void setVaccinated(VaccinationStatus vaccinated) {
		this.vaccinated = vaccinated;
	}

	public RoutineVaccinationType getRoutineVaccinationType() {
		return routineVaccinationType;
	}

	public void setRoutineVaccinationType(RoutineVaccinationType routineVaccinationType) {
		this.routineVaccinationType = routineVaccinationType;
	}

	public VaccinationRecordType getVaccinationRecordType() {
		return vaccinationRecordType;
	}

	public void setVaccinationRecordType(VaccinationRecordType vaccinationRecordType) {
		this.vaccinationRecordType = vaccinationRecordType;
	}

	public Integer getNumberOfVaccinationDoses() {
		return numberOfVaccinationDoses;
	}

	public void setNumberOfVaccinationDoses(Integer numberOfVaccinationDoses) {
		this.numberOfVaccinationDoses = numberOfVaccinationDoses;
	}

	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public YesNoUnknown getSmallpoxVaccinationScar() {
		return smallpoxVaccinationScar;
	}

	public void setSmallpoxVaccinationScar(YesNoUnknown smallpoxVaccinationScar) {
		this.smallpoxVaccinationScar = smallpoxVaccinationScar;
	}

	public YesNoUnknown getSmallpoxVaccinationReceived() {
		return smallpoxVaccinationReceived;
	}

	public void setSmallpoxVaccinationReceived(YesNoUnknown smallpoxVaccinationReceived) {
		this.smallpoxVaccinationReceived = smallpoxVaccinationReceived;
	}

	public Date getSmallpoxLastVaccinationDate() {
		return smallpoxLastVaccinationDate;
	}

	public void setSmallpoxLastVaccinationDate(Date smallpoxLastVaccinationDate) {
		this.smallpoxLastVaccinationDate = smallpoxLastVaccinationDate;
	}

	public YesNoUnknown getAtLeastOneYellowFeverDose() {
		return atLeastOneYellowFeverDose;
	}

	public void setAtLeastOneYellowFeverDose(YesNoUnknown atLeastOneYellowFeverDose) {
		this.atLeastOneYellowFeverDose = atLeastOneYellowFeverDose;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
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

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getOutcomeDate() {
		return outcomeDate;
	}

	public void setOutcomeDate(Date outcomeDate) {
		this.outcomeDate = outcomeDate;
	}

	public YesNoUnknown getSequelae() {
		return sequelae;
	}

	public void setSequelae(YesNoUnknown sequelae) {
		this.sequelae = sequelae;
	}

	public String getSequelaeDetails() {
		return sequelaeDetails;
	}

	public void setSequelaeDetails(String sequelaeDetails) {
		this.sequelaeDetails = sequelaeDetails;
	}

	public HospitalWardType getNotifyingClinic() {
		return notifyingClinic;
	}

	public void setNotifyingClinic(HospitalWardType notifyingClinic) {
		this.notifyingClinic = notifyingClinic;
	}

	public String getNotifyingClinicDetails() {
		return notifyingClinicDetails;
	}

	public void setNotifyingClinicDetails(String notifyingClinicDetails) {
		this.notifyingClinicDetails = notifyingClinicDetails;
	}

	@ImportIgnore
	public String getCreationVersion() {
		return creationVersion;
	}

	public void setCreationVersion(String creationVersion) {
		this.creationVersion = creationVersion;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public void setCaseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public String getPointOfEntryDetails() {
		return pointOfEntryDetails;
	}

	public void setPointOfEntryDetails(String pointOfEntryDetails) {
		this.pointOfEntryDetails = pointOfEntryDetails;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public String getCaseReferenceNumber() {
		return caseReferenceNumber;
	}

	public void setCaseReferenceNumber(String caseReferenceNumber) {
		this.caseReferenceNumber = caseReferenceNumber;
	}

	public boolean isSharedToCountry() {
		return sharedToCountry;
	}

	public void setSharedToCountry(boolean sharedToCountry) {
		this.sharedToCountry = sharedToCountry;
	}

	public boolean isNosocomialOutbreak() {
		return nosocomialOutbreak;
	}

	public void setNosocomialOutbreak(boolean nosocomialOutbreak) {
		this.nosocomialOutbreak = nosocomialOutbreak;
	}

	public InfectionSetting getInfectionSetting() {
		return infectionSetting;
	}

	public void setInfectionSetting(InfectionSetting infectionSetting) {
		this.infectionSetting = infectionSetting;
	}

	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
	}

	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}

	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	public void setQuarantineExtended(boolean quarantineExtended) {
		this.quarantineExtended = quarantineExtended;
	}

	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	public void setQuarantineReduced(boolean quarantineReduced) {
		this.quarantineReduced = quarantineReduced;
	}

	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	public void setQuarantineOfficialOrderSent(boolean quarantineOfficialOrderSent) {
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
	}

	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	public void setQuarantineOfficialOrderSentDate(Date quarantineOfficialOrderSentDate) {
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
	}

	public YesNoUnknown getPostpartum() {
		return postpartum;
	}

	public void setPostpartum(YesNoUnknown postpartum) {
		this.postpartum = postpartum;
	}

	public Trimester getTrimester() {
		return trimester;
	}

	public void setTrimester(Trimester trimester) {
		this.trimester = trimester;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public String getFollowUpComment() {
		return followUpComment;
	}

	public void setFollowUpComment(String followUpComment) {
		this.followUpComment = followUpComment;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public boolean isOverwriteFollowUpUntil() {
		return overwriteFollowUpUntil;
	}

	public void setOverwriteFollowUpUntil(boolean overwriteFollowUpUntil) {
		this.overwriteFollowUpUntil = overwriteFollowUpUntil;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public Integer getCaseIdIsm() {
		return caseIdIsm;
	}

	public void setCaseIdIsm(Integer caseIdIsm) {
		this.caseIdIsm = caseIdIsm;
	}

	public ContactTracingContactType getContactTracingFirstContactType() {
		return contactTracingFirstContactType;
	}

	public void setContactTracingFirstContactType(ContactTracingContactType contactTracingFirstContactType) {
		this.contactTracingFirstContactType = contactTracingFirstContactType;
	}

	public Date getContactTracingFirstContactDate() {
		return contactTracingFirstContactDate;
	}

	public void setContactTracingFirstContactDate(Date contactTracingFirstContactDate) {
		this.contactTracingFirstContactDate = contactTracingFirstContactDate;
	}

	public YesNoUnknown getWasInQuarantineBeforeIsolation() {
		return wasInQuarantineBeforeIsolation;
	}

	public void setWasInQuarantineBeforeIsolation(YesNoUnknown wasInQuarantineBeforeIsolation) {
		this.wasInQuarantineBeforeIsolation = wasInQuarantineBeforeIsolation;
	}

	public QuarantineReason getQuarantineReasonBeforeIsolation() {
		return quarantineReasonBeforeIsolation;
	}

	public void setQuarantineReasonBeforeIsolation(QuarantineReason quarantineReasonBeforeIsolation) {
		this.quarantineReasonBeforeIsolation = quarantineReasonBeforeIsolation;
	}

	public String getQuarantineReasonBeforeIsolationDetails() {
		return quarantineReasonBeforeIsolationDetails;
	}

	public void setQuarantineReasonBeforeIsolationDetails(String quarantineReasonBeforeIsolationDetails) {
		this.quarantineReasonBeforeIsolationDetails = quarantineReasonBeforeIsolationDetails;
	}

	public EndOfIsolationReason getEndOfIsolationReason() {
		return endOfIsolationReason;
	}

	public void setEndOfIsolationReason(EndOfIsolationReason endOfIsolationReason) {
		this.endOfIsolationReason = endOfIsolationReason;
	}

	public String getEndOfIsolationReasonDetails() {
		return endOfIsolationReasonDetails;
	}

	public void setEndOfIsolationReasonDetails(String endOfIsolationReasonDetails) {
		this.endOfIsolationReasonDetails = endOfIsolationReasonDetails;
	}

	public YesNoUnknown getProhibitionToWork() {
		return prohibitionToWork;
	}

	public void setProhibitionToWork(YesNoUnknown prohibitionToWork) {
		this.prohibitionToWork = prohibitionToWork;
	}

	public Date getProhibitionToWorkFrom() {
		return prohibitionToWorkFrom;
	}

	public void setProhibitionToWorkFrom(Date prohibitionToWorkFrom) {
		this.prohibitionToWorkFrom = prohibitionToWorkFrom;
	}

	public Date getProhibitionToWorkUntil() {
		return prohibitionToWorkUntil;
	}

	public void setProhibitionToWorkUntil(Date prohibitionToWorkUntil) {
		this.prohibitionToWorkUntil = prohibitionToWorkUntil;
	}

	public YesNoUnknown getReInfection() {
		return reInfection;
	}

	public void setReInfection(YesNoUnknown reInfection) {
		this.reInfection = reInfection;
	}

	public Date getPreviousInfectionDate() {
		return previousInfectionDate;
	}

	public void setPreviousInfectionDate(Date previousInfectionDate) {
		this.previousInfectionDate = previousInfectionDate;
	}

	@ImportIgnore
	public ReinfectionStatus getReinfectionStatus() {
		return reinfectionStatus;
	}

	public void setReinfectionStatus(ReinfectionStatus reinfectionStatus) {
		this.reinfectionStatus = reinfectionStatus;
	}

	@ImportIgnore
	public Map<ReinfectionDetail, Boolean> getReinfectionDetails() {
		return reinfectionDetails;
	}

	public void setReinfectionDetails(Map<ReinfectionDetail, Boolean> reinfectionDetails) {
		this.reinfectionDetails = reinfectionDetails;
	}

	public YesNoUnknown getBloodOrganOrTissueDonated() {
		return bloodOrganOrTissueDonated;
	}

	public void setBloodOrganOrTissueDonated(YesNoUnknown bloodOrganOrTissueDonated) {
		this.bloodOrganOrTissueDonated = bloodOrganOrTissueDonated;
	}

	public boolean isNotACaseReasonNegativeTest() {
		return notACaseReasonNegativeTest;
	}

	public void setNotACaseReasonNegativeTest(boolean notACaseReasonNegativeTest) {
		this.notACaseReasonNegativeTest = notACaseReasonNegativeTest;
	}

	public boolean isNotACaseReasonPhysicianInformation() {
		return notACaseReasonPhysicianInformation;
	}

	public void setNotACaseReasonPhysicianInformation(boolean notACaseReasonPhysicianInformation) {
		this.notACaseReasonPhysicianInformation = notACaseReasonPhysicianInformation;
	}

	public boolean isNotACaseReasonDifferentPathogen() {
		return notACaseReasonDifferentPathogen;
	}

	public void setNotACaseReasonDifferentPathogen(boolean notACaseReasonDifferentPathogen) {
		this.notACaseReasonDifferentPathogen = notACaseReasonDifferentPathogen;
	}

	public boolean isNotACaseReasonOther() {
		return notACaseReasonOther;
	}

	public void setNotACaseReasonOther(boolean notACaseReasonOther) {
		this.notACaseReasonOther = notACaseReasonOther;
	}

	public String getNotACaseReasonDetails() {
		return notACaseReasonDetails;
	}

	public void setNotACaseReasonDetails(String notACaseReasonDetails) {
		this.notACaseReasonDetails = notACaseReasonDetails;
	}

	public Date getFollowUpStatusChangeDate() {
		return followUpStatusChangeDate;
	}

	public void setFollowUpStatusChangeDate(Date followUpStatusChangeDate) {
		this.followUpStatusChangeDate = followUpStatusChangeDate;
	}

	public UserReferenceDto getFollowUpStatusChangeUser() {
		return followUpStatusChangeUser;
	}

	public void setFollowUpStatusChangeUser(UserReferenceDto followUpStatusChangeUser) {
		this.followUpStatusChangeUser = followUpStatusChangeUser;
	}

	public boolean hasResponsibleJurisdiction() {
		return responsibleRegion != null || responsibleDistrict != null || responsibleCommunity != null;
	}

	public boolean isDontShareWithReportingTool() {
		return dontShareWithReportingTool;
	}

	public void setDontShareWithReportingTool(boolean dontShareWithReportingTool) {
		this.dontShareWithReportingTool = dontShareWithReportingTool;
	}

	public CaseReferenceDefinition getCaseReferenceDefinition() {
		return caseReferenceDefinition;
	}

	public void setCaseReferenceDefinition(CaseReferenceDefinition caseReferenceDefinition) {
		this.caseReferenceDefinition = caseReferenceDefinition;
	}

	public Date getPreviousQuarantineTo() {
		return previousQuarantineTo;
	}

	public void setPreviousQuarantineTo(Date previousQuarantineTo) {
		this.previousQuarantineTo = previousQuarantineTo;
	}

	public String getQuarantineChangeComment() {
		return quarantineChangeComment;
	}

	public void setQuarantineChangeComment(String quarantineChangeComment) {
		this.quarantineChangeComment = quarantineChangeComment;
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

	public Map<String, String> getExternalData() {
		return externalData;
	}

	public void setExternalData(Map<String, String> externalData) {
		this.externalData = externalData;
	}

	public HealthConditionsDto getHealthConditions() {
		return healthConditions;
	}

	public void setHealthConditions(HealthConditionsDto healthConditions) {
		this.healthConditions = healthConditions;
	}

	public NotifierReferenceDto getNotifier() {
		return notifier;
	}

	public void setNotifier(NotifierReferenceDto notifier) {
		this.notifier = notifier;
	}

	public boolean isPostMortem() {
		return postMortem;
	}

	public void setPostMortem(boolean postMortem) {
		this.postMortem = postMortem;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public RadiographyCompatibility getRadiographyCompatibility() {
		return radiographyCompatibility;
	}

	public void setRadiographyCompatibility(RadiographyCompatibility radiographyCompatibility) {
		this.radiographyCompatibility = radiographyCompatibility;
	}

	public String getOtherDiagnosticCriteria() {
		return otherDiagnosticCriteria;
	}

	public void setOtherDiagnosticCriteria(String otherDiagnosticCriteria) {
		this.otherDiagnosticCriteria = otherDiagnosticCriteria;
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
	public NotifiedBy getNotifiedBy() {
		return notifiedBy;
	}

	public void setNotifiedBy(NotifiedBy notifiedBy) {
		this.notifiedBy = notifiedBy;
	}

	public String getNotifiedByDetails() {
		return notifiedByDetails;
	}

	public void setNotifiedByDetails(String notifiedByDetails) {
		this.notifiedByDetails = notifiedByDetails;
	}

	public Date getDateOfNotification() {
		return dateOfNotification;
	}

	public void setDateOfNotification(Date dateOfNotification) {
		this.dateOfNotification = dateOfNotification;
	}

	public Date getDateOfInvestigation() {
		return dateOfInvestigation;
	}

	public void setDateOfInvestigation(Date dateOfInvestigation) {
		this.dateOfInvestigation = dateOfInvestigation;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
	public String getCompoundOwner() {
		return compoundOwner;
	}

	public void setCompoundOwner(String compoundOwner) {
		this.compoundOwner = compoundOwner;
	}
	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public YesNoUnknown getMotherVaccinatedWithTT() {
		return motherVaccinatedWithTT;
	}

	public void setMotherVaccinatedWithTT(YesNoUnknown motherVaccinatedWithTT) {
		this.motherVaccinatedWithTT = motherVaccinatedWithTT;
	}

	public YesNoUnknown getMotherHaveCard() {
		return motherHaveCard;
	}

	public void setMotherHaveCard(YesNoUnknown motherHaveCard) {
		this.motherHaveCard = motherHaveCard;
	}

	public String getMotherNumberOfDoses() {
		return motherNumberOfDoses;
	}

	public void setMotherNumberOfDoses(String motherNumberOfDoses) {
		this.motherNumberOfDoses = motherNumberOfDoses;
	}

	public MotherVaccinationStatus getMotherVaccinationStatus() {
		return motherVaccinationStatus;
	}

	public void setMotherVaccinationStatus(MotherVaccinationStatus motherVaccinationStatus) {
		this.motherVaccinationStatus = motherVaccinationStatus;
	}

	public Date getMotherTTDateOne() {
		return motherTTDateOne;
	}

	public void setMotherTTDateOne(Date motherTTDateOne) {
		this.motherTTDateOne = motherTTDateOne;
	}

	public Date getMotherTTDateTwo() {
		return motherTTDateTwo;
	}

	public void setMotherTTDateTwo(Date motherTTDateTwo) {
		this.motherTTDateTwo = motherTTDateTwo;
	}

	public Date getMotherTTDateThree() {
		return motherTTDateThree;
	}

	public void setMotherTTDateThree(Date motherTTDateThree) {
		this.motherTTDateThree = motherTTDateThree;
	}

	public Date getMotherTTDateFour() {
		return motherTTDateFour;
	}

	public void setMotherTTDateFour(Date motherTTDateFour) {
		this.motherTTDateFour = motherTTDateFour;
	}

	public Date getMotherTTDateFive() {
		return motherTTDateFive;
	}

	public void setMotherTTDateFive(Date motherTTDateFive) {
		this.motherTTDateFive = motherTTDateFive;
	}

	public Date getMotherLastDoseDate() {
		return motherLastDoseDate;
	}
	public void setMotherLastDoseDate(Date motherLastDoseDate) {
		this.motherLastDoseDate = motherLastDoseDate;
	}

	public String getInvestigatorName() {
		return investigatorName;
	}

	public void setInvestigatorName(String investigatorName) {
		this.investigatorName = investigatorName;
	}

	public String getInvestigatorTitle() {
		return investigatorTitle;
	}

	public void setInvestigatorTitle(String investigatorTitle) {
		this.investigatorTitle = investigatorTitle;
	}

	public String getInvestigatorUnit() {
		return investigatorUnit;
	}

	public void setInvestigatorUnit(String investigatorUnit) {
		this.investigatorUnit = investigatorUnit;
	}

	public String getInvestigatorAddress() {
		return investigatorAddress;
	}

	public void setInvestigatorAddress(String investigatorAddress) {
		this.investigatorAddress = investigatorAddress;
	}

	public String getInvestigatorTel() {
		return investigatorTel;
	}

	public void setInvestigatorTel(String investigatorTel) {
		this.investigatorTel = investigatorTel;
	}

	public String getInvestigatorEmail() {
		return investigatorEmail;
	}

	public void setInvestigatorEmail(String investigatorEmail) {
		this.investigatorEmail = investigatorEmail;
	}

	public Date getDateReceivedAtDistrictLevel() {
		return dateReceivedAtDistrictLevel;
	}

	public void setDateReceivedAtDistrictLevel(Date dateReceivedAtDistrictLevel) {
		this.dateReceivedAtDistrictLevel = dateReceivedAtDistrictLevel;
	}

	public YesNo getSourceOfInfectionIdentified() {
		return sourceOfInfectionIdentified;
	}

	public void setSourceOfInfectionIdentified(YesNo sourceOfInfectionIdentified) {
		this.sourceOfInfectionIdentified = sourceOfInfectionIdentified;
	}

	public YesNo getMeaslesCommunityInvestigation() {
		return measlesCommunityInvestigation;
	}

	public void setMeaslesCommunityInvestigation(YesNo measlesCommunityInvestigation) {
		this.measlesCommunityInvestigation = measlesCommunityInvestigation;
	}

	public String getMeaslesInvestigationResults() {
		return measlesInvestigationResults;
	}

	public void setMeaslesInvestigationResults(String measlesInvestigationResults) {
		this.measlesInvestigationResults = measlesInvestigationResults;
	}

	public YesNoUnknown getMotherGivenProtectiveDoseTT() {
		return motherGivenProtectiveDoseTT;
	}

	public void setMotherGivenProtectiveDoseTT(YesNoUnknown motherGivenProtectiveDoseTT) {
		this.motherGivenProtectiveDoseTT = motherGivenProtectiveDoseTT;
	}

	public Date getMotherGivenProtectiveDoseTTDate() {
		return motherGivenProtectiveDoseTTDate;
	}

	public void setMotherGivenProtectiveDoseTTDate(Date motherGivenProtectiveDoseTTDate) {
		this.motherGivenProtectiveDoseTTDate = motherGivenProtectiveDoseTTDate;
	}

	public YesNoUnknown getSupplementalImmunization() {
		return supplementalImmunization;
	}

	public void setSupplementalImmunization(YesNoUnknown supplementalImmunization) {
		this.supplementalImmunization = supplementalImmunization;
	}

	public String getSupplementalImmunizationDetails() {
		return supplementalImmunizationDetails;
	}

	public void setSupplementalImmunizationDetails(String supplementalImmunizationDetails) {
		this.supplementalImmunizationDetails = supplementalImmunizationDetails;
	}

	public FinalClassification getFinalClassification() {
		return finalClassification;
	}

	public void setFinalClassification(FinalClassification finalClassification) {
		this.finalClassification = finalClassification;
	}

	public ClassificationByOrigin getClassificationByOrigin() {
		return classificationByOrigin;
	}

	public void setClassificationByOrigin(ClassificationByOrigin classificationByOrigin) {
		this.classificationByOrigin = classificationByOrigin;
	}

	public Date getArrivalAtRegionalPublicHealthOfficeDate() {
		return arrivalAtRegionalPublicHealthOfficeDate;
	}

	public void setArrivalAtRegionalPublicHealthOfficeDate(Date arrivalAtRegionalPublicHealthOfficeDate) {
		this.arrivalAtRegionalPublicHealthOfficeDate = arrivalAtRegionalPublicHealthOfficeDate;
	}

	public Date getArrivalAtNationalLevelDate() {
		return arrivalAtNationalLevelDate;
	}

	public void setArrivalAtNationalLevelDate(Date arrivalAtNationalLevelDate) {
		this.arrivalAtNationalLevelDate = arrivalAtNationalLevelDate;
	}

	public VaccineType getVaccineType() {
		return vaccineType;
	}

	public void setVaccineType(VaccineType vaccineType) {
		this.vaccineType = vaccineType;
	}

	public YesNoUnknown getMenac() {
		return menac;
	}

	public void setMenac(YesNoUnknown menac) {
		this.menac = menac;
	}

	public Date getMenacDate() {
		return menacDate;
	}

	public void setMenacDate(Date menacDate) {
		this.menacDate = menacDate;
	}

	public YesNoUnknown getMenacw() {
		return menacw;
	}

	public void setMenacw(YesNoUnknown menacw) {
		this.menacw = menacw;
	}

	public Date getMenacwDate() {
		return menacwDate;
	}

	public void setMenacwDate(Date menacwDate) {
		this.menacwDate = menacwDate;
	}

	public YesNoUnknown getMenacwy() {
		return menacwy;
	}

	public void setMenacwy(YesNoUnknown menacwy) {
		this.menacwy = menacwy;
	}

	public Date getMenacwyDate() {
		return menacwyDate;
	}

	public void setMenacwyDate(Date menacwyDate) {
		this.menacwyDate = menacwyDate;
	}

	public YesNoUnknown getMenaConjunate() {
		return menaConjunate;
	}

	public void setMenaConjunate(YesNoUnknown menaConjunate) {
		this.menaConjunate = menaConjunate;
	}

	public Date getMenaConjunateDate() {
		return menaConjunateDate;
	}

	public void setMenaConjunateDate(Date menaConjunateDate) {
		this.menaConjunateDate = menaConjunateDate;
	}

	public YesNoUnknown getPcvi3I() {
		return pcvi3I;
	}

	public void setPcvi3I(YesNoUnknown pcvi3i) {
		this.pcvi3I = pcvi3i;
	}

	public Date getPcvi3IDate() {
		return pcvi3IDate;
	}

	public void setPcvi3IDate(Date pcvi3iDate) {
		this.pcvi3IDate = pcvi3iDate;
	}

	public YesNoUnknown getPcvi3_2() {
		return pcvi3_2;
	}

	public void setPcvi3_2(YesNoUnknown pcvi3_2) {
		this.pcvi3_2 = pcvi3_2;
	}

	public Date getPcvi3_2Date() {
		return pcvi3_2Date;
	}

	public void setPcvi3_2Date(Date pcvi3_2Date) {
		this.pcvi3_2Date = pcvi3_2Date;
	}

	public YesNoUnknown getPcv13_3() {
		return pcv13_3;
	}

	public void setPcv13_3(YesNoUnknown pcv13_3) {
		this.pcv13_3 = pcv13_3;
	}

	public Date getPcv13_3Date() {
		return pcv13_3Date;
	}

	public void setPcv13_3Date(Date pcv13_3Date) {
		this.pcv13_3Date = pcv13_3Date;
	}

	public YesNoUnknown getHibI() {
		return hibI;
	}

	public void setHibI(YesNoUnknown hibI) {
		this.hibI = hibI;
	}

	public Date getHibIDate() {
		return hibIDate;
	}

	public void setHibIDate(Date hibIDate) {
		this.hibIDate = hibIDate;
	}

	public YesNoUnknown getHib2() {
		return hib2;
	}

	public void setHib2(YesNoUnknown hib2) {
		this.hib2 = hib2;
	}

	public Date getHib2Date() {
		return hib2Date;
	}

	public void setHib2Date(Date hib2Date) {
		this.hib2Date = hib2Date;
	}

	public YesNoUnknown getHib3() {
		return hib3;
	}

	public void setHib3(YesNoUnknown hib3) {
		this.hib3 = hib3;
	}

	public Date getHib3Date() {
		return hib3Date;
	}

	public void setHib3Date(Date hib3Date) {
		this.hib3Date = hib3Date;
	}

	public String getHealthWorkerCompletingForm() {
		return healthWorkerCompletingForm;
	}

	public void setHealthWorkerCompletingForm(String healthWorkerCompletingForm) {
		this.healthWorkerCompletingForm = healthWorkerCompletingForm;
	}

	public String getNotifiedByText() {
		return notifiedByText;
	}

	public void setNotifiedByText(String notifiedByText) {
		this.notifiedByText = notifiedByText;
	}

	public Date getDateFormSentToRegion() {
		return dateFormSentToRegion;
	}

	public void setDateFormSentToRegion(Date dateFormSentToRegion) {
		this.dateFormSentToRegion = dateFormSentToRegion;
	}

	public String getPersonFullName() {
		return personFullName;
	}

	public void setPersonFullName(String personFullName) {
		this.personFullName = personFullName;
	}

	public String getPersonTelephone() {
		return personTelephone;
	}

	public void setPersonTelephone(String personTelephone) {
		this.personTelephone = personTelephone;
	}

	public String getPersonDesignation() {
		return personDesignation;
	}

	public void setPersonDesignation(String personDesignation) {
		this.personDesignation = personDesignation;
	}

	public Date getDistrictNotificationDate() {
		return districtNotificationDate;
	}

	public void setDistrictNotificationDate(Date districtNotificationDate) {
		this.districtNotificationDate = districtNotificationDate;
	}

	public Date getDateFormSentToDistrict() {
		return dateFormSentToDistrict;
	}

	public void setDateFormSentToDistrict(Date dateFormSentToDistrict) {
		this.dateFormSentToDistrict = dateFormSentToDistrict;
	}

	public Date getDateFormReceivedAtDistrict() {
		return dateFormReceivedAtDistrict;
	}

	public void setDateFormReceivedAtDistrict(Date dateFormReceivedAtDistrict) {
		this.dateFormReceivedAtDistrict = dateFormReceivedAtDistrict;
	}

	public Date getDateFormReceivedAtRegion() {
		return dateFormReceivedAtRegion;
	}

	public void setDateFormReceivedAtRegion(Date dateFormReceivedAtRegion) {
		this.dateFormReceivedAtRegion = dateFormReceivedAtRegion;
	}

	public Date getDateFormSentToNational() {
		return dateFormSentToNational;
	}

	public void setDateFormSentToNational(Date dateFormSentToNational) {
		this.dateFormSentToNational = dateFormSentToNational;
	}

	public Date getDateFormReceivedAtNational() {
		return dateFormReceivedAtNational;
	}

	public void setDateFormReceivedAtNational(Date dateFormReceivedAtNational) {
		this.dateFormReceivedAtNational = dateFormReceivedAtNational;
	}

	public YesNoUnknown getImmunocompromisedStatusSuspected() {
		return immunocompromisedStatusSuspected;
	}

	public void setImmunocompromisedStatusSuspected(YesNoUnknown immunocompromisedStatusSuspected) {
		this.immunocompromisedStatusSuspected = immunocompromisedStatusSuspected;
	}
	public Date getDateRegionReceivesLabResults() {
		return dateRegionReceivesLabResults;
	}

	public void setDateRegionReceivesLabResults(Date dateRegionReceivesLabResults) {
		this.dateRegionReceivesLabResults = dateRegionReceivesLabResults;
	}

	public Date getDateLabResultsSentHealthFacilityRegion() {
		return dateLabResultsSentHealthFacilityRegion;
	}

	public void setDateLabResultsSentHealthFacilityRegion(Date dateLabResultsSentHealthFacilityRegion) {
		this.dateLabResultsSentHealthFacilityRegion = dateLabResultsSentHealthFacilityRegion;
	}

	public Date getDateLabResultsReceivedAtHealthFacility() {
		return dateLabResultsReceivedAtHealthFacility;
	}

	public void setDateLabResultsReceivedAtHealthFacility(Date dateLabResultsReceivedAtHealthFacility) {
		this.dateLabResultsReceivedAtHealthFacility = dateLabResultsReceivedAtHealthFacility;
	}

	public RegionReferenceDto getRegionLabResultsReceived() {
		return regionLabResultsReceived;
	}

	public void setRegionLabResultsReceived(RegionReferenceDto regionLabResultsReceived) {
		this.regionLabResultsReceived = regionLabResultsReceived;
	}

	public Vdpv getVdpvClassification() {
		return vdpvClassification;
	}

	public void setVdpvClassification(Vdpv vdpvClassification) {
		this.vdpvClassification = vdpvClassification;
	}

	public SeroType getSeroClassification() {
		return seroClassification;
	}

	public void setSeroClassification(SeroType seroClassification) {
		this.seroClassification = seroClassification;
	}

	public Date getDateDifferentiationReceivedEpi() {
		return dateDifferentiationReceivedEpi;
	}

	public void setDateDifferentiationReceivedEpi(Date dateDifferentiationReceivedEpi) {
		this.dateDifferentiationReceivedEpi = dateDifferentiationReceivedEpi;
	}

	public Date getDateCapturedResultsReceivedAtNationalEPIOffice() {
		return dateCapturedResultsReceivedAtNationalEPIOffice;
	}

	public void setDateCapturedResultsReceivedAtNationalEPIOffice(Date dateCapturedResultsReceivedAtNationalEPIOffice) {
		this.dateCapturedResultsReceivedAtNationalEPIOffice = dateCapturedResultsReceivedAtNationalEPIOffice;
	}

    @JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + (StringUtils.isNotBlank(this.getExternalID()) ? " - " + this.getExternalID() : StringUtils.EMPTY);
	}
}

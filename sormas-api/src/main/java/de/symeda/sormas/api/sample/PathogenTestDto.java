/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.*;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.SAMPLES_LAB)
public class PathogenTestDto extends PseudonymizableDto {

	private static final long serialVersionUID = -5213210080802372054L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 3391;

	public static final String I18N_PREFIX = "PathogenTest";

	public static final String SAMPLE = "sample";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TESTED_DISEASE_VARIANT = "testedDiseaseVariant";
	public static final String TESTED_DISEASE_VARIANT_DETAILS = "testedDiseaseVariantDetails";
	public static final String TESTED_PATHOGEN = "testedPathogen";
	public static final String TESTED_PATHOGEN_DETAILS = "testedPathogenDetails";
	public static final String TYPING_ID = "typingId";
	public static final String TEST_TYPE = "testType";
	public static final String PCR_TEST_SPECIFICATION = "pcrTestSpecification";
	public static final String TESTED_DISEASE_DETAILS = "testedDiseaseDetails";
	public static final String TEST_TYPE_TEXT = "testTypeText";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String VIRUS_DETECTION_GENOTYPE = "virusDetectionGenotype";
	public static final String VIRUS_ISOLATED = "virusIsolated";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	public static final String FOUR_FOLD_INCREASE_ANTIBODY_TITER = "fourFoldIncreaseAntibodyTiter";
	public static final String SEROTYPE = "serotype";
	public static final String CQ_VALUE = "cqValue";
	public static final String CT_VALUE_E = "ctValueE";
	public static final String CT_VALUE_N = "ctValueN";
	public static final String CT_VALUE_RDRP = "ctValueRdrp";
	public static final String CT_VALUE_S = "ctValueS";
	public static final String CT_VALUE_ORF_1 = "ctValueOrf1";
	public static final String CT_VALUE_RDRP_S = "ctValueRdrpS";
	public static final String REPORT_DATE = "reportDate";
	public static final String VIA_LIMS = "viaLims";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EXTERNAL_ORDER_ID = "externalOrderId";
	public static final String PRELIMINARY = "preliminary";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";
	public static final String ENVIRONMENT_SAMPLE = "environmentSample";
	public static final String RIFAMPICIN_RESISTANT = "rifampicinResistant";
	public static final String ISONIAZID_RESISTANT = "isoniazidResistant";
	public static final String SPECIE = "specie";
	public static final String PATTERN_PROFILE = "patternProfile";
	public static final String STRAIN_CALL_STATUS = "strainCallStatus";
	public static final String TEST_SCALE = "testScale";
	public static final String DRUG_SUSCEPTIBILITY = "drugSusceptibility";
	public static final String SEROTYPING_METHOD = "seroTypingMethod";
	public static final String SERO_TYPING_METHOD_TEXT = "seroTypingMethodText";
	public static final String SERO_GROUP_SPECIFICATION = "seroGroupSpecification";
	public static final String SERO_GROUP_SPECIFICATION_TEXT = "seroGroupSpecificationText";
	public static final String DATE_RESULTS_SENT_TO_DISTRICT = "dateResultsSentToDistrict";
	public static final String DATE_DISTRICT_RECEIVED_LAB_RESULTS = "dateDistrictReceivedLabResults";
	public static final String DATE_RESULTS_SENT_TO_DISEASE_SURVEILLANCE = "dateResultsSentToDiseaseSurveillance";
	public static final String DATE_INDIRECT_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE = "dateIndirectResultsReceivedAtNationalEPIOffice";
	public static final String DATE_CAPTURED_RESULTS_RECEIVED_AT_NATIONAL_EPI_OFFICE = "dateCapturedResultsReceivedAtNationalEPIOffice";
	public static final String FINAL_CLASSIFICATION = "finalClassification";
	public static final String COMMUNITY_INVESTIGATION = "communityInvestigation";
	public static final String PERFORM_RUBELLA_TEST = "performRubellaTest";
	public static final String INVESTIGATION_RESULTS = "investigationResults";
	public static final String SOURCE_OF_INFECTION_IDENTIFIED = "sourceOfInfectionIdentified";
	public static final String MACROSCOPIC_EXAMINATION = "macroscopicExamination";
	public static final String CELL_COUNT_NORMAL = "cellCountNormal";
	public static final String CELL_COUNT_ABNORMAL = "cellCountAbnormal";
	public static final String WBC_COUNT_POLYCYTES_PERCENT = "wbcCountPolycytesPercent";
	public static final String WBC_COUNT_MONOCYTES_PERCENT = "wbcCountMonocytesPercent";
	public static final String GRAM_STAIN_RESULT = "gramStainResult";
	public static final String AGGLUTINATION_RESULT = "agglutinationResult";
	public static final String AGGLUTINATION_POSITIVE_RESULTS = "agglutinationPositiveResults";
	public static final String AGGLUTINATION_OTHER_MICROORGANISM = "agglutinationOtherMicroorganism";
	public static final String DATE_RESULTS_SENT_TO_REGION = "dateResultsSentToRegion";
	public static final String OTHER_TESTS_PENDING = "otherTestsPending";
	public static final String OTHER_TESTS_PENDING_SPECIFY = "otherTestsPendingSpecify";
	public static final String DATE_RESULTS_SENT_TO_REFERENCE_LABORATORY = "dateResultsSentToReferenceLaboratory";
	public static final String DATE_FINAL_RESULTS_SENT_TO_REPORTING_HEALTH_FACILITY = "dateFinalResultsSentToReportingHealthFacility";
	public static final String DATE_RESULTS_SENT_TO_EDC_UNIT_EPI = "dateResultsSentToEdcUnitEpi";
	public static final String REFERENCE_LABORATORY = "referenceLaboratory";
	public static final String SELECTED_PATHOGEN_TEST_TYPES = "selectedPathogenTestTypes";
	public static final String CULTURE_FINDINGS = "cultureFindings";
	public static final String PCR_FINDINGS = "pcrFindings";
	public static final String CULTURE_OTHER_GERMS_SPECIFY = "cultureOtherGermsSpecify";
	public static final String PCR_OTHER_GERMS_SPECIFY = "pcrOtherGermsSpecify";
	public static final String CELL_COUNT_LEUCOCYTES_PER_MM3 = "cellCountLeucocytesPerMm3";
	public static final String CSF_GLUCOSE = "csfGlucose";
	public static final String CSF_PROTEIN = "csfProtein";
	public static final String GRAM_STAIN_GPD = "gramStainGpd";
	public static final String GRAM_STAIN_GND = "gramStainGnd";
	public static final String GRAM_STAIN_GPB = "gramStainGpb";
	public static final String GRAM_STAIN_GNB = "gramStainGnb";
	public static final String GRAM_STAIN_OTHER_PATHOGENS = "gramStainOtherPathogens";
	public static final String GRAM_STAIN_OTHER_PATHOGENS_SPECIFY = "gramStainOtherPathogensSpecify";
	public static final String GRAM_STAIN_NO_ORGANISM_SEEN = "gramStainNoOrganismSeen";
	public static final String LATEX_NMA = "latexNmA";
	public static final String LATEX_NMC = "latexNmC";
	public static final String LATEX_NMWY = "latexNmWY";
	public static final String LATEX_NM_B_E_COLI_KI = "latexNmBEcoliKi";
	public static final String LATEX_S_PNEUMONIAE = "latexSPneumoniae";
	public static final String LATEX_HIB = "latexHib";
	public static final String LATEX_STREP_B = "latexStrepB";
	public static final String LATEX_NEGATIVE = "latexNegative";
	public static final String RDT_DIPSTICK_PERFORMED = "rdtDipstickPerformed";
	public static final String RDT_DIPSTICK_RESULTS = "rdtDipstickResults";
	public static final String CEFTRIAXONE_SUSCEPTIBILITY = "ceftriaxoneSusceptibility";
	public static final String AMPICILLIN_SUSCEPTIBILITY = "ampicillinSusceptibility";
	public static final String GENTAMYCIN_SUSCEPTIBILITY = "gentamycinSusceptibility";
	public static final String OXACILLIN_SUSCEPTIBILITY = "oxacillinSusceptibility";
	public static final String CHLORAMPHENICOL_SUSCEPTIBILITY = "chloramphenicolSusceptibility";
	public static final String BENZYL_PENICILLIN_SUSCEPTIBILITY = "benzylPenicillinSusceptibility";
	public static final String OTHER_ANTIMICROBIAL_DRUG_NAME = "otherAntimicrobialDrugName";
	public static final String OTHER_ANTIMICROBIAL_SUSCEPTIBILITY = "otherAntimicrobialSusceptibility";
	public static final String DATE_PCR_PERFORMED = "datePcrPerformed";
	public static final String PCR_TYPE_TEXT = "pcrTypeText";
	public static final String PCR_SEROTYPE = "pcrSerotype";
	public static final String OTHER_TEST_TYPE_SPECIFY = "otherTestTypeSpecify";
	public static final String OTHER_TEST_RESULTS = "otherTestResults";
	public static final String VIRAL_DETECTION = "viralDetection";
	public static final String VIRAL_DETECTION_TEST_TYPE = "viralDetectionTestType";
	public static final String VIRAL_DETECTION_RESULTS = "viralDetectionResults";
	public static final String DATE_LAB_RESULTS_SENT_DIVISION = "dateLabResultsSentDivision";
	public static final String NAME_LAB_TECHNICIAN_SEND_RESULTS = "nameLabTechnicianSendResults";
	public static final String DATE_COMBINED_CELL_CULTURE_RESULTS = "dateCombinedCellCultureResults";
	public static final String DATE_RESULTS_SENT_TO_NATIONAL_EPI = "dateResultsSentToNationalEpi";
	public static final String DATE_SENT_FROM_IC_NATIONAL_REG_LAB = "dateSentFromIcNationalRegLab";
	public static final String DATE_DIFFERENTIATION_SENT_EPI = "dateDifferentiationSentEpi";
	public static final String DATE_ISOLATE_SENT_SEQUENCING = "dateIsolateSentSequencing";
	public static final String DATE_SEQ_RESULTS_SENT_PROGRAM = "dateSeqResultsSentProgram";
	public static final String W1 = "w1";
	public static final String W2 = "w2";
	public static final String W3 = "w3";
	public static final String SL1 = "sl1";
	public static final String SL2 = "sl2";
	public static final String SL3 = "sl3";
	public static final String DISCORDANT_SABIN = "discordantSabin";
	public static final String NPENT = "npent";
	public static final String NEV = "nev";
	public static final String FINAL_CELL_CULTURE_RESULTS = "finalCellCultureResults";
	public static final String DATE_FOLLOWUP_EXAM = "dateFollowupExam";
	public static final String RESIDUAL_ANALYSIS = "residualAnalysis";
	public static final String RESULT_EXAM = "resultExam";
	public static final String DATE_SAMPLE_SENT_TO_REFERENCE_LABORATORY = "dateSampleSentToReferenceLaboratory";


	private Date dateCombinedCellCultureResults;
	private Date dateResultsSentToNationalEpi;
	private Date dateSentFromIcNationalRegLab;
	private Date dateDifferentiationSentEpi;
	private Date dateDifferentiationReceivedEpi;
	private Date dateIsolateSentSequencing;
	private Date dateSeqResultsSentProgram;
	private PosNeg w1;
	private PosNeg w2;
	private PosNeg w3;
	private PosNeg sl1;
	private PosNeg sl2;
	private PosNeg sl3;
	private SabinType discordantSabin;
	private PosNeg npent;
	private PosNeg nev;
	private PathogenTestResultType finalCellCultureResults;
	private Date dateFollowupExam;
	private InjectionSite residualAnalysis;
	private ExamResult resultExam;


	private SampleReferenceDto sample;
	private EnvironmentSampleReferenceDto environmentSample;
	private Disease testedDisease;
	private DiseaseVariant testedDiseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testedDiseaseDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testedDiseaseVariantDetails;
	private Pathogen testedPathogen;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testedPathogenDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String typingId;
	@NotNull(message = Validations.requiredField)
	private PathogenTestType testType;
	private PCRTestSpecification pcrTestSpecification;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testTypeText;
	private Date testDateTime;
	@NotNull(message = Validations.requiredField)
	private FacilityReferenceDto lab;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labDetails;
	@SensitiveData
	private UserReferenceDto labUser;
	@NotNull(message = Validations.requiredField)
	private PathogenTestResultType testResult;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String testResultText;
	@Diseases(value = { Disease.YELLOW_FEVER })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String virusDetectionGenotype;
	@Diseases(value = { Disease.YELLOW_FEVER })
	private Boolean virusIsolated;
	@NotNull(message = Validations.requiredField)
	private Boolean testResultVerified;
	private boolean fourFoldIncreaseAntibodyTiter;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String serotype;
	private Float cqValue;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueE;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueN;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrp;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueS;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueOrf1;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrpS;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private Date reportDate;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean viaLims;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalOrderId;
	private Boolean preliminary;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhysicianCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberFirstName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberLastName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhoneNumber;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberAddress;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPostalCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberCity;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private CountryReferenceDto prescriberCountry;
	private YesNoUnknown rifampicinResistant;
	private YesNoUnknown isoniazidResistant;
	private PathogenSpecie specie;
	private String patternProfile;
	private PathogenStrainCallStatus strainCallStatus;
	private PathogenTestScale testScale;
	private DrugSusceptibilityDto drugSusceptibility;
	@SensitiveData
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Diseases(value = {Disease.INVASIVE_PNEUMOCOCCAL_INFECTION})
	private String seroTypingMethodText;
	@SensitiveData
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Diseases(value = {Disease.INVASIVE_PNEUMOCOCCAL_INFECTION})
	private SerotypingMethod seroTypingMethod;
	@SensitiveData
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION})
	private SeroGroupSpecification seroGroupSpecification;
	@SensitiveData
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION})
	private String seroGroupSpecificationText;
	private Date dateResultsSentToDistrict;
	private Date dateDistrictReceivedLabResults;
	private Date dateResultsSentToDiseaseSurveillance;
	@Diseases(value = {Disease.MEASLES})
	private Date dateIndirectResultsReceivedAtNationalEPIOffice;
	@Diseases(value = {Disease.MEASLES, Disease.AFP})
	private Date dateCapturedResultsReceivedAtNationalEPIOffice;
	private FinalClassification finalClassification;
	private Boolean communityInvestigation;
	private Boolean performRubellaTest;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String investigationResults;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String sourceOfInfectionIdentified;
	@Diseases(value = {Disease.CSM})
	private MacroscopicExamination macroscopicExamination;
	@Diseases(value = {Disease.CSM})
	private Boolean cellCountNormal;
	@Diseases(value = {Disease.CSM})
	private Boolean cellCountAbnormal;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String wbcCountPolycytesPercent;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String wbcCountMonocytesPercent;
	@Diseases(value = {Disease.CSM})
	private GramStainResult gramStainResult;
	@Diseases(value = {Disease.CSM})
	private AgglutinationTestResult agglutinationResult;
	@Diseases(value = {Disease.CSM})
	@SensitiveData
	private AgglutinationPositiveResult agglutinationPositiveResults;
	@Diseases(value = {Disease.CSM})
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String agglutinationOtherMicroorganism;
	@Diseases(value = {Disease.CSM})
	private Date dateResultsSentToRegion;
	@Diseases(value = {Disease.CSM})
	private Boolean otherTestsPending;
	@Diseases(value = {Disease.CSM})
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherTestsPendingSpecify;
	@Diseases(value = {Disease.CSM})
	private Date dateResultsSentToReferenceLaboratory;
	@Diseases(value = {Disease.CSM})
	private Date dateFinalResultsSentToReportingHealthFacility;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GAMBIA)
	private Date dateResultsSentToEdcUnitEpi;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GAMBIA)
	private Date dateSampleSentToReferenceLaboratory;
	@Diseases(value = {Disease.CSM})
	private FacilityReferenceDto referenceLaboratory;
	@Diseases(value = {Disease.CSM})
	private Set<PathogenTestType> selectedPathogenTestTypes;
	@Diseases(value = {Disease.CSM})
	private Set<CulturePcrFinding> cultureFindings;
	@Diseases(value = {Disease.CSM})
	private Set<CulturePcrFinding> pcrFindings;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String cultureOtherGermsSpecify;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String pcrOtherGermsSpecify;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String cellCountLeucocytesPerMm3;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String csfGlucose;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String csfProtein;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainGpd;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainGnd;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainGpb;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainGnb;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainOtherPathogens;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String gramStainOtherPathogensSpecify;
	@Diseases(value = {Disease.CSM})
	private Boolean gramStainNoOrganismSeen;
	@Diseases(value = {Disease.CSM})
	private Boolean latexNmA;
	@Diseases(value = {Disease.CSM})
	private Boolean latexNmC;
	@Diseases(value = {Disease.CSM})
	private Boolean latexNmWY;
	@Diseases(value = {Disease.CSM})
	private Boolean latexNmBEcoliKi;
	@Diseases(value = {Disease.CSM})
	private Boolean latexSPneumoniae;
	@Diseases(value = {Disease.CSM})
	private Boolean latexHib;
	@Diseases(value = {Disease.CSM})
	private Boolean latexStrepB;
	@Diseases(value = {Disease.CSM})
	private Boolean latexNegative;
	@Diseases(value = {Disease.CSM})
	private YesNo rdtDipstickPerformed;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String rdtDipstickResults;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility ceftriaxoneSusceptibility;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility ampicillinSusceptibility;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility gentamycinSusceptibility;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility oxacillinSusceptibility;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility chloramphenicolSusceptibility;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility benzylPenicillinSusceptibility;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherAntimicrobialDrugName;
	@Diseases(value = {Disease.CSM})
	private AntimicrobialSusceptibility otherAntimicrobialSusceptibility;
	@Diseases(value = {Disease.CSM})
	private Date datePcrPerformed;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String pcrTypeText;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String pcrSerotype;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherTestTypeSpecify;
	@Diseases(value = {Disease.CSM})
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String otherTestResults;
	@Diseases(value = {Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private YesNo viralDetection;
	@Diseases(value = {Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private ViralDetectionTestType viralDetectionTestType;
	@Diseases(value = {Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private PathogenTestResultType viralDetectionResults;
	@Diseases(value = {Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private Date dateLabResultsSentDivision;
	@Diseases(value = {Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS})
	private String nameLabTechnicianSendResults;

	public static PathogenTestDto build(SampleDto sample, UserDto currentUser) {

		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setSample(sample.toReference());
		if (sample.getSamplePurpose() == SamplePurpose.INTERNAL) {
			pathogenTest.setTestResultVerified(true);
		}
		pathogenTest.setLab(currentUser.getLaboratory());
		if (pathogenTest.getLab() == null) {
			pathogenTest.setLab(sample.getLab());
			pathogenTest.setLabDetails(sample.getLabDetails());
		}
		pathogenTest.setLabUser(currentUser.toReference());
		pathogenTest.setDrugSusceptibility(DrugSusceptibilityDto.build());
		return pathogenTest;
	}

	public static PathogenTestDto build(SampleReferenceDto sample, UserReferenceDto currentUser) {

		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setSample(sample);
		pathogenTest.setLabUser(currentUser);
		pathogenTest.setDrugSusceptibility(DrugSusceptibilityDto.build());
		return pathogenTest;
	}

	public static PathogenTestDto build(EnvironmentSampleDto environmentSample, UserDto currentUser) {
		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setEnvironmentSample(environmentSample.toReference());
		// Initialize with an empty drug susceptibility to avoid multiple unnecessary conditional addFields ana checks of the drug susceptibility field in the form
		pathogenTest.setDrugSusceptibility(DrugSusceptibilityDto.build());
		pathogenTest.setLab(currentUser.getLaboratory());
		if (pathogenTest.getLab() == null) {
			pathogenTest.setLab(environmentSample.getLaboratory());
			pathogenTest.setLabDetails(environmentSample.getLaboratoryDetails());
		}
		pathogenTest.setLabUser(currentUser.toReference());
		return pathogenTest;
	}

	public static PathogenTestDto build(EnvironmentSampleReferenceDto environmentSample, UserReferenceDto currentUser) {
		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setEnvironmentSample(environmentSample);
		pathogenTest.setLabUser(currentUser);
		return pathogenTest;
	}

	@ImportIgnore
	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	@ImportIgnore
	public EnvironmentSampleReferenceDto getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSampleReferenceDto environmentSample) {
		this.environmentSample = environmentSample;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public DiseaseVariant getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(DiseaseVariant testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
	}

	public String getTestedDiseaseDetails() {
		return testedDiseaseDetails;
	}

	public void setTestedDiseaseDetails(String testedDiseaseDetails) {
		this.testedDiseaseDetails = testedDiseaseDetails;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	public Pathogen getTestedPathogen() {
		return testedPathogen;
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
	}

	public String getTestedPathogenDetails() {
		return testedPathogenDetails;
	}

	public void setTestedPathogenDetails(String testedPathogenDetails) {
		this.testedPathogenDetails = testedPathogenDetails;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public PCRTestSpecification getPcrTestSpecification() {
		return pcrTestSpecification;
	}

	public void setPcrTestSpecification(PCRTestSpecification pcrTestSpecification) {
		this.pcrTestSpecification = pcrTestSpecification;
	}

	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
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

	public UserReferenceDto getLabUser() {
		return labUser;
	}

	public void setLabUser(UserReferenceDto labUser) {
		this.labUser = labUser;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public String getVirusDetectionGenotype() {
		return virusDetectionGenotype;
	}

	public void setVirusDetectionGenotype(String virusDetectionGenotype) {
		this.virusDetectionGenotype = virusDetectionGenotype;
	}

	public Boolean getVirusIsolated() {
		return virusIsolated;
	}

	public void setVirusIsolated(Boolean virusIsolated) {
		this.virusIsolated = virusIsolated;
	}

	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	public PathogenTestReferenceDto toReference() {
		return new PathogenTestReferenceDto(getUuid());
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	@Override
	public String buildCaption() {
		return DateFormatHelper.formatLocalDateTime(testDateTime) + " - " + testType + " (" + testedDisease + "): " + testResult;
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public boolean isViaLims() {
		return viaLims;
	}

	public void setViaLims(boolean viaLims) {
		this.viaLims = viaLims;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
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

	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	public CountryReferenceDto getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(CountryReferenceDto prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	public YesNoUnknown getRifampicinResistant() {
		return rifampicinResistant;
	}

	public void setRifampicinResistant(YesNoUnknown rifampicinResistant) {
		this.rifampicinResistant = rifampicinResistant;
	}

	public YesNoUnknown getIsoniazidResistant() {
		return isoniazidResistant;
	}

	public void setIsoniazidResistant(YesNoUnknown isoniazidResistant) {
		this.isoniazidResistant = isoniazidResistant;
	}

	public PathogenSpecie getSpecie() {
		return specie;
	}

	public void setSpecie(PathogenSpecie specie) {
		this.specie = specie;
	}

	public String getPatternProfile() {
		return patternProfile;
	}

	public void setPatternProfile(String patternProfile) {
		this.patternProfile = patternProfile;
	}

	public PathogenStrainCallStatus getStrainCallStatus() {
		return strainCallStatus;
	}

	public void setStrainCallStatus(PathogenStrainCallStatus strainCallStatus) {
		this.strainCallStatus = strainCallStatus;
	}

	public PathogenTestScale getTestScale() {
		return testScale;
	}

	public void setTestScale(PathogenTestScale testScale) {
		this.testScale = testScale;
	}

	public DrugSusceptibilityDto getDrugSusceptibility() {
		return drugSusceptibility;
	}

	public void setDrugSusceptibility(DrugSusceptibilityDto drugSusceptibility) {
		this.drugSusceptibility = drugSusceptibility;
	}

	public SerotypingMethod getSeroTypingMethod() {
		return seroTypingMethod;
	}

	public void setSeroTypingMethod(SerotypingMethod seroTypingMethod) {
		this.seroTypingMethod = seroTypingMethod;
	}

	public String getSeroTypingMethodText() {
		return seroTypingMethodText;
	}

	public void setSeroTypingMethodText(String seroTypingMethodText) {
		this.seroTypingMethodText = seroTypingMethodText;
	}

	public SeroGroupSpecification getSeroGroupSpecification() {
		return seroGroupSpecification;
	}

	public void setSeroGroupSpecification(SeroGroupSpecification seroGroupSpecification) {
		this.seroGroupSpecification = seroGroupSpecification;
	}

	public String getSeroGroupSpecificationText() {
		return seroGroupSpecificationText;
	}

	public void setSeroGroupSpecificationText(String seroGroupSpecificationText) {
		this.seroGroupSpecificationText = seroGroupSpecificationText;
	}

	public Date getDateResultsSentToDistrict() {
		return dateResultsSentToDistrict;
	}

	public void setDateResultsSentToDistrict(Date dateResultsSentToDistrict) {
		this.dateResultsSentToDistrict = dateResultsSentToDistrict;
	}

	public Date getDateDistrictReceivedLabResults() {
		return dateDistrictReceivedLabResults;
	}

	public void setDateDistrictReceivedLabResults(Date dateDistrictReceivedLabResults) {
		this.dateDistrictReceivedLabResults = dateDistrictReceivedLabResults;
	}

	public Date getDateResultsSentToDiseaseSurveillance() {
		return dateResultsSentToDiseaseSurveillance;
	}

	public void setDateResultsSentToDiseaseSurveillance(Date dateResultsSentToDiseaseSurveillance) {
		this.dateResultsSentToDiseaseSurveillance = dateResultsSentToDiseaseSurveillance;
	}

	public Date getDateIndirectResultsReceivedAtNationalEPIOffice() {
		return dateIndirectResultsReceivedAtNationalEPIOffice;
	}

	public void setDateIndirectResultsReceivedAtNationalEPIOffice(Date dateIndirectResultsReceivedAtNationalEPIOffice) {
		this.dateIndirectResultsReceivedAtNationalEPIOffice = dateIndirectResultsReceivedAtNationalEPIOffice;
	}

	public Date getDateCapturedResultsReceivedAtNationalEPIOffice() {
		return dateCapturedResultsReceivedAtNationalEPIOffice;
	}

	public void setDateCapturedResultsReceivedAtNationalEPIOffice(Date dateCapturedResultsReceivedAtNationalEPIOffice) {
		this.dateCapturedResultsReceivedAtNationalEPIOffice = dateCapturedResultsReceivedAtNationalEPIOffice;
	}

	public FinalClassification getFinalClassification() {
		return finalClassification;
	}

	public void setFinalClassification(FinalClassification finalClassification) {
		this.finalClassification = finalClassification;
	}

	public Boolean getCommunityInvestigation() {
		return communityInvestigation;
	}

	public void setCommunityInvestigation(Boolean communityInvestigation) {
		this.communityInvestigation = communityInvestigation;
	}

	public Boolean getPerformRubellaTest() {
		return performRubellaTest;
	}

	public void setPerformRubellaTest(Boolean performRubellaTest) {
		this.performRubellaTest = performRubellaTest;
	}

	public String getInvestigationResults() {
		return investigationResults;
	}

	public void setInvestigationResults(String investigationResults) {
		this.investigationResults = investigationResults;
	}

	public String getSourceOfInfectionIdentified() {
		return sourceOfInfectionIdentified;
	}

	public void setSourceOfInfectionIdentified(String sourceOfInfectionIdentified) {
		this.sourceOfInfectionIdentified = sourceOfInfectionIdentified;
	}

	public MacroscopicExamination getMacroscopicExamination() {
		return macroscopicExamination;
	}

	public void setMacroscopicExamination(MacroscopicExamination macroscopicExamination) {
		this.macroscopicExamination = macroscopicExamination;
	}

	public Boolean getCellCountNormal() {
		return cellCountNormal;
	}

	public void setCellCountNormal(Boolean cellCountNormal) {
		this.cellCountNormal = cellCountNormal;
	}

	public Boolean getCellCountAbnormal() {
		return cellCountAbnormal;
	}

	public void setCellCountAbnormal(Boolean cellCountAbnormal) {
		this.cellCountAbnormal = cellCountAbnormal;
	}

	public String getWbcCountPolycytesPercent() {
		return wbcCountPolycytesPercent;
	}

	public void setWbcCountPolycytesPercent(String wbcCountPolycytesPercent) {
		this.wbcCountPolycytesPercent = wbcCountPolycytesPercent;
	}

	public String getWbcCountMonocytesPercent() {
		return wbcCountMonocytesPercent;
	}

	public void setWbcCountMonocytesPercent(String wbcCountMonocytesPercent) {
		this.wbcCountMonocytesPercent = wbcCountMonocytesPercent;
	}

	public GramStainResult getGramStainResult() {
		return gramStainResult;
	}

	public void setGramStainResult(GramStainResult gramStainResult) {
		this.gramStainResult = gramStainResult;
	}

	public AgglutinationTestResult getAgglutinationResult() {
		return agglutinationResult;
	}

	public void setAgglutinationResult(AgglutinationTestResult agglutinationResult) {
		this.agglutinationResult = agglutinationResult;
	}

	public AgglutinationPositiveResult getAgglutinationPositiveResults() {
		return agglutinationPositiveResults;
	}

	public void setAgglutinationPositiveResults(AgglutinationPositiveResult agglutinationPositiveResults) {
		this.agglutinationPositiveResults = agglutinationPositiveResults;
	}

	public String getAgglutinationOtherMicroorganism() {
		return agglutinationOtherMicroorganism;
	}

	public void setAgglutinationOtherMicroorganism(String agglutinationOtherMicroorganism) {
		this.agglutinationOtherMicroorganism = agglutinationOtherMicroorganism;
	}

	public Date getDateResultsSentToRegion() {
		return dateResultsSentToRegion;
	}

	public void setDateResultsSentToRegion(Date dateResultsSentToRegion) {
		this.dateResultsSentToRegion = dateResultsSentToRegion;
	}

	public Boolean getOtherTestsPending() {
		return otherTestsPending;
	}

	public void setOtherTestsPending(Boolean otherTestsPending) {
		this.otherTestsPending = otherTestsPending;
	}

	public String getOtherTestsPendingSpecify() {
		return otherTestsPendingSpecify;
	}

	public void setOtherTestsPendingSpecify(String otherTestsPendingSpecify) {
		this.otherTestsPendingSpecify = otherTestsPendingSpecify;
	}

	public Date getDateResultsSentToReferenceLaboratory() {
		return dateResultsSentToReferenceLaboratory;
	}

	public void setDateResultsSentToReferenceLaboratory(Date dateResultsSentToReferenceLaboratory) {
		this.dateResultsSentToReferenceLaboratory = dateResultsSentToReferenceLaboratory;
	}

	public Date getDateFinalResultsSentToReportingHealthFacility() {
		return dateFinalResultsSentToReportingHealthFacility;
	}

	public void setDateFinalResultsSentToReportingHealthFacility(Date dateFinalResultsSentToReportingHealthFacility) {
		this.dateFinalResultsSentToReportingHealthFacility = dateFinalResultsSentToReportingHealthFacility;
	}

	public Date getDateResultsSentToEdcUnitEpi() {
		return dateResultsSentToEdcUnitEpi;
	}

	public void setDateResultsSentToEdcUnitEpi(Date dateResultsSentToEdcUnitEpi) {
		this.dateResultsSentToEdcUnitEpi = dateResultsSentToEdcUnitEpi;
	}

	public Date getDateSampleSentToReferenceLaboratory() {
		return dateSampleSentToReferenceLaboratory;
	}

	public void setDateSampleSentToReferenceLaboratory(Date dateSampleSentToReferenceLaboratory) {
		this.dateSampleSentToReferenceLaboratory = dateSampleSentToReferenceLaboratory;
	}

	public FacilityReferenceDto getReferenceLaboratory() {
		return referenceLaboratory;
	}

	public void setReferenceLaboratory(FacilityReferenceDto referenceLaboratory) {
		this.referenceLaboratory = referenceLaboratory;
	}

	public Set<PathogenTestType> getSelectedPathogenTestTypes() {
		return selectedPathogenTestTypes;
	}

	public void setSelectedPathogenTestTypes(Set<PathogenTestType> selectedPathogenTestTypes) {
		this.selectedPathogenTestTypes = selectedPathogenTestTypes;
	}

	public Set<CulturePcrFinding> getCultureFindings() {
		return cultureFindings;
	}

	public void setCultureFindings(Set<CulturePcrFinding> cultureFindings) {
		this.cultureFindings = cultureFindings;
	}

	public Set<CulturePcrFinding> getPcrFindings() {
		return pcrFindings;
	}

	public void setPcrFindings(Set<CulturePcrFinding> pcrFindings) {
		this.pcrFindings = pcrFindings;
	}

	public String getCultureOtherGermsSpecify() {
		return cultureOtherGermsSpecify;
	}

	public void setCultureOtherGermsSpecify(String cultureOtherGermsSpecify) {
		this.cultureOtherGermsSpecify = cultureOtherGermsSpecify;
	}

	public String getPcrOtherGermsSpecify() {
		return pcrOtherGermsSpecify;
	}

	public void setPcrOtherGermsSpecify(String pcrOtherGermsSpecify) {
		this.pcrOtherGermsSpecify = pcrOtherGermsSpecify;
	}

	public String getCellCountLeucocytesPerMm3() {
		return cellCountLeucocytesPerMm3;
	}

	public void setCellCountLeucocytesPerMm3(String cellCountLeucocytesPerMm3) {
		this.cellCountLeucocytesPerMm3 = cellCountLeucocytesPerMm3;
	}

	public String getCsfGlucose() {
		return csfGlucose;
	}

	public void setCsfGlucose(String csfGlucose) {
		this.csfGlucose = csfGlucose;
	}

	public String getCsfProtein() {
		return csfProtein;
	}

	public void setCsfProtein(String csfProtein) {
		this.csfProtein = csfProtein;
	}

	public Boolean getGramStainGpd() {
		return gramStainGpd;
	}

	public void setGramStainGpd(Boolean gramStainGpd) {
		this.gramStainGpd = gramStainGpd;
	}

	public Boolean getGramStainGnd() {
		return gramStainGnd;
	}

	public void setGramStainGnd(Boolean gramStainGnd) {
		this.gramStainGnd = gramStainGnd;
	}

	public Boolean getGramStainGpb() {
		return gramStainGpb;
	}

	public void setGramStainGpb(Boolean gramStainGpb) {
		this.gramStainGpb = gramStainGpb;
	}

	public Boolean getGramStainGnb() {
		return gramStainGnb;
	}

	public void setGramStainGnb(Boolean gramStainGnb) {
		this.gramStainGnb = gramStainGnb;
	}

	public Boolean getGramStainOtherPathogens() {
		return gramStainOtherPathogens;
	}

	public void setGramStainOtherPathogens(Boolean gramStainOtherPathogens) {
		this.gramStainOtherPathogens = gramStainOtherPathogens;
	}

	public String getGramStainOtherPathogensSpecify() {
		return gramStainOtherPathogensSpecify;
	}

	public void setGramStainOtherPathogensSpecify(String gramStainOtherPathogensSpecify) {
		this.gramStainOtherPathogensSpecify = gramStainOtherPathogensSpecify;
	}

	public Boolean getGramStainNoOrganismSeen() {
		return gramStainNoOrganismSeen;
	}

	public void setGramStainNoOrganismSeen(Boolean gramStainNoOrganismSeen) {
		this.gramStainNoOrganismSeen = gramStainNoOrganismSeen;
	}

	public Boolean getLatexNmA() {
		return latexNmA;
	}

	public void setLatexNmA(Boolean latexNmA) {
		this.latexNmA = latexNmA;
	}

	public Boolean getLatexNmC() {
		return latexNmC;
	}

	public void setLatexNmC(Boolean latexNmC) {
		this.latexNmC = latexNmC;
	}

	public Boolean getLatexNmWY() {
		return latexNmWY;
	}

	public void setLatexNmWY(Boolean latexNmWY) {
		this.latexNmWY = latexNmWY;
	}

	public Boolean getLatexNmBEcoliKi() {
		return latexNmBEcoliKi;
	}

	public void setLatexNmBEcoliKi(Boolean latexNmBEcoliKi) {
		this.latexNmBEcoliKi = latexNmBEcoliKi;
	}

	public Boolean getLatexSPneumoniae() {
		return latexSPneumoniae;
	}

	public void setLatexSPneumoniae(Boolean latexSPneumoniae) {
		this.latexSPneumoniae = latexSPneumoniae;
	}

	public Boolean getLatexHib() {
		return latexHib;
	}

	public void setLatexHib(Boolean latexHib) {
		this.latexHib = latexHib;
	}

	public Boolean getLatexStrepB() {
		return latexStrepB;
	}

	public void setLatexStrepB(Boolean latexStrepB) {
		this.latexStrepB = latexStrepB;
	}

	public Boolean getLatexNegative() {
		return latexNegative;
	}

	public void setLatexNegative(Boolean latexNegative) {
		this.latexNegative = latexNegative;
	}

	public YesNo getRdtDipstickPerformed() {
		return rdtDipstickPerformed;
	}

	public void setRdtDipstickPerformed(YesNo rdtDipstickPerformed) {
		this.rdtDipstickPerformed = rdtDipstickPerformed;
	}

	public String getRdtDipstickResults() {
		return rdtDipstickResults;
	}

	public void setRdtDipstickResults(String rdtDipstickResults) {
		this.rdtDipstickResults = rdtDipstickResults;
	}

	public AntimicrobialSusceptibility getCeftriaxoneSusceptibility() {
		return ceftriaxoneSusceptibility;
	}

	public void setCeftriaxoneSusceptibility(AntimicrobialSusceptibility ceftriaxoneSusceptibility) {
		this.ceftriaxoneSusceptibility = ceftriaxoneSusceptibility;
	}

	public AntimicrobialSusceptibility getAmpicillinSusceptibility() {
		return ampicillinSusceptibility;
	}

	public void setAmpicillinSusceptibility(AntimicrobialSusceptibility ampicillinSusceptibility) {
		this.ampicillinSusceptibility = ampicillinSusceptibility;
	}

	public AntimicrobialSusceptibility getGentamycinSusceptibility() {
		return gentamycinSusceptibility;
	}

	public void setGentamycinSusceptibility(AntimicrobialSusceptibility gentamycinSusceptibility) {
		this.gentamycinSusceptibility = gentamycinSusceptibility;
	}

	public AntimicrobialSusceptibility getOxacillinSusceptibility() {
		return oxacillinSusceptibility;
	}

	public void setOxacillinSusceptibility(AntimicrobialSusceptibility oxacillinSusceptibility) {
		this.oxacillinSusceptibility = oxacillinSusceptibility;
	}

	public AntimicrobialSusceptibility getChloramphenicolSusceptibility() {
		return chloramphenicolSusceptibility;
	}

	public void setChloramphenicolSusceptibility(AntimicrobialSusceptibility chloramphenicolSusceptibility) {
		this.chloramphenicolSusceptibility = chloramphenicolSusceptibility;
	}

	public AntimicrobialSusceptibility getBenzylPenicillinSusceptibility() {
		return benzylPenicillinSusceptibility;
	}

	public void setBenzylPenicillinSusceptibility(AntimicrobialSusceptibility benzylPenicillinSusceptibility) {
		this.benzylPenicillinSusceptibility = benzylPenicillinSusceptibility;
	}

	public String getOtherAntimicrobialDrugName() {
		return otherAntimicrobialDrugName;
	}

	public void setOtherAntimicrobialDrugName(String otherAntimicrobialDrugName) {
		this.otherAntimicrobialDrugName = otherAntimicrobialDrugName;
	}

	public AntimicrobialSusceptibility getOtherAntimicrobialSusceptibility() {
		return otherAntimicrobialSusceptibility;
	}

	public void setOtherAntimicrobialSusceptibility(AntimicrobialSusceptibility otherAntimicrobialSusceptibility) {
		this.otherAntimicrobialSusceptibility = otherAntimicrobialSusceptibility;
	}

	public Date getDatePcrPerformed() {
		return datePcrPerformed;
	}

	public void setDatePcrPerformed(Date datePcrPerformed) {
		this.datePcrPerformed = datePcrPerformed;
	}

	public String getPcrTypeText() {
		return pcrTypeText;
	}

	public void setPcrTypeText(String pcrTypeText) {
		this.pcrTypeText = pcrTypeText;
	}

	public String getPcrSerotype() {
		return pcrSerotype;
	}

	public void setPcrSerotype(String pcrSerotype) {
		this.pcrSerotype = pcrSerotype;
	}

	public String getOtherTestTypeSpecify() {
		return otherTestTypeSpecify;
	}

	public void setOtherTestTypeSpecify(String otherTestTypeSpecify) {
		this.otherTestTypeSpecify = otherTestTypeSpecify;
	}

	public String getOtherTestResults() {
		return otherTestResults;
	}

	public void setOtherTestResults(String otherTestResults) {
		this.otherTestResults = otherTestResults;
	}

	public YesNo getViralDetection() {
		return viralDetection;
	}

	public void setViralDetection(YesNo viralDetection) {
		this.viralDetection = viralDetection;
	}

	public ViralDetectionTestType getViralDetectionTestType() {
		return viralDetectionTestType;
	}

	public void setViralDetectionTestType(ViralDetectionTestType viralDetectionTestType) {
		this.viralDetectionTestType = viralDetectionTestType;
	}

	public PathogenTestResultType getViralDetectionResults() {
		return viralDetectionResults;
	}

	public void setViralDetectionResults(PathogenTestResultType viralDetectionResults) {
		this.viralDetectionResults = viralDetectionResults;
	}

	public Date getDateLabResultsSentDivision() {
		return dateLabResultsSentDivision;
	}

	public void setDateLabResultsSentDivision(Date dateLabResultsSentDivision) {
		this.dateLabResultsSentDivision = dateLabResultsSentDivision;
	}

	public String getNameLabTechnicianSendResults() {
		return nameLabTechnicianSendResults;
	}

	public void setNameLabTechnicianSendResults(String nameLabTechnicianSendResults) {
		this.nameLabTechnicianSendResults = nameLabTechnicianSendResults;
	}
	public Date getDateCombinedCellCultureResults() {
		return dateCombinedCellCultureResults;
	}

	public void setDateCombinedCellCultureResults(Date dateCombinedCellCultureResults) {
		this.dateCombinedCellCultureResults = dateCombinedCellCultureResults;
	}

	public Date getDateResultsSentToNationalEpi() {
		return dateResultsSentToNationalEpi;
	}

	public void setDateResultsSentToNationalEpi(Date dateResultsSentToNationalEpi) {
		this.dateResultsSentToNationalEpi = dateResultsSentToNationalEpi;
	}

	public Date getDateSentFromIcNationalRegLab() {
		return dateSentFromIcNationalRegLab;
	}

	public void setDateSentFromIcNationalRegLab(Date dateSentFromIcNationalRegLab) {
		this.dateSentFromIcNationalRegLab = dateSentFromIcNationalRegLab;
	}

	public Date getDateDifferentiationSentEpi() {
		return dateDifferentiationSentEpi;
	}

	public void setDateDifferentiationSentEpi(Date dateDifferentiationSentEpi) {
		this.dateDifferentiationSentEpi = dateDifferentiationSentEpi;
	}

	public Date getDateDifferentiationReceivedEpi() {
		return dateDifferentiationReceivedEpi;
	}

	public void setDateDifferentiationReceivedEpi(Date dateDifferentiationReceivedEpi) {
		this.dateDifferentiationReceivedEpi = dateDifferentiationReceivedEpi;
	}

	public Date getDateIsolateSentSequencing() {
		return dateIsolateSentSequencing;
	}

	public void setDateIsolateSentSequencing(Date dateIsolateSentSequencing) {
		this.dateIsolateSentSequencing = dateIsolateSentSequencing;
	}

	public Date getDateSeqResultsSentProgram() {
		return dateSeqResultsSentProgram;
	}

	public void setDateSeqResultsSentProgram(Date dateSeqResultsSentProgram) {
		this.dateSeqResultsSentProgram = dateSeqResultsSentProgram;
	}

	public PosNeg getW1() {
		return w1;
	}

	public void setW1(PosNeg w1) {
		this.w1 = w1;
	}

	public PosNeg getW2() {
		return w2;
	}

	public void setW2(PosNeg w2) {
		this.w2 = w2;
	}

	public PosNeg getW3() {
		return w3;
	}

	public void setW3(PosNeg w3) {
		this.w3 = w3;
	}

	public PosNeg getSl1() {
		return sl1;
	}

	public void setSl1(PosNeg sl1) {
		this.sl1 = sl1;
	}

	public PosNeg getSl2() {
		return sl2;
	}

	public void setSl2(PosNeg sl2) {
		this.sl2 = sl2;
	}

	public PosNeg getSl3() {
		return sl3;
	}

	public void setSl3(PosNeg sl3) {
		this.sl3 = sl3;
	}

	public PosNeg getNpent() {
		return npent;
	}

	public void setNpent(PosNeg npent) {
		this.npent = npent;
	}

	public PosNeg getNev() {
		return nev;
	}

	public void setNev(PosNeg nev) {
		this.nev = nev;
	}

	public PathogenTestResultType getFinalCellCultureResults() {
		return finalCellCultureResults;
	}

	public void setFinalCellCultureResults(PathogenTestResultType finalCellCultureResults) {
		this.finalCellCultureResults = finalCellCultureResults;
	}

	public Date getDateFollowupExam() {
		return dateFollowupExam;
	}

	public void setDateFollowupExam(Date dateFollowupExam) {
		this.dateFollowupExam = dateFollowupExam;
	}

	public InjectionSite getResidualAnalysis() {
		return residualAnalysis;
	}

	public void setResidualAnalysis(InjectionSite residualAnalysis) {
		this.residualAnalysis = residualAnalysis;
	}

	public ExamResult getResultExam() {
		return resultExam;
	}

	public void setResultExam(ExamResult resultExam) {
		this.resultExam = resultExam;
	}

	public SabinType getDiscordantSabin() {
		return discordantSabin;
	}

	public void setDiscordantSabin(SabinType discordantSabin) {
		this.discordantSabin = discordantSabin;
	}

	@Override
	public PathogenTestDto clone() throws CloneNotSupportedException {
		return (PathogenTestDto) super.clone();
	}
}

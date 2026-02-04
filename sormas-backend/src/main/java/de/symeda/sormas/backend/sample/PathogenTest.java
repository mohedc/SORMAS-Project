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

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.disease.DiseaseVariantConverter;
import de.symeda.sormas.api.disease.PathogenConverter;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.sample.*;
import de.symeda.sormas.api.utils.*;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.therapy.DrugSusceptibility;
import de.symeda.sormas.backend.user.User;

@Entity
public class PathogenTest extends DeletableAdo {

	private static final long serialVersionUID = 2290351143518627813L;

	public static final String TABLE_NAME = "pathogentest";

	public static final String SAMPLE = "sample";
	public static final String ENVIRONMENT_SAMPLE = "environmentSample";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TESTED_DISEASE_VARIANT_VALUE = "testedDiseaseVariantValue";
	public static final String TESTED_DISEASE_VARIANT_DETAILS = "testedDiseaseVariantDetails";
	public static final String TESTED_PATHOGEN_VALUE = "testedPathogenValue";
	public static final String TESTED_PATHOGEN_DETAILS = "testedPathogenDetails";
	public static final String TYPING_ID = "typingId";
	public static final String TEST_TYPE = "testType";
	public static final String PCR_TEST_SPECIFICATION = "pcrTestSpecification";
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
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";
	public static final String RIFAMPICIN_RESISTANT = "rifampicinResistant";
	public static final String ISONIAZID_RESISTANT = "isoniazidResistant";
	public static final String SPECIE = "specie";
	public static final String PATTERN_PROFILE = "patternProfile";
	public static final String STRAIN_CALL_STATUS = "strainCallStatus";
	public static final String TEST_SCALE = "testScale";
	public static final String DRUG_SUSCEPTIBILITY = "drugSusceptibility";
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
	public static final String REFERENCE_LABORATORY = "referenceLaboratory";

	private Sample sample;
	private EnvironmentSample environmentSample;
	private Disease testedDisease;
	private String testedDiseaseVariantValue;
	private DiseaseVariant testedDiseaseVariant;
	private String testedDiseaseDetails;
	private String testedDiseaseVariantDetails;
	private String testedPathogenValue;
	private Pathogen testedPathogen;
	private String testedPathogenDetails;
	private String typingId;
	private PathogenTestType testType;
	private PCRTestSpecification pcrTestSpecification;
	private String testTypeText;
	private Date testDateTime;
	private Facility lab;
	private String labDetails;
	private User labUser;
	private PathogenTestResultType testResult;
	private String testResultText;
	private String virusDetectionGenotype;
	private Boolean virusIsolated;
	private Boolean testResultVerified;
	private boolean fourFoldIncreaseAntibodyTiter;
	private String serotype;
	private Float cqValue;
	private Float ctValueE;
	private Float ctValueN;
	private Float ctValueRdrp;
	private Float ctValueS;
	private Float ctValueOrf1;
	private Float ctValueRdrpS;
	private Date reportDate;
	private boolean viaLims;
	private String externalId;
	private String externalOrderId;
	private Boolean preliminary;
	private String prescriberPhysicianCode;
	private String prescriberFirstName;
	private String prescriberLastName;
	private String prescriberPhoneNumber;
	private String prescriberAddress;
	private String prescriberPostalCode;
	private String prescriberCity;
	private Country prescriberCountry;
	private YesNoUnknown rifampicinResistant;
	private YesNoUnknown isoniazidResistant;
	private PathogenSpecie specie;
	private String patternProfile;
	private PathogenStrainCallStatus strainCallStatus;
	private PathogenTestScale testScale;
	private DrugSusceptibility drugSusceptibility;
	private String miruPatternProfile;
	private SerotypingMethod seroTypingMethod;
	private String seroTypingMethodText;
	private SeroGroupSpecification seroGroupSpecification;
	private String seroGroupSpecificationText;
	private Date dateResultsSentToDistrict;
	private Date dateDistrictReceivedLabResults;
	private Date dateResultsSentToDiseaseSurveillance;
	private Date dateIndirectResultsReceivedAtNationalEPIOffice;
	private Date dateCapturedResultsReceivedAtNationalEPIOffice;
	private FinalClassification finalClassification;
	private Boolean communityInvestigation;
	private Boolean performRubellaTest;
	private String investigationResults;
	private String sourceOfInfectionIdentified;
	private MacroscopicExamination macroscopicExamination;
	private Boolean cellCountNormal;
	private Boolean cellCountAbnormal;
	private String wbcCountPolycytesPercent;
	private String wbcCountMonocytesPercent;
	private GramStainResult gramStainResult;
	private PathogenTestResultType agglutinationResult;
	private String agglutinationPositiveResults;
	private String agglutinationOtherMicroorganism;
	private Date dateResultsSentToRegion;
	private Boolean otherTestsPending;
	private String otherTestsPendingSpecify;
	private Date dateResultsSentToReferenceLaboratory;
	private Facility referenceLaboratory;
	private YesNo viralDetection;
	private ViralDetectionTestType viralDetectionTestType;
	private PathogenTestResultType viralDetectionResults;
	private Date dateLabResultsSentDivision;
	private String nameLabTechnicianSendResults;
	private Date dateCombinedCellCultureResults;
	private Date dateResultsSentToNationalEpi;
	private Date dateSentFromIcNationalRegLab;
	private Date dateDifferentiationSentEpi;
	private Date dateDifferentiationReceivedEpi;
	private Date dateIsolateSentSequencing;
	private Date dateSeqResultsSentProgram;
	private YesNo w1;
	private YesNo w2;
	private YesNo w3;
	private YesNo sl1;
	private YesNo sl2;
	private YesNo sl3;
	private YesNo sabinType1;
	private YesNo sabinType2;
	private YesNo sabinType3;
	private PosNeg npent;
	private PosNeg nev;
	private PathogenTestResultType finalCellCultureResults;
	private Date dateFollowupExam;
	private InjectionSite residualAnalysis;
	private ExamResult resultExam;
	@ManyToOne(fetch = FetchType.LAZY)
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public EnvironmentSample getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSample environmentSample) {
		this.environmentSample = environmentSample;
	}

	@Enumerated(EnumType.STRING)
	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedDiseaseDetails() {
		return testedDiseaseDetails;
	}

	public void setTestedDiseaseDetails(String testedDiseaseDetails) {
		this.testedDiseaseDetails = testedDiseaseDetails;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	@Column(name = "testeddiseasevariant")
	public String getTestedDiseaseVariantValue() {
		return testedDiseaseVariantValue;
	}

	public void setTestedDiseaseVariantValue(String diseaseVariantValue) {
		this.testedDiseaseVariantValue = diseaseVariantValue;
		this.testedDiseaseVariant = new DiseaseVariantConverter().convertToEntityAttribute(testedDisease, testedDiseaseVariantValue);
	}

	@Transient
	public DiseaseVariant getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.testedDiseaseVariant = diseaseVariant;
		this.testedDiseaseVariantValue = new DiseaseVariantConverter().convertToDatabaseColumn(diseaseVariant);
	}

	@Column(name = "testedpathogen")
	public String getTestedPathogenValue() {
		return testedPathogenValue;
	}

	public void setTestedPathogenValue(String testedPathogenValue) {
		this.testedPathogenValue = testedPathogenValue;
		this.testedPathogen = new PathogenConverter().convertToEntityAttribute(null, testedPathogenValue);
	}

	@Transient
	public Pathogen getTestedPathogen() {
		return testedPathogen;
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
		this.testedPathogenValue = new PathogenConverter().convertToDatabaseColumn(testedPathogen);
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedPathogenDetails() {
		return testedPathogenDetails;
	}

	public void setTestedPathogenDetails(String testedPathogenDetails) {
		this.testedPathogenDetails = testedPathogenDetails;
	}

	@Column
	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Enumerated(EnumType.STRING)
	public PCRTestSpecification getPcrTestSpecification() {
		return pcrTestSpecification;
	}

	public void setPcrTestSpecification(PCRTestSpecification pcrTestSpecification) {
		this.pcrTestSpecification = pcrTestSpecification;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getLabUser() {
		return labUser;
	}

	public void setLabUser(User labUser) {
		this.labUser = labUser;
	}

	@Enumerated(EnumType.STRING)
	@JoinColumn(nullable = false)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getVirusDetectionGenotype() {
		return virusDetectionGenotype;
	}

	public void setVirusDetectionGenotype(String virusDetectionGenotype) {
		this.virusDetectionGenotype = virusDetectionGenotype;
	}

	@Column
	public Boolean getVirusIsolated() {
		return virusIsolated;
	}

	public void setVirusIsolated(Boolean virusIsolated) {
		this.virusIsolated = virusIsolated;
	}

	@Column(nullable = false)
	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	@Column
	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	@Column
	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	@Column
	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	@Column
	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	@Column
	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	@Column
	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	@Column
	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	@Column
	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Column
	public boolean isViaLims() {
		return viaLims;
	}

	public void setViaLims(boolean viaLims) {
		this.viaLims = viaLims;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	@Column
	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(Country prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	public PathogenTestReferenceDto toReference() {
		return new PathogenTestReferenceDto(getUuid());
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRifampicinResistant() {
		return rifampicinResistant;
	}

	public void setRifampicinResistant(YesNoUnknown rifampicinResistant) {
		this.rifampicinResistant = rifampicinResistant;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIsoniazidResistant() {
		return isoniazidResistant;
	}

	public void setIsoniazidResistant(YesNoUnknown isoniazidResistant) {
		this.isoniazidResistant = isoniazidResistant;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public PathogenStrainCallStatus getStrainCallStatus() {
		return strainCallStatus;
	}

	public void setStrainCallStatus(PathogenStrainCallStatus strainCallStatus) {
		this.strainCallStatus = strainCallStatus;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestScale getTestScale() {
		return testScale;
	}

	public void setTestScale(PathogenTestScale testScale) {
		this.testScale = testScale;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public DrugSusceptibility getDrugSusceptibility() {
		return drugSusceptibility;
	}

	public void setDrugSusceptibility(DrugSusceptibility drugSusceptibility) {
		this.drugSusceptibility = drugSusceptibility;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateResultsSentToDistrict() {
		return dateResultsSentToDistrict;
	}

	public void setDateResultsSentToDistrict(Date dateResultsSentToDistrict) {
		this.dateResultsSentToDistrict = dateResultsSentToDistrict;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateDistrictReceivedLabResults() {
		return dateDistrictReceivedLabResults;
	}

	public void setDateDistrictReceivedLabResults(Date dateDistrictReceivedLabResults) {
		this.dateDistrictReceivedLabResults = dateDistrictReceivedLabResults;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateResultsSentToDiseaseSurveillance() {
		return dateResultsSentToDiseaseSurveillance;
	}

	public void setDateResultsSentToDiseaseSurveillance(Date dateResultsSentToDiseaseSurveillance) {
		this.dateResultsSentToDiseaseSurveillance = dateResultsSentToDiseaseSurveillance;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateIndirectResultsReceivedAtNationalEPIOffice() {
		return dateIndirectResultsReceivedAtNationalEPIOffice;
	}

	public void setDateIndirectResultsReceivedAtNationalEPIOffice(Date dateIndirectResultsReceivedAtNationalEPIOffice) {
		this.dateIndirectResultsReceivedAtNationalEPIOffice = dateIndirectResultsReceivedAtNationalEPIOffice;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateCapturedResultsReceivedAtNationalEPIOffice() {
		return dateCapturedResultsReceivedAtNationalEPIOffice;
	}

	public void setDateCapturedResultsReceivedAtNationalEPIOffice(Date dateCapturedResultsReceivedAtNationalEPIOffice) {
		this.dateCapturedResultsReceivedAtNationalEPIOffice = dateCapturedResultsReceivedAtNationalEPIOffice;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public FinalClassification getFinalClassification() {
		return finalClassification;
	}

	public void setFinalClassification(FinalClassification finalClassification) {
		this.finalClassification = finalClassification;
	}

	@Column
	public Boolean getCommunityInvestigation() {
		return communityInvestigation;
	}

	public void setCommunityInvestigation(Boolean communityInvestigation) {
		this.communityInvestigation = communityInvestigation;
	}

	@Column
	public Boolean getPerformRubellaTest() {
		return performRubellaTest;
	}

	public void setPerformRubellaTest(Boolean performRubellaTest) {
		this.performRubellaTest = performRubellaTest;
	}

	@Column(columnDefinition = "text")
	public String getInvestigationResults() {
		return investigationResults;
	}

	public void setInvestigationResults(String investigationResults) {
		this.investigationResults = investigationResults;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSourceOfInfectionIdentified() {
		return sourceOfInfectionIdentified;
	}

	public void setSourceOfInfectionIdentified(String sourceOfInfectionIdentified) {
		this.sourceOfInfectionIdentified = sourceOfInfectionIdentified;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public MacroscopicExamination getMacroscopicExamination() {
		return macroscopicExamination;
	}

	public void setMacroscopicExamination(MacroscopicExamination macroscopicExamination) {
		this.macroscopicExamination = macroscopicExamination;
	}

	@Column
	public Boolean getCellCountNormal() {
		return cellCountNormal;
	}

	public void setCellCountNormal(Boolean cellCountNormal) {
		this.cellCountNormal = cellCountNormal;
	}

	@Column
	public Boolean getCellCountAbnormal() {
		return cellCountAbnormal;
	}

	public void setCellCountAbnormal(Boolean cellCountAbnormal) {
		this.cellCountAbnormal = cellCountAbnormal;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getWbcCountPolycytesPercent() {
		return wbcCountPolycytesPercent;
	}

	public void setWbcCountPolycytesPercent(String wbcCountPolycytesPercent) {
		this.wbcCountPolycytesPercent = wbcCountPolycytesPercent;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getWbcCountMonocytesPercent() {
		return wbcCountMonocytesPercent;
	}

	public void setWbcCountMonocytesPercent(String wbcCountMonocytesPercent) {
		this.wbcCountMonocytesPercent = wbcCountMonocytesPercent;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public GramStainResult getGramStainResult() {
		return gramStainResult;
	}

	public void setGramStainResult(GramStainResult gramStainResult) {
		this.gramStainResult = gramStainResult;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public PathogenTestResultType getAgglutinationResult() {
		return agglutinationResult;
	}

	public void setAgglutinationResult(PathogenTestResultType agglutinationResult) {
		this.agglutinationResult = agglutinationResult;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getAgglutinationPositiveResults() {
		return agglutinationPositiveResults;
	}

	public void setAgglutinationPositiveResults(String agglutinationPositiveResults) {
		this.agglutinationPositiveResults = agglutinationPositiveResults;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getAgglutinationOtherMicroorganism() {
		return agglutinationOtherMicroorganism;
	}

	public void setAgglutinationOtherMicroorganism(String agglutinationOtherMicroorganism) {
		this.agglutinationOtherMicroorganism = agglutinationOtherMicroorganism;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateResultsSentToRegion() {
		return dateResultsSentToRegion;
	}

	public void setDateResultsSentToRegion(Date dateResultsSentToRegion) {
		this.dateResultsSentToRegion = dateResultsSentToRegion;
	}

	@Column
	public Boolean getOtherTestsPending() {
		return otherTestsPending;
	}

	public void setOtherTestsPending(Boolean otherTestsPending) {
		this.otherTestsPending = otherTestsPending;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getOtherTestsPendingSpecify() {
		return otherTestsPendingSpecify;
	}

	public void setOtherTestsPendingSpecify(String otherTestsPendingSpecify) {
		this.otherTestsPendingSpecify = otherTestsPendingSpecify;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateResultsSentToReferenceLaboratory() {
		return dateResultsSentToReferenceLaboratory;
	}

	public void setDateResultsSentToReferenceLaboratory(Date dateResultsSentToReferenceLaboratory) {
		this.dateResultsSentToReferenceLaboratory = dateResultsSentToReferenceLaboratory;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Facility getReferenceLaboratory() {
		return referenceLaboratory;
	}

	public void setReferenceLaboratory(Facility referenceLaboratory) {
		this.referenceLaboratory = referenceLaboratory;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getViralDetection() {
		return viralDetection;
	}

	public void setViralDetection(YesNo viralDetection) {
		this.viralDetection = viralDetection;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public ViralDetectionTestType getViralDetectionTestType() {
		return viralDetectionTestType;
	}

	public void setViralDetectionTestType(ViralDetectionTestType viralDetectionTestType) {
		this.viralDetectionTestType = viralDetectionTestType;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public PathogenTestResultType getViralDetectionResults() {
		return viralDetectionResults;
	}

	public void setViralDetectionResults(PathogenTestResultType viralDetectionResults) {
		this.viralDetectionResults = viralDetectionResults;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateLabResultsSentDivision() {
		return dateLabResultsSentDivision;
	}

	public void setDateLabResultsSentDivision(Date dateLabResultsSentDivision) {
		this.dateLabResultsSentDivision = dateLabResultsSentDivision;
	}
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getNameLabTechnicianSendResults() {
		return nameLabTechnicianSendResults;
	}

	public void setNameLabTechnicianSendResults(String nameLabTechnicianSendResults) {
		this.nameLabTechnicianSendResults = nameLabTechnicianSendResults;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateCombinedCellCultureResults() {
		return dateCombinedCellCultureResults;
	}

	public void setDateCombinedCellCultureResults(Date dateCombinedCellCultureResults) {
		this.dateCombinedCellCultureResults = dateCombinedCellCultureResults;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateResultsSentToNationalEpi() {
		return dateResultsSentToNationalEpi;
	}

	public void setDateResultsSentToNationalEpi(Date dateResultsSentToNationalEpi) {
		this.dateResultsSentToNationalEpi = dateResultsSentToNationalEpi;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSentFromIcNationalRegLab() {
		return dateSentFromIcNationalRegLab;
	}

	public void setDateSentFromIcNationalRegLab(Date dateSentFromIcNationalRegLab) {
		this.dateSentFromIcNationalRegLab = dateSentFromIcNationalRegLab;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateDifferentiationSentEpi() {
		return dateDifferentiationSentEpi;
	}

	public void setDateDifferentiationSentEpi(Date dateDifferentiationSentEpi) {
		this.dateDifferentiationSentEpi = dateDifferentiationSentEpi;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateDifferentiationReceivedEpi() {
		return dateDifferentiationReceivedEpi;
	}

	public void setDateDifferentiationReceivedEpi(Date dateDifferentiationReceivedEpi) {
		this.dateDifferentiationReceivedEpi = dateDifferentiationReceivedEpi;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateIsolateSentSequencing() {
		return dateIsolateSentSequencing;
	}

	public void setDateIsolateSentSequencing(Date dateIsolateSentSequencing) {
		this.dateIsolateSentSequencing = dateIsolateSentSequencing;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateSeqResultsSentProgram() {
		return dateSeqResultsSentProgram;
	}

	public void setDateSeqResultsSentProgram(Date dateSeqResultsSentProgram) {
		this.dateSeqResultsSentProgram = dateSeqResultsSentProgram;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getW1() {
		return w1;
	}

	public void setW1(YesNo w1) {
		this.w1 = w1;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getW2() {
		return w2;
	}

	public void setW2(YesNo w2) {
		this.w2 = w2;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getW3() {
		return w3;
	}

	public void setW3(YesNo w3) {
		this.w3 = w3;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSl1() {
		return sl1;
	}

	public void setSl1(YesNo sl1) {
		this.sl1 = sl1;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSl2() {
		return sl2;
	}

	public void setSl2(YesNo sl2) {
		this.sl2 = sl2;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSl3() {
		return sl3;
	}

	public void setSl3(YesNo sl3) {
		this.sl3 = sl3;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSabinType1() {
		return sabinType1;
	}

	public void setSabinType1(YesNo sabinType1) {
		this.sabinType1 = sabinType1;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSabinType2() {
		return sabinType2;
	}

	public void setSabinType2(YesNo sabinType2) {
		this.sabinType2 = sabinType2;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public YesNo getSabinType3() {
		return sabinType3;
	}

	public void setSabinType3(YesNo sabinType3) {
		this.sabinType3 = sabinType3;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public PosNeg getNpent() {
		return npent;
	}

	public void setNpent(PosNeg npent) {
		this.npent = npent;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public PosNeg getNev() {
		return nev;
	}

	public void setNev(PosNeg nev) {
		this.nev = nev;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public PathogenTestResultType getFinalCellCultureResults() {
		return finalCellCultureResults;
	}

	public void setFinalCellCultureResults(PathogenTestResultType finalCellCultureResults) {
		this.finalCellCultureResults = finalCellCultureResults;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateFollowupExam() {
		return dateFollowupExam;
	}

	public void setDateFollowupExam(Date dateFollowupExam) {
		this.dateFollowupExam = dateFollowupExam;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public InjectionSite getResidualAnalysis() {
		return residualAnalysis;
	}

	public void setResidualAnalysis(InjectionSite residualAnalysis) {
		this.residualAnalysis = residualAnalysis;
	}
	@Enumerated(EnumType.STRING)
	@Column
	public ExamResult getResultExam() {
		return resultExam;
	}

	public void setResultExam(ExamResult resultExam) {
		this.resultExam = resultExam;
	}
}

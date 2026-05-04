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

package de.symeda.sormas.app.backend.sample;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.sample.AgglutinationPositiveResult;
import de.symeda.sormas.api.sample.AgglutinationTestResult;
import de.symeda.sormas.api.sample.AntimicrobialSusceptibility;
import de.symeda.sormas.api.sample.CulturePcrFinding;
import de.symeda.sormas.api.sample.GramStainResult;
import de.symeda.sormas.api.sample.MacroscopicExamination;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DateFormatHelper;

@Entity(name = PathogenTest.TABLE_NAME)
@DatabaseTable(tableName = PathogenTest.TABLE_NAME)
public class PathogenTest extends PseudonymizableAdo {

	private static final long serialVersionUID = 2290351143518627813L;

	public static final String TABLE_NAME = "pathogenTest";
	public static final String I18N_PREFIX = "PathogenTest";

	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String SAMPLE = "sample";
	public static final String ENVIRONMENT_SAMPLE = "environmentSample";

	public static final String TEST_RESULT = "testResult";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Sample sample;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EnvironmentSample environmentSample;

	@Enumerated(EnumType.STRING)
	private PathogenTestType testType;

	@Enumerated(EnumType.STRING)
	private PCRTestSpecification pcrTestSpecification;

	@Column
	private String testTypeText;

	@Enumerated(EnumType.STRING)
	private Disease testedDisease;

	@Column(name = "testedDiseaseVariant")
	private String testedDiseaseVariantString;
	private DiseaseVariant testedDiseaseVariant;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedDiseaseDetails;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedDiseaseVariantDetails;

	@Column(name = "testedPathogen", length = CHARACTER_LIMIT_DEFAULT)
	private String testedPathogenString;

	private Pathogen testedPathogen;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedPathogenDetails;

	@Column
	private String typingId;

	@Enumerated(EnumType.STRING)
	@Column
	private PathogenTestResultType testResult;

	@Column
	private Boolean testResultVerified;

	@Column(length = CHARACTER_LIMIT_BIG)
	private String testResultText;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date testDateTime;

	@Column
	private boolean fourFoldIncreaseAntibodyTiter;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String serotype;

	@DatabaseField
	private Float cqValue;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility lab;

	@Column
	private String labDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User labUser;

	@Column
	private boolean viaLims;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateResultsSentToDistrict;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateDistrictReceivedLabResults;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateResultsSentToDiseaseSurveillance;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateIndirectResultsReceivedAtNationalEPIOffice;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateCapturedResultsReceivedAtNationalEPIOffice;

	@Enumerated(EnumType.STRING)
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private FinalClassification finalClassification;

	@Column
	private Boolean communityInvestigation;

	@Column
	private Boolean performRubellaTest;

	@Column(columnDefinition = "text")
	private String investigationResults;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String sourceOfInfectionIdentified;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String virusDetectionGenotype;

	@DatabaseField
	private Boolean virusIsolated;

	@Enumerated(EnumType.STRING)
	private MacroscopicExamination macroscopicExamination;

	@DatabaseField
	private Boolean cellCountNormal;

	@DatabaseField
	private Boolean cellCountAbnormal;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String wbcCountPolycytesPercent;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String wbcCountMonocytesPercent;

	@Enumerated(EnumType.STRING)
	private GramStainResult gramStainResult;

	@Enumerated(EnumType.STRING)
	private AgglutinationTestResult agglutinationResult;

	@Enumerated(EnumType.STRING)
	private AgglutinationPositiveResult agglutinationPositiveResults;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String agglutinationOtherMicroorganism;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateResultsSentToRegion;

	@DatabaseField
	private Boolean otherTestsPending;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String otherTestsPendingSpecify;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dateResultsSentToReferenceLaboratory;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility referenceLaboratory;

	@Column(columnDefinition = "text")
	private String selectedPathogenTestTypesString;

	@Column(columnDefinition = "text")
	private String cultureFindingsString;

	@Column(columnDefinition = "text")
	private String pcrFindingsString;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String cultureOtherGermsSpecify;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String pcrOtherGermsSpecify;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String cellCountLeucocytesPerMm3;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String csfGlucose;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String csfProtein;

	@DatabaseField
	private Boolean gramStainGpd;

	@DatabaseField
	private Boolean gramStainGnd;

	@DatabaseField
	private Boolean gramStainGpb;

	@DatabaseField
	private Boolean gramStainGnb;

	@DatabaseField
	private Boolean gramStainOtherPathogens;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String gramStainOtherPathogensSpecify;

	@DatabaseField
	private Boolean gramStainNoOrganismSeen;

	@DatabaseField
	private Boolean latexNmA;

	@DatabaseField
	private Boolean latexNmC;

	@DatabaseField
	private Boolean latexNmWY;

	@DatabaseField
	private Boolean latexNmBEcoliKi;

	@DatabaseField
	private Boolean latexSPneumoniae;

	@DatabaseField
	private Boolean latexHib;

	@DatabaseField
	private Boolean latexStrepB;

	@DatabaseField
	private Boolean latexNegative;

	@Enumerated(EnumType.STRING)
	private YesNo rdtDipstickPerformed;

	@Column(length = CHARACTER_LIMIT_BIG)
	private String rdtDipstickResults;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility ceftriaxoneSusceptibility;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility ampicillinSusceptibility;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility gentamycinSusceptibility;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility oxacillinSusceptibility;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility chloramphenicolSusceptibility;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility benzylPenicillinSusceptibility;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String otherAntimicrobialDrugName;

	@Enumerated(EnumType.STRING)
	private AntimicrobialSusceptibility otherAntimicrobialSusceptibility;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date datePcrPerformed;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String pcrTypeText;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String pcrSerotype;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String otherTestTypeSpecify;

	@Column(length = CHARACTER_LIMIT_BIG)
	private String otherTestResults;

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public EnvironmentSample getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSample environmentSample) {
		this.environmentSample = environmentSample;
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

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public String getTestedDiseaseVariantString() {
		return testedDiseaseVariantString;
	}

	public void setTestedDiseaseVariantString(String testedDiseaseVariantString) {
		this.testedDiseaseVariantString = testedDiseaseVariantString;
	}

	@Transient
	public DiseaseVariant getTestedDiseaseVariant() {
		if (StringUtils.isBlank(testedDiseaseVariantString)) {
			return null;
		} else {
			return DatabaseHelper.getCustomizableEnumValueDao().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, testedDiseaseVariantString);
		}
	}

	public void setTestedDiseaseVariant(DiseaseVariant testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
		if (testedDiseaseVariant == null) {
			testedDiseaseVariantString = null;
		} else {
			testedDiseaseVariantString = testedDiseaseVariant.getValue();
		}
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

	public String getTestedPathogenString() {
		return testedPathogenString;
	}

	public void setTestedPathogenString(String testedPathogenString) {
		this.testedPathogenString = testedPathogenString;
	}

	@Transient
	public Pathogen getTestedPathogen() {
		if (StringUtils.isBlank(testedPathogenString)) {
			return null;
		} else {
			return DatabaseHelper.getCustomizableEnumValueDao().getEnumValue(CustomizableEnumType.PATHOGEN, testedPathogenString);
		}
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
		if (testedPathogen == null) {
			testedPathogenString = null;
		} else {
			testedPathogenString = testedPathogen.getValue();
		}
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

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public Facility getLab() {
		return lab;
	}

	public void setLab(Facility lab) {
		this.lab = lab;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public User getLabUser() {
		return labUser;
	}

	public void setLabUser(User labUser) {
		this.labUser = labUser;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
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

	public Facility getReferenceLaboratory() {
		return referenceLaboratory;
	}

	public void setReferenceLaboratory(Facility referenceLaboratory) {
		this.referenceLaboratory = referenceLaboratory;
	}

	public String getSelectedPathogenTestTypesString() {
		return selectedPathogenTestTypesString;
	}

	public void setSelectedPathogenTestTypesString(String selectedPathogenTestTypesString) {
		this.selectedPathogenTestTypesString = selectedPathogenTestTypesString;
	}

	public String getCultureFindingsString() {
		return cultureFindingsString;
	}

	public void setCultureFindingsString(String cultureFindingsString) {
		this.cultureFindingsString = cultureFindingsString;
	}

	public String getPcrFindingsString() {
		return pcrFindingsString;
	}

	public void setPcrFindingsString(String pcrFindingsString) {
		this.pcrFindingsString = pcrFindingsString;
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

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String buildCaption() {
		return super.buildCaption() + DateFormatHelper.formatLocalDate(getTestDateTime());
	}
}

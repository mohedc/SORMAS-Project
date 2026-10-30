/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import de.symeda.sormas.api.importexport.ExportEntity;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportTarget;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.Order;

@ExportEntity(CaseDataDto.class)
public class CaseExportDetailedSampleDto extends CaseExportDto {

	private static final long serialVersionUID = 1L;

	public static final String SAMPLE_UUID = "sampleUuid";
	public static final String SAMPLE_LAB_SAMPLE_ID = "sampleLabSampleID";
	public static final String SAMPLE_FIELD_SAMPLE_ID = "sampleFieldSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_REPORT_DATE_TIME = "sampleReportDateTime";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String SAMPLE_PURPOSE = "samplePurpose";
	public static final String SAMPLE_LAB = "sampleLab";
	public static final String SAMPLE_SHIPPED = "sampleShipped";
	public static final String SAMPLE_SHIPMENT_DATE = "sampleShipmentDate";
	public static final String SAMPLE_RECEIVED = "sampleReceived";
	public static final String SAMPLE_RECEIVED_DATE = "sampleReceivedDate";
	public static final String SAMPLE_SPECIMEN_CONDITION = "sampleSpecimenCondition";
	public static final String SAMPLE_PATHOGEN_TEST_RESULT = "samplePathogenTestResult";
	public static final String SAMPLE_SAMPLING_REASON = "sampleSamplingReason";
	public static final String SAMPLE_COMMENT = "sampleComment";
	public static final String PATHOGEN_TESTS_TEST_TYPE = "pathogenTests.testType";
	public static final String PATHOGEN_TESTS_TEST_DATE_TIME = "pathogenTests.testDateTime";
	public static final String PATHOGEN_TESTS_LAB = "pathogenTests.lab";
	public static final String PATHOGEN_TESTS_TEST_RESULT = "pathogenTests.testResult";
	public static final String PATHOGEN_TESTS_TEST_RESULT_TEXT = "pathogenTests.testResultText";
	public static final String PATHOGEN_TESTS_TESTED_DISEASE = "pathogenTests.testedDisease";
	public static final String PATHOGEN_TESTS_CQ_VALUE = "pathogenTests.cqValue";
	public static final String PATHOGEN_TESTS_SEROTYPE = "pathogenTests.serotype";
	public static final String PATHOGEN_TESTS_VERIFIED = "pathogenTests.testResultVerified";

	private String sampleUuid;
	private String sampleLabSampleID;
	private String sampleFieldSampleID;
	private Date sampleDateTime;
	private Date sampleReportDateTime;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private SamplePurpose samplePurpose;
	private String sampleLab;
	private Boolean sampleShipped;
	private Date sampleShipmentDate;
	private Boolean sampleReceived;
	private Date sampleReceivedDate;
	private SpecimenCondition sampleSpecimenCondition;
	private PathogenTestResultType samplePathogenTestResult;
	private SamplingReason sampleSamplingReason;
	private String sampleComment;
	private String pathogenTestsTestType = "";
	private String pathogenTestsTestDateTime = "";
	private String pathogenTestsLab = "";
	private String pathogenTestsTestResult = "";
	private String pathogenTestsTestResultText = "";
	private String pathogenTestsTestedDisease = "";
	private String pathogenTestsCqValue = "";
	private String pathogenTestsSerotype = "";
	private String pathogenTestsTestResultVerified = "";

	public CaseExportDetailedSampleDto() {
		super((String) null);
	}

	public static CaseExportDetailedSampleDto copyOf(CaseExportDto source) {
		CaseExportDetailedSampleDto target = new CaseExportDetailedSampleDto();
		Class<?> type = CaseExportDto.class;
		try {
			while (type != null && type != Object.class) {
				for (Field field : type.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
						continue;
					}
					field.setAccessible(true);
					field.set(target, field.get(source));
				}
				type = type.getSuperclass();
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return target;
	}

	public static Set<String> extraPropertyIds() {
		Set<String> properties = new LinkedHashSet<>();
		properties.add(SAMPLE_UUID);
		properties.add(SAMPLE_LAB_SAMPLE_ID);
		properties.add(SAMPLE_FIELD_SAMPLE_ID);
		properties.add(SAMPLE_DATE_TIME);
		properties.add(SAMPLE_REPORT_DATE_TIME);
		properties.add(SAMPLE_MATERIAL);
		properties.add(SAMPLE_MATERIAL_TEXT);
		properties.add(SAMPLE_PURPOSE);
		properties.add(SAMPLE_LAB);
		properties.add(SAMPLE_SHIPPED);
		properties.add(SAMPLE_SHIPMENT_DATE);
		properties.add(SAMPLE_RECEIVED);
		properties.add(SAMPLE_RECEIVED_DATE);
		properties.add(SAMPLE_SPECIMEN_CONDITION);
		properties.add(SAMPLE_PATHOGEN_TEST_RESULT);
		properties.add(SAMPLE_SAMPLING_REASON);
		properties.add(SAMPLE_COMMENT);
		properties.add(PATHOGEN_TESTS_TEST_TYPE);
		properties.add(PATHOGEN_TESTS_TEST_DATE_TIME);
		properties.add(PATHOGEN_TESTS_LAB);
		properties.add(PATHOGEN_TESTS_TEST_RESULT);
		properties.add(PATHOGEN_TESTS_TEST_RESULT_TEXT);
		properties.add(PATHOGEN_TESTS_TESTED_DISEASE);
		properties.add(PATHOGEN_TESTS_CQ_VALUE);
		properties.add(PATHOGEN_TESTS_SEROTYPE);
		properties.add(PATHOGEN_TESTS_VERIFIED);
		return properties;
	}

	@Order(500)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_UUID)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleUuid() {
		return sampleUuid;
	}

	public void setSampleUuid(String sampleUuid) {
		this.sampleUuid = sampleUuid;
	}

	@Order(501)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_LAB_SAMPLE_ID)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleLabSampleID() {
		return sampleLabSampleID;
	}

	public void setSampleLabSampleID(String sampleLabSampleID) {
		this.sampleLabSampleID = sampleLabSampleID;
	}

	@Order(502)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_FIELD_SAMPLE_ID)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleFieldSampleID() {
		return sampleFieldSampleID;
	}

	public void setSampleFieldSampleID(String sampleFieldSampleID) {
		this.sampleFieldSampleID = sampleFieldSampleID;
	}

	@Order(503)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_DATE_TIME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Order(504)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_REPORT_DATE_TIME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Date getSampleReportDateTime() {
		return sampleReportDateTime;
	}

	public void setSampleReportDateTime(Date sampleReportDateTime) {
		this.sampleReportDateTime = sampleReportDateTime;
	}

	@Order(505)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_MATERIAL)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Order(506)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_MATERIAL_TEXT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	@Order(507)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_PURPOSE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	@Order(508)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_LAB)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleLab() {
		return sampleLab;
	}

	public void setSampleLab(String sampleLab) {
		this.sampleLab = sampleLab;
	}

	@Order(509)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_SHIPPED)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Boolean getSampleShipped() {
		return sampleShipped;
	}

	public void setSampleShipped(Boolean sampleShipped) {
		this.sampleShipped = sampleShipped;
	}

	@Order(510)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_SHIPMENT_DATE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Date getSampleShipmentDate() {
		return sampleShipmentDate;
	}

	public void setSampleShipmentDate(Date sampleShipmentDate) {
		this.sampleShipmentDate = sampleShipmentDate;
	}

	@Order(511)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_RECEIVED)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Boolean getSampleReceived() {
		return sampleReceived;
	}

	public void setSampleReceived(Boolean sampleReceived) {
		this.sampleReceived = sampleReceived;
	}

	@Order(512)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_RECEIVED_DATE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public Date getSampleReceivedDate() {
		return sampleReceivedDate;
	}

	public void setSampleReceivedDate(Date sampleReceivedDate) {
		this.sampleReceivedDate = sampleReceivedDate;
	}

	@Order(513)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_SPECIMEN_CONDITION)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public SpecimenCondition getSampleSpecimenCondition() {
		return sampleSpecimenCondition;
	}

	public void setSampleSpecimenCondition(SpecimenCondition sampleSpecimenCondition) {
		this.sampleSpecimenCondition = sampleSpecimenCondition;
	}

	@Order(514)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_PATHOGEN_TEST_RESULT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public PathogenTestResultType getSamplePathogenTestResult() {
		return samplePathogenTestResult;
	}

	public void setSamplePathogenTestResult(PathogenTestResultType samplePathogenTestResult) {
		this.samplePathogenTestResult = samplePathogenTestResult;
	}

	@Order(515)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_SAMPLING_REASON)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public SamplingReason getSampleSamplingReason() {
		return sampleSamplingReason;
	}

	public void setSampleSamplingReason(SamplingReason sampleSamplingReason) {
		this.sampleSamplingReason = sampleSamplingReason;
	}

	@Order(516)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(SAMPLE_COMMENT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@ExportEntity(SampleDto.class)
	public String getSampleComment() {
		return sampleComment;
	}

	public void setSampleComment(String sampleComment) {
		this.sampleComment = sampleComment;
	}

	@Order(520)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_TEST_TYPE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestType() {
		return pathogenTestsTestType;
	}

	public void setPathogenTestsTestType(String pathogenTestsTestType) {
		this.pathogenTestsTestType = pathogenTestsTestType;
	}

	@Order(521)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_TEST_DATE_TIME)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestDateTime() {
		return pathogenTestsTestDateTime;
	}

	public void setPathogenTestsTestDateTime(String pathogenTestsTestDateTime) {
		this.pathogenTestsTestDateTime = pathogenTestsTestDateTime;
	}

	@Order(522)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_LAB)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsLab() {
		return pathogenTestsLab;
	}

	public void setPathogenTestsLab(String pathogenTestsLab) {
		this.pathogenTestsLab = pathogenTestsLab;
	}

	@Order(523)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_TEST_RESULT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestResult() {
		return pathogenTestsTestResult;
	}

	public void setPathogenTestsTestResult(String pathogenTestsTestResult) {
		this.pathogenTestsTestResult = pathogenTestsTestResult;
	}

	@Order(524)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_TEST_RESULT_TEXT)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestResultText() {
		return pathogenTestsTestResultText;
	}

	public void setPathogenTestsTestResultText(String pathogenTestsTestResultText) {
		this.pathogenTestsTestResultText = pathogenTestsTestResultText;
	}

	@Order(525)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_TESTED_DISEASE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestedDisease() {
		return pathogenTestsTestedDisease;
	}

	public void setPathogenTestsTestedDisease(String pathogenTestsTestedDisease) {
		this.pathogenTestsTestedDisease = pathogenTestsTestedDisease;
	}

	@Order(526)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_CQ_VALUE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsCqValue() {
		return pathogenTestsCqValue;
	}

	public void setPathogenTestsCqValue(String pathogenTestsCqValue) {
		this.pathogenTestsCqValue = pathogenTestsCqValue;
	}

	@Order(527)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_SEROTYPE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsSerotype() {
		return pathogenTestsSerotype;
	}

	public void setPathogenTestsSerotype(String pathogenTestsSerotype) {
		this.pathogenTestsSerotype = pathogenTestsSerotype;
	}

	@Order(528)
	@ExportTarget(caseExportTypes = CaseExportType.CASE_SURVEILLANCE)
	@ExportProperty(PATHOGEN_TESTS_VERIFIED)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getPathogenTestsTestResultVerified() {
		return pathogenTestsTestResultVerified;
	}

	public void setPathogenTestsTestResultVerified(String pathogenTestsTestResultVerified) {
		this.pathogenTestsTestResultVerified = pathogenTestsTestResultVerified;
	}
}

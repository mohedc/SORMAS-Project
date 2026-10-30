/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDetailedSampleDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class CaseExportDetailedSampleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetExportListDetailedZeroOneAndManySamples() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto nonePerson = creator.createPerson("No", "Sample");
		PersonDto onePerson = creator.createPerson("One", "Sample");
		PersonDto manyPerson = creator.createPerson("Many", "Sample");

		CaseDataDto none = creator.createCase(
			user.toReference(),
			nonePerson.toReference(),
			Disease.MEASLES,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		CaseDataDto one = creator.createCase(
			user.toReference(),
			onePerson.toReference(),
			Disease.MEASLES,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		CaseDataDto many = creator.createCase(
			user.toReference(),
			manyPerson.toReference(),
			Disease.MEASLES,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		SampleDto singleSample = creator.createSample(one.toReference(), user.toReference(), rdcf.facility);
		SampleDto firstSample = creator.createSample(many.toReference(), user.toReference(), rdcf.facility);
		SampleDto secondSample = creator.createSample(many.toReference(), user.toReference(), rdcf.facility);

		creator.createPathogenTest(
			singleSample.toReference(),
			PathogenTestType.PCR_RT_PCR,
			Disease.MEASLES,
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"first-text",
			true);
		creator.createPathogenTest(
			singleSample.toReference(),
			PathogenTestType.IGM_SERUM_ANTIBODY,
			Disease.MEASLES,
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.NEGATIVE,
			"second-text",
			true);

		List<CaseExportDetailedSampleDto> noneRows = getCaseFacade().getExportListDetailed(
			new CaseCriteria(),
			Collections.singleton(none.getUuid()),
			CaseExportType.CASE_SURVEILLANCE,
			0,
			100,
			null,
			Language.EN);
		assertEquals(1, noneRows.size());
		assertNull(noneRows.get(0).getSampleUuid());
		assertEquals("", noneRows.get(0).getPathogenTestsTestType());

		List<CaseExportDetailedSampleDto> oneRows = getCaseFacade().getExportListDetailed(
			new CaseCriteria(),
			Collections.singleton(one.getUuid()),
			CaseExportType.CASE_SURVEILLANCE,
			0,
			100,
			null,
			Language.EN);
		assertEquals(1, oneRows.size());
		assertEquals(singleSample.getUuid(), oneRows.get(0).getSampleUuid());
		assertTrue(oneRows.get(0).getPathogenTestsTestType().contains(" | "));
		assertTrue(oneRows.get(0).getPathogenTestsTestResult().contains(" | "));
		assertTrue(oneRows.get(0).getPathogenTestsTestResultText().contains("first-text"));
		assertTrue(oneRows.get(0).getPathogenTestsTestResultText().contains("second-text"));

		List<CaseExportDetailedSampleDto> manyRows = getCaseFacade().getExportListDetailed(
			new CaseCriteria(),
			Collections.singleton(many.getUuid()),
			CaseExportType.CASE_SURVEILLANCE,
			0,
			100,
			null,
			Language.EN);
		assertEquals(2, manyRows.size());
		assertEquals(many.getUuid(), manyRows.get(0).getUuid());
		assertEquals(many.getUuid(), manyRows.get(1).getUuid());
		assertTrue(
			manyRows.stream().anyMatch(row -> firstSample.getUuid().equals(row.getSampleUuid()))
				&& manyRows.stream().anyMatch(row -> secondSample.getUuid().equals(row.getSampleUuid())));
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class DiseaseSpecificExportFieldServiceTest extends AbstractBeanTest {

	@Test
	public void testSeedOnceThenAppendMissingUnionCatalogPathsInactive() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "en-GM");
		DiseaseSpecificExportFieldService service = getDiseaseSpecificExportFieldService();
		service.seedFromCsvResources();

		for (Disease disease : DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES) {
			assertTrue(service.countByDisease(disease) > 0, "No rows seeded for " + disease);
		}

		Disease measles = Disease.MEASLES;
		long firstCount = service.countByDisease(measles);
		List<DiseaseSpecificExportField> first = service.getByDisease(measles);
		DiseaseSpecificExportField activeField = first.stream().filter(DiseaseSpecificExportField::isActive).findFirst().orElse(first.get(0));
		String deactivatedPath = activeField.getPropertyPath();
		Long activeId = activeField.getId();
		executeInTransaction(em -> {
			DiseaseSpecificExportField managed = em.find(DiseaseSpecificExportField.class, activeId);
			managed.setActive(false);
		});

		service.seedFromCsvResources();
		assertEquals(firstCount, service.countByDisease(measles));
		DiseaseSpecificExportField stillInactive =
			service.getByDisease(measles).stream().filter(f -> f.getPropertyPath().equals(deactivatedPath)).findFirst().orElse(null);
		assertTrue(stillInactive != null);
		assertFalse(stillInactive.isActive(), "Existing rows must not be re-seeded");

		List<DiseaseSpecificExportField> measlesRows = service.getByDisease(measles);
		DiseaseSpecificExportField last = measlesRows.get(measlesRows.size() - 1);
		Long lastId = last.getId();
		executeInTransaction(em -> {
			DiseaseSpecificExportField managed = em.find(DiseaseSpecificExportField.class, lastId);
			em.remove(managed);
		});
		assertTrue(service.getByDisease(measles).stream().noneMatch(f -> last.getPropertyPath().equals(f.getPropertyPath())));

		service.seedFromCsvResources();
		DiseaseSpecificExportField restored =
			service.getByDisease(measles).stream().filter(f -> last.getPropertyPath().equals(f.getPropertyPath())).findFirst().orElse(null);
		assertTrue(restored != null);
		assertFalse(restored.isActive(), "Re-appended union-catalog paths must be inactive");
	}
}

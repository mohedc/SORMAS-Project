/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportFileDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportKind;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class CaseSamplesDetailedExportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testStartDiseaseSpecificValidation() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "en-GM");
		getDiseaseSpecificExportFieldService().seedFromCsvResources();

		assertThrows(
			ValidationRuntimeException.class,
			() -> getCaseSamplesDetailedExportFacade()
				.startDiseaseSpecificDetailedExport(new CaseCriteria(), Collections.emptySet(), Language.EN));

		assertThrows(
			ValidationRuntimeException.class,
			() -> getCaseSamplesDetailedExportFacade()
				.startDiseaseSpecificDetailedExport(new CaseCriteria().disease(Disease.EVD), Collections.emptySet(), Language.EN));

		List<Long> measlesIds = getDiseaseSpecificExportFieldService().getByDisease(Disease.MEASLES)
			.stream()
			.map(DiseaseSpecificExportField::getId)
			.collect(Collectors.toList());
		executeInTransaction(em -> {
			for (Long id : measlesIds) {
				DiseaseSpecificExportField managed = em.find(DiseaseSpecificExportField.class, id);
				managed.setActive(false);
			}
		});
		assertThrows(
			ValidationRuntimeException.class,
			() -> getCaseSamplesDetailedExportFacade()
				.startDiseaseSpecificDetailedExport(new CaseCriteria().disease(Disease.MEASLES), Collections.emptySet(), Language.EN));
	}

	@Test
	public void testTokenAndAuthDownloadRules() throws Exception {
		Path file = Files.createTempFile("export", ".csv");
		Files.write(file, "a,b\n".getBytes());
		String token = CaseSamplesDetailedExportFacadeEjb.createToken();
		Date future = new Date(System.currentTimeMillis() + 3_600_000L);
		Date past = new Date(System.currentTimeMillis() - 1000L);

		CaseSamplesDetailedExport success = persistExport(CaseSamplesDetailedExportResult.SUCCESS, file, token, future);
		CaseSamplesDetailedExportFileDto tokenFile = getCaseSamplesDetailedExportFacade().getFileForToken(token);
		assertEquals(success.getUuid(), tokenFile.getExportUuid());

		String failedToken = CaseSamplesDetailedExportFacadeEjb.createToken();
		CaseSamplesDetailedExport failed = persistExport(CaseSamplesDetailedExportResult.FAILED, file, failedToken, future);
		assertThrows(ValidationRuntimeException.class, () -> getCaseSamplesDetailedExportFacade().getFileForToken(failedToken));
		CaseSamplesDetailedExportFileDto authFailed = getCaseSamplesDetailedExportFacade().getFileForAuth(failed.getUuid());
		assertEquals(failed.getUuid(), authFailed.getExportUuid());

		String inProgressToken = CaseSamplesDetailedExportFacadeEjb.createToken();
		CaseSamplesDetailedExport inProgress = persistExport(CaseSamplesDetailedExportResult.IN_PROGRESS, file, inProgressToken, future);
		assertThrows(ValidationRuntimeException.class, () -> getCaseSamplesDetailedExportFacade().getFileForAuth(inProgress.getUuid()));

		String expiredToken = CaseSamplesDetailedExportFacadeEjb.createToken();
		CaseSamplesDetailedExport expired = persistExport(CaseSamplesDetailedExportResult.SUCCESS, file, expiredToken, past);
		assertThrows(ValidationRuntimeException.class, () -> getCaseSamplesDetailedExportFacade().getFileForToken(expiredToken));
		assertThrows(ValidationRuntimeException.class, () -> getCaseSamplesDetailedExportFacade().getFileForAuth(expired.getUuid()));

		Files.deleteIfExists(file);
	}

	@Test
	public void testStaleAndExpiryCleanup() throws Exception {
		Path file = Files.createTempFile("expired-export", ".csv");
		Files.write(file, "partial\n".getBytes());

		CaseSamplesDetailedExport neverStarted = persistExport(export -> {
			export.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
			export.setRequestedAt(new Date(System.currentTimeMillis() - 4L * 60 * 60 * 1000));
			export.setProgressRowCount(null);
		});

		CaseSamplesDetailedExport staleStarted = persistExport(export -> {
			export.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
			export.setProgressRowCount(10L);
			export.setLastProgressAt(new Date(System.currentTimeMillis() - 40L * 60 * 1000));
		});

		CaseSamplesDetailedExport fresh = persistExport(export -> {
			export.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
			export.setProgressRowCount(5L);
			export.setLastProgressAt(new Date());
		});

		getCaseSamplesDetailedExportFacade().cleanupStaleInProgressExports();
		assertEquals(CaseSamplesDetailedExportResult.FAILED, getCaseSamplesDetailedExportService().getByUuid(neverStarted.getUuid()).getResult());
		assertEquals(
			CaseSamplesDetailedExportFacadeEjb.STALE_MESSAGE,
			getCaseSamplesDetailedExportService().getByUuid(neverStarted.getUuid()).getErrorMessage());
		assertEquals(CaseSamplesDetailedExportResult.FAILED, getCaseSamplesDetailedExportService().getByUuid(staleStarted.getUuid()).getResult());
		assertEquals(CaseSamplesDetailedExportResult.IN_PROGRESS, getCaseSamplesDetailedExportService().getByUuid(fresh.getUuid()).getResult());

		CaseSamplesDetailedExport expired = persistExport(
			CaseSamplesDetailedExportResult.SUCCESS,
			file,
			CaseSamplesDetailedExportFacadeEjb.createToken(),
			new Date(System.currentTimeMillis() - 1000));
		getCaseSamplesDetailedExportFacade().cleanupExpiredExports();
		CaseSamplesDetailedExport after = getCaseSamplesDetailedExportService().getByUuid(expired.getUuid());
		assertNull(after.getFilePath());
		assertFalse(Files.exists(file));
		assertEquals(CaseSamplesDetailedExportResult.SUCCESS, after.getResult());
	}

	private CaseSamplesDetailedExport persistExport(CaseSamplesDetailedExportResult result, Path file, String token, Date expiresAt) {
		return persistExport(export -> {
			export.setResult(result);
			export.setTokenHash(CaseSamplesDetailedExportFacadeEjb.sha256(token));
			export.setExpiresAt(expiresAt);
			if (file != null) {
				export.setFilePath(file.toAbsolutePath().toString());
				export.setFileName(file.getFileName().toString());
			}
		});
	}

	private CaseSamplesDetailedExport persistExport(Consumer<CaseSamplesDetailedExport> customizer) {
		CaseSamplesDetailedExport export = new CaseSamplesDetailedExport();
		export.setRequestingUser(getUserService().getByUuid(nationalAdmin.getUuid()));
		export.setRequestedAt(new Date());
		export.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
		export.setExportKind(CaseSamplesDetailedExportKind.DETAILED);
		export.setLanguage(Language.EN);
		export.setDownloadCount(0);
		export.setTokenHash(CaseSamplesDetailedExportFacadeEjb.sha256(CaseSamplesDetailedExportFacadeEjb.createToken()));
		customizer.accept(export);
		getCaseSamplesDetailedExportService().ensurePersisted(export);
		return export;
	}
}

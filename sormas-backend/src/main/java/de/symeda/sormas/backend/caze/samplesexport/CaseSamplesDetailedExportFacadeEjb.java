/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportDownloadDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportFacade;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportFileDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportKind;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "CaseSamplesDetailedExportFacade")
@RightsAllowed(UserRight._CASE_EXPORT)
public class CaseSamplesDetailedExportFacadeEjb implements CaseSamplesDetailedExportFacade {

	static final String STALE_MESSAGE = "Export did not complete within the expected time. Please try again.";

	@EJB
	private CaseSamplesDetailedExportService exportService;
	@EJB
	private CaseSamplesDetailedExportDownloadService downloadService;
	@EJB
	private CaseSamplesDetailedExportAsyncService asyncService;
	@EJB
	private DiseaseSpecificExportFieldService fieldService;
	@EJB
	private UserService userService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public String startDetailedSampleExport(
		CaseCriteria criteria,
		Collection<String> selectedRows,
		CaseExportType exportType,
		ExportConfigurationDto exportConfiguration,
		Language language) {

		User user = requireCurrentUser();
		String token = createToken();
		CaseSamplesDetailedExport entity = new CaseSamplesDetailedExport();
		entity.setRequestingUser(user);
		entity.setRequestedAt(new Date());
		entity.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
		entity.setExportKind(CaseSamplesDetailedExportKind.DETAILED);
		entity.setLanguage(language);
		entity.setDownloadCount(0);
		entity.setCriteriaJson(criteria != null ? criteria.toUrlParams() : "");
		entity.setSelectedUuids(joinUuids(selectedRows));
		entity.setFilterSummary(buildFilterSummary(false, criteria));
		entity.setSnapshottedPaths(exportConfiguration != null && exportConfiguration.getProperties() != null
			? String.join(",", exportConfiguration.getProperties())
			: null);
		entity.setTokenHash(sha256(token));
		entity.setTotalCases(countCases(criteria, selectedRows));
		exportService.ensurePersisted(entity);

		asyncService.generate(entity.getUuid(), token);
		return entity.getUuid();
	}

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public String startDiseaseSpecificDetailedExport(CaseCriteria criteria, Collection<String> selectedRows, Language language) {
		User user = requireCurrentUser();
		if (criteria == null || criteria.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageDiseaseSpecificExportNeedsDisease));
		}
		if (!DiseaseSpecificExportFieldConfiguration.isSupported(criteria.getDisease())) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageDiseaseSpecificExportUnsupportedDisease));
		}
		List<String> activePaths = fieldService.getActivePropertyPaths(criteria.getDisease());
		if (activePaths.isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageDiseaseSpecificExportNoActiveFields));
		}

		String token = createToken();
		CaseSamplesDetailedExport entity = new CaseSamplesDetailedExport();
		entity.setRequestingUser(user);
		entity.setRequestedAt(new Date());
		entity.setResult(CaseSamplesDetailedExportResult.IN_PROGRESS);
		entity.setExportKind(CaseSamplesDetailedExportKind.DISEASE_SPECIFIC);
		entity.setDisease(criteria.getDisease());
		entity.setLanguage(language);
		entity.setDownloadCount(0);
		entity.setCriteriaJson(criteria.toUrlParams());
		entity.setSelectedUuids(joinUuids(selectedRows));
		entity.setFilterSummary(buildFilterSummary(true, criteria));
		entity.setSnapshottedPaths(String.join(",", activePaths));
		entity.setTokenHash(sha256(token));
		entity.setTotalCases(countCases(criteria, selectedRows));
		exportService.ensurePersisted(entity);

		asyncService.generate(entity.getUuid(), token);
		return entity.getUuid();
	}

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public List<CaseSamplesDetailedExportDto> getIndexList() {
		requireCurrentUser();
		return exportService.getAllOrderedByRequestedAtDesc().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public CaseSamplesDetailedExportDto getByUuid(String uuid) {
		requireCurrentUser();
		return toDto(exportService.getByUuid(uuid));
	}

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public List<CaseSamplesDetailedExportDownloadDto> getDownloads(String exportUuid) {
		requireCurrentUser();
		CaseSamplesDetailedExport export = exportService.getByUuid(exportUuid);
		if (export == null) {
			return Collections.emptyList();
		}
		return downloadService.getByExport(export).stream().map(this::toDownloadDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public void recordDownload(String exportUuid, String userName) {
		CaseSamplesDetailedExport export = exportService.getByUuid(exportUuid);
		if (export == null) {
			return;
		}
		CaseSamplesDetailedExportDownload download = new CaseSamplesDetailedExportDownload();
		download.setExport(export);
		download.setDownloadedAt(new Date());
		User user = resolveDownloadingUser(userName);
		download.setDownloadingUser(user);
		downloadService.ensurePersisted(download);
		export.setDownloadCount(export.getDownloadCount() + 1);
		exportService.ensurePersisted(export);
	}

	@Override
	@PermitAll
	public CaseSamplesDetailedExportFileDto getFileForToken(String token) {
		if (StringUtils.isBlank(token)) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageExportDownloadNotAvailable));
		}
		CaseSamplesDetailedExport export = exportService.getByTokenHash(sha256(token));
		if (export == null || export.getResult() != CaseSamplesDetailedExportResult.SUCCESS || isExpired(export) || StringUtils.isBlank(export.getFilePath())) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageExportDownloadNotAvailable));
		}
		return toFileDto(export);
	}

	@Override
	@RightsAllowed(UserRight._CASE_EXPORT)
	public CaseSamplesDetailedExportFileDto getFileForAuth(String uuid) {
		requireCurrentUser();
		CaseSamplesDetailedExport export = exportService.getByUuid(uuid);
		if (export == null || isExpired(export) || StringUtils.isBlank(export.getFilePath())) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageExportDownloadNotAvailable));
		}
		if (export.getResult() != CaseSamplesDetailedExportResult.SUCCESS && export.getResult() != CaseSamplesDetailedExportResult.FAILED) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageExportDownloadNotAvailable));
		}
		return toFileDto(export);
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public void cleanupStaleInProgressExports() {
		Date now = new Date();
		Date neverStartedCutoff = new Date(now.getTime() - 3L * 60 * 60 * 1000);
		Date startedCutoff = new Date(now.getTime() - 30L * 60 * 1000);
		List<CaseSamplesDetailedExport> stale = new ArrayList<>();
		stale.addAll(exportService.getStaleNeverStarted(neverStartedCutoff));
		stale.addAll(exportService.getStaleStarted(startedCutoff));
		for (CaseSamplesDetailedExport export : stale) {
			export.setResult(CaseSamplesDetailedExportResult.FAILED);
			export.setErrorMessage(STALE_MESSAGE);
			export.setLastProgressAt(now);
			exportService.ensurePersisted(export);
		}
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public void cleanupExpiredExports() {
		Date now = new Date();
		for (CaseSamplesDetailedExport export : exportService.getExpiredWithFile(now)) {
			if (StringUtils.isNotBlank(export.getFilePath())) {
				try {
					Files.deleteIfExists(Paths.get(export.getFilePath()));
				} catch (Exception ignored) {
					// keep history row even if the file cannot be deleted
				}
			}
			export.setFilePath(null);
			exportService.ensurePersisted(export);
		}
	}

	private CaseSamplesDetailedExportDto toDto(CaseSamplesDetailedExport source) {
		if (source == null) {
			return null;
		}
		CaseSamplesDetailedExportDto target = new CaseSamplesDetailedExportDto();
		DtoHelper.fillDto(target, source);
		target.setRequestedBy(UserFacadeEjb.toReferenceDto(source.getRequestingUser()));
		target.setRequestedAt(source.getRequestedAt());
		target.setResult(source.getResult());
		target.setFilterSummary(source.getFilterSummary());
		target.setRowCount(source.getRowCount());
		target.setExpiresAt(source.getExpiresAt());
		target.setDownloadCount(source.getDownloadCount());
		target.setErrorMessage(source.getErrorMessage());
		target.setErrorStack(source.getErrorStack());
		target.setProgressRowCount(source.getProgressRowCount());
		target.setLastProgressAt(source.getLastProgressAt());
		target.setFileName(source.getFileName());
		target.setHasFile(StringUtils.isNotBlank(source.getFilePath()));
		target.setExportKind(source.getExportKind());
		target.setDisease(source.getDisease());
		if (source.getResult() == CaseSamplesDetailedExportResult.IN_PROGRESS
			&& source.getProgressRowCount() != null
			&& source.getTotalCases() != null
			&& source.getTotalCases() > 0) {
			target.setProgressPercent((int) Math.min(100, Math.round(source.getProgressRowCount() * 100.0 / source.getTotalCases())));
		}
		return target;
	}

	private CaseSamplesDetailedExportDownloadDto toDownloadDto(CaseSamplesDetailedExportDownload source) {
		CaseSamplesDetailedExportDownloadDto dto = new CaseSamplesDetailedExportDownloadDto();
		dto.setDownloadedAt(source.getDownloadedAt());
		dto.setDownloadingUser(UserFacadeEjb.toReferenceDto(source.getDownloadingUser()));
		return dto;
	}

	private CaseSamplesDetailedExportFileDto toFileDto(CaseSamplesDetailedExport export) {
		CaseSamplesDetailedExportFileDto dto = new CaseSamplesDetailedExportFileDto();
		dto.setExportUuid(export.getUuid());
		dto.setFileName(export.getFileName());
		dto.setFilePath(export.getFilePath());
		dto.setResult(export.getResult());
		return dto;
	}

	private User requireCurrentUser() {
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new AccessDeniedException("A logged-in user is required");
		}
		return user;
	}

	private User resolveDownloadingUser(String userName) {
		if (StringUtils.isBlank(userName) || "ANONYMOUS".equalsIgnoreCase(userName) || "SYSTEM".equalsIgnoreCase(userName)) {
			return null;
		}
		return userService.getByUserName(userName);
	}

	private boolean isExpired(CaseSamplesDetailedExport export) {
		return export.getExpiresAt() != null && export.getExpiresAt().before(new Date());
	}

	private String joinUuids(Collection<String> selectedRows) {
		if (CollectionUtils.isEmpty(selectedRows)) {
			return null;
		}
		return String.join(",", selectedRows);
	}

	private String buildFilterSummary(boolean diseaseSpecific, CaseCriteria criteria) {
		String summary = criteria == null ? "" : StringUtils.defaultString(criteria.toUrlParams());
		if (StringUtils.isBlank(summary)) {
			summary = "No filters";
		}
		return diseaseSpecific ? "Disease-specific: " + summary : summary;
	}

	private long countCases(CaseCriteria criteria, Collection<String> selectedRows) {
		if (CollectionUtils.isNotEmpty(selectedRows)) {
			return selectedRows.size();
		}
		return caseFacade.count(criteria == null ? new CaseCriteria() : criteria, false);
	}

	static String createToken() {
		byte[] bytes = new byte[32];
		new SecureRandom().nextBytes(bytes);
		return toHex(bytes);
	}

	static String sha256(String value) {
		try {
			return toHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	@LocalBean
	@Stateless
	public static class CaseSamplesDetailedExportFacadeEjbLocal extends CaseSamplesDetailedExportFacadeEjb {
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseExportDetailedSampleDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportKind;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.utils.CsvStreamUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.messaging.EmailService;

@Stateless
@LocalBean
public class CaseSamplesDetailedExportAsyncService {

	private static final int PROGRESS_EVERY_ROWS = 1000;
	private static final int ERROR_STACK_LIMIT = 8000;
	private static final int ERROR_MESSAGE_LIMIT = 2000;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private CaseSamplesDetailedExportService exportService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private EmailService emailService;
	@EJB
	private DiseaseSpecificCaseExportService diseaseSpecificCaseExportService;

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void generate(String exportUuid, String plaintextToken) {
		CaseSamplesDetailedExport export = exportService.getByUuid(exportUuid);
		if (export == null) {
			return;
		}
		String recipientEmail = exportService.getRequestingUserEmail(exportUuid);

		Path directory = Paths.get(configFacade.getGeneratedFilesPath(), "case_sample_exports");
		String fileName = buildFileName(export);
		Path filePath = directory.resolve(fileName);
		File file = filePath.toFile();
		try {
			Files.createDirectories(directory);
			if (export.getExportKind() == CaseSamplesDetailedExportKind.DISEASE_SPECIFIC) {
				writeDiseaseSpecific(export, file);
			} else {
				writeDetailed(export, file);
			}
			long rowCount = export.getProgressRowCount() == null ? 0 : export.getProgressRowCount();
			Date expiresAt = DateHelper.addDays(new Date(), 2);
			exportService.markSuccess(exportUuid, file.getAbsolutePath(), fileName, rowCount, expiresAt);
			sendMail(export, recipientEmail, plaintextToken, true, fileName, null);
		} catch (Exception e) {
			logger.error("Detailed case/sample export failed for {}", exportUuid, e);
			String partialPath = file.exists() && file.length() > 0 ? file.getAbsolutePath() : null;
			Date expiresAt = partialPath != null ? DateHelper.addDays(new Date(), 2) : null;
			exportService.markFailed(exportUuid, abbreviate(e.getMessage(), ERROR_MESSAGE_LIMIT), abbreviate(stackTrace(e), ERROR_STACK_LIMIT), partialPath, fileName, expiresAt);
			sendMail(export, recipientEmail, plaintextToken, false, fileName, e.getMessage());
		}
	}

	private void writeDetailed(CaseSamplesDetailedExport export, File file) throws Exception {
		CaseCriteria criteria = restoreCriteria(export);
		Collection<String> selected = restoreSelected(export);
		Language language = export.getLanguage() != null ? export.getLanguage() : Language.EN;
		ExportConfigurationDto config = buildDetailedConfiguration(export);
		int pageSize = Math.max(1, configFacade.getStepSizeForCsvExport());
		AtomicInteger caseFirst = new AtomicInteger(0);
		AtomicLong csvRows = new AtomicLong(0);

		try (FileOutputStream out = new FileOutputStream(file)) {
			CsvStreamUtils.writeCsvPagesToStream(
				CaseExportDetailedSampleDto.class,
				() -> {
					List<CaseExportDetailedSampleDto> page = caseFacade.getExportListDetailed(
						criteria,
						selected,
						CaseExportType.CASE_SURVEILLANCE,
						caseFirst.getAndAdd(pageSize),
						pageSize,
						config,
						language);
					if (!page.isEmpty()) {
						long total = csvRows.addAndGet(page.size());
						if (total % PROGRESS_EVERY_ROWS < page.size() || page.size() >= PROGRESS_EVERY_ROWS) {
							exportService.updateProgress(export.getUuid(), total, new Date());
						}
					}
					return page;
				},
				(propertyId, type) -> I18nProperties.findPrefixCaption(propertyId, CaseExportDto.I18N_PREFIX, CaseExportDetailedSampleDto.class.getSimpleName()),
				config,
				null,
				configFacade,
				out);
		}
		exportService.updateProgress(export.getUuid(), csvRows.get(), new Date());
	}

	private void writeDiseaseSpecific(CaseSamplesDetailedExport export, File file) throws Exception {
		CaseCriteria criteria = restoreCriteria(export);
		Collection<String> selected = restoreSelected(export);
		List<String> paths = export.getSnapshottedPaths() == null
			? Collections.emptyList()
			: Arrays.asList(export.getSnapshottedPaths().split(","));
		AtomicLong csvRows = new AtomicLong(0);
		diseaseSpecificCaseExportService.writeExport(criteria, selected, paths, export.getDisease(), file, rowsWritten -> {
			long total = csvRows.addAndGet(rowsWritten);
			if (total % PROGRESS_EVERY_ROWS < rowsWritten || rowsWritten >= PROGRESS_EVERY_ROWS) {
				exportService.updateProgress(export.getUuid(), total, new Date());
			}
		});
		exportService.updateProgress(export.getUuid(), csvRows.get(), new Date());
	}

	private ExportConfigurationDto buildDetailedConfiguration(CaseSamplesDetailedExport export) {
		ExportConfigurationDto config = ExportConfigurationDto.build(null, ExportType.CASE);
		Set<String> properties = new LinkedHashSet<>();
		if (StringUtils.isNotBlank(export.getSnapshottedPaths())) {
			properties.addAll(Arrays.asList(export.getSnapshottedPaths().split(",")));
		}
		properties.remove(CaseExportDto.SAMPLE_INFORMATION);
		properties.addAll(CaseExportDetailedSampleDto.extraPropertyIds());
		config.setProperties(properties);
		return config;
	}

	private CaseCriteria restoreCriteria(CaseSamplesDetailedExport export) {
		CaseCriteria criteria = new CaseCriteria();
		if (StringUtils.isNotBlank(export.getCriteriaJson())) {
			criteria.fromUrlParams(export.getCriteriaJson());
		}
		return criteria;
	}

	private Collection<String> restoreSelected(CaseSamplesDetailedExport export) {
		if (StringUtils.isBlank(export.getSelectedUuids())) {
			return Collections.emptySet();
		}
		return new HashSet<>(Arrays.asList(export.getSelectedUuids().split(",")));
	}

	private String buildFileName(CaseSamplesDetailedExport export) {
		String instance = configFacade.getSormasInstanceName();
		String date = DateHelper.formatDateForExport(new Date());
		String uuid8 = export.getUuid().replace("-", "").substring(0, 8);
		if (export.getExportKind() == CaseSamplesDetailedExportKind.DISEASE_SPECIFIC) {
			return instance + "_cases_disease_specific_" + export.getDisease().name() + "_" + date + "_" + uuid8 + ".csv";
		}
		return instance + "_cases_samples_detailed_" + date + "_" + uuid8 + ".csv";
	}

	private void sendMail(CaseSamplesDetailedExport export, String recipientEmail, String token, boolean success, String fileName, String error) {
		if (StringUtils.isBlank(recipientEmail)) {
			return;
		}
		boolean diseaseSpecific = export.getExportKind() == CaseSamplesDetailedExportKind.DISEASE_SPECIFIC;
		String subject;
		String body;
		if (success) {
			subject = diseaseSpecific ? "SORMAS disease-specific case export ready" : "SORMAS detailed sample export ready";
			String uiUrl = StringUtils.removeEnd(StringUtils.defaultString(configFacade.getUiUrl()), "/");
			String downloadUrl = uiUrl + "/export-downloads/" + token;
			body = "The export file " + fileName + " is ready.\nIt expires in 48 hours.\nDownload: " + downloadUrl;
		} else {
			subject = diseaseSpecific ? "SORMAS disease-specific case export failed" : "SORMAS detailed sample export failed";
			body = "The export failed. " + StringUtils.defaultString(error) + "\nPlease retry or contact an administrator.";
		}
		try {
			emailService.sendEmailAsync(recipientEmail, subject, body);
		} catch (MessagingException e) {
			logger.warn("Could not send export email to {}", recipientEmail, e);
		}
	}

	private static String stackTrace(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	private static String abbreviate(String value, int max) {
		if (value == null) {
			return null;
		}
		return value.length() <= max ? value : value.substring(0, max);
	}
}

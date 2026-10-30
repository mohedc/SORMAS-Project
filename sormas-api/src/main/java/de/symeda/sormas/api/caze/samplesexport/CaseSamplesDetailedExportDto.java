/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class CaseSamplesDetailedExportDto extends EntityDto {

	private static final long serialVersionUID = 1L;

	public static final String I18N_PREFIX = "CaseSamplesDetailedExport";
	public static final String REQUESTED_BY = "requestedBy";
	public static final String REQUESTED_AT = "requestedAt";
	public static final String RESULT = "result";
	public static final String FILTER_SUMMARY = "filterSummary";
	public static final String ROW_COUNT = "rowCount";
	public static final String EXPIRES_AT = "expiresAt";
	public static final String DOWNLOAD_COUNT = "downloadCount";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String ERROR_STACK = "errorStack";
	public static final String PROGRESS_ROW_COUNT = "progressRowCount";
	public static final String LAST_PROGRESS_AT = "lastProgressAt";
	public static final String FILE_NAME = "fileName";
	public static final String HAS_FILE = "hasFile";
	public static final String EXPORT_KIND = "exportKind";
	public static final String DISEASE = "disease";

	private UserReferenceDto requestedBy;
	private Date requestedAt;
	private CaseSamplesDetailedExportResult result;
	private String filterSummary;
	private Long rowCount;
	private Date expiresAt;
	private int downloadCount;
	private String errorMessage;
	private String errorStack;
	private Long progressRowCount;
	private Date lastProgressAt;
	private String fileName;
	private boolean hasFile;
	private CaseSamplesDetailedExportKind exportKind;
	private Disease disease;
	private Integer progressPercent;

	public UserReferenceDto getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(UserReferenceDto requestedBy) {
		this.requestedBy = requestedBy;
	}

	public Date getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(Date requestedAt) {
		this.requestedAt = requestedAt;
	}

	public CaseSamplesDetailedExportResult getResult() {
		return result;
	}

	public void setResult(CaseSamplesDetailedExportResult result) {
		this.result = result;
	}

	public String getFilterSummary() {
		return filterSummary;
	}

	public void setFilterSummary(String filterSummary) {
		this.filterSummary = filterSummary;
	}

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorStack() {
		return errorStack;
	}

	public void setErrorStack(String errorStack) {
		this.errorStack = errorStack;
	}

	public Long getProgressRowCount() {
		return progressRowCount;
	}

	public void setProgressRowCount(Long progressRowCount) {
		this.progressRowCount = progressRowCount;
	}

	public Date getLastProgressAt() {
		return lastProgressAt;
	}

	public void setLastProgressAt(Date lastProgressAt) {
		this.lastProgressAt = lastProgressAt;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}

	public CaseSamplesDetailedExportKind getExportKind() {
		return exportKind;
	}

	public void setExportKind(CaseSamplesDetailedExportKind exportKind) {
		this.exportKind = exportKind;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Integer getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(Integer progressPercent) {
		this.progressPercent = progressPercent;
	}
}

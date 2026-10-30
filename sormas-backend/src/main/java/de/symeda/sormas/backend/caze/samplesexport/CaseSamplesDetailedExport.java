/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportKind;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Entity(name = CaseSamplesDetailedExport.TABLE_NAME)
public class CaseSamplesDetailedExport extends AbstractDomainObject {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "casesamplesdetailedexport";

	public static final String REQUESTING_USER = "requestingUser";
	public static final String REQUESTED_AT = "requestedAt";
	public static final String RESULT = "result";
	public static final String FILTER_SUMMARY = "filterSummary";
	public static final String CRITERIA_JSON = "criteriaJson";
	public static final String SELECTED_UUIDS = "selectedUuids";
	public static final String EXPORT_KIND = "exportKind";
	public static final String DISEASE = "disease";
	public static final String LANGUAGE = "language";
	public static final String SNAPSHOTTED_PATHS = "snapshottedPaths";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_NAME = "fileName";
	public static final String ROW_COUNT = "rowCount";
	public static final String PROGRESS_ROW_COUNT = "progressRowCount";
	public static final String LAST_PROGRESS_AT = "lastProgressAt";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String ERROR_STACK = "errorStack";
	public static final String TOKEN_HASH = "tokenHash";
	public static final String EXPIRES_AT = "expiresAt";
	public static final String DOWNLOAD_COUNT = "downloadCount";
	public static final String TOTAL_CASES = "totalCases";

	private User requestingUser;
	private Date requestedAt;
	private CaseSamplesDetailedExportResult result;
	private String filterSummary;
	private String criteriaJson;
	private String selectedUuids;
	private CaseSamplesDetailedExportKind exportKind;
	private Disease disease;
	private Language language;
	private String snapshottedPaths;
	private String filePath;
	private String fileName;
	private Long rowCount;
	private Long progressRowCount;
	private Date lastProgressAt;
	private String errorMessage;
	private String errorStack;
	private String tokenHash;
	private Date expiresAt;
	private int downloadCount;
	private Long totalCases;
	private List<CaseSamplesDetailedExportDownload> downloads = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getRequestingUser() {
		return requestingUser;
	}

	public void setRequestingUser(User requestingUser) {
		this.requestingUser = requestingUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(Date requestedAt) {
		this.requestedAt = requestedAt;
	}

	@Enumerated(EnumType.STRING)
	public CaseSamplesDetailedExportResult getResult() {
		return result;
	}

	public void setResult(CaseSamplesDetailedExportResult result) {
		this.result = result;
	}

	@Column(length = 4096)
	public String getFilterSummary() {
		return filterSummary;
	}

	public void setFilterSummary(String filterSummary) {
		this.filterSummary = filterSummary;
	}

	@Column(columnDefinition = "text")
	public String getCriteriaJson() {
		return criteriaJson;
	}

	public void setCriteriaJson(String criteriaJson) {
		this.criteriaJson = criteriaJson;
	}

	@Column(columnDefinition = "text")
	public String getSelectedUuids() {
		return selectedUuids;
	}

	public void setSelectedUuids(String selectedUuids) {
		this.selectedUuids = selectedUuids;
	}

	@Enumerated(EnumType.STRING)
	public CaseSamplesDetailedExportKind getExportKind() {
		return exportKind;
	}

	public void setExportKind(CaseSamplesDetailedExportKind exportKind) {
		this.exportKind = exportKind;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Enumerated(EnumType.STRING)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(columnDefinition = "text")
	public String getSnapshottedPaths() {
		return snapshottedPaths;
	}

	public void setSnapshottedPaths(String snapshottedPaths) {
		this.snapshottedPaths = snapshottedPaths;
	}

	@Column(length = 1024)
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(length = 512)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}

	public Long getProgressRowCount() {
		return progressRowCount;
	}

	public void setProgressRowCount(Long progressRowCount) {
		this.progressRowCount = progressRowCount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastProgressAt() {
		return lastProgressAt;
	}

	public void setLastProgressAt(Date lastProgressAt) {
		this.lastProgressAt = lastProgressAt;
	}

	@Column(columnDefinition = "text")
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Column(columnDefinition = "text")
	public String getErrorStack() {
		return errorStack;
	}

	public void setErrorStack(String errorStack) {
		this.errorStack = errorStack;
	}

	@Column(length = 64, unique = true)
	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Column(nullable = false)
	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Long getTotalCases() {
		return totalCases;
	}

	public void setTotalCases(Long totalCases) {
		this.totalCases = totalCases;
	}

	@OneToMany(mappedBy = CaseSamplesDetailedExportDownload.EXPORT, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<CaseSamplesDetailedExportDownload> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<CaseSamplesDetailedExportDownload> downloads) {
		this.downloads = downloads;
	}
}

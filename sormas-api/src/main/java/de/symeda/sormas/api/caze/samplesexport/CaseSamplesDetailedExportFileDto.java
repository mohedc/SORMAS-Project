/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.io.Serializable;

public class CaseSamplesDetailedExportFileDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String exportUuid;
	private String fileName;
	private String filePath;
	private CaseSamplesDetailedExportResult result;

	public String getExportUuid() {
		return exportUuid;
	}

	public void setExportUuid(String exportUuid) {
		this.exportUuid = exportUuid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CaseSamplesDetailedExportResult getResult() {
		return result;
	}

	public void setResult(CaseSamplesDetailedExportResult result) {
		this.result = result;
	}
}

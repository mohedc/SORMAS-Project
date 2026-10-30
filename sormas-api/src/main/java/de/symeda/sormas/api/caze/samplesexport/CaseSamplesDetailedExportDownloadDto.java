/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.user.UserReferenceDto;

public class CaseSamplesDetailedExportDownloadDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DOWNLOADED_AT = "downloadedAt";
	public static final String DOWNLOADING_USER = "downloadingUser";

	private Date downloadedAt;
	private UserReferenceDto downloadingUser;

	public Date getDownloadedAt() {
		return downloadedAt;
	}

	public void setDownloadedAt(Date downloadedAt) {
		this.downloadedAt = downloadedAt;
	}

	public UserReferenceDto getDownloadingUser() {
		return downloadingUser;
	}

	public void setDownloadingUser(UserReferenceDto downloadingUser) {
		this.downloadingUser = downloadingUser;
	}
}

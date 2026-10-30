/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;

@Remote
public interface CaseSamplesDetailedExportFacade {

	String startDetailedSampleExport(
		CaseCriteria criteria,
		Collection<String> selectedRows,
		CaseExportType exportType,
		ExportConfigurationDto exportConfiguration,
		Language language);

	String startDiseaseSpecificDetailedExport(CaseCriteria criteria, Collection<String> selectedRows, Language language);

	List<CaseSamplesDetailedExportDto> getIndexList();

	CaseSamplesDetailedExportDto getByUuid(String uuid);

	List<CaseSamplesDetailedExportDownloadDto> getDownloads(String exportUuid);

	void recordDownload(String exportUuid, String userName);

	CaseSamplesDetailedExportFileDto getFileForToken(String token);

	CaseSamplesDetailedExportFileDto getFileForAuth(String uuid);

	void cleanupStaleInProgressExports();

	void cleanupExpiredExports();
}

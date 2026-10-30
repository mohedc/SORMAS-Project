/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;

@Remote
public interface DiseaseSpecificExportFieldFacade {

	List<DiseaseSpecificExportFieldDto> getByDisease(Disease disease);

	void saveActivePaths(Disease disease, Set<String> activePropertyPaths);

	byte[] exportCsv(Disease disease);

	void importCsv(byte[] csvContent);

	List<String> getActivePropertyPaths(Disease disease);

	boolean isSupported(Disease disease);
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class CaseSamplesDetailedExportDownloadService extends BaseAdoService<CaseSamplesDetailedExportDownload> {

	public CaseSamplesDetailedExportDownloadService() {
		super(CaseSamplesDetailedExportDownload.class);
	}

	public List<CaseSamplesDetailedExportDownload> getByExport(CaseSamplesDetailedExport export) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseSamplesDetailedExportDownload> cq = cb.createQuery(getElementClass());
		Root<CaseSamplesDetailedExportDownload> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(CaseSamplesDetailedExportDownload.EXPORT), export));
		cq.orderBy(cb.desc(from.get(CaseSamplesDetailedExportDownload.DOWNLOADED_AT)));
		return em.createQuery(cq).getResultList();
	}
}

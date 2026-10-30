/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class CaseSamplesDetailedExportService extends BaseAdoService<CaseSamplesDetailedExport> {

	public CaseSamplesDetailedExportService() {
		super(CaseSamplesDetailedExport.class);
	}

	public List<CaseSamplesDetailedExport> getAllOrderedByRequestedAtDesc() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseSamplesDetailedExport> cq = cb.createQuery(getElementClass());
		Root<CaseSamplesDetailedExport> from = cq.from(getElementClass());
		cq.orderBy(cb.desc(from.get(CaseSamplesDetailedExport.REQUESTED_AT)));
		return em.createQuery(cq).getResultList();
	}

	public String getRequestingUserEmail(String uuid) {
		CaseSamplesDetailedExport entity = getByUuid(uuid);
		if (entity == null || entity.getRequestingUser() == null) {
			return null;
		}
		return entity.getRequestingUser().getUserEmail();
	}

	public CaseSamplesDetailedExport getByTokenHash(String tokenHash) {
		List<CaseSamplesDetailedExport> matches = getByPredicate((cb, from, cq) -> cb.equal(from.get(CaseSamplesDetailedExport.TOKEN_HASH), tokenHash));
		return matches.isEmpty() ? null : matches.get(0);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateProgress(String uuid, long progressRowCount, Date lastProgressAt) {
		CaseSamplesDetailedExport entity = getByUuid(uuid);
		if (entity == null) {
			return;
		}
		entity.setProgressRowCount(progressRowCount);
		entity.setLastProgressAt(lastProgressAt);
		ensurePersisted(entity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void markSuccess(String uuid, String filePath, String fileName, long rowCount, Date expiresAt) {
		CaseSamplesDetailedExport entity = getByUuid(uuid);
		if (entity == null) {
			return;
		}
		entity.setResult(CaseSamplesDetailedExportResult.SUCCESS);
		entity.setFilePath(filePath);
		entity.setFileName(fileName);
		entity.setRowCount(rowCount);
		entity.setProgressRowCount(rowCount);
		entity.setLastProgressAt(new Date());
		entity.setExpiresAt(expiresAt);
		ensurePersisted(entity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void markFailed(String uuid, String errorMessage, String errorStack, String filePath, String fileName, Date expiresAt) {
		CaseSamplesDetailedExport entity = getByUuid(uuid);
		if (entity == null) {
			return;
		}
		entity.setResult(CaseSamplesDetailedExportResult.FAILED);
		entity.setErrorMessage(errorMessage);
		entity.setErrorStack(errorStack);
		entity.setFilePath(filePath);
		entity.setFileName(fileName);
		entity.setLastProgressAt(new Date());
		entity.setExpiresAt(expiresAt);
		ensurePersisted(entity);
	}

	public List<CaseSamplesDetailedExport> getStaleNeverStarted(Date requestedBefore) {
		return getByPredicate(
			(cb, from, cq) -> cb.and(
				cb.equal(from.get(CaseSamplesDetailedExport.RESULT), CaseSamplesDetailedExportResult.IN_PROGRESS),
				cb.isNull(from.get(CaseSamplesDetailedExport.PROGRESS_ROW_COUNT)),
				cb.lessThan(from.get(CaseSamplesDetailedExport.REQUESTED_AT), requestedBefore)));
	}

	public List<CaseSamplesDetailedExport> getStaleStarted(Date lastProgressBefore) {
		return getByPredicate(
			(cb, from, cq) -> cb.and(
				cb.equal(from.get(CaseSamplesDetailedExport.RESULT), CaseSamplesDetailedExportResult.IN_PROGRESS),
				cb.isNotNull(from.get(CaseSamplesDetailedExport.PROGRESS_ROW_COUNT)),
				cb.lessThan(from.get(CaseSamplesDetailedExport.LAST_PROGRESS_AT), lastProgressBefore)));
	}

	public List<CaseSamplesDetailedExport> getExpiredWithFile(Date now) {
		return getByPredicate(
			(cb, from, cq) -> cb.and(
				cb.isNotNull(from.get(CaseSamplesDetailedExport.FILE_PATH)),
				cb.lessThan(from.get(CaseSamplesDetailedExport.EXPIRES_AT), now)));
	}
}

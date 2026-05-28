/*
 * SORMAS(R) - Surveillance Outbreak Response Management & Analysis System
 * Copyright (C) 2016-2026 Helmholtz-Zentrum fuer Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.app.backend.afpimmunization;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class AfpImmunizationDao extends AbstractAdoDao<AfpImmunization> {

	public AfpImmunizationDao(Dao<AfpImmunization, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<AfpImmunization> getAdoClass() {
		return AfpImmunization.class;
	}

	@Override
	public String getTableName() {
		return AfpImmunization.TABLE_NAME;
	}
}

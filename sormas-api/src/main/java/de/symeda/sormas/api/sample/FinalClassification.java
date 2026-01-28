/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

import java.util.Arrays;
import java.util.List;

public enum FinalClassification {

	LAB_CONFIRMED,
	CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE,
	COMPATIBLE,
	DISCARDED_IGM_NEGATIVE,
	PENDING_SUSPECTED_WITH_SPECIMEN_LAB_RESULTS_PENDING,
	CONFIRMED_POLIO,
	DISCARDED,
	NOT_AN_AFP_CASE,
	cVDPV,
	aVDPV,
	iVDPV,
	SERO_TYPE,
	CONFIRMED_BY_LAB_TEST_OR_EPIDEMIOLOGICAL_LINK,
	CLINICAL_SUSPECTED_NO_BLOOD_SPECIMEN;

	public static final List<FinalClassification> AFP_CLASSIFICATION = Arrays.asList(CONFIRMED_POLIO, COMPATIBLE, DISCARDED, NOT_AN_AFP_CASE, cVDPV, aVDPV, iVDPV, SERO_TYPE);

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

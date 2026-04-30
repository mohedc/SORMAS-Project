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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

import java.util.Arrays;
import java.util.List;

public enum FinalClassification {

	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA })
	LAB_CONFIRMED,
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA })
	CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE,
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA })
	CLINICAL,
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA })
	DISCARDED,
	@Diseases({
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA })
	PENDING_LAB_RESULTS,
	@Diseases({
			Disease.AFP})
	CONFIRMED_POLIO,
	@Diseases({
			Disease.AFP})
	COMPATIBLE,
	@Diseases({
			Disease.AFP})
	NOT_AN_AFP_CASE,
	@Diseases({
			Disease.AFP})
	cVDPV,
	@Diseases({
			Disease.AFP})
	aVDPV,
	@Diseases({
			Disease.AFP})
	iVDPV,
	@Diseases({
			Disease.AFP})
	SERO_TYPE,
	PROBABLE,
	SUSPECTED,
	AFP_CLASSIFICATION;
	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

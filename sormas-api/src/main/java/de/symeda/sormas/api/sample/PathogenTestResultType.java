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

public enum PathogenTestResultType {

	// hide for yellow_fever
	@Diseases(value = { Disease.CONGENITAL_RUBELLA }, hide = true)
	INDETERMINATE,
	UNKNOWN,
	@Diseases(value = { Disease.YELLOW_FEVER }, hide = true)
	PENDING,
	NEGATIVE,
	POSITIVE,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	CONTAMINATED,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	NOT_DONE,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	NOT_APPLICABLE,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	SUSPECTED_POLIOVIRUS,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	NPENT,
	@Diseases(value = { Disease.CONGENITAL_RUBELLA, Disease.YELLOW_FEVER }, hide = true)
	SUSPECT_POLIOVIRUS_NPENT,
	@Diseases(value = { Disease.YELLOW_FEVER }, hide = true)
	INCONCLUSIVE,
	@Diseases(value = { Disease.YELLOW_FEVER, Disease.AFP }, hide = true)
	NOT_TESTED,
	@Diseases(value = { Disease.YELLOW_FEVER, Disease.CSM }, hide = true)
	IN_PROCESS,
	@Diseases(value = { Disease.CSM }, hide = false)
	IN_PROGRESS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

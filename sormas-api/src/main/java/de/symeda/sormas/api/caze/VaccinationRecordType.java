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
package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Enum representing the source type of vaccination information.
 * Used for diseases that require vaccination information (e.g., Measles).
 */
public enum VaccinationRecordType {

	/**
	 * Vaccination information from vaccination card
	 */
	CARD,

	/**
	 * Vaccination information from historical record or recall
	 */
	HISTORY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

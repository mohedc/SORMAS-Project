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
package de.symeda.sormas.api.utils.fieldvisibility;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;

/**
 * Enum representing different sections/tabs in the case form.
 * Each section maps to a specific DTO class that contains the fields for that section.
 */
public enum CaseFormSection {

	/**
	 * New Case section - used when creating a new case
	 */
	NEW_CASE(CaseDataDto.class),

	/**
	 * Case Edit section - used when editing case data
	 */
	CASE_EDIT(CaseDataDto.class),

	/**
	 * Case Person section - person-related information
	 */
	CASE_PERSON(PersonDto.class),

	/**
	 * Hospitalization section - hospitalization-related information
	 */
	HOSPITALIZATION(HospitalizationDto.class),

	/**
	 * Clinical History section - symptoms and clinical information
	 */
	CLINICAL_HISTORY(SymptomsDto.class),

	/**
	 * Epidemiological Data section - exposure and epidemiological information
	 */
	EPIDEMIOLOGICAL_DATA(EpiDataDto.class);

	private final Class<?> dtoClass;

	CaseFormSection(Class<?> dtoClass) {
		this.dtoClass = dtoClass;
	}

	/**
	 * Returns the DTO class associated with this section
	 */
	public Class<?> getDtoClass() {
		return dtoClass;
	}
}

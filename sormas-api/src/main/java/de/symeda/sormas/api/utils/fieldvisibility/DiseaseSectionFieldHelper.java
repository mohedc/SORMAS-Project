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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;

/**
 * Helper class that determines which fields should be visible for a given disease and section.
 * This helper scans the DTO class associated with the section and returns only fields that are
 * annotated with @Diseases for the specified disease, or fields that have no @Diseases annotation
 * (which are visible for all diseases by default).
 * 
 * Usage:
 * <pre>
 * Set<String> visibleFields = DiseaseSectionFieldHelper.getVisibleFields(Disease.MEASLES, CaseFormSection.NEW_CASE);
 * </pre>
 */
public class DiseaseSectionFieldHelper {

	private DiseaseSectionFieldHelper() {
		// Hide Utility Class Constructor
	}

	/**
	 * Returns a set of field names that should be visible for the given disease in the specified section.
	 * 
	 * @param disease The disease to check field visibility for
	 * @param section The form section to get fields for
	 * @return Set of field names that are visible for the disease in this section
	 */
	public static Set<String> getVisibleFields(Disease disease, CaseFormSection section) {
		if (disease == null || section == null) {
			return Collections.emptySet();
		}

		Class<?> dtoClass = section.getDtoClass();
		return getVisibleFieldsForDisease(dtoClass, disease);
	}

	/**
	 * Returns a set of field names that should be visible for the given disease in the specified DTO class.
	 * 
	 * @param dtoClass The DTO class to scan for fields
	 * @param disease The disease to check field visibility for
	 * @return Set of field names that are visible for the disease
	 */
	public static Set<String> getVisibleFieldsForDisease(Class<?> dtoClass, Disease disease) {
		if (dtoClass == null || disease == null) {
			return Collections.emptySet();
		}

		Set<String> visibleFields = new HashSet<>();

		// Get all declared fields from the DTO class
		Field[] fields = dtoClass.getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();

			// Check if field is visible for this disease using DiseasesConfiguration
			if (Diseases.DiseasesConfiguration.isDefinedOrMissing(dtoClass, fieldName, disease)) {
				visibleFields.add(fieldName);
			}
		}

		return visibleFields;
	}

	/**
	 * Returns a set of field names that should be hidden for the given disease in the specified section.
	 * This is the inverse of getVisibleFields - it returns fields that are NOT visible.
	 * 
	 * @param disease The disease to check field visibility for
	 * @param section The form section to get fields for
	 * @return Set of field names that should be hidden for the disease in this section
	 */
	public static Set<String> getHiddenFields(Disease disease, CaseFormSection section) {
		if (disease == null || section == null) {
			return Collections.emptySet();
		}

		Class<?> dtoClass = section.getDtoClass();
		Set<String> allFields = getAllFields(dtoClass);
		Set<String> visibleFields = getVisibleFieldsForDisease(dtoClass, disease);

		// Return fields that are in allFields but not in visibleFields
		return allFields.stream()
			.filter(field -> !visibleFields.contains(field))
			.collect(Collectors.toSet());
	}

	/**
	 * Returns all field names declared in the DTO class.
	 * 
	 * @param dtoClass The DTO class to scan
	 * @return Set of all field names
	 */
	public static Set<String> getAllFields(Class<?> dtoClass) {
		if (dtoClass == null) {
			return Collections.emptySet();
		}

		Set<String> allFields = new HashSet<>();
		Field[] fields = dtoClass.getDeclaredFields();

		for (Field field : fields) {
			allFields.add(field.getName());
		}

		return allFields;
	}

	/**
	 * Checks if a specific field is visible for the given disease in the specified section.
	 * 
	 * @param disease The disease to check
	 * @param section The form section
	 * @param fieldName The name of the field to check
	 * @return true if the field is visible, false otherwise
	 */
	public static boolean isFieldVisible(Disease disease, CaseFormSection section, String fieldName) {
		if (disease == null || section == null || fieldName == null) {
			return false;
		}

		Class<?> dtoClass = section.getDtoClass();
		return Diseases.DiseasesConfiguration.isDefinedOrMissing(dtoClass, fieldName, disease);
	}
}

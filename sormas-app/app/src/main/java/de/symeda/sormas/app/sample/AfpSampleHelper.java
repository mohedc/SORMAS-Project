/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.app.sample;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * App-local copy of AFP sampling-number captions.
 * The Android module depends on a published sormas-api JAR, so it cannot
 * reference newly added API classes until that artifact is republished.
 */
public final class AfpSampleHelper {

	private static final String DATE_OF_NTH_SAMPLING = "Sample.dateOfNthSampling";
	private static final String DATE_OF_NTH_SAMPLING_DEFAULT = "Date %s specimen";

	private AfpSampleHelper() {
	}

	public static String toOrdinal(int number) {
		if (number <= 0) {
			return String.valueOf(number);
		}
		int mod100 = number % 100;
		if (mod100 >= 11 && mod100 <= 13) {
			return number + "th";
		}
		switch (number % 10) {
		case 1:
			return number + "st";
		case 2:
			return number + "nd";
		case 3:
			return number + "rd";
		default:
			return number + "th";
		}
	}

	public static int compareCreationThenUuid(Date leftCreation, String leftUuid, Date rightCreation, String rightUuid) {
		int dateCompare = Comparator.nullsLast(Date::compareTo).compare(leftCreation, rightCreation);
		if (dateCompare != 0) {
			return dateCompare;
		}
		return Comparator.nullsLast(String::compareTo).compare(leftUuid, rightUuid);
	}

	public static int getSamplingNumber(String currentUuid, List<String> siblingUuidsByCreation) {
		if (siblingUuidsByCreation == null || siblingUuidsByCreation.isEmpty()) {
			return 1;
		}
		if (currentUuid != null) {
			int index = siblingUuidsByCreation.indexOf(currentUuid);
			if (index >= 0) {
				return index + 1;
			}
		}
		return siblingUuidsByCreation.size() + 1;
	}

	public static String getSamplingDateCaption(int sampleNumber) {
		String template = I18nProperties.getCaption(DATE_OF_NTH_SAMPLING, DATE_OF_NTH_SAMPLING_DEFAULT);
		if (template == null || DATE_OF_NTH_SAMPLING.equals(template)) {
			template = DATE_OF_NTH_SAMPLING_DEFAULT;
		}
		return String.format(template, toOrdinal(sampleNumber));
	}

	public static String getSamplingDateCaption(String currentUuid, List<String> siblingUuidsByCreation) {
		return getSamplingDateCaption(getSamplingNumber(currentUuid, siblingUuidsByCreation));
	}

	public static <T> String getSamplingDateCaption(
		String currentUuid,
		List<T> siblings,
		Function<T, String> uuidGetter,
		Function<T, Date> creationDateGetter) {

		if (siblings == null || siblings.isEmpty()) {
			return getSamplingDateCaption(currentUuid, Collections.emptyList());
		}

		List<String> orderedUuids = siblings.stream()
			.sorted((left, right) -> compareCreationThenUuid(
				creationDateGetter.apply(left),
				uuidGetter.apply(left),
				creationDateGetter.apply(right),
				uuidGetter.apply(right)))
			.map(uuidGetter)
			.collect(Collectors.toList());

		return getSamplingDateCaption(currentUuid, orderedUuids);
	}
}

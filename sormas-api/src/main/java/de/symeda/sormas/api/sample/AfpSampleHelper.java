/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * AFP stool collections are entered as separate sample records. The collection-date caption
 * shows which sampling this is (1st, 2nd, 3rd, …) for the associated case/contact/event participant.
 */
public final class AfpSampleHelper {

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
		return String.format(I18nProperties.getCaption(Captions.Sample_dateOfNthSampling), toOrdinal(sampleNumber));
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

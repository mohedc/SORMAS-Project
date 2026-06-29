/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Keeps flat phone/email fields on {@link Person} aligned with primary {@link PersonContactDetail} entries.
 * Mirrors {@link de.symeda.sormas.api.person.PersonDto#setPhone(String)} / {@code setEmailAddress} on the web UI.
 */
final class PersonPrimaryContactSync {

	private PersonPrimaryContactSync() {
	}

	static void syncFlatFieldsWithPrimaryContactDetails(Person person) {
		removeBlankPrimaryPhoneOrEmailDetails(person);
		reconcileFlatFieldWithPrimaryDetail(person, PersonContactDetailType.PHONE, person.getPhone(), person::setPhone);
		reconcileFlatFieldWithPrimaryDetail(person, PersonContactDetailType.EMAIL, person.getEmailAddress(), person::setEmailAddress);
	}

	static List<PersonContactDetailDto> buildContactDetailDtosForPush(
		Person person,
		PersonReferenceDto personReference,
		PersonContactDetailDtoHelper dtoHelper) {

		List<PersonContactDetailDto> dtos = new ArrayList<>();
		for (PersonContactDetail detail : person.getPersonContactDetails()) {
			if (isBlankPrimaryPhoneOrEmail(detail)) {
				continue;
			}
			dtos.add(dtoHelper.adoToDto(detail));
		}
		ensurePrimaryDetail(dtos, personReference, person.getPhone(), PersonContactDetailType.PHONE);
		ensurePrimaryDetail(dtos, personReference, person.getEmailAddress(), PersonContactDetailType.EMAIL);
		return dtos;
	}

	private static void reconcileFlatFieldWithPrimaryDetail(
		Person person,
		PersonContactDetailType type,
		String flatValue,
		Consumer<String> flatFieldSetter) {

		String primaryValue = findPrimaryContactInformation(person, type);
		if (StringUtils.isNotBlank(primaryValue)) {
			flatFieldSetter.accept(primaryValue);
		} else if (StringUtils.isNotBlank(flatValue)) {
			upsertPrimaryContactDetail(person, flatValue, type);
		}
	}

	private static void removeBlankPrimaryPhoneOrEmailDetails(Person person) {
		if (person.getPersonContactDetails() == null) {
			return;
		}
		person.getPersonContactDetails().removeIf(PersonPrimaryContactSync::isBlankPrimaryPhoneOrEmail);
	}

	private static boolean isBlankPrimaryPhoneOrEmail(PersonContactDetail detail) {
		return detail.isPrimaryContact()
			&& isPhoneOrEmail(detail.getPersonContactDetailType())
			&& StringUtils.isBlank(detail.getContactInformation());
	}

	private static boolean isPhoneOrEmail(PersonContactDetailType type) {
		return type == PersonContactDetailType.PHONE || type == PersonContactDetailType.EMAIL;
	}

	private static String findPrimaryContactInformation(Person person, PersonContactDetailType type) {
		if (person.getPersonContactDetails() == null) {
			return null;
		}
		for (PersonContactDetail detail : person.getPersonContactDetails()) {
			if (detail.getPersonContactDetailType() == type && detail.isPrimaryContact()) {
				return detail.getContactInformation();
			}
		}
		return null;
	}

	private static void upsertPrimaryContactDetail(Person person, String contactInformation, PersonContactDetailType type) {
		if (person.getPersonContactDetails() == null) {
			person.setPersonContactDetails(new ArrayList<>());
		}

		PersonContactDetail existingPrimary = null;
		for (PersonContactDetail detail : person.getPersonContactDetails()) {
			if (detail.getPersonContactDetailType() == type && detail.isPrimaryContact()) {
				existingPrimary = detail;
				break;
			}
		}

		if (existingPrimary != null) {
			if (!StringUtils.equals(contactInformation, existingPrimary.getContactInformation())) {
				existingPrimary.setContactInformation(contactInformation);
			}
			return;
		}

		PersonContactDetail primaryDetail = DatabaseHelper.getPersonContactDetailDao().build();
		primaryDetail.setPerson(person);
		primaryDetail.setPrimaryContact(true);
		primaryDetail.setPersonContactDetailType(type);
		primaryDetail.setContactInformation(contactInformation);
		person.getPersonContactDetails().add(primaryDetail);
	}

	private static void ensurePrimaryDetail(
		List<PersonContactDetailDto> dtos,
		PersonReferenceDto personReference,
		String contactInformation,
		PersonContactDetailType type) {

		if (StringUtils.isBlank(contactInformation)) {
			return;
		}
		boolean hasPrimary = dtos.stream().anyMatch(detail -> detail.isPrimaryContact() && detail.getPersonContactDetailType() == type);
		if (!hasPrimary) {
			dtos.add(PersonContactDetailDto.build(personReference, true, type, null, null, contactInformation, null, false, null, null));
		}
	}
}

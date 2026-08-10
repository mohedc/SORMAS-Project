/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.contact;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Bridges the multi-selected {@link ContactDto#getContactProximities()} and the legacy single valued
 * {@link ContactDto#getContactProximity()}, which is still what the contact directory, exports, similarity search and external
 * interfaces read.
 */
public final class ContactProximitySelectionHelper {

	private static final String SEPARATOR = ",";

	private ContactProximitySelectionHelper() {
	}

	/**
	 * {@link ContactProximity} is declared from the closest to the most distant kind of contact, so the first selected value in
	 * declaration order is the closest one - matching the rule documented on the {@code Contact.contactProximityLongForm} caption.
	 */
	public static ContactProximity derivePrimaryContactProximity(Collection<ContactProximity> selected) {

		if (selected == null || selected.isEmpty()) {
			return null;
		}

		for (ContactProximity proximity : ContactProximity.values()) {
			if (selected.contains(proximity)) {
				return proximity;
			}
		}

		return null;
	}

	/**
	 * A contact needs follow-up as soon as any of the selected kinds of contact requires it. Falls back to the legacy single value for
	 * records that were created before multiple selection was supported.
	 */
	public static boolean hasFollowUp(Collection<ContactProximity> selected, ContactProximity primaryContactProximity) {

		if (selected != null && !selected.isEmpty()) {
			return selected.stream().anyMatch(ContactProximity::hasFollowUp);
		}

		return primaryContactProximity == null || primaryContactProximity.hasFollowUp();
	}

	/**
	 * Serializes in {@link ContactProximity} declaration order so that an unordered set always yields the same string and unchanged
	 * selections don't look like modifications to the synchronization.
	 */
	public static String serialize(Collection<ContactProximity> proximities) {

		if (proximities == null || proximities.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (ContactProximity proximity : ContactProximity.values()) {
			if (proximities.contains(proximity)) {
				if (sb.length() > 0) {
					sb.append(SEPARATOR);
				}
				sb.append(proximity.name());
			}
		}

		return sb.length() > 0 ? sb.toString() : null;
	}

	public static Set<ContactProximity> deserialize(String proximities) {

		Set<ContactProximity> result = new HashSet<>();
		if (proximities == null || proximities.trim().isEmpty()) {
			return result;
		}

		for (String value : proximities.split(SEPARATOR)) {
			String trimmed = value.trim();
			if (!trimmed.isEmpty()) {
				result.add(ContactProximity.valueOf(trimmed));
			}
		}

		return result;
	}

	/**
	 * Keeps both representations aligned regardless of which of them the client filled in, so that legacy clients writing only
	 * {@link ContactDto#getContactProximity()} keep producing valid data.
	 */
	public static void synchronize(ContactDto contact) {

		Set<ContactProximity> contactProximities = contact.getContactProximities();

		if (contactProximities != null && !contactProximities.isEmpty()) {
			contact.setContactProximity(derivePrimaryContactProximity(contactProximities));
		} else if (contact.getContactProximity() != null) {
			contact.setContactProximities(new HashSet<>(Collections.singletonList(contact.getContactProximity())));
		} else {
			contact.setContactProximities(new HashSet<>());
		}
	}
}

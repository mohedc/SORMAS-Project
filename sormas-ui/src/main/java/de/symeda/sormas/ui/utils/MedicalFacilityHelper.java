/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.List;
import java.util.Objects;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;

/**
 * Facility pickers list every medical accommodation facility; the facility type is derived from the selected facility.
 */
public final class MedicalFacilityHelper {

	private MedicalFacilityHelper() {
	}

	public static List<FacilityType> getMedicalFacilityTypes() {
		return FacilityType.getAccommodationTypes(FacilityTypeGroup.MEDICAL_FACILITY);
	}

	public static List<FacilityReferenceDto> getMedicalFacilities(DistrictReferenceDto district, CommunityReferenceDto community) {
		if (community != null) {
			return FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndTypes(community, getMedicalFacilityTypes(), true, false);
		}
		if (district != null) {
			return FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndTypes(district, getMedicalFacilityTypes(), true, false);
		}
		return null;
	}

	/**
	 * "Other facility" has no type of its own and counts as a hospital; None and placeholder entries have no type.
	 */
	public static FacilityType resolveFacilityType(FacilityReferenceDto facility) {
		if (facility == null || FacilityDto.NONE_FACILITY_UUID.equals(facility.getUuid())) {
			return null;
		}
		if (FacilityDto.OTHER_FACILITY_UUID.equals(facility.getUuid())) {
			return FacilityType.HOSPITAL;
		}
		FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facility.getUuid());
		return facilityDto != null ? facilityDto.getType() : null;
	}

	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	public static void setReadOnlyValue(Field field, Object value) {
		if (Objects.equals(field.getValue(), value)) {
			return;
		}
		boolean readOnly = field.isReadOnly();
		field.setReadOnly(false);
		if (value != null && field instanceof AbstractSelect && !((AbstractSelect) field).containsId(value)) {
			Item item = ((AbstractSelect) field).addItem(value);
			Property captionProperty = item != null ? item.getItemProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID) : null;
			if (captionProperty != null) {
				captionProperty.setValue(value.toString());
			}
		}
		field.setValue(value);
		field.setReadOnly(readOnly);
	}
}

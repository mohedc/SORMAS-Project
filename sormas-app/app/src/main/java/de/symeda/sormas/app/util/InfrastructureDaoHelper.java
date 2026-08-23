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

package de.symeda.sormas.app.util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.util.DataUtils.toItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.Area;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Continent;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.Subcontinent;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;

public final class InfrastructureDaoHelper {

	public static final Region unknownRegion = new Region("uuid-region-unknown");
	public static final District unknownDistrict = new District("uuid-district-unknown");
	public static final Facility unknownFacility = new Facility("uuid-facility-unknown");

	public static List<Item> itemsWithEmpty() {
		List<Item> items = new ArrayList<>();
		items.add(new Item<>("", null));

		return items;
	}

	public static List<Item> loadContinents() {
		List<Item> items = itemsWithEmpty();
		items.addAll(
			DatabaseHelper.getContinentDao()
				.queryActiveForAll(Continent.DEFAULT_NAME, true)
				.stream()
				.map(c -> new Item<>(I18nProperties.getContinentName(c.getDefaultName()), c))
				.sorted(Comparator.comparing(Item::getKey))
				.collect(Collectors.toList()));
		return items;
	}

	public static List<Item> loadSubcontinents() {
		List<Item> items = itemsWithEmpty();
		items.addAll(mapToDisplaySubcontinentNames(DatabaseHelper.getSubcontinentDao().queryActiveForAll(Subcontinent.DEFAULT_NAME, true)));
		return items;
	}

	public static List<Item> loadSubcontinentsByContinent(Continent continent) {
		List<Item> items = itemsWithEmpty();
		items.addAll(mapToDisplaySubcontinentNames(DatabaseHelper.getSubcontinentDao().queryActiveByContinent(continent)));
		return items;
	}

	private static List<Item<Subcontinent>> mapToDisplaySubcontinentNames(List<Subcontinent> subcontinents) {
		return subcontinents.stream()
			.map(c -> new Item<>(I18nProperties.getSubcontinentName(c.getDefaultName()), c))
			.sorted(Comparator.comparing(Item::getKey))
			.collect(Collectors.toList());
	}

	public static List<Item> loadCountries() {
		List<Item> items = itemsWithEmpty();
		items.addAll(mapToDisplayCountryNames(DatabaseHelper.getCountryDao().queryActiveForAll(Country.ISO_CODE, true)));

		return items;
	}

	public static List<Item> loadCountriesBySubcontinent(Subcontinent subcontinent) {
		List<Item> items = itemsWithEmpty();
		items.addAll(mapToDisplayCountryNames(DatabaseHelper.getCountryDao().queryActiveBySubcontinent(subcontinent)));

		return items;
	}

	public static List<Item> loadCountriesByContinent(Continent continent) {
		List<Item> items = new ArrayList<>();

		List<Subcontinent> subcontinents = DatabaseHelper.getSubcontinentDao().queryActiveByContinent(continent);
		subcontinents.forEach(subcontinent -> items.addAll(loadCountriesBySubcontinent(subcontinent)));

		return items;
	}

	private static List<Item<Country>> mapToDisplayCountryNames(List<Country> countries) {
		return countries.stream()
			.map(c -> new Item<>(I18nProperties.getCountryName(c.getIsoCode(), c.getName()), c))
			.sorted(Comparator.comparing(Item::getKey))
			.collect(Collectors.toList());
	}

	public static List<Item> loadAreas() {
		return toItems(DatabaseHelper.getAreaDao().queryActiveForAll());
	}

	public static List<Item> loadRegionsByArea(Area area) {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByArea(area));
	}

	public static List<Item> loadRegionsByServerCountry() {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByServerCountry());
	}

	public static List<Item> loadRegionsByCountry(Country country) {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByCountry(country));
	}

	public static List<Item> loadAllDistricts() {
		return toItems(DatabaseHelper.getDistrictDao().queryActiveForAll());
	}

	public static List<Item> loadDistricts(Region region) {
		return toItems(isEmptyRegion(region) ? new ArrayList<>() : DatabaseHelper.getDistrictDao().getByRegion(region), true);
	}

	public static List<Item> loadAllCommunities() {
		return toItems(DatabaseHelper.getCommunityDao().queryActiveForAll());
	}

	public static List<Item> loadCommunities(District district) {
		return toItems(isEmptyDistrict(district) ? new ArrayList<>() : DatabaseHelper.getCommunityDao().getByDistrict(district), true);
	}

	public static List<Item> loadFacilities(District district, Community community, FacilityType type) {
		return toItems(
			community != null
				? DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByCommunityAndType(community, type, true, false)
				: isEmptyDistrict(district)
					? new ArrayList<>()
					: DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByDistrictAndType(district, type, true, false),
			true);
	}

	public static List<Item> loadPointsOfEntry(District district) {
		return toItems(isEmptyDistrict(district) ? new ArrayList<>() : DatabaseHelper.getPointOfEntryDao().getActiveByDistrict(district, true), true);
	}

	public static void initializeRegionAreaFields(
		final ControlSpinnerField areaField,
		List<Item> initialAreas,
		Area initialArea,
		final ControlSpinnerField regionField,
		List<Item> initialRegions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> initialDistricts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> initialCommunities,
		Community initialCommunity) {

		Item areaItem = initialArea != null ? DataUtils.toItem(initialArea) : null;
		Item regionItem = initialRegion != null ? DataUtils.toItem(initialRegion) : null;
		Item districtItem = initialDistrict != null ? DataUtils.toItem(initialDistrict) : null;
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;

		if (areaItem != null && !initialAreas.contains(areaItem)) {
			initialAreas.add(areaItem);
		}
		if (regionItem != null && !initialRegions.contains(regionItem)) {
			initialRegions.add(regionItem);
		}
		if (districtItem != null && !initialDistricts.contains(districtItem)) {
			initialDistricts.add(districtItem);
		}
		if (communityItem != null && !initialCommunities.contains(communityItem)) {
			initialCommunities.add(communityItem);
		}

		areaField.initializeSpinner(initialAreas, field -> {
			Area selectedArea = (Area) field.getValue();
			if (selectedArea != null) {
				List<Item> newRegions = loadRegionsByArea(selectedArea);
				if (initialRegion != null && selectedArea.equals(initialRegion.getArea()) && !newRegions.contains(regionItem)) {
					newRegions.add(regionItem);
				}
				regionField.setSpinnerData(newRegions, regionField.getValue());
			} else {
				regionField.setSpinnerData(null);
			}
		});

		regionField.initializeSpinner(initialRegions, field -> {
			Region selectedRegion = (Region) field.getValue();
			if (selectedRegion != null) {
				List<Item> newDistricts = loadDistricts(selectedRegion);
				if (initialDistrict != null && selectedRegion.equals(initialDistrict.getRegion()) && !newDistricts.contains(districtItem)) {
					newDistricts.add(districtItem);
				}
				districtField.setSpinnerData(newDistricts, districtField.getValue());

				areaField.setValue(selectedRegion.getArea());
			} else {
				districtField.setSpinnerData(null);
			}
		});

		if (communityField != null) {
			districtField.initializeSpinner(initialDistricts, field -> {
				District selectedDistrict = (District) field.getValue();
				if (selectedDistrict != null) {
					List<Item> newCommunities = loadCommunities(selectedDistrict);
					if (initialCommunity != null
						&& selectedDistrict.equals(initialCommunity.getDistrict())
						&& !newCommunities.contains(communityItem)) {
						newCommunities.add(communityItem);
					}
					communityField.setSpinnerData(newCommunities, communityField.getValue());
				} else {
					communityField.setSpinnerData(null);
				}
			});
		} else {
			districtField.initializeSpinner(initialDistricts);
		}

		if (communityField != null) {
			communityField.initializeSpinner(initialCommunities);
		}
	}

	/**
	 * Hide facilityDetails when no static health facility is selected and adjust the caption based on
	 * the selected static health facility.
	 */
	public static void initializeHealthFacilityDetailsFieldVisibility(
		final ControlPropertyField healthFacilityField,
		final ControlPropertyField healthFacilityDetailsField) {
		initializeHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField, null);
	}

	/**
	 * Same as {@link #initializeHealthFacilityDetailsFieldVisibility(ControlPropertyField, ControlPropertyField)},
	 * but when {@code facilityOrHomeField} is set, visibility and required state follow web case create rules
	 * ({@code CaseCreateForm#updateFacilityFields}): hide details for {@link TypeOfPlace#HOME}; show "place description"
	 * for {@link TypeOfPlace#OTHER} with a none facility; keep behaviour for {@link TypeOfPlace#FACILITY}.
	 */
	public static void initializeHealthFacilityDetailsFieldVisibility(
		final ControlPropertyField healthFacilityField,
		final ControlPropertyField healthFacilityDetailsField,
		@Nullable final ControlSpinnerField facilityOrHomeField) {

		// Same rule as ControlPropertyField#setVisibilityBasedOnParentField: the value may only be erased when hiding is
		// triggered by a value change. The initial pass runs while the facility and place of stay fields are still being
		// populated, so erasing there would drop an already stored place description.
		Consumer<Boolean> refresh = clearValue -> {
			TypeOfPlace place = facilityOrHomeField != null ? (TypeOfPlace) facilityOrHomeField.getValue() : null;
			setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField, place, clearValue);
		};
		refresh.accept(false);
		healthFacilityField.addValueChangedListener(field -> refresh.accept(true));
		if (facilityOrHomeField != null) {
			facilityOrHomeField.addValueChangedListener(field -> refresh.accept(true));
		}
	}

	public static void setHealthFacilityDetailsFieldVisibility(
		ControlPropertyField healthFacilityField,
		ControlPropertyField healthFacilityDetailsField) {
		setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField, null);
	}

	public static void setHealthFacilityDetailsFieldVisibility(
		ControlPropertyField healthFacilityField,
		ControlPropertyField healthFacilityDetailsField,
		@Nullable TypeOfPlace placeOfStay) {
		setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField, placeOfStay, true);
	}

	/**
	 * @param clearValue
	 *            whether hiding the field may also erase its value. Pass {@code false} while the form is still being set
	 *            up, because the facility and place of stay fields are not populated yet at that point and erasing would
	 *            drop a place description that was entered elsewhere.
	 */
	public static void setHealthFacilityDetailsFieldVisibility(
		ControlPropertyField healthFacilityField,
		ControlPropertyField healthFacilityDetailsField,
		@Nullable TypeOfPlace placeOfStay,
		boolean clearValue) {

		Facility selectedFacility = (Facility) healthFacilityField.getValue();

		if (placeOfStay != null && TypeOfPlace.HOME.equals(placeOfStay)) {
			hideHealthFacilityDetails(healthFacilityDetailsField, clearValue, true);
			return;
		}

		if (selectedFacility == null) {
			if (TypeOfPlace.OTHER.equals(placeOfStay)) {
				// "Other" is stored as the none facility, which loadFacilities() leaves out of the facility spinner, so the
				// facility field can read null while the place description still applies. Show it and keep the stored value.
				showPlaceDescriptionForNoneFacility(healthFacilityDetailsField);
			} else if (placeOfStay != null) {
				hideHealthFacilityDetails(healthFacilityDetailsField, clearValue, true);
			} else {
				hideHealthFacilityDetails(healthFacilityDetailsField, false, false);
			}
			return;
		}

		boolean otherHealthFacility = FacilityDto.OTHER_FACILITY_UUID.equals(selectedFacility.getUuid());
		boolean noneHealthFacility = FacilityDto.NONE_FACILITY_UUID.equals(selectedFacility.getUuid());

		if (placeOfStay != null) {
			boolean showNonePlaceDescription = noneHealthFacility && TypeOfPlace.OTHER.equals(placeOfStay);
			boolean visibleAndRequired = otherHealthFacility || showNonePlaceDescription;
			if (visibleAndRequired) {
				healthFacilityDetailsField.setVisibility(VISIBLE);
				setHealthFacilityDetailsRequired(healthFacilityDetailsField, true);
				if (otherHealthFacility) {
					applyHealthFacilityDetailsCaption(
						healthFacilityDetailsField,
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				} else {
					applyHealthFacilityDetailsCaption(
						healthFacilityDetailsField,
						I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
				}
			} else {
				hideHealthFacilityDetails(healthFacilityDetailsField, clearValue, true);
			}
			return;
		}

		if (otherHealthFacility) {
			healthFacilityDetailsField.setVisibility(VISIBLE);
			String caption = I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS);
			applyHealthFacilityDetailsCaption(healthFacilityDetailsField, caption);
		} else if (noneHealthFacility) {
			healthFacilityDetailsField.setVisibility(VISIBLE);
			String caption = I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails);
			applyHealthFacilityDetailsCaption(healthFacilityDetailsField, caption);
		} else {
			hideHealthFacilityDetails(healthFacilityDetailsField, false, false);
		}
	}

	private static void showPlaceDescriptionForNoneFacility(ControlPropertyField healthFacilityDetailsField) {
		healthFacilityDetailsField.setVisibility(VISIBLE);
		setHealthFacilityDetailsRequired(healthFacilityDetailsField, true);
		applyHealthFacilityDetailsCaption(healthFacilityDetailsField, I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
	}

	private static void setHealthFacilityDetailsRequired(ControlPropertyField field, boolean required) {
		if (field instanceof ControlPropertyEditField) {
			((ControlPropertyEditField) field).setRequired(required);
		}
	}

	private static void applyHealthFacilityDetailsCaption(ControlPropertyField healthFacilityDetailsField, String caption) {
		healthFacilityDetailsField.setCaption(caption);
		if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
			((ControlPropertyEditField) healthFacilityDetailsField).setHint(caption);
		}
	}

	private static void hideHealthFacilityDetails(
		ControlPropertyField healthFacilityDetailsField,
		boolean clearValue,
		boolean clearRequired) {
		healthFacilityDetailsField.setVisibility(GONE);
		if (clearRequired) {
			setHealthFacilityDetailsRequired(healthFacilityDetailsField, false);
		}
		if (clearValue && healthFacilityDetailsField instanceof ControlPropertyEditField) {
			((ControlPropertyEditField) healthFacilityDetailsField).setValue(null);
		}
	}

	public static void initializePointOfEntryDetailsFieldVisibility(
		final ControlPropertyField pointOfEntryField,
		final ControlPropertyField pointOfEntryDetailsField) {
		setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField);
		pointOfEntryField.addValueChangedListener(e -> setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField));
	}

	public static void setPointOfEntryDetailsFieldVisibility(
		final ControlPropertyField pointOfEntryField,
		final ControlPropertyField pointOfEntryDetailsField) {
		PointOfEntry selectedPointOfEntry = (PointOfEntry) pointOfEntryField.getValue();
		if (selectedPointOfEntry != null) {
			pointOfEntryDetailsField.setVisibility(
				selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_AIRPORT_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_SEAPORT_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_POE_UUID) ? VISIBLE : GONE);
		} else {
			pointOfEntryDetailsField.setVisibility(GONE);
		}
	}

	public static boolean isEmptyRegion(@Nullable Region region) {
		return region == null || isUnknownRegion(region);
	}

	public static boolean isEmptyDistrict(@Nullable District district) {
		return district == null || isUnknownDistrict(district);
	}

	public static boolean isEmptyFacility(@Nullable Facility facility) {
		return facility == null || isUnknownFacility(facility);
	}

	public static boolean isUnknownRegion(Region region) {
		return unknownRegion.equals(region);
	}

	public static boolean isUnknownDistrict(District district) {
		return unknownDistrict.equals(district);
	}

	public static boolean isUnknownFacility(Facility healthFacility) {
		return unknownFacility.equals(healthFacility);
	}
}

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

package de.symeda.sormas.app.caze.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.IdsrType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonValidator;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class CaseNewFragment extends BaseEditFragment<FragmentCaseNewLayoutBinding, Case, Case> {

	public static final String TAG = CaseNewFragment.class.getSimpleName();

	private Case record;

	private List<Item> yearList;
	private List<Item> monthList;
	private List<Item> sexList;
	private List<Item> presentConditionList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
	private List<Item> plagueTypeList;
	private List<Item> dengueFeverTypeList;
	private List<Item> rabiesTypeList;
	private List<Item> idsrDiagnosisList;
	private List<Item> initialResponsibleDistricts;
	private List<Item> initialResponsibleCommunities;
	private List<Item> initialRegions;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> initialFacilities;
	private List<Item> initialPointsOfEntry;
	private List<Item> facilityOrHomeList;
	private List<Item> facilityTypeGroupList;
	private List<Item> countryList;

	public static CaseNewFragment newInstance(Case activityRootData) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundle().get(), activityRootData);
	}

	static CaseNewFragment newInstanceFromContact(Case activityRootData, String contactUuid) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundleWithContact(contactUuid).get(), activityRootData);
	}

	static CaseNewFragment newInstanceFromEventParticipant(Case activityRootData, String eventParticipantUuid) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundleWithEventParticipant(eventParticipantUuid).get(), activityRootData);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_new_case);
	}

	@Override
	public Case getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getDisease() != null && !diseases.contains(record.getDisease())) {
			diseaseList.add(DataUtils.toItem(record.getDisease()));
		}
		List<DiseaseVariant> diseaseVariants =
			DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, record.getDisease());
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
		dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
		rabiesTypeList = DataUtils.getEnumItems(RabiesType.class, true);
		idsrDiagnosisList = DataUtils.getEnumItems(IdsrType.class, true);

		yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
		monthList = DataUtils.getMonthItems(true);

		sexList = DataUtils.getEnumItems(Sex.class, true);
		presentConditionList = DataUtils.getEnumItems(PresentCondition.class, true);

		initialResponsibleDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialResponsibleCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRegion());
		initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getDistrict());
		initialFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getResponsibleDistrict(), record.getResponsibleCommunity(), record.getFacilityType());
		initialPointsOfEntry = InfrastructureDaoHelper.loadPointsOfEntry(record.getResponsibleDistrict());

		facilityOrHomeList = DataUtils.toItems(TypeOfPlace.FOR_CASES, true);
		facilityTypeGroupList = DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups(), true);
		countryList = InfrastructureDaoHelper.loadCountries();
	}

	@Override
	public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setCaseOriginClass(CaseOrigin.class);

		PersonValidator
			.initializeBirthDateValidation(contentBinding.personBirthdateYYYY, contentBinding.personBirthdateMM, contentBinding.personBirthdateDD);

		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);

		Facility initialHealthFacility = record.getHealthFacility();

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
			contentBinding.caseDataResponsibleRegion,
			initialRegions,
			record.getResponsibleRegion(),
			contentBinding.caseDataResponsibleDistrict,
			initialResponsibleDistricts,
			record.getResponsibleDistrict(),
			contentBinding.caseDataResponsibleCommunity,
			initialResponsibleCommunities,
			record.getResponsibleCommunity());

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFieldListeners(
			contentBinding.caseDataResponsibleRegion,
			contentBinding.caseDataResponsibleDistrict,
			record.getResponsibleDistrict(),
			contentBinding.caseDataResponsibleCommunity,
			record.getResponsibleCommunity(),
			contentBinding.caseDataFacilityType,
			contentBinding.caseDataHealthFacility,
			initialHealthFacility,
			null,
			null,
			() -> Boolean.TRUE.equals(contentBinding.caseDataDifferentPlaceOfStayJurisdiction.getValue()));

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			record,
			contentBinding.caseDataRegion,
			initialRegions,
			record.getRegion(),
			contentBinding.caseDataDistrict,
			initialDistricts,
			record.getDistrict(),
			contentBinding.caseDataCommunity,
			initialCommunities,
			record.getCommunity(),
			contentBinding.facilityOrHome,
			facilityOrHomeList,
			contentBinding.facilityTypeGroup,
			facilityTypeGroupList,
			contentBinding.caseDataFacilityType,
			null,
			contentBinding.caseDataHealthFacility,
			initialFacilities,
			record.getHealthFacility(),
			contentBinding.caseDataHealthFacilityDetails,
			contentBinding.caseDataPointOfEntry,
			initialPointsOfEntry,
			record.getPointOfEntry(),
			false,
			() -> Boolean.FALSE.equals(contentBinding.caseDataDifferentPlaceOfStayJurisdiction.getValue()));

		// trigger responsible jurisdiction change handlers removing place of stay region/district/community
		contentBinding.caseDataDifferentPlaceOfStayJurisdiction.addValueChangedListener(f -> {
			if (Boolean.FALSE.equals(f.getValue())) {
				InfrastructureFieldsDependencyHandler.instance.handleCommunityChange(
					contentBinding.caseDataResponsibleCommunity,
					contentBinding.caseDataResponsibleDistrict,
					contentBinding.caseDataHealthFacility,
					contentBinding.caseDataFacilityType,
					initialHealthFacility);
			}
		});

		contentBinding.caseDataDisease.initializeSpinner(diseaseList, DiseaseConfigurationCache.getInstance().getDefaultDisease());
		contentBinding.caseDataDiseaseVariant.initializeSpinner(diseaseVariantList);

		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
		contentBinding.caseDataHumanRabiesType.initializeSpinner(rabiesTypeList);
		contentBinding.caseDataIdsrDiagnosis.initializeSpinner(idsrDiagnosisList);
		contentBinding.caseDataReportDate.initializeDateField(getFragmentManager());
		contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

		List<Item> approximateAgeTypeList = DataUtils.getEnumItems(ApproximateAgeType.class, true);
		contentBinding.personApproximateAgeType.initializeSpinner(approximateAgeTypeList);

		contentBinding.personBirthdateDD.initializeSpinner(new ArrayList<>(), field -> updateApproximateAgeField(contentBinding));
		contentBinding.personBirthdateMM.initializeSpinner(
			monthList,
			field -> {
				updateApproximateAgeField(contentBinding);
				DataUtils.updateListOfDays(
					contentBinding.personBirthdateDD,
					(Integer) contentBinding.personBirthdateYYYY.getValue(),
					(Integer) field.getValue());
			});
		contentBinding.personBirthdateYYYY.initializeSpinner(
			yearList,
			field -> {
				updateApproximateAgeField(contentBinding);
				DataUtils.updateListOfDays(
					contentBinding.personBirthdateDD,
					(Integer) field.getValue(),
					(Integer) contentBinding.personBirthdateMM.getValue());
			});

		int year = Calendar.getInstance().get(Calendar.YEAR);
		contentBinding.personBirthdateYYYY.setSelectionOnOpen(year - 35);

		contentBinding.personSex.initializeSpinner(sexList);

		contentBinding.personCitizenship.initializeSpinner(countryList);

		contentBinding.personPresentCondition.initializeSpinner(presentConditionList);

		contentBinding.facilityOrHome.addValueChangedListener(e -> {
			TypeOfPlace place = (TypeOfPlace) e.getValue();
			if (TypeOfPlace.FACILITY.equals(place)) {
				contentBinding.facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
				contentBinding.caseDataFacilityType.setValue(FacilityType.HOSPITAL);
				User user = ConfigProvider.getUser();
				if (!user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY)) {
					contentBinding.caseDataHealthFacility.setValue(null);
				}
			} else if (TypeOfPlace.HOME.equals(place)) {
				contentBinding.caseDataHealthFacilityDetails.setValue(null);
			}
			updatePlaceOfStayDependentFieldVisibility(contentBinding);
		});

		setUpPersonHomeAddressClick(contentBinding);

		contentBinding.caseDataDisease.addValueChangedListener(e -> {
			contentBinding.rapidCaseEntryCheckBox.setVisibility(
				e.getValue() != null && ((CaseNewActivity) getActivity()).getLineListingDiseases().contains(e.getValue()) ? VISIBLE : GONE);
			updateDiseaseVariantsField(contentBinding);
			updatePresentConditionField(contentBinding);
			Disease selectedDisease = (Disease) e.getValue();
			if (selectedDisease != null) {
				super.hideFieldsForDisease(selectedDisease, contentBinding.mainContent, FormType.CASE_CREATE);
				CaseOrigin currentCaseOrigin = (CaseOrigin) contentBinding.caseDataCaseOrigin.getValue();
				contentBinding.personPassportNumber.setVisibility(currentCaseOrigin == CaseOrigin.POINT_OF_ENTRY ? VISIBLE : GONE);
				// HOME address is only required when facilityOrHome is HOME
				contentBinding.personPlaceOfStayHomeAddressLayout.setVisibility(TypeOfPlace.HOME.equals(contentBinding.facilityOrHome.getValue()) ? VISIBLE : GONE);
			}
			});
	}

	@Override
	public void onAfterLayoutBinding(final FragmentCaseNewLayoutBinding contentBinding) {
		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.caseDataHealthFacility,
			contentBinding.caseDataHealthFacilityDetails,
			contentBinding.facilityOrHome);

		contentBinding.personAddress.setValidationCallback(() -> {
			if (contentBinding.personPlaceOfStayHomeAddressLayout.getVisibility() != VISIBLE) {
				return false;
			}
			return validatePersonHomeAddressWhenRequired(contentBinding);
		});
		InfrastructureDaoHelper.initializePointOfEntryDetailsFieldVisibility(
			contentBinding.caseDataPointOfEntry,
			contentBinding.caseDataPointOfEntryDetails);

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataExternalID.setVisibility(GONE);
			contentBinding.caseDataExternalToken.setVisibility(GONE);
		} else {
			contentBinding.caseDataEpidNumber.setVisibility(GONE);
		}

		contentBinding.caseDataResponsibleRegion.setEnabled(false);
		contentBinding.caseDataResponsibleRegion.setRequired(false);
		contentBinding.caseDataResponsibleDistrict.setEnabled(false);
		contentBinding.caseDataResponsibleDistrict.setRequired(false);

		User user = ConfigProvider.getUser();

		if (user.getPointOfEntry() == null) {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.FACILITY);
		}

		if (user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY)) {
			// Hospital Informants are not allowed to create cases in another health facility
			contentBinding.caseDataCommunity.setEnabled(false);
			contentBinding.caseDataCommunity.setRequired(false);
			contentBinding.caseDataHealthFacility.setEnabled(false);
			contentBinding.caseDataHealthFacility.setRequired(false);
			contentBinding.facilityOrHome.setEnabled(false);
			contentBinding.facilityTypeGroup.setEnabled(false);
			contentBinding.caseDataFacilityType.setEnabled(false);
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setEnabled(false);
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setVisibility(GONE);
		}

		if (user.getPointOfEntry() != null) {
			contentBinding.caseDataPointOfEntry.setEnabled(false);
			contentBinding.caseDataPointOfEntry.setRequired(false);
		}

		if (user.hasJurisdictionLevel(JurisdictionLevel.COMMUNITY)) {
			// Community Informants are not allowed to create cases in another community
			contentBinding.caseDataCommunity.setEnabled(false);
			contentBinding.caseDataCommunity.setRequired(false);
		}

		// Disable personal details and disease fields when case is created from contact
		// or event person
		Bundler bundler = new Bundler(getArguments());
		if (bundler.getContactUuid() != null || bundler.getEventParticipantUuid() != null) {
			contentBinding.caseDataFirstName.setEnabled(false);
			contentBinding.caseDataLastName.setEnabled(false);
			contentBinding.personSex.setEnabled(false);
			contentBinding.personBirthdateYYYY.setEnabled(false);
			contentBinding.personBirthdateMM.setEnabled(false);
			contentBinding.personBirthdateDD.setEnabled(false);
			contentBinding.caseDataDisease.setEnabled(false);
			contentBinding.caseDataDiseaseDetails.setEnabled(false);
			contentBinding.caseDataDiseaseVariant.setEnabled(false);
			contentBinding.caseDataDiseaseVariantDetails.setEnabled(false);
			contentBinding.caseDataPlagueType.setEnabled(false);
			contentBinding.caseDataDengueFeverType.setEnabled(false);
			contentBinding.caseDataHumanRabiesType.setEnabled(false);
		}

		// Set up port health visibilities
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.caseDataDisease.setVisibility(GONE);
			contentBinding.caseDataDiseaseDetails.setVisibility(GONE);
			contentBinding.caseDataDiseaseVariant.setVisibility(GONE);
			contentBinding.caseDataDiseaseVariantDetails.setVisibility(GONE);
			contentBinding.facilityOrHome.setVisibility(GONE);
			contentBinding.personPlaceOfStayHomeAddressLayout.setVisibility(GONE);
			contentBinding.caseDataCommunity.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.facilityTypeGroup.setRequired(false);
			contentBinding.caseDataFacilityType.setRequired(false);
			contentBinding.caseDataHealthFacility.setRequired(false);
		} else if (DatabaseHelper.getPointOfEntryDao().hasActiveEntriesInDistrict()) {
			if (record.getCaseOrigin() == CaseOrigin.IN_COUNTRY) {
				contentBinding.caseDataPointOfEntry.setRequired(false);
				contentBinding.caseDataPointOfEntry.setVisibility(GONE);
			} else {
				contentBinding.caseDataHealthFacility.setRequired(false);
			}
			contentBinding.caseDataCaseOrigin.addValueChangedListener(e -> {
				if (e.getValue() == CaseOrigin.IN_COUNTRY) {
					contentBinding.caseDataPointOfEntry.setVisibility(GONE);
					contentBinding.caseDataPointOfEntry.setRequired(false);
					contentBinding.caseDataPointOfEntry.setValue(null);
					contentBinding.caseDataHealthFacility.setRequired(true);
					contentBinding.facilityOrHome.setRequired(true);
				} else {
					contentBinding.caseDataPointOfEntry.setVisibility(VISIBLE);
					contentBinding.caseDataHealthFacility.setRequired(false);
					contentBinding.caseDataPointOfEntry.setRequired(true);
					contentBinding.facilityOrHome.setRequired(false);
					contentBinding.facilityOrHome.setValue(null);
				}
				updatePlaceOfStayDependentFieldVisibility(contentBinding);
			});
		} else {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.caseDataPointOfEntry.setVisibility(GONE);
		}

		updatePlaceOfStayDependentFieldVisibility(contentBinding);
	}

	/**
	 * Same pattern as {@link de.symeda.sormas.app.person.edit.PersonEditFragment#setUpControlListeners}:
	 * {@code locationValue} binding only updates the caption; opening the editor requires a click listener.
	 */
	private void setUpPersonHomeAddressClick(FragmentCaseNewLayoutBinding contentBinding) {
		contentBinding.personAddress.setOnClickListener(v -> openPersonHomeAddressPopup(contentBinding));
	}

	private void openPersonHomeAddressPopup(FragmentCaseNewLayoutBinding contentBinding) {
		Location location = record.getPerson().getAddress();
		if (location == null) {
			location = DatabaseHelper.getLocationDao().build();
			record.getPerson().setAddress(location);
		}
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, getFieldAccessCheckers());
		locationDialog.show();
		locationDialog.showHideFieldsForDisease(record.getDisease(), FormType.CASE_CREATE_LOCATION);

		locationDialog.setPositiveCallback(() -> {
			contentBinding.personAddress.setValue(locationClone);
			record.getPerson().setAddress(locationClone);
		});
	}

	private void updatePlaceOfStayDependentFieldVisibility(FragmentCaseNewLayoutBinding contentBinding) {
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			return;
		}

		TypeOfPlace place = (TypeOfPlace) contentBinding.facilityOrHome.getValue();
		boolean home = TypeOfPlace.HOME.equals(place);
		contentBinding.personPlaceOfStayHomeAddressLayout.setVisibility(home ? VISIBLE : GONE);
		contentBinding.personAddress.setRequired(home);
		if (!home) {
			contentBinding.personAddress.disableErrorState();
		}
	}

	/**
	 * @return true if a validation error was applied (see {@link de.symeda.sormas.app.component.controls.ControlPropertyEditField#setValidationCallback}).
	 */
	private static boolean validatePersonHomeAddressWhenRequired(FragmentCaseNewLayoutBinding contentBinding) {
		Location address = contentBinding.getData().getPerson().getAddress();
		if (address == null || address.isEmptyLocation()) {
			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.requiredField));
			return true;
		}
		if (address.getRegion() == null) {
			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.validRegion));
			return true;
		}
		if (address.getDistrict() == null) {
			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.validDistrict));
			return true;
		}
		if (address.getCommunity() == null) {
			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.requiredField));
			return true;
		}
//		if (DataHelper.isNullOrEmpty(address.getVillage())) {
//			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.requiredField));
//			return true;
//		}
//		if (DataHelper.isNullOrEmpty(address.getNearestHealthFacility())) {
//			contentBinding.personAddress.enableErrorState(I18nProperties.getValidationError(Validations.requiredField));
//			return true;
//		}
		contentBinding.personAddress.disableErrorState();
		return false;
	}

	private void updateDiseaseVariantsField(FragmentCaseNewLayoutBinding contentBinding) {
		List<DiseaseVariant> diseaseVariants =
			DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, record.getDisease());
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.caseDataDiseaseVariant.setSpinnerData(diseaseVariantList);
		contentBinding.caseDataDiseaseVariant.setValue(null);
		contentBinding.caseDataDiseaseVariant.setVisibility(DataUtils.emptyOrWithOneNullItem(diseaseVariantList) ? GONE : VISIBLE);
	}

	private void updatePresentConditionField(FragmentCaseNewLayoutBinding contentBinding) {
		Disease diseaseValue = (Disease) contentBinding.caseDataDisease.getValue();
		PresentCondition presentConditionValue = (PresentCondition) contentBinding.personPresentCondition.getValue();
		List<Item> items;
		if (diseaseValue == null) {
			items = DataUtils.getEnumItems(PresentCondition.class, true);
		} else {
			items = DataUtils.getEnumItems(PresentCondition.class, true, FieldVisibilityCheckers.withDisease(diseaseValue));
		}
		if (presentConditionValue != null) {
			Item currentValueItem = new Item(presentConditionValue.toString(), presentConditionValue);
			if (!items.contains(currentValueItem)) {
				items.add(currentValueItem);
			}
		}
		contentBinding.personPresentCondition.initializeSpinner(items);
		if (presentConditionValue != null) {
			contentBinding.personPresentCondition.setValue(presentConditionValue);
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_new_layout;
	}

	private static Date calculateBirthDateValue(FragmentCaseNewLayoutBinding contentBinding) {
		Integer birthYear = (Integer) contentBinding.personBirthdateYYYY.getValue();

		if (birthYear != null) {
			contentBinding.personApproximateAge.setEnabled(false);
			contentBinding.personApproximateAgeType.setEnabled(false);

			Integer birthDay = (Integer) contentBinding.personBirthdateDD.getValue();
			Integer birthMonth = (Integer) contentBinding.personBirthdateMM.getValue();

			Calendar birthDate = new GregorianCalendar();
			birthDate.set(birthYear, birthMonth != null ? birthMonth - 1 : 0, birthDay != null ? birthDay : 1);
			return birthDate.getTime();
		}
		return null;
	}

	private static void updateApproximateAgeField(FragmentCaseNewLayoutBinding contentBinding) {
		Date birthDate = calculateBirthDateValue(contentBinding);
		if (birthDate != null) {
			contentBinding.personApproximateAge.setEnabled(false);
			contentBinding.personApproximateAgeType.setEnabled(false);

			Date to = new Date();

			DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeType.ApproximateAgeHelper.getApproximateAge(birthDate, to);
			ApproximateAgeType ageType = approximateAge.getElement1();
			contentBinding.personApproximateAge.setValue(String.valueOf(approximateAge.getElement0()));
			contentBinding.personApproximateAgeType.setValue(ageType);
		} else {
			if (contentBinding.personApproximateAge.isEnabled() == false && contentBinding.personApproximateAgeType.isEnabled() == false) {
				contentBinding.personApproximateAge.setValue(null);
				contentBinding.personApproximateAgeType.setValue(null);
			}
			contentBinding.personApproximateAge.setEnabled(true);
			contentBinding.personApproximateAgeType.setEnabled(true);
		}
	}

	void updateForRapidCaseEntry(Case lastCase) {
		setLiveValidationDisabled(true);

		record = getActivityRootData();
		record.setResponsibleRegion(lastCase.getResponsibleRegion());
		record.setResponsibleDistrict(lastCase.getResponsibleDistrict());
		record.setResponsibleCommunity(lastCase.getResponsibleCommunity());
		record.setRegion(lastCase.getRegion());
		record.setDistrict(lastCase.getDistrict());
		record.setCommunity(lastCase.getCommunity());
		record.setFacilityType(lastCase.getFacilityType());
		record.setHealthFacility(lastCase.getHealthFacility());
		record.setHealthFacilityDetails(lastCase.getHealthFacilityDetails());
		record.setDepartment(lastCase.getDepartment());
		record.setPointOfEntry(lastCase.getPointOfEntry());
		record.setPointOfEntryDetails(lastCase.getPointOfEntryDetails());
		record.setReportDate(lastCase.getReportDate());
		record.setDisease(lastCase.getDisease());
		record.setDiseaseVariant(lastCase.getDiseaseVariant());
		record.setDiseaseDetails(lastCase.getDiseaseDetails());
		record.setCaseOrigin(lastCase.getCaseOrigin());

		getContentBinding().setData(record);

		FragmentCaseNewLayoutBinding binding = getContentBinding();
		if (binding != null) {
			updatePlaceOfStayDependentFieldVisibility(binding);
		}
	}
}

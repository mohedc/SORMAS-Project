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

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.utils.InpatOutpat;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class CaseEditHospitalizationFragment extends BaseEditFragment<FragmentCaseEditHospitalizationLayoutBinding, Hospitalization, Case> {

	private Hospitalization record;
	private Case caze;

	private IEntryItemOnClickListener onPrevHosItemClickListener;

	// Static methods

	public static CaseEditHospitalizationFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditHospitalizationFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	// Instance methods

	private void setUpControlListeners() {
		onPrevHosItemClickListener = (v, item) -> {
			final PreviousHospitalization previousHospitalization = (PreviousHospitalization) item;
			final PreviousHospitalization previousHospitalizationClone = (PreviousHospitalization) previousHospitalization.clone();
			final PreviousHospitalizationDialog dialog =
				new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), previousHospitalizationClone, false);

			dialog.setPositiveCallback(() -> {
				record.getPreviousHospitalizations()
					.set(record.getPreviousHospitalizations().indexOf(previousHospitalization), previousHospitalizationClone);
				updatePreviousHospitalizations();
			});

			dialog.setDeleteCallback(() -> removePreviousHospitalization(previousHospitalization));

			dialog.show();
		};

		getContentBinding().btnAddPrevHosp.setOnClickListener(v -> {
			final PreviousHospitalization previousHospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
			final PreviousHospitalizationDialog dialog =
				new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), previousHospitalization, true);

			dialog.setPositiveCallback(() -> addPreviousHospitalization(previousHospitalization));

			dialog.setDeleteCallback(() -> removePreviousHospitalization(previousHospitalization));

			dialog.show();
		});
	}

	private ObservableArrayList<PreviousHospitalization> getPreviousHospitalizations() {
		ObservableArrayList<PreviousHospitalization> newPreHospitalizations = new ObservableArrayList<>();
		newPreHospitalizations.addAll(record.getPreviousHospitalizations());
		return newPreHospitalizations;
	}

	private void clearPreviousHospitalizations() {
		record.getPreviousHospitalizations().clear();
		updatePreviousHospitalizations();
	}

	private void removePreviousHospitalization(PreviousHospitalization item) {
		record.getPreviousHospitalizations().remove(item);
		updatePreviousHospitalizations();
	}

	private void updatePreviousHospitalizations() {
		getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());

		verifyPrevHospitalizationStatus();
	}

	private void addPreviousHospitalization(PreviousHospitalization item) {
		record.getPreviousHospitalizations().add(0, item);
		updatePreviousHospitalizations();
	}

	private void verifyPrevHospitalizationStatus() {
		YesNoUnknown hospitalizedPreviously = record.getHospitalizedPreviously();
		if (hospitalizedPreviously == YesNoUnknown.YES && getPreviousHospitalizations().size() <= 0) {
			getContentBinding().caseHospitalizationHospitalizedPreviously.enableWarningState(R.string.validation_soft_add_list_entry);
		} else {
			getContentBinding().caseHospitalizationHospitalizedPreviously.disableWarningState();
		}

		getContentBinding().caseHospitalizationHospitalizedPreviously.setEnabled(getPreviousHospitalizations().size() == 0);
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_hospitalization);
	}

	@Override
	public Hospitalization getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		caze = getActivityRootData();
		record = caze.getHospitalization();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		setUpControlListeners();

		CaseValidator.initializeHospitalizationValidation(contentBinding, caze);

		List<Item> hospitalizationReasons = DataUtils.getEnumItems(HospitalizationReasonType.class, true);

		contentBinding.setData(record);
		contentBinding.setCaze(caze);
		contentBinding.setInpatOutpatClass(InpatOutpat.class);
		contentBinding.setYesNoClass(YesNo.class);
		contentBinding.setPreviousHospitalizationList(getPreviousHospitalizations());
		contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
		getContentBinding().setPreviousHospitalizationBindCallback(this::setFieldVisibilitiesAndAccesses);
		contentBinding.caseHospitalizationHospitalizationReason.initializeSpinner(hospitalizationReasons);

		contentBinding.caseHospitalizationHospitalizedPreviously.addValueChangedListener(field -> {
			YesNoUnknown value = (YesNoUnknown) field.getValue();
			contentBinding.prevHospitalizationsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			if (value != YesNoUnknown.YES) {
				clearPreviousHospitalizations();
			}

			verifyPrevHospitalizationStatus();
		});

		Disease disease = caze.getDisease();

		if (disease != null) {
			super.hideFieldsForDisease(disease, contentBinding.mainContent, FormType.HOSPITALIZATION_EDIT);
		}
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(HospitalizationDto.class, contentBinding.mainContent);

		InfrastructureDaoHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.caseHospitalizationAdmissionHealthFacility,
			contentBinding.caseHospitalizationAdmissionHealthFacilityDetails);

		initializeAdmissionFacilityFields(contentBinding);

		// Initialize ControlDateFields
		contentBinding.caseHospitalizationAdmissionDate.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationDischargeDate.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationDateOfDiseaseOnset.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationDateFirstSeenAtHealthFacility.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationDateHealthFacilityNotifiedDistrict.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIntensiveCareUnitStart.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIntensiveCareUnitEnd.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIsolationDate.initializeDateField(getFragmentManager());
		if (contentBinding.hospitalizationDateHealthRegionNotified != null) {
			contentBinding.hospitalizationDateHealthRegionNotified.initializeDateField(getFragmentManager());
		}
		if (contentBinding.hospitalizationDateOfConsultationAtHealthFacility != null) {
			contentBinding.hospitalizationDateOfConsultationAtHealthFacility.initializeDateField(getFragmentManager());
		}

		if (caze.getDisease() != null && caze.getDisease() == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS){
			contentBinding.caseHospitalizationDateFirstSeenAtHealthFacility.setCaption("Date seen at health facility");
			contentBinding.hospitalizationDateHealthRegionNotified.setCaption("Date Health Region Notified");
		}
		if (caze.getDisease() != null && caze.getDisease() == Disease.AFP){
			contentBinding.caseHospitalizationAdmissionDate.setCaption("Date of Admission to Hospital, If Applicable");
			initializeAfpAdmissionDischargeVisibility(contentBinding);
		}

		if (caze.getDisease() != null && caze.getDisease() == Disease.CONGENITAL_RUBELLA || caze.getDisease() == Disease.CSM) {
			updateVisibityForAdminisionDischarge(
				contentBinding,
				(InpatOutpat) contentBinding.caseHospitalizationSelectInpatientOutpatient.getValue());
			contentBinding.caseHospitalizationSelectInpatientOutpatient.addValueChangedListener(new ValueChangeListener() {
				@Override
				public void onChange(ControlPropertyField field) {
					updateVisibityForAdminisionDischarge(contentBinding, (InpatOutpat) field.getValue());
				}
			});
		}

		verifyPrevHospitalizationStatus();
	}

	private void initializeAdmissionFacilityFields(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		List<Item> initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		List<Item> initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getAdmissionRegion());
		List<Item> initialFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getAdmissionDistrict(), null, FacilityType.HOSPITAL);
		if (record.getAdmissionHealthFacility() != null) {
			Item facilityItem = DataUtils.toItem(record.getAdmissionHealthFacility());
			if (facilityItem != null && !initialFacilities.contains(facilityItem)) {
				initialFacilities.add(facilityItem);
			}
		}

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
			contentBinding.caseHospitalizationAdmissionRegion,
			initialRegions,
			record.getAdmissionRegion(),
			contentBinding.caseHospitalizationAdmissionDistrict,
			initialDistricts,
			record.getAdmissionDistrict(),
			null,
			null,
			null);

		contentBinding.caseHospitalizationAdmissionHealthFacility
			.initializeSpinner(initialFacilities, record.getAdmissionHealthFacility());
		contentBinding.caseHospitalizationAdmissionDistrict.addValueChangedListener(field -> {
			District selectedDistrict = (District) field.getValue();
			List<Item> facilities = InfrastructureDaoHelper.loadFacilities(selectedDistrict, null, FacilityType.HOSPITAL);
			Facility selectedFacility = (Facility) contentBinding.caseHospitalizationAdmissionHealthFacility.getValue();
			if (selectedFacility != null) {
				Item facilityItem = DataUtils.toItem(selectedFacility);
				if (facilityItem != null && !facilities.contains(facilityItem)) {
					facilities.add(facilityItem);
				}
			}
			contentBinding.caseHospitalizationAdmissionHealthFacility.setSpinnerData(facilities, selectedFacility);
		});

		contentBinding.caseHospitalizationAdmittedToDifferentHealthFacility.addValueChangedListener(field -> {
			if (field.getValue() == YesNo.YES) {
				ensureDefaultAdmissionJurisdiction(contentBinding);
			} else {
				clearAdmissionJurisdiction(contentBinding);
			}
		});

		if (record.getAdmittedToDifferentHealthFacility() == YesNo.YES) {
			ensureDefaultAdmissionJurisdiction(contentBinding);
		}
	}

	private void ensureDefaultAdmissionJurisdiction(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		User user = ConfigProvider.getUser();

		if (contentBinding.caseHospitalizationAdmissionRegion.getValue() == null) {
			Region defaultRegion = user != null && user.getRegion() != null ? user.getRegion() : caze.getResponsibleRegion();
			if (defaultRegion != null) {
				contentBinding.caseHospitalizationAdmissionRegion.setValue(defaultRegion);
			}
		}

		if (contentBinding.caseHospitalizationAdmissionDistrict.getValue() == null) {
			District defaultDistrict = user != null && user.getDistrict() != null ? user.getDistrict() : caze.getResponsibleDistrict();
			if (defaultDistrict != null) {
				contentBinding.caseHospitalizationAdmissionDistrict.setValue(defaultDistrict);
			}
		}
	}

	private void clearAdmissionJurisdiction(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		contentBinding.caseHospitalizationAdmissionRegion.setValue(null);
		contentBinding.caseHospitalizationAdmissionDistrict.setValue(null);
		contentBinding.caseHospitalizationAdmissionHealthFacility.setValue(null);
		contentBinding.caseHospitalizationAdmissionHealthFacilityDetails.setValue(null);

		record.setAdmissionRegion(null);
		record.setAdmissionDistrict(null);
		record.setAdmissionHealthFacility(null);
		record.setAdmissionHealthFacilityDetails(null);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_hospitalization_layout;
	}

	private void initializeAfpAdmissionDischargeVisibility(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		contentBinding.caseHospitalizationSelectInpatientOutpatient
			.addValueChangedListener(field -> updateAfpAdmissionDischargeVisibility(contentBinding));

		updateAfpAdmissionDischargeVisibility(contentBinding);
	}

	private void updateAfpAdmissionDischargeVisibility(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		boolean showAdmissionDischargeDates =
			contentBinding.caseHospitalizationSelectInpatientOutpatient.getValue() == InpatOutpat.INPATIENT;

		contentBinding.caseHospitalizationAdmissionDischargeDateLayout.setVisibility(showAdmissionDischargeDates ? View.VISIBLE : View.GONE);
		contentBinding.caseHospitalizationAdmissionDate.setVisibility(showAdmissionDischargeDates ? View.VISIBLE : View.GONE);
		contentBinding.caseHospitalizationDischargeDate.setVisibility(showAdmissionDischargeDates ? View.VISIBLE : View.GONE);
	}

	private void setFieldVisibilitiesAndAccesses(View view) {
		FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
			PreviousHospitalizationDto.class,
			(ViewGroup) view,
			new FieldVisibilityCheckers(),
			getFieldAccessCheckers());

	}

	private void updateVisibityForAdminisionDischarge(
		FragmentCaseEditHospitalizationLayoutBinding contentBinding,
		InpatOutpat inpatOutpatValue) {

		boolean showCongenitalRubellaDateFields = inpatOutpatValue == InpatOutpat.OUTPATIENT || inpatOutpatValue == InpatOutpat.INPATIENT;
		int visibility = showCongenitalRubellaDateFields ? View.VISIBLE : View.GONE;

		contentBinding.caseHospitalizationAdmissionDate.setVisibility(visibility);
		contentBinding.caseHospitalizationDischargeDate.setVisibility(visibility);
	}
}

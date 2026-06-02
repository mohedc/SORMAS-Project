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

import java.util.List;

import android.content.res.Resources;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditMaternalHistoryLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class CaseEditMaternalHistoryFragment extends BaseEditFragment<FragmentCaseEditMaternalHistoryLayoutBinding, MaternalHistory, Case> {

	public static final String TAG = CaseEditMaternalHistoryFragment.class.getSimpleName();

	private MaternalHistory record;

	// Static methods

	public static CaseEditMaternalHistoryFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditMaternalHistoryFragment.class,
			null,
			activityRootData,
			null,
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_maternal_history);
	}

	@Override
	public MaternalHistory getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		Case caze = getActivityRootData();
		record = caze.getMaternalHistory();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditMaternalHistoryLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditMaternalHistoryLayoutBinding contentBinding) {
		// Initialize ControlDateFields
		contentBinding.maternalHistoryArthralgiaArthritisOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryConjunctivitisOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryMaculopapularRashOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryOtherComplicationsOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryRubellaOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistorySwollenLymphsOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryRashExposureDate.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryRubellaVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryMotherRubellaLabConfirmedDate.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryMotherTraveledDuringPregnancyDate.initializeDateField(getFragmentManager());

		setFieldVisibilitiesAndAccesses(MaternalHistoryDto.class, contentBinding.mainContent);
		

		List<Item> initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		List<Item> initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRashExposureRegion());
		List<Item> initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getRashExposureDistrict());
		InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
			contentBinding.maternalHistoryRashExposureRegion,
			initialRegions,
			record.getRashExposureRegion(),
			contentBinding.maternalHistoryRashExposureDistrict,
			initialDistricts,
			record.getRashExposureDistrict(),
			contentBinding.maternalHistoryRashExposureCommunity,
			initialCommunities,
			record.getRashExposureCommunity());


		Disease disease = getActivityRootData().getDisease();
		if (disease != null) {
			super.hideFieldsForDisease(disease, contentBinding.mainContent, FormType.MATERNAL_HISTORY_EDIT);
			if (disease == Disease.CONGENITAL_RUBELLA) {
				handleCongenitalRubella(contentBinding);
			}
		}


	}

	public void handleCongenitalRubella(FragmentCaseEditMaternalHistoryLayoutBinding contentBinding) {
		
		// listerner
		//conjunctivitis, maculopapularRash, swollenLymphs, arthralgiaArthritis, otherComplications
		// conjunctivitis date onset
		contentBinding.maternalHistoryConjunctivitisOnset.setVisibility(contentBinding.maternalHistoryConjunctivitis.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryMaculopapularRashOnset.setVisibility(contentBinding.maternalHistoryMaculopapularRash.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistorySwollenLymphsOnset.setVisibility(contentBinding.maternalHistorySwollenLymphs.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryArthralgiaArthritisOnset.setVisibility(contentBinding.maternalHistoryArthralgiaArthritis.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryOtherComplicationsOnset.setVisibility(contentBinding.maternalHistoryOtherComplications.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);


		contentBinding.maternalHistoryConjunctivitis.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryConjunctivitisOnset.setVisibility(contentBinding.maternalHistoryConjunctivitis.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.	GONE);
			}
		});
		contentBinding.maternalHistoryMaculopapularRash.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryMaculopapularRashOnset.setVisibility(contentBinding.maternalHistoryMaculopapularRash.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});
		contentBinding.maternalHistorySwollenLymphs.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistorySwollenLymphsOnset.setVisibility(contentBinding.maternalHistorySwollenLymphs.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});
		contentBinding.maternalHistoryArthralgiaArthritis.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryOtherComplicationsOnset.setVisibility(contentBinding.maternalHistoryOtherComplications.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});
		contentBinding.maternalHistoryOtherComplications.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryOtherComplicationsOnset.setVisibility(contentBinding.maternalHistoryOtherComplications.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});

		// maternalHistory_motherRubellaLabConfirmed
		contentBinding.maternalHistoryMotherRubellaLabConfirmedDate.setVisibility(contentBinding.maternalHistoryMotherRubellaLabConfirmed.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);

		contentBinding.maternalHistoryMotherRubellaLabConfirmed.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryMotherRubellaLabConfirmedDate.setVisibility(contentBinding.maternalHistoryMotherRubellaLabConfirmed.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});

		// motherTraveledDuringPregnancy
		contentBinding.maternalHistoryMotherTraveledDuringPregnancyDate.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryGestationalAgeAtTravel.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryTravelLocationDescription.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);

		contentBinding.maternalHistoryMotherTraveledDuringPregnancy.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryGestationalAgeAtTravel.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				contentBinding.maternalHistoryTravelLocationDescription.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				contentBinding.maternalHistoryMotherTraveledDuringPregnancyDate.setVisibility(contentBinding.maternalHistoryMotherTraveledDuringPregnancy.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});

		// rashExposure
		contentBinding.maternalHistoryRashExposureDate.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryGestationalAgeAtExposure.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
		contentBinding.maternalHistoryExposureLocationDescription.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);

		contentBinding.maternalHistoryRashExposure.addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.maternalHistoryRashExposureDate.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				contentBinding.maternalHistoryGestationalAgeAtExposure.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				contentBinding.maternalHistoryExposureLocationDescription.setVisibility(contentBinding.maternalHistoryRashExposure.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			}
		});
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_maternal_history_layout;
	}
}

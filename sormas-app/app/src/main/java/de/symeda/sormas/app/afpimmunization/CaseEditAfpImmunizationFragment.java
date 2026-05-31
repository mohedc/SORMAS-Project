/*
 * SORMAS(R) - Surveillance Outbreak Response Management & Analysis System
 * Copyright (C) 2016-2026 Helmholtz-Zentrum fuer Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.app.afpimmunization;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.afpimmunization.AfpImmunizationDto;
import de.symeda.sormas.api.utils.CardRecall;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.afpimmunization.AfpImmunization;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.databinding.FragmentAfpImmunizationEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class CaseEditAfpImmunizationFragment
	extends BaseEditFragment<FragmentAfpImmunizationEditLayoutBinding, AfpImmunization, Case> {

	public static final String TAG = CaseEditAfpImmunizationFragment.class.getSimpleName();

	private AfpImmunization record;

	public static CaseEditAfpImmunizationFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditAfpImmunizationFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()).andWithCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_afp_immunization);
	}

	@Override
	public AfpImmunization getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		Case caze = getActivityRootData();
		record = caze.getAfpImmunization();
		if (record == null) {
			record = DatabaseHelper.getAfpImmunizationDao().build();
			caze.setAfpImmunization(record);
		}
	}

	@Override
	public void onLayoutBinding(FragmentAfpImmunizationEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentAfpImmunizationEditLayoutBinding contentBinding) {
		contentBinding.afpImmunizationOpvDoseAtBirth.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationFirstDose.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationSecondDose.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationThirdDose.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationFourthDose.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationLastDose.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationDateLastOpvDosesReceivedThroughSia.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationDateLastIpvDosesReceivedThroughSia.initializeDateField(getFragmentManager());
		contentBinding.afpImmunizationSourceRiVaccinationInformation.initializeSpinner(DataUtils.getEnumItems(CardRecall.class, true));

		setFieldVisibilitiesAndAccesses(AfpImmunizationDto.class, contentBinding.mainContent);
		if (getActivityRootData().getDisease() != null) {
			try {
				super.hideFieldsForDisease(
					getActivityRootData().getDisease(),
					contentBinding.mainContent,
					FormType.valueOf("AFP_IMMUNIZATION_EDIT"));
			} catch (IllegalArgumentException ignored) {
				// The AFP immunization form type is provided by newer API/server builds.
			}
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_afp_immunization_edit_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}
}

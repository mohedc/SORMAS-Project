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

import android.os.Bundle;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.afpimmunization.AfpImmunizationDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.afpimmunization.AfpImmunization;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.databinding.FragmentAfpImmunizationReadLayoutBinding;

public class CaseReadAfpImmunizationFragment
	extends BaseReadFragment<FragmentAfpImmunizationReadLayoutBinding, AfpImmunization, Case> {

	public static final String TAG = CaseReadAfpImmunizationFragment.class.getSimpleName();

	private AfpImmunization record;

	public static CaseReadAfpImmunizationFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseReadAfpImmunizationFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()).andWithCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData().getAfpImmunization();
		if (record == null) {
			record = new AfpImmunization();
		}
	}

	@Override
	public void onLayoutBinding(FragmentAfpImmunizationReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentAfpImmunizationReadLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(AfpImmunizationDto.class, contentBinding.mainContent);
		if (getActivityRootData().getDisease() != null) {
			super.hideFieldsForDisease(getActivityRootData().getDisease(), contentBinding.mainContent, FormType.AFP_IMMUNIZATION_EDIT);
		}
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
	public int getReadLayout() {
		return R.layout.fragment_afp_immunization_read_layout;
	}
}

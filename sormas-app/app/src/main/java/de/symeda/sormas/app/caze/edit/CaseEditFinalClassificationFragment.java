/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.app.caze.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.ClassificationByOrigin;
import de.symeda.sormas.api.sample.FinalClassification;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentCaseEditFinalClassificationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class CaseEditFinalClassificationFragment
	extends BaseEditFragment<FragmentCaseEditFinalClassificationLayoutBinding, Case, Case> {

	public static CaseEditFinalClassificationFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditFinalClassificationFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	private Case record;

	@Override
	protected String getSubHeadingTitle() {

		Resources r = getResources();
		return r.getString(R.string.caption_case_final_classification);
	}

	@Override
	public Case getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditFinalClassificationLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setYesNoClass(YesNo.class);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditFinalClassificationLayoutBinding contentBinding) {

		contentBinding.caseDataClassificationDateFinal.initializeDateField(getFragmentManager());

		List<Item> nonCsmFinalClasses = DataUtils.toItems(
			Arrays.asList(
				FinalClassification.LAB_CONFIRMED,
				FinalClassification.CONFIRMED_BY_EPIDEMIOLOGICAL_LINKAGE,
				FinalClassification.CLINICAL,
				FinalClassification.DISCARDED,
				FinalClassification.PENDING_LAB_RESULTS));
		List<Item> csmFinalClasses = DataUtils.toItems(
			Arrays.asList(FinalClassification.LAB_CONFIRMED, FinalClassification.PROBABLE, FinalClassification.SUSPECTED));

		List<Item> classificationByOriginItems = DataUtils.toItems(Arrays.asList(ClassificationByOrigin.values()));

		Disease disease = record.getDisease();
		if (disease == Disease.CSM) {
			contentBinding.caseDataFinalClassification.initializeSpinner(csmFinalClasses, record.getFinalClassification());
		} else {
			contentBinding.caseDataFinalClassification.initializeSpinner(nonCsmFinalClasses, record.getFinalClassification());
		}
		contentBinding.caseDataClassificationByOrigin.initializeSpinner(classificationByOriginItems, record.getClassificationByOrigin());

		setFieldVisibilitiesAndAccesses(CaseDataDto.class, contentBinding.mainContent);

		if (disease == Disease.MEASLES) {
			contentBinding.caseDataMeaslesCommunityInvestigation.setVisibility(VISIBLE);
			contentBinding.caseDataMeaslesInvestigationResults.setVisibility(VISIBLE);
			contentBinding.caseDataSourceOfInfectionIdentifiedFinal.setVisibility(VISIBLE);
			contentBinding.caseDataFinalClassification.setVisibility(VISIBLE);
		} else if (disease == Disease.YELLOW_FEVER) {
			contentBinding.caseDataFinalClassification.setVisibility(VISIBLE);
			contentBinding.caseDataClassificationCommentFinal.setVisibility(VISIBLE);
		} else if (disease == Disease.CONGENITAL_RUBELLA) {
			contentBinding.caseDataFinalClassification.setVisibility(VISIBLE);
			contentBinding.caseDataFinalClassificationDiscarded.setVisibility(VISIBLE);
			contentBinding.caseDataClassificationDateFinal.setVisibility(VISIBLE);
			contentBinding.caseDataClassificationByOrigin.setVisibility(VISIBLE);
			contentBinding.caseDataInvestigatorNameFinal.setVisibility(VISIBLE);
			contentBinding.caseDataInvestigatorTelFinal.setVisibility(VISIBLE);
		} else if (disease == Disease.CSM) {
			contentBinding.caseDataFinalClassification.setVisibility(VISIBLE);
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_final_classification_layout;
	}
}

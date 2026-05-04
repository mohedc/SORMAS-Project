/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.app.caze.read;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.databinding.FragmentCaseReadFinalClassificationLayoutBinding;

public class CaseReadFinalClassificationFragment extends BaseReadFragment<FragmentCaseReadFinalClassificationLayoutBinding, Case, Case> {

	private Case record;

	public static CaseReadFinalClassificationFragment newInstance(Case activityRootData) {
		return newInstance(CaseReadFinalClassificationFragment.class, null, activityRootData);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentCaseReadFinalClassificationLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseReadFinalClassificationLayoutBinding contentBinding) {

		Disease disease = record.getDisease();
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
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_final_classification);
	}

	@Override
	public Case getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_case_read_final_classification_layout;
	}
}

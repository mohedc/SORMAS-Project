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

package de.symeda.sormas.app.sample.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.android.gms.common.api.CommonStatusCodes;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.CsfAppearance;
import de.symeda.sormas.api.sample.LpNotDoneReason;
import de.symeda.sormas.api.sample.MeningitisRdtResult;
import de.symeda.sormas.api.sample.SampleContainerType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.AdditionalTest;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.barcode.BarcodeActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.util.DataUtils;

public class SampleEditFragment extends BaseEditFragment<FragmentSampleEditLayoutBinding, Sample, Sample> {

	private Sample record;
	private Sample referredSample;
	private PathogenTest mostRecentTest;
	private AdditionalTest mostRecentAdditionalTests;

	// Enum lists

	private List<Item> sampleMaterialList;
	private List<Item> sampleSourceList;
	private List<Item> sampleSuspectedList;
	private List<Facility> labList;
	private List<Item> samplePurposeList;
	private List<Item> samplingReasonList;
	private List<String> requestedPathogenTests = new ArrayList<>();
	private List<String> requestedAdditionalTests = new ArrayList<>();
	private List<Item> finalTestResults;

	public static SampleEditFragment newInstance(Sample activityRootData) {
		return newInstanceWithFieldCheckers(
			SampleEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(getDiseaseOfAssociatedEntity(activityRootData)).andWithCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()),
			UserRight.SAMPLE_EDIT);
	}

	private void setUpControlListeners(FragmentSampleEditLayoutBinding contentBinding) {
		if (!StringUtils.isEmpty(record.getReferredToUuid())) {
			contentBinding.sampleReferredToUuid.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (referredSample != null) {
						// Activity needs to be destroyed because it is only resumed, not created otherwise
						// and therefore the record uuid is not changed
						if (getActivity() != null) {
							getActivity().finish();
						}
						SampleReadActivity.startActivity(getActivity(), record.getUuid());
					}
				}
			});
		}
	}

//	private void configureDiseaseSpecificSampleUi(FragmentSampleEditLayoutBinding contentBinding, Disease disease) {
//		contentBinding.sampleIpDakarResultsLayout.setVisibility(GONE);
//		contentBinding.sampleCsmSampleCollectionLayout.setVisibility(GONE);
//		contentBinding.sampleSampleMaterial.setVisibility(VISIBLE);
//		contentBinding.sampleSampleMaterialText.setVisibility(VISIBLE);
//		contentBinding.sampleWasSpecimenTaken.setEnabled(true);
//
//		if (disease == null) {
//			return;
//		}
//
//			contentBinding.sampleElisaIgm.initializeSpinner(DataUtils.getEnumItems(SimpleTestResultType.class, true));
//			contentBinding.sampleIpDakarPcr.initializeSpinner(DataUtils.toItems(Arrays.asList(PathogenTestResultType.values()), true));
//			contentBinding.samplePrnt.initializeSpinner(DataUtils.toItems(Arrays.asList(PathogenTestResultType.values()), true));
//		if (disease == Disease.YELLOW_FEVER && record.getId() != null) {
//			contentBinding.samplePathogenTestResult.setEnabled(false);
//		}
//		if (disease == Disease.CSM) {
//			contentBinding.sampleCsmSampleCollectionLayout.setVisibility(VISIBLE);
//			contentBinding.sampleSampleMaterial.setVisibility(GONE);
//			contentBinding.sampleSampleMaterialText.setVisibility(GONE);
//			record.setWasSpecimenTaken(YesNo.YES);
//			contentBinding.sampleWasSpecimenTaken.setValue(YesNo.YES);
//			contentBinding.sampleWasSpecimenTaken.setEnabled(false);
//			if (record.getSampleMaterial() == null) {
//				record.setSampleMaterial(SampleMaterial.CEREBROSPINAL_FLUID);
//				contentBinding.sampleSampleMaterial.setValue(SampleMaterial.CEREBROSPINAL_FLUID);
//			}
//		}
//	}

	private void setUpFieldVisibilities(final FragmentSampleEditLayoutBinding contentBinding) {
		// Most recent test layout
		if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE) {
			contentBinding.mostRecentTestLayout.setVisibility(GONE);
		} else {
			if (mostRecentTest != null) {
				contentBinding.noRecentTest.setVisibility(GONE);
			}
		}

		// Most recent additional tests layout
		if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
			&& !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.ADDITIONAL_TESTS)) {
			if (!record.isReceived()
				|| record.getSpecimenCondition() != SpecimenCondition.ADEQUATE
				|| !record.getAdditionalTestingRequested()
				|| mostRecentAdditionalTests == null) {
				contentBinding.mostRecentAdditionalTestsLayout.setVisibility(GONE);
			} else {
				if (!mostRecentAdditionalTests.hasArterialVenousGasValue()) {
					contentBinding.mostRecentAdditionalTests.arterialVenousGasLayout.setVisibility(GONE);
				}
			}
		} else {
			contentBinding.mostRecentAdditionalTestsLayout.setVisibility(GONE);
		}

		if (record.getId() == null) {
			contentBinding.samplePathogenTestResult.setVisibility(GONE);
		}
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_sample_information);
	}

	@Override
	public Sample getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		if (record.getId() != null) {
			mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
			if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
				&& !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.ADDITIONAL_TESTS)) {
				mostRecentAdditionalTests = DatabaseHelper.getAdditionalTestDao().queryMostRecentBySample(record);
			}
		}
		if (!StringUtils.isEmpty(record.getReferredToUuid())) {
			referredSample = DatabaseHelper.getSampleDao().queryUuid(record.getReferredToUuid());
		} else {
			referredSample = null;
		}

		Disease associatedDisease = getDiseaseOfAssociatedEntity(record);
		if (associatedDisease == Disease.MEASLES) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(SampleMaterial.BLOOD, SampleMaterial.THROAT_SWAB, SampleMaterial.OTHER));
		} else if (associatedDisease == Disease.CSM) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(SampleMaterial.CEREBROSPINAL_FLUID, SampleMaterial.OTHER));
		} else if (associatedDisease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(
					SampleMaterial.STOOL,
					SampleMaterial.BLOOD,
					SampleMaterial.CSF,
					SampleMaterial.OTHER));

		} else {
			sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, true, getFieldVisibilityCheckers());
		}
		sampleSourceList = DataUtils.getEnumItems(SampleSource.class, true);
		sampleSuspectedList = DataUtils.toItems(Arrays.asList(
				Disease.AFP,
				Disease.CORONAVIRUS,
				Disease.CONGENITAL_RUBELLA,
				Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
				Disease.MEASLES,
				Disease.CSM,
				Disease.NEONATAL_TETANUS,
				Disease.YELLOW_FEVER
		), true);
		labList = DatabaseHelper.getFacilityDao().getActiveLaboratories(true);
		samplePurposeList = DataUtils.getEnumItems(SamplePurpose.class, true);
		samplingReasonList = DataUtils.getEnumItems(SamplingReason.class, true, getFieldVisibilityCheckers());

		for (PathogenTestType pathogenTest : record.getRequestedPathogenTests()) {
			requestedPathogenTests.clear();
			if (pathogenTest != PathogenTestType.OTHER) {
				requestedPathogenTests.add(pathogenTest.toString());
			}
		}
		if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
			&& !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.ADDITIONAL_TESTS)) {
			requestedAdditionalTests.clear();
			for (AdditionalTestType additionalTest : record.getRequestedAdditionalTests()) {
				requestedAdditionalTests.add(additionalTest.toString());
			}
		}

		if (record.getId() != null) {
			if (DatabaseHelper.getSampleTestDao()
				.queryBySample(record)
				.stream()
				.allMatch(pathogenTest -> pathogenTest.getTestResult() == PathogenTestResultType.PENDING)) {
				finalTestResults = DataUtils.toItems(Arrays.asList(PathogenTestResultType.values()));
			} else {
				finalTestResults = DataUtils.toItems(
					Arrays.stream(PathogenTestResultType.values())
						.filter(type -> type != PathogenTestResultType.NOT_DONE)
						.collect(Collectors.toList()));
			}
		}
	}

	@Override
	public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setPathogenTest(mostRecentTest);
		contentBinding.setAdditionalTest(mostRecentAdditionalTests);
		contentBinding.setReferredSample(referredSample);
		contentBinding.setYesNoClass(YesNo.class);

		SampleValidator.initializeSampleValidation(contentBinding);

		contentBinding.setPathogenTestTypeClass(PathogenTestType.class);
		contentBinding.setAdditionalTestTypeClass(AdditionalTestType.class);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentSampleEditLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);
		setFieldVisibilitiesAndAccesses(SampleDto.class, contentBinding.mainContent);
		setUpFieldVisibilities(contentBinding);
		Disease disease = getDiseaseOfAssociatedEntity(record);
		if (disease != null) {
			FormType formType = record.getId() == null ? FormType.SAMPLE_CREATE : FormType.SAMPLE_EDIT;
			super.hideFieldsForDisease(disease, contentBinding.mainContent, formType);
		}

		if (disease == Disease.MEASLES) {
			handleMeasles(contentBinding);
		} else if (disease == Disease.YELLOW_FEVER) {
			handleYellowFever(contentBinding);
		}

		// Initialize ControlSpinnerFields
		contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList);
		contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);
		contentBinding.sampleSuspectedDisease.initializeSpinner(sampleSuspectedList);
		contentBinding.samplePurpose.setEnabled(referredSample == null || record.getSamplePurpose() != SamplePurpose.EXTERNAL);
		contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), field -> {
			Facility laboratory = (Facility) field.getValue();
			if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
				contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
			} else {
				contentBinding.sampleLabDetails.hideField(true);
			}
		});

		if (finalTestResults != null) {
			contentBinding.samplePathogenTestResult.initializeSpinner(finalTestResults);
			if (contentBinding.samplePathogenTestResult.getValue() == null) {
				contentBinding.samplePathogenTestResult.setValue(PathogenTestResultType.PENDING);
			}
		}

		//initialize sample purpose
		if (disease == Disease.AFP) {
			contentBinding.samplePurpose.initializeSpinner(samplePurposeList);
		} else {
			contentBinding.samplePurpose.initializeSpinner(DataUtils.toItems(Arrays.asList(
				SamplePurpose.INTERNAL,
				SamplePurpose.EXTERNAL
			), true));
		}

		// contentBinding.samplePurpose.initializeSpinner(samplePurposeList, field -> {
		// 	SamplePurpose samplePurpose = (SamplePurpose) field.getValue();
		// 	if (SamplePurpose.EXTERNAL == samplePurpose) {
		// 		contentBinding.externalSampleFieldsLayout.setVisibility(VISIBLE);
		// 		contentBinding.samplePathogenTestingRequested.setVisibility(ConfigProvider.getUser().equals(record.getReportingUser()) ? VISIBLE : GONE);
		// 		contentBinding.sampleAdditionalTestingRequested.setVisibility(ConfigProvider.getUser().equals(record.getReportingUser()) ? VISIBLE : GONE);
		// 	} else {
		// 		contentBinding.sampleShipped.setValue(null);
		// 		contentBinding.sampleShipmentDate.setValue(null);
		// 		contentBinding.sampleShipmentDetails.setValue(null);
		// 		contentBinding.externalSampleFieldsLayout.setVisibility(GONE);
		// 		contentBinding.samplePathogenTestingRequested.setVisibility(GONE);
		// 		contentBinding.sampleAdditionalTestingRequested.setVisibility(GONE);
		// 	}
		// });
		getContentBinding().sampleReceived.setEnabled(false);
		contentBinding.sampleDateSpecimenReceivedAtNationalLab.setEnabled(false);
		contentBinding.sampleDateSpecimenReceivedAtRegionalReferenceLab.setEnabled(false);
		contentBinding.sampleSamplingReason.initializeSpinner(samplingReasonList);

//		configureDiseaseSpecificSampleUi(contentBinding, disease);

		// Initialize ControlDateFields and ControlDateTimeFields
		contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
		contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());
		contentBinding.sampleDateFormSentToHigherLevel.initializeDateField(getFragmentManager());
		contentBinding.sampleDispatchedToRegionalColdroomDate.initializeDateField(getFragmentManager());
		contentBinding.sampleDispatchedToNationalLabByCourierDate.initializeDateField(getFragmentManager());
		contentBinding.sampleDispatchedToNationalLabByRegionDistrictDate.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenSentFromFieldToNationalLab.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenSentToRegionalReferenceLab.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenReceivedAtNationalLab.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenReceivedAtRegionalReferenceLab.initializeDateField(getFragmentManager());
		contentBinding.sampleReceivedDate.initializeDateField(getFragmentManager());
		contentBinding.sampleReceivedDate.setEnabled(false);
		contentBinding.sampleDateResultsSentToReferringClinician.initializeDateField(getFragmentManager());
		contentBinding.sampleElisaIgmDate.initializeDateField(getFragmentManager());
		contentBinding.samplePcrDate.initializeDateField(getFragmentManager());
		contentBinding.samplePrntDate.initializeDateField(getFragmentManager());
		if (contentBinding.sampleDateFormCsfDispatchedToHealthDistrict != null) {
			contentBinding.sampleDateFormCsfDispatchedToHealthDistrict.initializeDateField(getFragmentManager());
		}
		if (contentBinding.sampleDateHealthFacilityNotifyRegion != null) {
			contentBinding.sampleDateHealthFacilityNotifyRegion.initializeDateField(getFragmentManager());
		}
		if (contentBinding.sampleDateOfLp != null) {
			contentBinding.sampleDateOfLp.initializeDateField(getFragmentManager());
		}
		if (contentBinding.sampleDateSpecimenSentToLaboratoryType != null) {
			contentBinding.sampleDateSpecimenSentToLaboratoryType.initializeDateField(getFragmentManager());
		}
		contentBinding.sampleTimeOfInoculationIntoTransportMedia.initializeDateField(getFragmentManager());
		contentBinding.sampleDateTimeSampleSentToLab.initializeDateTimeField(getFragmentManager());

		// Initialize enum spinners for Meningitis fields
		if (contentBinding.sampleLpAspect != null) {
			contentBinding.sampleLpAspect.initializeSpinner(DataUtils.getEnumItems(de.symeda.sormas.api.sample.LpAspect.class, true));
		}
		if (contentBinding.sampleLpPackaging != null) {
			contentBinding.sampleLpPackaging.initializeSpinner(DataUtils.getEnumItems(de.symeda.sormas.api.sample.LpPackaging.class, true));
		}
		if (contentBinding.sampleLaboratoryType != null) {
			contentBinding.sampleLaboratoryType.initializeSpinner(DataUtils.getEnumItems(de.symeda.sormas.api.sample.LaboratoryType.class, true));
		}
		if (contentBinding.samplePackaging != null) {
			contentBinding.samplePackaging.initializeSpinner(DataUtils.getEnumItems(de.symeda.sormas.api.sample.Packaging.class, true));
		}
		contentBinding.sampleLpNotDoneReason.initializeSpinner(DataUtils.getEnumItems(LpNotDoneReason.class, true));
		contentBinding.sampleSampleContainerUsed.initializeSpinner(DataUtils.getEnumItems(SampleContainerType.class, true));
		contentBinding.sampleMeningitisRdtResult.initializeSpinner(DataUtils.getEnumItems(MeningitisRdtResult.class, true));
		contentBinding.sampleSampleContainerReceived.initializeSpinner(DataUtils.getEnumItems(SampleContainerType.class, true));
		contentBinding.sampleSampleConditionAtReception.initializeSpinner(DataUtils.getEnumItems(SpecimenCondition.class, true));
		contentBinding.sampleCsfAppearanceAtCollection.initializeSpinner(DataUtils.getEnumItems(CsfAppearance.class, true));
		contentBinding.sampleCsfAppearanceAtReception.initializeSpinner(DataUtils.getEnumItems(CsfAppearance.class, true));

		// Initialize on clicks
		contentBinding.buttonScanFieldSampleId.setOnClickListener((View v) -> {
			Intent intent = new Intent(getContext(), BarcodeActivity.class);
			startActivityForResult(intent, BarcodeActivity.RC_BARCODE_CAPTURE);
		});

		// Disable fields the user doesn't have access to - this involves almost all fields when
		// the user is not the one that originally reported the sample
		if (!ConfigProvider.getUser().equals(record.getReportingUser())) {
			contentBinding.sampleSampleDateTime.setEnabled(false);
			contentBinding.sampleSampleMaterial.setEnabled(false);
			contentBinding.sampleSampleMaterialText.setEnabled(false);
			contentBinding.sampleSampleSource.setEnabled(false);
			contentBinding.sampleLab.setEnabled(false);
			contentBinding.sampleLabDetails.setEnabled(false);
			contentBinding.sampleShipped.setEnabled(false);
			contentBinding.sampleShipmentDate.setEnabled(false);
			contentBinding.sampleShipmentDetails.setEnabled(false);
			contentBinding.samplePurpose.setEnabled(false);
			contentBinding.sampleReceived.setEnabled(false);
			contentBinding.sampleLabSampleID.setEnabled(false);
			contentBinding.sampleSpecimenCondition.setEnabled(false);
			contentBinding.samplePathogenTestingRequested.setVisibility(GONE);
			contentBinding.sampleRequestedPathogenTests.setVisibility(GONE);
			contentBinding.sampleAdditionalTestingRequested.setVisibility(GONE);
			contentBinding.sampleRequestedAdditionalTests.setVisibility(GONE);

			if (!requestedPathogenTests.isEmpty()) {
				contentBinding.sampleRequestedPathogenTestsTags.setTags(requestedPathogenTests);
				if (StringUtils.isEmpty(record.getRequestedOtherPathogenTests())) {
					contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
				}
			} else {
				contentBinding.sampleRequestedPathogenTestsTags.setVisibility(GONE);
				contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
			}

			if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
				&& !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.ADDITIONAL_TESTS)) {
				if (!requestedAdditionalTests.isEmpty()) {
					contentBinding.sampleRequestedAdditionalTestsTags.setTags(requestedAdditionalTests);
					if (StringUtils.isEmpty(record.getRequestedOtherAdditionalTests())) {
						contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
					}
				} else {
					contentBinding.sampleRequestedAdditionalTestsTags.setVisibility(GONE);
					contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
				}
			}

			if (requestedPathogenTests.isEmpty() && requestedAdditionalTests.isEmpty()) {
				contentBinding.pathogenTestingDivider.setVisibility(GONE);
			}
		} else {
			contentBinding.sampleRequestedPathogenTestsTags.setVisibility(GONE);
			contentBinding.sampleRequestedPathogenTests.removeItem(PathogenTestType.OTHER);
			contentBinding.sampleRequestedAdditionalTestsTags.setVisibility(GONE);
		}

		if (!ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
			&& !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.ADDITIONAL_TESTS)) {
			contentBinding.additionalTestingLayout.setVisibility(GONE);
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_sample_edit_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return ConfigProvider.hasUserRight(UserRight.SAMPLE_EDIT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == BarcodeActivity.RC_BARCODE_CAPTURE) {
			if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
				getContentBinding().sampleFieldSampleID.setValue(data.getStringExtra(BarcodeActivity.BARCODE_RESULT));
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void handleMeasles(FragmentSampleEditLayoutBinding contentBinding) {
		List<Item> pathogenTestResultList = DataUtils.toItems(
			Arrays.asList(PathogenTestResultType.PENDING, PathogenTestResultType.NEGATIVE, PathogenTestResultType.POSITIVE));
		contentBinding.samplePathogenTestResult.initializeSpinner(pathogenTestResultList);

		contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleDateSpecimenSentFromFieldToNationalLab.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleDateSpecimenSentToRegionalReferenceLab.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleShipped.addValueChangedListener(field -> {
			contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
			contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
			contentBinding.sampleDateSpecimenSentFromFieldToNationalLab.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
			contentBinding.sampleDateSpecimenSentToRegionalReferenceLab.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
		});
		
		//received, 
		contentBinding.sampleSpecimenCondition.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleLabSampleID.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleReceivedDate.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleDateSpecimenReceivedAtRegionalReferenceLab.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleDateSpecimenReceivedAtNationalLab.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
		contentBinding.samplePathogenTestResult.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);

	}


	private void handleYellowFever(FragmentSampleEditLayoutBinding contentBinding) {
		contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);

		contentBinding.sampleShipped.addValueChangedListener(field -> {
			contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
			contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
		});

		contentBinding.sampleReceivedDate.setVisibility(Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? VISIBLE : GONE);
	}


		protected static Disease getDiseaseOfAssociatedEntity(Sample sample) {
		if (sample.getAssociatedCase() != null) {
			return sample.getAssociatedCase().getDisease();
		} else if (sample.getAssociatedContact() != null) {
			return sample.getAssociatedContact().getDisease();
		} else if (sample.getAssociatedEventParticipant() != null) {
			return sample.getAssociatedEventParticipant().getEvent().getDisease();
		} else {
			return null;
		}
	}
}

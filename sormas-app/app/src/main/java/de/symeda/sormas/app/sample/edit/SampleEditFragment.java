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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.CsfAppearance;
import de.symeda.sormas.api.sample.LaboratoryType;
import de.symeda.sormas.api.sample.LpNotDoneReason;
import de.symeda.sormas.api.sample.MeningitisRdtResult;
import de.symeda.sormas.api.sample.SampleContainerType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
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
	private List<Item> afpPcrPrntResultList;
	private List<Item> afpElisaIgmList;
	private List<Item> afpSamplePurposeList;

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
			sampleMaterialList = DataUtils.toItems(Arrays.asList(SampleMaterial.BLOOD));
		} else if (associatedDisease == Disease.CSM) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(
					SampleMaterial.CSF,
					SampleMaterial.BLOOD,
					SampleMaterial.THROAT_SWAB,
					SampleMaterial.OTHER));
		} else if (associatedDisease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(
					SampleMaterial.STOOL,
					SampleMaterial.BLOOD,
					SampleMaterial.CSF,
					SampleMaterial.OTHER));

		} else if (associatedDisease == Disease.CONGENITAL_RUBELLA) {
			sampleMaterialList = DataUtils.toItems(Arrays.asList(SampleMaterial.BLOOD));
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

		if (associatedDisease == Disease.AFP) {
			afpPcrPrntResultList = DataUtils.toItems(
				Arrays.asList(PathogenTestResultType.POSITIVE, PathogenTestResultType.NEGATIVE, PathogenTestResultType.NOT_TESTED),
				true);
			afpElisaIgmList = DataUtils.getEnumItems(SimpleTestResultType.class, true);
			boolean isNationalUser = canSeeOutsideCountryLabTesting();
			if (isNationalUser) {
				afpSamplePurposeList = DataUtils.toItems(
					Arrays.asList(SamplePurpose.EXTERNAL, SamplePurpose.INTERNAL, SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING),
					true);
			} else {
				afpSamplePurposeList = DataUtils.toItems(
					Arrays.asList(SamplePurpose.EXTERNAL, SamplePurpose.INTERNAL),
					true);
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
		contentBinding.setCsfAppearanceClass(CsfAppearance.class);
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
		} else if (disease == Disease.CSM) {
			handleMeningitis(contentBinding);
		} else if (disease == Disease.CONGENITAL_RUBELLA) {
			handleCongenitalRubella(contentBinding);
		}

		// Initialize ControlSpinnerFields
		contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList);
		contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);
		contentBinding.sampleSuspectedDisease.initializeSpinner(sampleSuspectedList);
		contentBinding.samplePurpose.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_PURPOSE));
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

		//initialize sample purpose and AFP-specific UI
		if (disease == Disease.AFP) {
			handleAfp(contentBinding);
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
		if (contentBinding.sampleLaboratoryType != null && disease != Disease.CSM) {
			contentBinding.sampleLaboratoryType.initializeSpinner(
				DataUtils.getEnumItems(LaboratoryType.class, true, getFieldVisibilityCheckers()));
		}
		if (contentBinding.samplePackaging != null) {
			contentBinding.samplePackaging.initializeSpinner(DataUtils.getEnumItems(de.symeda.sormas.api.sample.Packaging.class, true));
		}
		contentBinding.sampleLpNotDoneReason.initializeSpinner(DataUtils.getEnumItems(LpNotDoneReason.class, true));
		contentBinding.sampleSampleContainerUsed.initializeSpinner(DataUtils.getEnumItems(SampleContainerType.class, true));
		contentBinding.sampleMeningitisRdtResult.initializeSpinner(DataUtils.getEnumItems(MeningitisRdtResult.class, true));
		contentBinding.sampleSampleContainerReceived.initializeSpinner(DataUtils.getEnumItems(SampleContainerType.class, true));
		contentBinding.sampleSampleConditionAtReception.initializeSpinner(DataUtils.getEnumItems(SpecimenCondition.class, true));
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

		// Has to run last so that it cannot be overruled by any of the configuration above
		applyReceivalRightRestrictions(contentBinding);
	}

	/**
	 * Receiving a sample and recording what the laboratory finds on arrival is reserved for laboratory personnel; see
	 * {@link UserRight#SAMPLE_EDIT_RECEIVAL}. The server rejects such changes as well, so disabling the fields keeps users without the
	 * right from producing edits that would only fail on the next synchronization.
	 */
	private void applyReceivalRightRestrictions(FragmentSampleEditLayoutBinding contentBinding) {

		if (ConfigProvider.hasUserRight(UserRight.SAMPLE_EDIT_RECEIVAL)) {
			return;
		}

		disableFields(
			contentBinding.sampleReceived,
			contentBinding.sampleReceivedDate,
			contentBinding.sampleLabSampleID,
			contentBinding.sampleLabNumber,
			contentBinding.sampleSpecimenCondition,
			contentBinding.sampleNoTestPossibleReason,
			contentBinding.samplePathogenTestResult,
			contentBinding.sampleComment,
			contentBinding.sampleSentToIpDakar,
			contentBinding.sampleSampleContainerReceived,
			contentBinding.sampleSampleContainerReceivedOther,
			contentBinding.sampleCsfAppearanceAtReception,
			contentBinding.sampleSampleConditionAtReception,
			contentBinding.sampleDateSpecimenReceivedAtNationalLab,
			contentBinding.sampleDateSpecimenReceivedAtRegionalReferenceLab,
			contentBinding.sampleDateSpecimenReceivedNationalLevel,
			contentBinding.sampleDateSpecimenReceivedInter,
			contentBinding.sampleElisaIgm,
			contentBinding.sampleElisaIgmDate,
			contentBinding.sampleIpDakarPcr,
			contentBinding.samplePcrDate,
			contentBinding.samplePrnt,
			contentBinding.samplePrntInputValue,
			contentBinding.samplePrntDate);
	}

	private static void disableFields(ControlPropertyField<?>... fields) {
		for (ControlPropertyField<?> field : fields) {
			if (field != null) {
				field.setEnabled(false);
			}
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

	private void handleMeningitis(FragmentSampleEditLayoutBinding contentBinding) {
		if (contentBinding.sampleLaboratoryType == null || contentBinding.sampleLab == null) {
			return;
		}

		contentBinding.sampleLaboratoryType.initializeSpinner(DataUtils.toItems(
			Arrays.asList(LaboratoryType.REGIONAL_LABORATORY, LaboratoryType.REFERENCE_LABORATORY),
			true));

		String defaultLabCaption = I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.LAB);

		Runnable updateLabCaption = () -> {
			LaboratoryType selectedType = (LaboratoryType) contentBinding.sampleLaboratoryType.getValue();
			if (selectedType != null) {
				contentBinding.sampleLab.setCaption("Name of " + selectedType);
			} else {
				contentBinding.sampleLab.setCaption(defaultLabCaption);
			}
		};

		updateLabCaption.run();
		contentBinding.sampleLaboratoryType.addValueChangedListener(field -> updateLabCaption.run());

		//dispached lisnter
		boolean shipped = Boolean.TRUE.equals(contentBinding.sampleShipped.getValue());
		contentBinding.sampleShipmentDate.setVisibility(shipped ? VISIBLE : GONE);
		contentBinding.sampleSampleContainerUsed.setVisibility(shipped ? VISIBLE : GONE);
		contentBinding.sampleSampleContainerUsedOther.setVisibility(shipped ? VISIBLE : GONE);
		contentBinding.sampleShipmentDetails.setVisibility(shipped ? VISIBLE : GONE);
		contentBinding.sampleSamplesNotSentReason.setVisibility(shipped ? GONE : VISIBLE);
		contentBinding.sampleShipped.addValueChangedListener(field -> {
				boolean isShipped = Boolean.TRUE.equals(field.getValue());
				contentBinding.sampleShipmentDate.setVisibility(isShipped ? VISIBLE : GONE);
				contentBinding.sampleShipmentDetails.setVisibility(isShipped ? VISIBLE : GONE);
				contentBinding.sampleSampleContainerUsed.setVisibility(isShipped ? VISIBLE : GONE);
				contentBinding.sampleSampleContainerUsedOther.setVisibility(isShipped ? VISIBLE : GONE);
				contentBinding.sampleSamplesNotSentReason.setVisibility(isShipped ? GONE : VISIBLE);
				if (isShipped) {
					contentBinding.sampleSamplesNotSentReason.setValue(null);
				} else {
					contentBinding.sampleSampleContainerUsed.setValue(null);
				}
			});

	}

	private void handleCongenitalRubella(FragmentSampleEditLayoutBinding contentBinding) {
		contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()) ? VISIBLE : GONE);
		contentBinding.sampleShipped.addValueChangedListener(field -> {
			contentBinding.sampleShipmentDate.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
			contentBinding.sampleShipmentDetails.setVisibility(Boolean.TRUE.equals(field.getValue()) ? VISIBLE : GONE);
		});
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

	private boolean canSeeOutsideCountryLabTesting() {
		User user = ConfigProvider.getUser();
		return user != null
			&& user.getUserRoles() != null
			&& user.getUserRoles().stream().anyMatch(r -> r.getLinkedDefaultUserRole() == DefaultUserRole.NATIONAL_USER);
	}

	private void handleAfp(final FragmentSampleEditLayoutBinding contentBinding) {
		// Show AFP-specific heading and container
		contentBinding.sampleHeadingStoolSpecimenCollection.setVisibility(VISIBLE);
		contentBinding.sampleAfpSampleLayout.setVisibility(VISIBLE);

		// ── Sample material: forced to STOOL ──────────────────────
		if (record.getSampleMaterial() == null) {
			record.setSampleMaterial(SampleMaterial.STOOL);
			contentBinding.sampleSampleMaterial.setValue(SampleMaterial.STOOL);
		}
		contentBinding.sampleSampleMaterial.setEnabled(false);
		contentBinding.sampleSampleMaterialText.setVisibility(GONE);

		// ── Sample purpose: role-filtered options, mandatory ──────
		contentBinding.sampleSampleMaterial.setRequired(false);
		contentBinding.samplePurpose.setRequired(true);
		contentBinding.samplePurpose.initializeSpinner(afpSamplePurposeList, field -> {
			SamplePurpose purpose = (SamplePurpose) field.getValue();
			updateAfpOutsideCountryVisibility(contentBinding, purpose);
		});

		// If non-national user somehow has OUTSIDE_COUNTRY_LAB_TESTING, clear it
		boolean isNationalUser = canSeeOutsideCountryLabTesting();
		if (!isNationalUser && record.getSamplePurpose() == SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING) {
			record.setSamplePurpose(null);
			contentBinding.samplePurpose.setValue(null);
		}

		// Init visibility immediately for current purpose value
		updateAfpOutsideCountryVisibility(contentBinding, record.getSamplePurpose());

		// ── The date the sample was collected is the date of the first specimen, so the separate field is not asked for ──
		contentBinding.sampleSampleDateTime.setCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.DATE_FIRST_SPECIMEN));
		contentBinding.sampleDateSecondSpecimen.setVisibility(VISIBLE);

		// ── Initialize AFP date fields ────────────────────────────
		contentBinding.sampleDateSecondSpecimen.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenSentNationalLevel.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenSentInter.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenReceivedNationalLevel.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenReceivedInter.initializeDateField(getFragmentManager());
		contentBinding.sampleDateSpecimenReceivedNationalLevel.setEnabled(false);
		contentBinding.sampleDateSpecimenReceivedInter.setEnabled(false);

		updateAfpShipmentVisibility(contentBinding, Boolean.TRUE.equals(contentBinding.sampleShipped.getValue()));
		contentBinding.sampleShipped.addValueChangedListener(field -> {
			updateAfpShipmentVisibility(contentBinding, Boolean.TRUE.equals(field.getValue()));
		});

		// ── ELISA IGM spinner ─────────────────────────────────────
		contentBinding.sampleElisaIgm.initializeSpinner(afpElisaIgmList);

		// ── PCR / PRNT spinners (restricted result types) ─────────
		contentBinding.sampleIpDakarPcr.initializeSpinner(afpPcrPrntResultList);
		contentBinding.samplePrnt.initializeSpinner(afpPcrPrntResultList);

		// ── SENT_TO_IP_DAKAR: visible only when RECEIVED == true ──
		updateAfpReceivedVisibility(contentBinding, record.isReceived());
		contentBinding.sampleReceived.addValueChangedListener(field -> {
			updateAfpReceivedVisibility(contentBinding, Boolean.TRUE.equals(field.getValue()));
		});

		// ── ELISA/PCR/PRNT headings: follow sentToIpDakar value ───
		updateAfpIpDakarSectionVisibility(contentBinding, record.isReceived() ? record.getSentToIpDakar() : null);
		contentBinding.sampleSentToIpDakar.addValueChangedListener(field -> {
			YesNo val = (YesNo) field.getValue();
			updateAfpIpDakarSectionVisibility(contentBinding, Boolean.TRUE.equals(contentBinding.sampleReceived.getValue()) ? val : null);
		});
	}

	private void updateAfpOutsideCountryVisibility(
		FragmentSampleEditLayoutBinding contentBinding, SamplePurpose purpose) {

		boolean isNationalUser = canSeeOutsideCountryLabTesting();
		boolean isOutside = isNationalUser && SamplePurpose.OUTSIDE_COUNTRY_LAB_TESTING == purpose;

		if (isOutside) {
			contentBinding.sampleOutsideCountryName.setVisibility(VISIBLE);
			contentBinding.sampleOutsideCountryName.setRequired(true);
			contentBinding.sampleLabDetails.setVisibility(VISIBLE);
			contentBinding.sampleLabDetails.setRequired(true);
			// Default lab to "Other facility"
			if (contentBinding.sampleLab.getValue() == null) {
				de.symeda.sormas.app.backend.facility.Facility otherFacility =
					DatabaseHelper.getFacilityDao().queryUuid(FacilityDto.OTHER_FACILITY_UUID);
				if (otherFacility != null) {
					record.setLab(otherFacility);
					contentBinding.sampleLab.setValue(otherFacility);
				}
			}
		} else {
			contentBinding.sampleOutsideCountryName.setVisibility(GONE);
			contentBinding.sampleOutsideCountryName.setRequired(false);
			contentBinding.sampleLabDetails.setRequired(false);
		}
	}

	private void updateAfpShipmentVisibility(
		FragmentSampleEditLayoutBinding contentBinding, boolean shipped) {

		int vis = shipped ? VISIBLE : GONE;
		contentBinding.sampleAfpSentDatesLayout.setVisibility(vis);
		contentBinding.sampleShipmentDate.setVisibility(vis);
		contentBinding.sampleShipmentDetails.setVisibility(vis);
		contentBinding.sampleDateSpecimenSentNationalLevel.setVisibility(vis);
		contentBinding.sampleDateSpecimenSentInter.setVisibility(vis);
	}

	private void updateAfpReceivedVisibility(
		FragmentSampleEditLayoutBinding contentBinding, boolean received) {

		int vis = received ? VISIBLE : GONE;
		contentBinding.sampleReceivedDate.setVisibility(vis);
		contentBinding.sampleLabSampleID.setVisibility(vis);
		contentBinding.sampleDateSpecimenReceivedNationalLevel.setVisibility(vis);
		contentBinding.sampleDateSpecimenReceivedInter.setVisibility(vis);
		updateAfpSentToIpDakarVisibility(contentBinding, received);
		updateAfpIpDakarSectionVisibility(contentBinding, received ? (YesNo) contentBinding.sampleSentToIpDakar.getValue() : null);
	}

	private void updateAfpSentToIpDakarVisibility(
		FragmentSampleEditLayoutBinding contentBinding, boolean received) {

		int vis = received ? VISIBLE : GONE;
		contentBinding.sampleSentToIpDakar.setVisibility(vis);
		if (!received) {
			// Also collapse everything that depends on sentToIpDakar
			updateAfpIpDakarSectionVisibility(contentBinding, null);
		}
	}

	private void updateAfpIpDakarSectionVisibility(
		FragmentSampleEditLayoutBinding contentBinding, YesNo sentToIpDakar) {

		int vis = (sentToIpDakar == YesNo.YES) ? VISIBLE : GONE;
		contentBinding.sampleHeadingElisaIgm.setVisibility(vis);
		contentBinding.sampleElisaIgm.setVisibility(vis);
		contentBinding.sampleElisaIgmDate.setVisibility(vis);
		contentBinding.sampleHeadingPcr.setVisibility(vis);
		contentBinding.sampleIpDakarPcr.setVisibility(vis);
		contentBinding.samplePcrDate.setVisibility(vis);
		contentBinding.sampleHeadingPrnt.setVisibility(vis);
		contentBinding.samplePrnt.setVisibility(vis);
		contentBinding.samplePrntDate.setVisibility(vis);
		contentBinding.samplePrntInputValue.setVisibility(vis);
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

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
import static de.symeda.sormas.api.caze.CaseConfirmationBasis.CLINICAL_CONFIRMATION;
import static de.symeda.sormas.api.caze.CaseConfirmationBasis.EPIDEMIOLOGICAL_CONFIRMATION;
import static de.symeda.sormas.api.caze.CaseConfirmationBasis.LABORATORY_DIAGNOSTIC_CONFIRMATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseConfirmationBasis;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.ContactTracingContactType;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.HospitalWardType;
import de.symeda.sormas.api.caze.IdsrType;
import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.MeningitisVaccinationSource;
import de.symeda.sormas.api.caze.NotifiedBy;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.caze.RoutineVaccinationType;
import de.symeda.sormas.api.caze.ScreeningType;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.VaccinationRecordType;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.VaccineType;
import de.symeda.sormas.api.caze.caseimport.MotherVaccinationStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.sample.LpNotDoneReason;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlCheckBoxGroupField;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogClassificationRulesLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;
import de.symeda.sormas.app.util.LocationService;

public class CaseEditFragment extends BaseEditFragment<FragmentCaseEditLayoutBinding, Case, Case> {

	public static final String TAG = CaseEditFragment.class.getSimpleName();

	private Case record;
	private CaseConfirmationBasis caseConfirmationBasis;

	// Enum lists

	private List<Item> caseClassificationList;
	private List<Item> caseIdentificationSourceList;
	private List<Item> caseScreeningTypeList;
	private List<Item> caseOutcomeList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
	private List<Item> plagueTypeList;
	private List<Item> dengueFeverTypeList;
	private List<Item> humanRabiesTypeList;
	private List<Item> idsrDiagnosisList;
	private List<Item> hospitalWardTypeList;
	private List<Item> notifiedByList;
	private List<Item> initialResponsibleDistricts;
	private List<Item> initialResponsibleCommunities;
	private List<Item> initialRegions;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> initialFacilities;
	private List<Item> quarantineList;
	private List<Item> facilityOrHomeList;
	private List<Item> facilityTypeGroupList;
	private List<Item> quarantineReasonList;
	private List<Item> endOfIsolationReasonList;
	private List<Item> contactTracingContactTypeList;
	private List<Item> infectionSettingList;
	private List<Item> caseConfirmationBasisList;
	private List<Item> vaccineTypeList;
	private List<Item> meningitisVaccinationSourceList;

	private boolean differentPlaceOfStayJurisdiction;
	private boolean meningitisHandlersRegistered;
	private boolean updatingMeningitisVaccinationVisibility;

	// Static methods

	public static CaseEditFragment newInstance(Case activityRootData) {
		CaseEditFragment caseEditFragment = newInstanceWithFieldCheckers(
			CaseEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));

		caseEditFragment.differentPlaceOfStayJurisdiction =
			activityRootData.getRegion() != null || activityRootData.getDistrict() != null || activityRootData.getCommunity() != null;

		return caseEditFragment;
	}

	// Instance methods

	private void setUpFieldVisibilities(final FragmentCaseEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(CaseDataDto.class, contentBinding.mainContent);
		InfrastructureDaoHelper
			.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

		if (!isFieldAccessible(CaseDataDto.class, contentBinding.caseDataCommunity)) {
			contentBinding.caseDataRegion.setEnabled(false);
			contentBinding.caseDataDistrict.setEnabled(false);
		}

		// Smallpox vaccination scar image
		contentBinding.caseDataSmallpoxVaccinationScar.getViewTreeObserver()
			.addOnGlobalLayoutListener(
				() -> contentBinding.smallpoxVaccinationScarImg.setVisibility(contentBinding.caseDataSmallpoxVaccinationScar.getVisibility()));

		// Port Health fields
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.facilityOrHome.setVisibility(GONE);
			contentBinding.caseDataCommunity.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else {
			if (record.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY) {
				if (record.getHealthFacility() == null) {
					contentBinding.facilityOrHome.setVisibility(GONE);
					contentBinding.caseDataCommunity.setVisibility(GONE);
					contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
					contentBinding.caseDataHealthFacility.setVisibility(GONE);
					contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
				}
			} else {
				contentBinding.pointOfEntryFieldsLayout.setVisibility(GONE);
			}
		}

		// Button panel
		DiseaseClassificationCriteria classificationCriteria = DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(record.getDisease());
		if (classificationCriteria == null || !classificationCriteria.hasAnyCriteria()) {
			contentBinding.showClassificationRules.setVisibility(GONE);
		}
		if (!ConfigProvider.hasUserRight(UserRight.CASE_REFER_FROM_POE)
			|| record.getCaseOrigin() != CaseOrigin.POINT_OF_ENTRY
			|| record.getHealthFacility() != null) {
			contentBinding.referCaseFromPoe.setVisibility(GONE);
		}
		if (contentBinding.showClassificationRules.getVisibility() == GONE && contentBinding.referCaseFromPoe.getVisibility() == GONE) {
			contentBinding.caseButtonsPanel.setVisibility(GONE);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.caseDataExternalID.setVisibility(GONE);
		} else {
			contentBinding.caseDataEpidNumber.setVisibility(GONE);
		}

		updateCaseConfirmationVisibility(contentBinding);
		updateCaseConfirmationBasis(contentBinding);

		contentBinding.caseDataQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE);
		contentBinding.caseDataQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE);

		if (!isFieldAccessible(CaseDataDto.class, contentBinding.caseDataReportLat)
			|| !isFieldAccessible(CaseDataDto.class, contentBinding.caseDataReportLon)) {
			contentBinding.caseDataPickGpsCoordinates.setVisibility(GONE);
		}

		User user = ConfigProvider.getUser();
		if (user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY) || getPrimaryData().getHealthFacility() == null) {
			// Hospital Informants are not allowed to change place of stay
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setEnabled(false);
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setVisibility(GONE);
		}

		contentBinding.caseDataDiseaseVariant.setVisibility(DataUtils.emptyOrWithOneNullItem(diseaseVariantList) ? GONE : VISIBLE);

		// Disease-specific field visibility for CIF forms
		Disease disease = record.getDisease();
		if (disease == Disease.YELLOW_FEVER) {
			// YELLOW_FEVER_LAYOUT fields
			contentBinding.caseDataAtLeastOneYellowFeverDose.setVisibility(VISIBLE);
		} else {
			contentBinding.caseDataAtLeastOneYellowFeverDose.setVisibility(GONE);
		}

		if (disease == Disease.CONGENITAL_RUBELLA) {
			handleCongenitalRubella(contentBinding);
		}
	}

	private void updateCaseConfirmationBasis(FragmentCaseEditLayoutBinding contentBinding) {
		Disease disease = record.getDisease();
		if (hideCaseConfirmationBasisField(disease) || hideCaseConfirmationDetailFields(disease)) {
			return;
		}

		boolean extendedClassification = DiseaseConfigurationCache.getInstance().usesExtendedClassification(disease);
		boolean extendedClassificationMulti = DiseaseConfigurationCache.getInstance().usesExtendedClassificationMulti(disease);

		if (extendedClassification) {
			if (extendedClassificationMulti) {
				if (contentBinding.caseDataClinicalConfirmation.getValue() == YesNoUnknown.YES) {
					contentBinding.caseDataCaseConfirmationBasis.setValue(CLINICAL_CONFIRMATION);
				} else if (contentBinding.caseDataEpidemiologicalConfirmation.getValue() == YesNoUnknown.YES) {
					contentBinding.caseDataCaseConfirmationBasis.setValue(EPIDEMIOLOGICAL_CONFIRMATION);
				} else if (contentBinding.caseDataLaboratoryDiagnosticConfirmation.getValue() == YesNoUnknown.YES) {
					contentBinding.caseDataCaseConfirmationBasis.setValue(LABORATORY_DIAGNOSTIC_CONFIRMATION);
				}
			} else {
				contentBinding.caseDataClinicalConfirmation.setValue(null);
				contentBinding.caseDataEpidemiologicalConfirmation.setValue(null);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setValue(null);

				if (contentBinding.caseDataCaseConfirmationBasis.getValue() == CaseConfirmationBasis.CLINICAL_CONFIRMATION) {
					contentBinding.caseDataClinicalConfirmation.setValue(YesNoUnknown.YES);
				} else if (contentBinding.caseDataCaseConfirmationBasis.getValue() == CaseConfirmationBasis.EPIDEMIOLOGICAL_CONFIRMATION) {
					contentBinding.caseDataEpidemiologicalConfirmation.setValue(YesNoUnknown.YES);
				} else if (contentBinding.caseDataCaseConfirmationBasis.getValue() == CaseConfirmationBasis.LABORATORY_DIAGNOSTIC_CONFIRMATION) {
					contentBinding.caseDataLaboratoryDiagnosticConfirmation.setValue(YesNoUnknown.YES);
				}

				contentBinding.caseDataClinicalConfirmation.setValue(null);
				contentBinding.caseDataEpidemiologicalConfirmation.setValue(null);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setValue(null);

				final CaseConfirmationBasis confirmedCaseClassification =
					(CaseConfirmationBasis) contentBinding.caseDataCaseConfirmationBasis.getValue();

				if (confirmedCaseClassification != null) {
					switch (confirmedCaseClassification) {
					case CLINICAL_CONFIRMATION:
						contentBinding.caseDataClinicalConfirmation.setValue(YesNoUnknown.YES);
						break;
					case EPIDEMIOLOGICAL_CONFIRMATION:
						contentBinding.caseDataEpidemiologicalConfirmation.setValue(YesNoUnknown.YES);
						break;
					case LABORATORY_DIAGNOSTIC_CONFIRMATION:
						contentBinding.caseDataLaboratoryDiagnosticConfirmation.setValue(YesNoUnknown.YES);
						break;
					}
				}
			}
		} else {
			contentBinding.caseDataClinicalConfirmation.setValue(null);
			contentBinding.caseDataEpidemiologicalConfirmation.setValue(null);
			contentBinding.caseDataLaboratoryDiagnosticConfirmation.setValue(null);
			contentBinding.caseDataCaseConfirmationBasis.setValue(null);
		}
	}

	private void updateCaseConfirmationVisibility(FragmentCaseEditLayoutBinding contentBinding) {

		Disease disease = record.getDisease();
		boolean hideCaseConfirmationBasisField = hideCaseConfirmationBasisField(disease);
		boolean hideCaseConfirmationDetailFields = hideCaseConfirmationDetailFields(disease);
		boolean extendedClassification = DiseaseConfigurationCache.getInstance().usesExtendedClassification(disease);
		if (extendedClassification) {
			boolean extendedClassificationMulti = DiseaseConfigurationCache.getInstance().usesExtendedClassificationMulti(disease);
			if (extendedClassificationMulti) {
				contentBinding.caseDataClinicalConfirmation.setVisibility(hideCaseConfirmationDetailFields ? GONE : VISIBLE);
				contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(hideCaseConfirmationDetailFields ? GONE : VISIBLE);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(hideCaseConfirmationDetailFields ? GONE : VISIBLE);
				contentBinding.caseDataCaseConfirmationBasis.setVisibility(GONE);
			} else {
				contentBinding.caseDataClinicalConfirmation.setVisibility(GONE);
				contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(GONE);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(GONE);
				contentBinding.caseDataCaseConfirmationBasis
					.setVisibility(
						!hideCaseConfirmationBasisField && record.getCaseClassification() == CaseClassification.CONFIRMED ? VISIBLE : GONE);
			}
		} else {
			contentBinding.caseDataClinicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(GONE);
			contentBinding.caseDataCaseConfirmationBasis.setVisibility(GONE);
		}
	}

	private boolean hideCaseConfirmationBasisField(Disease disease) {
		return disease == Disease.MEASLES;
	}

	private boolean hideCaseConfirmationDetailFields(Disease disease) {
		return disease == Disease.CORONAVIRUS;
	}

	private void setUpButtonListeners(FragmentCaseEditLayoutBinding contentBinding) {

		contentBinding.referCaseFromPoe.setOnClickListener(e -> {
			final CaseEditActivity activity = (CaseEditActivity) CaseEditFragment.this.getActivity();
			activity.saveData(caze -> {
				final Case caseClone = (Case) caze.clone();
				final ReferCaseFromPoeDialog referCaseFromPoeDialog = new ReferCaseFromPoeDialog(BaseActivity.getActiveActivity(), caze);
				referCaseFromPoeDialog.setPositiveCallback(() -> {
					record = caseClone;
					requestLayoutRebind();
				});
				referCaseFromPoeDialog.show();
			});
		});

		contentBinding.showClassificationRules.setOnClickListener(v -> {
			final InfoDialog classificationDialog =
				new InfoDialog(CaseEditFragment.this.getContext(), R.layout.dialog_classification_rules_layout, null);
			WebView classificationView = ((DialogClassificationRulesLayoutBinding) classificationDialog.getBinding()).content;
			classificationView.loadData(DiseaseClassificationAppHelper.buildDiseaseClassificationHtml(record.getDisease()), "text/html", "utf-8");
			classificationDialog.show();
		});
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_information);
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
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.DISEASE_VARIANT,
				Optional.ofNullable(record.getDiseaseVariant()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		if (record.getDiseaseVariant() != null && !diseaseVariants.contains(record.getDiseaseVariant())) {
			diseaseVariantList.add(DataUtils.toItem(record.getDiseaseVariant()));
		}

		caseClassificationList = DataUtils.getEnumItems(CaseClassification.class, true);
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			caseClassificationList.remove(new Item<>(CaseClassification.CONFIRMED_NO_SYMPTOMS.toString(), CaseClassification.CONFIRMED_NO_SYMPTOMS));
			caseClassificationList
				.remove(new Item<>(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS.toString(), CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));
		}
		caseIdentificationSourceList = DataUtils.getEnumItems(CaseIdentificationSource.class, true);
		caseScreeningTypeList = DataUtils.getEnumItems(ScreeningType.class, true);
		caseOutcomeList = DataUtils.getEnumItems(CaseOutcome.class, true);
		plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
		dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
		humanRabiesTypeList = DataUtils.getEnumItems(RabiesType.class, true);
		idsrDiagnosisList = DataUtils.getEnumItems(IdsrType.class, true);
		hospitalWardTypeList = DataUtils.getEnumItems(HospitalWardType.class, true);
		notifiedByList = DataUtils.getEnumItems(NotifiedBy.class, true);
		quarantineList = DataUtils.getEnumItems(QuarantineType.class, true);

		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialResponsibleDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialResponsibleCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRegion());
		initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getDistrict());
		initialFacilities = InfrastructureDaoHelper.loadFacilities(record.getDistrict(), record.getCommunity(), record.getFacilityType());
		facilityOrHomeList = DataUtils.toItems(TypeOfPlace.FOR_CASES, true);
		facilityTypeGroupList = DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups(), true);

		quarantineReasonList = DataUtils.getEnumItems(QuarantineReason.class, true);
		endOfIsolationReasonList = DataUtils.getEnumItems(EndOfIsolationReason.class, true);
		contactTracingContactTypeList = DataUtils.getEnumItems(ContactTracingContactType.class, true);
		infectionSettingList = DataUtils.getEnumItems(InfectionSetting.class, true);

		caseConfirmationBasisList = DataUtils.getEnumItems(CaseConfirmationBasis.class, true);
		vaccineTypeList = DataUtils.getEnumItems(VaccineType.class, true);
		meningitisVaccinationSourceList = DataUtils.getEnumItems(MeningitisVaccinationSource.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
		setUpButtonListeners(contentBinding);

		fillConfirmedCaseClassificationCombo();

		// Case classification warning state
		if (ConfigProvider.hasUserRight(UserRight.CASE_CLASSIFY)) {
			contentBinding.caseDataCaseClassification.addValueChangedListener(field -> {

				final CaseClassification caseClassification = (CaseClassification) field.getValue();
				getContentBinding().caseDataCaseClassification.disableWarningState();

				updateCaseConfirmationVisibility(getContentBinding());

				CaseValidator.initializeGermanCaseClassificationValidation(record, caseClassification, getContentBinding());
			});

			boolean extendedClassification = DiseaseConfigurationCache.getInstance().usesExtendedClassification(record.getDisease());
			boolean extendedClassificationMulti = DiseaseConfigurationCache.getInstance().usesExtendedClassificationMulti(record.getDisease());

			if (extendedClassification) {
				if (extendedClassificationMulti) {
					if (!hideCaseConfirmationDetailFields(record.getDisease())) {
						contentBinding.caseDataClinicalConfirmation.addValueChangedListener(field -> updateCaseConfirmationBasis(getContentBinding()));
						contentBinding.caseDataEpidemiologicalConfirmation
							.addValueChangedListener(field -> updateCaseConfirmationBasis(getContentBinding()));
						contentBinding.caseDataLaboratoryDiagnosticConfirmation
							.addValueChangedListener(field -> updateCaseConfirmationBasis(getContentBinding()));
					}
				} else if (!hideCaseConfirmationBasisField(record.getDisease())) {
					contentBinding.caseDataCaseConfirmationBasis.addValueChangedListener(field -> updateCaseConfirmationBasis(getContentBinding()));
				}
			}
		}

		FragmentActivity thisActivity = this.getActivity();
		contentBinding.caseDataDisease.addValueChangedListener(new ValueChangeListener() {

			Disease currentDisease = record.getDisease();

			@Override
			public void onChange(ControlPropertyField field) {
				if (this.currentDisease != null && contentBinding.caseDataDisease.getValue() != currentDisease) {

					int headingResId = R.string.heading_change_case_disease;
					int subHeadingResId = R.string.message_change_case_disease;
					int positiveButtonTextResId = R.string.action_change_case_disease;
					int negativeButtonTextResId = R.string.action_cancel;

					ConfirmationDialog dlg =
						new ConfirmationDialog(thisActivity, headingResId, subHeadingResId, positiveButtonTextResId, negativeButtonTextResId);
					dlg.setCancelable(false);
					dlg.setNegativeCallback(() -> contentBinding.caseDataDisease.setValue(currentDisease));
					dlg.setPositiveCallback(() -> {
						this.currentDisease = null;

						updateDiseaseVariantsField(contentBinding);
						updateVaccinationRecordTypeForDisease(contentBinding);
					});
					dlg.show();
				} else if (this.currentDisease == null) {
					// It means the disease were already changed
					updateDiseaseVariantsField(contentBinding);
					updateVaccinationRecordTypeForDisease(contentBinding);
				}
			}
		});

		contentBinding.setData(record);
		contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
		contentBinding.setYesNoClass(YesNo.class);
		contentBinding.setLpNotDoneReasonClass(LpNotDoneReason.class);
		contentBinding.setVaccinationStatusClass(VaccinationStatus.class);
		contentBinding.setRoutineVaccinationTypeClass(RoutineVaccinationType.class);
		contentBinding.setVaccinationRecordTypeClass(VaccinationRecordType.class);
		contentBinding.setTrimesterClass(Trimester.class);
		contentBinding.setDifferentPlaceOfStayJurisdiction(differentPlaceOfStayJurisdiction);
		contentBinding.setMotherVaccinationStatusClass(MotherVaccinationStatus.class);

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
			initialHealthFacility,
			contentBinding.caseDataHealthFacilityDetails,
			null,
			null,
			null,
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

		if (record.getCaseOrigin() != CaseOrigin.POINT_OF_ENTRY && isFieldAccessible(CaseDataDto.class, contentBinding.caseDataHealthFacility)) {
			contentBinding.caseDataHealthFacility.setRequired(true);
		}

		contentBinding.caseDataQuarantine.addValueChangedListener(e -> {
			boolean visible = QuarantineType.HOME.equals(contentBinding.caseDataQuarantine.getValue())
				|| QuarantineType.INSTITUTIONELL.equals(contentBinding.caseDataQuarantine.getValue());
			if (visible) {
				if (ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
					|| ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
					contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(VISIBLE);
					contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(VISIBLE);
				}
			} else {
				contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
				contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
				contentBinding.caseDataQuarantineExtended.setVisibility(GONE);
				contentBinding.caseDataQuarantineReduced.setVisibility(GONE);
			}
		});
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedVerballyDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSent.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSentDate.setVisibility(GONE);
		}

		contentBinding.caseDataQuarantineExtended.setEnabled(false);
		contentBinding.caseDataQuarantineReduced.setEnabled(false);

		contentBinding.caseDataQuarantineTo.addValueChangedListener(new ValueChangeListener() {

			private Date currentQuarantineTo = record.getQuarantineTo();
			private boolean currentQuarantineExtended = record.isQuarantineExtended();
			private boolean currentQuarantineReduced = record.isQuarantineReduced();

			@Override
			public void onChange(ControlPropertyField e) {
				Date newQuarantineTo = (Date) e.getValue();

				if (newQuarantineTo == null) {
					contentBinding.caseDataQuarantineExtended.setValue(false);
					contentBinding.caseDataQuarantineReduced.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.after(currentQuarantineTo)) {
					extendQuarantine();
				} else if (!currentQuarantineExtended) {
					contentBinding.caseDataQuarantineExtended.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.before(currentQuarantineTo)) {
					reduceQuarantine();
				} else if (!currentQuarantineReduced) {
					contentBinding.caseDataQuarantineReduced.setValue(false);
				}
			}

			private void extendQuarantine() {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_extend_quarantine,
					R.string.confirmation_extend_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.caseDataQuarantineExtended.setValue(true);
					contentBinding.caseDataQuarantineReduced.setValue(false);
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.caseDataQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}

			private void reduceQuarantine() {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_reduce_quarantine,
					R.string.confirmation_reduce_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.caseDataQuarantineExtended.setValue(false);
					contentBinding.caseDataQuarantineReduced.setValue(true);
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.caseDataQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}
		});

		contentBinding.caseDataQuarantineExtended
			.addValueChangedListener(e -> contentBinding.caseDataQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE));
		contentBinding.caseDataQuarantineReduced
			.addValueChangedListener(e -> contentBinding.caseDataQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE));

		CaseValidator.initializeEpidNumberValidation(contentBinding.caseDataEpidNumber);
		CaseValidator.initializeProhibitionToWorkIntervalValidator(contentBinding);

		contentBinding.caseDataPickGpsCoordinates.setOnClickListener(v -> {
			final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
				getActivity(),
				R.string.heading_confirmation_dialog,
				R.string.confirmation_pick_gps,
				R.string.yes,
				R.string.no);

			confirmationDialog.setPositiveCallback(() -> {
				android.location.Location phoneLocation = LocationService.instance().getLocation(getActivity());
				if (phoneLocation != null) {
					contentBinding.caseDataReportLat.setDoubleValue(phoneLocation.getLatitude());
					contentBinding.caseDataReportLon.setDoubleValue(phoneLocation.getLongitude());
					contentBinding.caseDataReportLatLonAccuracy.setFloatValue(phoneLocation.getAccuracy());
				} else {
					NotificationHelper.showNotification(getContentBinding(), NotificationType.WARNING, R.string.message_gps_problem);
				}
			});
			confirmationDialog.show();
		});

		contentBinding.caseDataReportLat.setValidationCallback(() -> {
			Double latitude = ControlTextEditField.getDoubleValue(contentBinding.caseDataReportLat);
			return ValidationHelper.validateLatitude(latitude, contentBinding.caseDataReportLat);
		});

		contentBinding.caseDataReportLon.setValidationCallback(() -> {
			Double longitude = ControlTextEditField.getDoubleValue(contentBinding.caseDataReportLon);
			return ValidationHelper.validateLongitude(longitude, contentBinding.caseDataReportLon);
		});

		Disease disease = record.getDisease();
		updateVaccinationRecordTypeForDisease(contentBinding);

		if (disease != null) {
			super.hideFieldsForDisease(disease, contentBinding.mainContent, FormType.CASE_EDIT);
		}

		if (disease == Disease.NEONATAL_TETANUS) {
			handleNNT();
		}
		if (disease == Disease.CSM) {
			handleMeningitis();
		}
		if (disease == Disease.CONGENITAL_RUBELLA) {
			handleCongenitalRubella(getContentBinding());
		}
	}

	private void fillConfirmedCaseClassificationCombo() {
		if (record.getClinicalConfirmation() == YesNoUnknown.YES) {
			getContentBinding().caseDataCaseConfirmationBasis.setValue(CLINICAL_CONFIRMATION);
		} else if (record.getEpidemiologicalConfirmation() == YesNoUnknown.YES) {
			getContentBinding().caseDataCaseConfirmationBasis.setValue(EPIDEMIOLOGICAL_CONFIRMATION);
		} else if (record.getLaboratoryDiagnosticConfirmation() == YesNoUnknown.YES) {
			getContentBinding().caseDataCaseConfirmationBasis.setValue(LABORATORY_DIAGNOSTIC_CONFIRMATION);
		}
	}

	@Override
	public void onAfterLayoutBinding(final FragmentCaseEditLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);
		if (ConfigProvider.getUser().getHealthFacility() != null || ConfigProvider.getUser().getCommunity() != null) {
			contentBinding.caseDataDistrictLevelDate.setEnabled(false);
		}

		// Initialize ControlSpinnerFields
		contentBinding.caseDataDisease.initializeSpinner(diseaseList);
		contentBinding.caseDataDiseaseVariant.initializeSpinner(diseaseVariantList);
		contentBinding.caseDataCaseClassification.initializeSpinner(caseClassificationList);
		contentBinding.caseDataCaseIdentificationSource.initializeSpinner(caseIdentificationSourceList);
		contentBinding.caseDataScreeningType.initializeSpinner(caseScreeningTypeList);
		contentBinding.caseDataOutcome.initializeSpinner(caseOutcomeList);
		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
		contentBinding.caseDataRabiesType.initializeSpinner(humanRabiesTypeList);
		contentBinding.caseDataIdsrDiagnosis.initializeSpinner(idsrDiagnosisList);
		contentBinding.caseDataNotifyingClinic.initializeSpinner(hospitalWardTypeList);
		contentBinding.caseDataNotifiedBy.initializeSpinner(notifiedByList);
		contentBinding.caseDataQuarantine.initializeSpinner(quarantineList);
		contentBinding.caseDataCaseConfirmationBasis.initializeSpinner(caseConfirmationBasisList);
		contentBinding.caseDataVaccineType.initializeSpinner(vaccineTypeList);
		contentBinding.caseDataMenacSourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataMenacwSourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataMenacwySourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataMenaConjunateSourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataPcvi3ISourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataPcvi32SourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataPcv133SourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataHibISourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataHib2SourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);
		contentBinding.caseDataHib3SourceOfVaccination.initializeSpinner(meningitisVaccinationSourceList);

		// Initialize ControlDateFields
		contentBinding.caseDataReportDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataOutcomeDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataSmallpoxLastVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataLastVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateReceivedAtDistrictLevel.initializeDateField(getFragmentManager());
		contentBinding.caseDataDistrictLevelDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineFrom.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineTo.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineOrderedVerballyDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataQuarantineOfficialOrderSentDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataMotherTTDateOne.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherTTDateTwo.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherTTDateThree.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherTTDateFour.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherTTDateFive.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherLastDoseDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataMotherGivenProtectiveDoseTTDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateOfInvestigation.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateOfNotification.initializeDateField(getFragmentManager());
		contentBinding.caseDataRegionLevelDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataNationalLevelDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataArrivalAtRegionalPublicHealthOfficeDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataArrivalAtNationalLevelDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataMenacDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataMenacwDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataMenacwyDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataMenaConjunateDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataPcvi3IDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataPcvi32Date.initializeDateField(getFragmentManager());
		contentBinding.caseDataPcv133Date.initializeDateField(getFragmentManager());
		contentBinding.caseDataHibIDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataHib2Date.initializeDateField(getFragmentManager());
		contentBinding.caseDataHib3Date.initializeDateField(getFragmentManager());
		contentBinding.caseDataDistrictNotificationDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormSentToDistrict.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormReceivedAtDistrict.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormSentToRegion.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormReceivedAtRegion.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormSentToNational.initializeDateField(getFragmentManager());
		contentBinding.caseDataDateFormReceivedAtNational.initializeDateField(getFragmentManager());

		// Replace classification user field with classified by field when case has been classified automatically
		if (contentBinding.getData().getClassificationDate() != null && contentBinding.getData().getClassificationUser() == null) {
			contentBinding.caseDataClassificationUser.setVisibility(GONE);
			contentBinding.caseDataClassifiedBy.setVisibility(VISIBLE);
			contentBinding.caseDataClassifiedBy.setValue(getResources().getString(R.string.system));
		}

		if (!isFieldAccessible(CaseDataDto.class, contentBinding.caseDataHealthFacility)) {
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityOrHome);
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityTypeGroup);
		} else if (record.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY && record.getHealthFacility() == null) {
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else if (record.getHealthFacility() != null && FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())) {
			String healthFacilityDetails = record.getHealthFacilityDetails();
			if (healthFacilityDetails != null && !healthFacilityDetails.trim().isEmpty()) {
				contentBinding.facilityOrHome.setValue(TypeOfPlace.OTHER);
			} else {
				contentBinding.facilityOrHome.setValue(TypeOfPlace.HOME);
			}
		} else {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.FACILITY);
			if (record.getFacilityType() != null) {
				contentBinding.facilityTypeGroup.setValue(record.getFacilityType().getFacilityTypeGroup());
			}
		}

		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.caseDataHealthFacility,
			contentBinding.caseDataHealthFacilityDetails,
			contentBinding.facilityOrHome);

		// Swiss fields
		contentBinding.caseDataQuarantineReasonBeforeIsolation.initializeSpinner(quarantineReasonList);
		contentBinding.caseDataEndOfIsolationReason.initializeSpinner(endOfIsolationReasonList);

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactType)
			|| isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactDate)) {
			contentBinding.caseDataContactTracingDivider.setVisibility(VISIBLE);
			contentBinding.caseDataContactTracingFirstContactHeading.setVisibility(VISIBLE);

			contentBinding.caseDataContactTracingFirstContactType.initializeSpinner(contactTracingContactTypeList);
			contentBinding.caseDataContactTracingFirstContactDate.initializeDateField(getChildFragmentManager());
		}
		// end swiss fields

		contentBinding.caseDataInfectionSetting.initializeSpinner(infectionSettingList);
		contentBinding.caseDataProhibitionToWorkFrom.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataProhibitionToWorkUntil.initializeDateField(getChildFragmentManager());

		// reinfection
		contentBinding.caseDataPreviousInfectionDate.initializeDateField(getChildFragmentManager());

		contentBinding.caseDataReportingUser.setPseudonymized(record.isPseudonymized());
		contentBinding.caseDataSurveillanceOfficer.setPseudonymized(record.isPseudonymized());

		contentBinding.caseDataNotifiedBy.addValueChangedListener(field -> {
			updateNotifiedByDetailsVisibility(contentBinding);
		});
		updateNotifiedByDetailsVisibility(contentBinding);

		updateVaccinationRecordTypeForDisease(contentBinding);
		Disease disease = record.getDisease();
		if (disease == null) {
			disease = (Disease) contentBinding.caseDataDisease.getValue();
		}
		if (disease == Disease.CSM) {
			handleMeningitis();
		}
	}

	private void updateDiseaseVariantsField(FragmentCaseEditLayoutBinding contentBinding) {
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.DISEASE_VARIANT,
				Optional.ofNullable(record.getDiseaseVariant()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.caseDataDiseaseVariant.setSpinnerData(diseaseVariantList);
		contentBinding.caseDataDiseaseVariant.setValue(null);
		contentBinding.caseDataDiseaseVariant.setVisibility(diseaseVariants.isEmpty() ? GONE : VISIBLE);
	}

	private void updateVaccinationRecordTypeForDisease(FragmentCaseEditLayoutBinding contentBinding) {
		Disease disease = (Disease) contentBinding.caseDataDisease.getValue();
		if (disease == null) {
			disease = record.getDisease();
		}
		if (Arrays.asList(Disease.MEASLES, Disease.YELLOW_FEVER, Disease.CSM).contains(disease)) {
			ControlSwitchField vaccinationRecordTypeField = contentBinding.caseDataVaccinationRecordType;
			List<Item> vaccinationRecordTypeList = new ArrayList<>();
			vaccinationRecordTypeList.add(new Item<>(VaccinationRecordType.CARD.toString(), VaccinationRecordType.CARD));
			vaccinationRecordTypeList.add(new Item<>(VaccinationRecordType.HISTORY.toString(), VaccinationRecordType.HISTORY));
			vaccinationRecordTypeField.setEnumItems(vaccinationRecordTypeList);

			VaccinationRecordType currentValue = record.getVaccinationRecordType();
			if (currentValue != null && !Arrays.asList(VaccinationRecordType.CARD, VaccinationRecordType.HISTORY).contains(currentValue)) {
				vaccinationRecordTypeField.setValue(null);
			} else {
				vaccinationRecordTypeField.setValue(currentValue);
			}
		} else if (disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			handleIDSR();
		} else {
			ControlSwitchField vaccinationRecordTypeField = contentBinding.caseDataVaccinationRecordType;
			vaccinationRecordTypeField.setEnumClass(VaccinationRecordType.class);
			vaccinationRecordTypeField.setValue(record.getVaccinationRecordType());
		}
		if (disease == Disease.CSM) {
			handleMeningitis();
		}
		if (disease == Disease.CONGENITAL_RUBELLA) {
			handleCongenitalRubella(contentBinding);
		}
	}

	private void handleCongenitalRubella(FragmentCaseEditLayoutBinding contentBinding) {
		boolean showInvestigationDate = InvestigationStatus.DONE.equals(record.getInvestigationStatus())
			|| InvestigationStatus.DISCARDED.equals(record.getInvestigationStatus());

		contentBinding.caseDataDateOfInvestigation.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.INVESTIGATED_DATE));
		contentBinding.caseDataDateOfInvestigation.setValue(record.getInvestigatedDate());
		contentBinding.caseDataDateOfInvestigation.setEnabled(false);
		contentBinding.caseDataDateOfInvestigation.setVisibility(showInvestigationDate ? VISIBLE : GONE);
	}

	private void handleNNT() {
		FragmentCaseEditLayoutBinding contentBinding = getContentBinding();
		contentBinding.headingAdditionalMedicalInformation.setVisibility(GONE);
		contentBinding.caseDataHeadingInvestigatingOfficer.setVisibility(VISIBLE);

		contentBinding.caseDataMotherVaccinatedWithTT.addValueChangedListener(field -> updateNntMotherDoseDateVisibility(contentBinding));
		contentBinding.caseDataMotherHaveCard.addValueChangedListener(field -> updateNntMotherDoseDateVisibility(contentBinding));
		contentBinding.caseDataMotherNumberOfDoses.addValueChangedListener(field -> updateNntMotherDoseDateVisibility(contentBinding));

		updateNntMotherDoseDateVisibility(contentBinding);
	}

	private void updateNotifiedByDetailsVisibility(FragmentCaseEditLayoutBinding contentBinding) {
		Object notifiedBy = contentBinding.caseDataNotifiedBy.getValue();
		boolean hasNotifiedBy = notifiedBy != null;

		contentBinding.caseDataNotifiedBy.setVisibility(VISIBLE);
		contentBinding.caseDataNotifiedByDetails.setVisibility(hasNotifiedBy ? VISIBLE : GONE);
		if (!hasNotifiedBy && contentBinding.caseDataNotifiedByDetails.getValue() != null) {
			contentBinding.caseDataNotifiedByDetails.setValue(null);
		}
	}

	private void updateNntMotherDoseDateVisibility(FragmentCaseEditLayoutBinding contentBinding) {
		YesNoUnknown motherHaveCard = (YesNoUnknown) contentBinding.caseDataMotherHaveCard.getValue();
		boolean hasCard = motherHaveCard == YesNoUnknown.YES;
		contentBinding.caseDataMotherNumberOfDoses.setVisibility(hasCard ? VISIBLE : GONE);

		int numberOfDoses = 0;
		String motherNumberOfDoses = null;
		if (hasCard && contentBinding.caseDataMotherNumberOfDoses.getValue() != null) {
			motherNumberOfDoses = contentBinding.caseDataMotherNumberOfDoses.getValue().toString();
		}

		if (motherNumberOfDoses != null && !motherNumberOfDoses.isEmpty()) {
			try {
				numberOfDoses = Integer.parseInt(motherNumberOfDoses);
			} catch (NumberFormatException e) {
				Log.e("NNT", "Invalid number format: " + motherNumberOfDoses, e);
			}
		}

		contentBinding.caseDataMotherTTDateOne.setVisibility(numberOfDoses >= 1 ? VISIBLE : GONE);
		contentBinding.caseDataMotherTTDateTwo.setVisibility(numberOfDoses >= 2 ? VISIBLE : GONE);
		contentBinding.caseDataMotherTTDateThree.setVisibility(numberOfDoses >= 3 ? VISIBLE : GONE);
		contentBinding.caseDataMotherTTDateFour.setVisibility(numberOfDoses >= 4 ? VISIBLE : GONE);
		contentBinding.caseDataMotherTTDateFive.setVisibility(numberOfDoses >= 5 ? VISIBLE : GONE);
		contentBinding.caseDataMotherLastDoseDate.setVisibility(numberOfDoses >= 6 ? VISIBLE : GONE);

		contentBinding.caseDataVaccinationDateOneTwoLayout.setVisibility(numberOfDoses >= 1 ? VISIBLE : GONE);
		contentBinding.caseDataVaccinationDateThreeFourLayout.setVisibility(numberOfDoses >= 3 ? VISIBLE : GONE);
		contentBinding.caseDataVaccinationDateFiveLastDoseLayout.setVisibility(numberOfDoses >= 5 ? VISIBLE : GONE);
	}

	private void handleIDSR() {
		FragmentCaseEditLayoutBinding contentBinding = getContentBinding();
		ControlSwitchField vaccinationRecordTypeField = contentBinding.caseDataVaccinationRecordType;
		List<Item> vaccinationRecordTypeList = new ArrayList<>();
		vaccinationRecordTypeList.add(new Item<>(VaccinationRecordType.CARD.toString(), VaccinationRecordType.CARD));
		vaccinationRecordTypeList.add(new Item<>(VaccinationRecordType.HISTORY.toString(), VaccinationRecordType.HISTORY));
		vaccinationRecordTypeField.setEnumItems(vaccinationRecordTypeList);
		VaccinationRecordType currentValue = record.getVaccinationRecordType();
		if (currentValue != null && !Arrays.asList(VaccinationRecordType.CARD, VaccinationRecordType.HISTORY).contains(currentValue)) {
			vaccinationRecordTypeField.setValue(null);
		} else {
			vaccinationRecordTypeField.setValue(currentValue);
		}
		vaccinationRecordTypeField.addValueChangedListener(field -> {
			VaccinationRecordType value = (VaccinationRecordType) field.getValue();
			boolean showLastVaccinationDate = value == VaccinationRecordType.CARD;
			contentBinding.caseDataLastVaccinationDate.setVisibility(showLastVaccinationDate ? VISIBLE : GONE);
			if (!showLastVaccinationDate) {
				contentBinding.caseDataLastVaccinationDate.setValue(null);
			}
		});
		contentBinding.caseDataNumberOfVaccinationDoses.setCaption("Number of vaccine doses received:");
	}


	private boolean isCsmCaseVaccinated(FragmentCaseEditLayoutBinding contentBinding) {
		return VaccinationStatus.VACCINATED.equals(contentBinding.caseDataVaccinated.getValue())
			|| VaccinationStatus.VACCINATED.equals(contentBinding.caseDataVaccinationStatus.getValue());
	}

	private void setFieldAndParentsVisible(View field, View sectionRoot) {
		View view = field;
		while (view != null && view != sectionRoot) {
			view.setVisibility(VISIBLE);
			if (!(view.getParent() instanceof View)) {
				break;
			}
			view = (View) view.getParent();
		}
	}

	private void hideControlField(ControlPropertyField field, boolean eraseValue) {
		if (field == null) {
			return;
		}
		field.setVisibility(GONE);
		if (eraseValue && field.getValue() != null) {
			field.setValue(null);
		}
	}

	private void handleMeningitis() {
		FragmentCaseEditLayoutBinding contentBinding = getContentBinding();
		View csmSection = contentBinding.caseDataCsmExtendedSection;
		csmSection.setVisibility(VISIBLE);
		contentBinding.caseDataNumberOfVaccinationDoses.setVisibility(GONE);

		// Field ids with underscores (pcvi3_2, pcv13_3) resolve to wrong auto-captions ("2", "3")
		contentBinding.caseDataPcvi32.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCVI3_2));
		contentBinding.caseDataPcvi32Date.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCVI3_2_DATE));
		contentBinding.caseDataPcv133.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCV13_3));
		contentBinding.caseDataPcv133Date.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCV13_3_DATE));

		String sourceCaptionSuffix = " Source of Vaccination";
		contentBinding.caseDataMenacSourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MENAC) + sourceCaptionSuffix);
		contentBinding.caseDataMenacwSourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MENACW) + sourceCaptionSuffix);
		contentBinding.caseDataMenacwySourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MENACWY) + sourceCaptionSuffix);
		contentBinding.caseDataMenaConjunateSourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MENA_CONJUNATE) + sourceCaptionSuffix);
		contentBinding.caseDataPcvi3ISourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCVI3_I) + sourceCaptionSuffix);
		contentBinding.caseDataPcvi32SourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCVI3_2) + sourceCaptionSuffix);
		contentBinding.caseDataPcv133SourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PCV13_3) + sourceCaptionSuffix);
		contentBinding.caseDataHibISourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HIB_I) + sourceCaptionSuffix);
		contentBinding.caseDataHib2SourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HIB_2) + sourceCaptionSuffix);
		contentBinding.caseDataHib3SourceOfVaccination.setCaption(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HIB_3) + sourceCaptionSuffix);

		ControlSwitchField[] vaccineFields = {
			contentBinding.caseDataMenac,
			contentBinding.caseDataMenacw,
			contentBinding.caseDataMenacwy,
			contentBinding.caseDataMenaConjunate,
			contentBinding.caseDataPcvi3I,
			contentBinding.caseDataPcvi32,
			contentBinding.caseDataPcv133,
			contentBinding.caseDataHibI,
			contentBinding.caseDataHib2,
			contentBinding.caseDataHib3
		};
		ControlDateField[] vaccineDateFields = {
			contentBinding.caseDataMenacDate,
			contentBinding.caseDataMenacwDate,
			contentBinding.caseDataMenacwyDate,
			contentBinding.caseDataMenaConjunateDate,
			contentBinding.caseDataPcvi3IDate,
			contentBinding.caseDataPcvi32Date,
			contentBinding.caseDataPcv133Date,
			contentBinding.caseDataHibIDate,
			contentBinding.caseDataHib2Date,
			contentBinding.caseDataHib3Date
		};
		ControlSpinnerField[] vaccineSourceFields = {
			contentBinding.caseDataMenacSourceOfVaccination,
			contentBinding.caseDataMenacwSourceOfVaccination,
			contentBinding.caseDataMenacwySourceOfVaccination,
			contentBinding.caseDataMenaConjunateSourceOfVaccination,
			contentBinding.caseDataPcvi3ISourceOfVaccination,
			contentBinding.caseDataPcvi32SourceOfVaccination,
			contentBinding.caseDataPcv133SourceOfVaccination,
			contentBinding.caseDataHibISourceOfVaccination,
			contentBinding.caseDataHib2SourceOfVaccination,
			contentBinding.caseDataHib3SourceOfVaccination
		};

		View vaccinationRecordTypeLayout = contentBinding.getRoot()
			.findViewById(R.id.caseData_vaccinationRecordType_numberOfVaccinationDoses_layout);

		Runnable updateVaccinatedVisibility = () -> {
			if (updatingMeningitisVaccinationVisibility) {
				return;
			}
			updatingMeningitisVaccinationVisibility = true;
			try {
				Disease disease = (Disease) contentBinding.caseDataDisease.getValue();
				if (disease == null) {
					disease = record.getDisease();
				}
				if (disease != Disease.CSM) {
					return;
				}
				boolean show = isCsmCaseVaccinated(contentBinding);
				ControlSwitchField vaccinationRecordType = contentBinding.caseDataVaccinationRecordType;

				if (vaccinationRecordTypeLayout != null) {
					vaccinationRecordTypeLayout.setVisibility(show ? VISIBLE : GONE);
				}
				if (show) {
					vaccinationRecordType.setVisibility(VISIBLE);
				} else {
					hideControlField(vaccinationRecordType, true);
				}
				vaccinationRecordType.setRequired(show);

				for (ControlSwitchField vaccineField : vaccineFields) {
					if (show) {
						setFieldAndParentsVisible(vaccineField, csmSection);
						vaccineField.setVisibility(VISIBLE);
					} else {
						hideControlField(vaccineField, true);
					}
				}
				if (!show) {
					for (ControlDateField dateField : vaccineDateFields) {
						hideControlField(dateField, true);
					}
					for (ControlSpinnerField sourceField : vaccineSourceFields) {
						hideControlField(sourceField, true);
					}
				} else {
					for (int i = 0; i < vaccineFields.length; i++) {
						if (YesNoUnknown.YES.equals(vaccineFields[i].getValue())) {
							setFieldAndParentsVisible(vaccineDateFields[i], csmSection);
							vaccineDateFields[i].setVisibility(VISIBLE);
							setFieldAndParentsVisible(vaccineSourceFields[i], csmSection);
							vaccineSourceFields[i].setVisibility(VISIBLE);
						}
					}
				}
			} finally {
				updatingMeningitisVaccinationVisibility = false;
			}
		};

		if (!meningitisHandlersRegistered) {
			ValueChangeListener vaccinationVisibilityListener = field -> {
				if (!updatingMeningitisVaccinationVisibility) {
					updateVaccinatedVisibility.run();
				}
			};
			contentBinding.caseDataVaccinated.addValueChangedListener(vaccinationVisibilityListener);
			contentBinding.caseDataVaccinationStatus.addValueChangedListener(vaccinationVisibilityListener);

			setVisibleWhen(contentBinding.caseDataMenacDate, contentBinding.caseDataMenac, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenacwDate, contentBinding.caseDataMenacw, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenacwyDate, contentBinding.caseDataMenacwy, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenaConjunateDate, contentBinding.caseDataMenaConjunate, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcvi3IDate, contentBinding.caseDataPcvi3I, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcvi32Date, contentBinding.caseDataPcvi32, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcv133Date, contentBinding.caseDataPcv133, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHibIDate, contentBinding.caseDataHibI, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHib2Date, contentBinding.caseDataHib2, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHib3Date, contentBinding.caseDataHib3, YesNoUnknown.YES);

			setVisibleWhen(contentBinding.caseDataMenacSourceOfVaccination, contentBinding.caseDataMenac, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenacwSourceOfVaccination, contentBinding.caseDataMenacw, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenacwySourceOfVaccination, contentBinding.caseDataMenacwy, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataMenaConjunateSourceOfVaccination, contentBinding.caseDataMenaConjunate, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcvi3ISourceOfVaccination, contentBinding.caseDataPcvi3I, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcvi32SourceOfVaccination, contentBinding.caseDataPcvi32, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataPcv133SourceOfVaccination, contentBinding.caseDataPcv133, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHibISourceOfVaccination, contentBinding.caseDataHibI, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHib2SourceOfVaccination, contentBinding.caseDataHib2, YesNoUnknown.YES);
			setVisibleWhen(contentBinding.caseDataHib3SourceOfVaccination, contentBinding.caseDataHib3, YesNoUnknown.YES);

			registerSampleCollectionHandlers(contentBinding);

			meningitisHandlersRegistered = true;
		}
		updateVaccinatedVisibility.run();
	}

	private void registerSampleCollectionHandlers(FragmentCaseEditLayoutBinding contentBinding) {
		ControlCheckBoxGroupField lpNotDoneReasonField = contentBinding.caseDataLpNotDoneReason;
		ControlTextEditField lpNotDoneReasonOtherField = contentBinding.caseDataLpNotDoneReasonOther;

		Runnable updateLpNotDoneReasonOtherVisibility = () -> {
			YesNo csfSampleCollected = (YesNo) contentBinding.caseDataCsfSampleCollected.getValue();
			if (csfSampleCollected == null) {
				csfSampleCollected = record.getCsfSampleCollected();
			}
			if (!YesNo.NO.equals(csfSampleCollected)) {
				hideControlField(lpNotDoneReasonOtherField, false);
				return;
			}
			if (isLpNotDoneOtherReasonSelected(lpNotDoneReasonField.getValue())) {
				setFieldAndParentsVisible(lpNotDoneReasonOtherField, contentBinding.caseDataCsmExtendedSection);
				lpNotDoneReasonOtherField.setVisibility(VISIBLE);
			} else {
				hideControlField(lpNotDoneReasonOtherField, false);
			}
		};

		contentBinding.caseDataCsfSampleCollected.addValueChangedListener(field -> {
			if (!YesNo.NO.equals(field.getValue())) {
				lpNotDoneReasonField.setValue(new HashSet<>());
				lpNotDoneReasonOtherField.setValue(null);
			}
			updateLpNotDoneReasonOtherVisibility.run();
		});

		lpNotDoneReasonField.addValueChangedListener(field -> {
			if (!YesNo.NO.equals(contentBinding.caseDataCsfSampleCollected.getValue())) {
				return;
			}
			if (isLpNotDoneOtherReasonSelected(field.getValue())) {
				setFieldAndParentsVisible(lpNotDoneReasonOtherField, contentBinding.caseDataCsmExtendedSection);
				lpNotDoneReasonOtherField.setVisibility(VISIBLE);
			} else {
				hideControlField(lpNotDoneReasonOtherField, true);
			}
		});

		contentBinding.getRoot().post(updateLpNotDoneReasonOtherVisibility);
	}

	private boolean isLpNotDoneOtherReasonSelected(Object fieldValue) {
		if (fieldValue instanceof Set && ((Set<?>) fieldValue).contains(LpNotDoneReason.OTHER)) {
			return true;
		}
		Set<LpNotDoneReason> savedReasons = record.getLpNotDoneReason();
		return savedReasons != null && savedReasons.contains(LpNotDoneReason.OTHER);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}

	public CaseConfirmationBasis getCaseConfirmationBasis() {
		return caseConfirmationBasis;
	}

	public void setCaseConfirmationBasis(CaseConfirmationBasis caseConfirmationBasis) {
		this.caseConfirmationBasis = caseConfirmationBasis;
	}
}

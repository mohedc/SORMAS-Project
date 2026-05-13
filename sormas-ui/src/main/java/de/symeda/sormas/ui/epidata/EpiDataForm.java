/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.CssStyles.*;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.*;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.ui.Label;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import com.vaadin.v7.ui.ComboBox;
import de.symeda.sormas.ui.ActivityAsCase.ActivityAsCaseField;
import de.symeda.sormas.ui.exposure.ExposuresField;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String LOC_EXPOSURE_INVESTIGATION_HEADING = "locExposureInvestigationHeading";
	private static final String LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING = "locActivityAsCaseInvestigationHeading";
	private static final String LOC_SOURCE_CASE_CONTACTS_HEADING = "locSourceCaseContactsHeading";
	private static final String LOC_EPI_DATA_FIELDS_HINT = "locEpiDataFieldsHint";
	private static final String LOC_TRAVEL_LOCATION_HEADING = "locTravelLocationHeading";
	private static final String FILL_SECTION_HEADING_LOC = "fillSectionHeadingLoc";
	public static final String SEEK_HELP_HEADING_LOC = "seekHelpHeadingLoc";
	//@formatter:off
	private static final String MAIN_HTML_LAYOUT = 
			loc(LOC_EXPOSURE_INVESTIGATION_HEADING) + 
			loc(EpiDataDto.EXPOSURE_DETAILS_KNOWN) +
			loc(EpiDataDto.EXPOSURES) +
			loc(LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING) + 
			loc(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN)+
			loc(EpiDataDto.ACTIVITIES_AS_CASE) + 
			locCss(VSPACE_TOP_3, LOC_EPI_DATA_FIELDS_HINT) +
			loc(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA) +
			loc(EpiDataDto.LARGE_OUTBREAKS_AREA) + 
			loc(EpiDataDto.AREA_INFECTED_ANIMALS);

	private static final String SOURCE_CONTACTS_HTML_LAYOUT =
			locCss(VSPACE_TOP_3, LOC_SOURCE_CASE_CONTACTS_HEADING) +
			loc(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
	//@formatter:on

	private static final String MEASLES_HTML_LAYOUT =
			loc(LOC_TRAVEL_LOCATION_HEADING) +
			loc(EpiDataDto.TRAVEL_HISTORY_KNOWN) +
			loc(EpiDataDto.TRAVEL_LOCATION);

	private static final String YELLOW_FEVER_HTML_LAYOUT =
		loc(LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING) + 
			loc(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN)+
			loc((EpiDataDto.HIGH_TRANSMISSION_RISK_AREA)) +
			loc(EpiDataDto.ACTIVITIES_AS_CASE);

	private static final String CONGENITAL_RUBELLA_HTML_LAYOUT =
		loc(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED) +
		loc(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE) +
		loc(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY) +
		loc(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY_DATE) +
		loc(EpiDataDto.GESTATIONAL_AGE_AT_EXPOSURE) +
		loc(EpiDataDto.EXPOSURE_LOCATION_DESCRIPTION) +
		loc(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY) +
		loc(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE) +
		loc(EpiDataDto.GESTATIONAL_AGE_AT_TRAVEL) +
		loc(EpiDataDto.TRAVEL_LOCATION_DESCRIPTION);

	private static final String MENINGITIS_HTML_LAYOUT =
		loc(LOC_EXPOSURE_INVESTIGATION_HEADING) + 
		loc(EpiDataDto.CONTACT_SIMILAR_SYMPTOMS) +
		loc(EpiDataDto.EXPOSURE_DETAILS_KNOWN) +
		loc(EpiDataDto.EXPOSURES);

	private static final String IDSR_HTML_LAYOUT =
			loc(LOC_EXPOSURE_INVESTIGATION_HEADING) +
			loc(EpiDataDto.RECENT_TRAVEL_OUTBREAK)+
			loc(EpiDataDto.CONTACT_SIMILAR_SYMPTOMS)+
			loc(EpiDataDto.CONTACT_SICK_ANIMALS);

	private static final String AFP_HTML_LAYOUT =
			loc(LOC_EXPOSURE_INVESTIGATION_HEADING) +
			loc(FILL_SECTION_HEADING_LOC) +
			loc(SEEK_HELP_HEADING_LOC) +
			fluidRowLocs(EpiDataDto.PLACE, EpiDataDto.DURATION_MONTHS, EpiDataDto.DURATION_DAYS) +
			fluidRowLocs(EpiDataDto.PLACE2, EpiDataDto.DURATION_MONTHS2, EpiDataDto.DURATION_DAYS2) +
			fluidRowLocs(EpiDataDto.PLACE3, EpiDataDto.DURATION_MONTHS3, EpiDataDto.DURATION_DAYS3) +
			fluidRowLocs(EpiDataDto.PLACE4, EpiDataDto.DURATION_MONTHS4, EpiDataDto.DURATION_DAYS4);



	private final Disease disease;
	private final Class<? extends EntityDto> parentClass;
	private final Consumer<Boolean> sourceContactsToggleCallback;
	private final boolean isPseudonymized;
	private LocationEditForm travelLocationForm;

	public EpiDataForm(
		Disease disease,
		Class<? extends EntityDto> parentClass,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Consumer<Boolean> sourceContactsToggleCallback,
		boolean isEditAllowed) {
		super(
			EpiDataDto.class,
			EpiDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.disease = disease;
		this.parentClass = parentClass;
		this.sourceContactsToggleCallback = sourceContactsToggleCallback;
		this.isPseudonymized = isPseudonymized;
		addFields();
	}

	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}

		Label fillSectionHeadingLabel = new Label(I18nProperties.getString(Strings.headingfillSection));
		fillSectionHeadingLabel.addStyleName(H3);
		getContent().addComponent(fillSectionHeadingLabel, FILL_SECTION_HEADING_LOC);

		Label seekHelpHeadingLabel = new Label(I18nProperties.getString(Strings.headingseekHelp));
		seekHelpHeadingLabel.addStyleName(H3);
		seekHelpHeadingLabel.addStyleName("afp-childseek-label");
		seekHelpHeadingLabel.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(seekHelpHeadingLabel, SEEK_HELP_HEADING_LOC);

		// For Congenital Rubella, add only the specific fields
		if (disease == Disease.CONGENITAL_RUBELLA && parentClass == CaseDataDto.class) {
			addCongenitalRubellaFields();
			initializeVisibilitiesAndAllowedVisibilities();
			initializeAccessAndAllowedAccesses();
			return;
		}

		addHeadingsAndInfoTexts();

		NullableOptionGroup ogExposureDetailsKnown = addField(EpiDataDto.EXPOSURE_DETAILS_KNOWN, NullableOptionGroup.class);
		ExposuresField exposuresField = addField(EpiDataDto.EXPOSURES, new ExposuresField(disease, FieldVisibilityCheckers.withDisease(disease)
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())), UiFieldAccessCheckers.getDefault(false, FacadeProvider.getConfigFacade().getCountryLocale()), true));

		exposuresField.setEpiDataParentClass(parentClass);
		exposuresField.setWidthFull();
		exposuresField.setPseudonymized(isPseudonymized);

		if (parentClass == CaseDataDto.class) {
			addActivityAsCaseFields();
		}

		addField(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.LARGE_OUTBREAKS_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.AREA_INFECTED_ANIMALS, NullableOptionGroup.class);

		// TRAVEL_HISTORY_KNOWN
		NullableOptionGroup travelHistoryKnownField = addField(EpiDataDto.TRAVEL_HISTORY_KNOWN, NullableOptionGroup.class);

		addTravelHistoryFields(travelHistoryKnownField);

		NullableOptionGroup ogContactWithSourceCaseKnown = addField(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN, NullableOptionGroup.class);
		addField(EpiDataDto.RECENT_TRAVEL_OUTBREAK, NullableOptionGroup.class);
		addField(EpiDataDto.CONTACT_SIMILAR_SYMPTOMS, NullableOptionGroup.class);
		addField(EpiDataDto.CONTACT_SICK_ANIMALS, NullableOptionGroup.class);

		if (sourceContactsToggleCallback != null) {
			ogContactWithSourceCaseKnown.addValueChangeListener(e -> {
				YesNoUnknown sourceContactsKnown = (YesNoUnknown) FieldHelper.getNullableSourceFieldValue((Field) e.getProperty());
				sourceContactsToggleCallback.accept(YesNoUnknown.YES == sourceContactsKnown);
			});
		}

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.EXPOSURES,
			EpiDataDto.EXPOSURE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		exposuresField.addValueChangeListener(e -> {
			ogExposureDetailsKnown.setEnabled(CollectionUtils.isEmpty(exposuresField.getValue()));
		});

		addField(EpiDataDto.PLACE, TextField.class);
		addField(EpiDataDto.DURATION_MONTHS, TextField.class);
		addField(EpiDataDto.DURATION_DAYS, TextField.class);
		addField(EpiDataDto.PLACE2, TextField.class);
		addField(EpiDataDto.DURATION_MONTHS2, TextField.class);
		addField(EpiDataDto.DURATION_DAYS2, TextField.class);
		addField(EpiDataDto.PLACE3, TextField.class);
		addField(EpiDataDto.DURATION_MONTHS3, TextField.class);
		addField(EpiDataDto.DURATION_DAYS3, TextField.class);
		addField(EpiDataDto.PLACE4, TextField.class);
		addField(EpiDataDto.DURATION_MONTHS4, TextField.class);
		addField(EpiDataDto.DURATION_DAYS4, TextField.class);
	}

	private void addCongenitalRubellaFields() {
		// Mother rubella lab confirmed
		NullableOptionGroup motherRubellaLabConfirmedField = addField(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED, NullableOptionGroup.class);
		DateField motherRubellaLabConfirmedDateField = addField(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE, DateField.class);

		// Mother exposed during pregnancy
		NullableOptionGroup motherExposedDuringPregnancyField = addField(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY, NullableOptionGroup.class);
		DateField motherExposedDuringPregnancyDateField = addField(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY_DATE, DateField.class);
		TextField gestationalAgeAtExposureField = addField(EpiDataDto.GESTATIONAL_AGE_AT_EXPOSURE, TextField.class);
		TextField exposureLocationDescriptionField = addField(EpiDataDto.EXPOSURE_LOCATION_DESCRIPTION, TextField.class);

		// Mother traveled during pregnancy
		NullableOptionGroup motherTraveledDuringPregnancyField = addField(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY, NullableOptionGroup.class);
		DateField motherTraveledDuringPregnancyDateField = addField(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE, DateField.class);
		TextField gestationalAgeAtTravelField = addField(EpiDataDto.GESTATIONAL_AGE_AT_TRAVEL, TextField.class);
		TextField travelLocationDescriptionField = addField(EpiDataDto.TRAVEL_LOCATION_DESCRIPTION, TextField.class);

		// Set visibility conditions
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE,
			EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY_DATE,
			EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.GESTATIONAL_AGE_AT_EXPOSURE,
			EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.EXPOSURE_LOCATION_DESCRIPTION,
			EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE,
			EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.GESTATIONAL_AGE_AT_TRAVEL,
			EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.TRAVEL_LOCATION_DESCRIPTION,
			EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);
	}

	private void addActivityAsCaseFields() {

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingActivityAsCase))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoActivityAsCaseInvestigation)),
				ContentMode.HTML),
			LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING);

		NullableOptionGroup ogActivityAsCaseDetailsKnown = addField(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN, NullableOptionGroup.class);
		ActivityAsCaseField activityAsCaseField = addField(EpiDataDto.ACTIVITIES_AS_CASE, ActivityAsCaseField.class);
		activityAsCaseField.setWidthFull();
		activityAsCaseField.setPseudonymized(isPseudonymized);
		activityAsCaseField.setDisease(disease);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.ACTIVITIES_AS_CASE,
			EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		activityAsCaseField.addValueChangeListener(e -> {
			ogActivityAsCaseDetailsKnown.setEnabled(CollectionUtils.isEmpty(activityAsCaseField.getValue()));
		});
	}
	private void addTravelHistoryFields(NullableOptionGroup travelHistoryKnownField) {
		if (disease != Disease.MEASLES && disease != Disease.YELLOW_FEVER && disease != Disease.CONGENITAL_RUBELLA) {
			return;
		}

		// Add heading for travel location
		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getCaption(Captions.EpiData_travelLocation)),
				ContentMode.HTML),
			LOC_TRAVEL_LOCATION_HEADING);

		// Create LocationEditForm for travel location
		travelLocationForm = addField(
			EpiDataDto.TRAVEL_LOCATION,
			new LocationEditForm(
				FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
				UiFieldAccessCheckers.getNoop(),
				disease));
		travelLocationForm.setCaption(null);

		// Show only Region, District, Community fields
		travelLocationForm.hideFieldForMeaslesEpidataTravelLocation();

		// Hide additional fields for yellow fever and congenital rubella
		if (disease == Disease.YELLOW_FEVER || disease == Disease.CONGENITAL_RUBELLA) {
			travelLocationForm.hideFieldsForYellowFeverActivityCase();
		}

		// Populate region field since country is hidden
		ComboBox regionField = (ComboBox) travelLocationForm.getField(LocationDto.REGION);
		if (regionField != null) {
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		// Show travel location fields only when travelHistoryKnown is Yes
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.TRAVEL_LOCATION,
			EpiDataDto.TRAVEL_HISTORY_KNOWN,
			Collections.singletonList(YesNo.YES),
			true);

		travelHistoryKnownField.addValueChangeListener(event -> {
			YesNo travelHistoryKnown = (YesNo) FieldHelper.getNullableSourceFieldValue((Field) event.getProperty());
			if (travelHistoryKnown == YesNo.YES && travelLocationForm != null && travelLocationForm.getValue() == null) {
				LocationDto travelLocation = LocationDto.build();
				travelLocationForm.setValue(travelLocation);
			}
		});
	}

	@Override
	public void setValue(EpiDataDto newFieldValue) {
		super.setValue(newFieldValue);

		if (travelLocationForm != null && newFieldValue != null) {
			if (newFieldValue.getTravelHistoryKnown() == YesNo.YES && newFieldValue.getTravelLocation() == null) {
				LocationDto travelLocation = LocationDto.build();
				newFieldValue.setTravelLocation(travelLocation);
				travelLocationForm.setValue(travelLocation);
			}
		}
	}
	private void addHeadingsAndInfoTexts() {
		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingExposureInvestigation))
					+ divsCss(
						VSPACE_3,
						I18nProperties.getString(
							parentClass == ContactDto.class ? Strings.infoExposureInvestigationContacts : Strings.infoExposureInvestigation)),
				ContentMode.HTML),
			LOC_EXPOSURE_INVESTIGATION_HEADING);

		getContent().addComponent(
			new MultilineLabel(divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataFieldsHint)), ContentMode.HTML),
			LOC_EPI_DATA_FIELDS_HINT);

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingEpiDataSourceCaseContacts))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataSourceCaseContacts)),
				ContentMode.HTML),
			LOC_SOURCE_CASE_CONTACTS_HEADING);
	}

	public void disableContactWithSourceCaseKnownField() {
		setEnabled(false, EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
	}

	public void setGetSourceContactsCallback(Supplier<List<ContactReferenceDto>> callback) {
		Field exposuresField = getField(EpiDataDto.EXPOSURES);
		if (exposuresField != null && exposuresField instanceof ExposuresField) {
			((ExposuresField) exposuresField).setGetSourceContactsCallback(callback);
		}
	}

	@Override
	protected String createHtmlLayout() {
		String mainHtmlLayout = "";

		if (parentClass == CaseDataDto.class) {
			switch (disease) {
				case MEASLES:
					mainHtmlLayout = MEASLES_HTML_LAYOUT;
					break;
				case YELLOW_FEVER:
					mainHtmlLayout = YELLOW_FEVER_HTML_LAYOUT;
					break;
				case CONGENITAL_RUBELLA:
					mainHtmlLayout = CONGENITAL_RUBELLA_HTML_LAYOUT;
					break;
				case CSM:
					mainHtmlLayout = MENINGITIS_HTML_LAYOUT;
					break;
				case IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS:
					mainHtmlLayout = IDSR_HTML_LAYOUT;
					break;
				case AFP:
					mainHtmlLayout = AFP_HTML_LAYOUT;
					break;
				default:
					mainHtmlLayout = SOURCE_CONTACTS_HTML_LAYOUT;
					break;
			}
		}

		return mainHtmlLayout;
	}

}

package de.symeda.sormas.ui.caze.maternalhistory;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.ViewMode;

public class MaternalHistoryForm extends AbstractEditForm<MaternalHistoryDto> {

	private static final long serialVersionUID = 1L;

	private static final String MATERNAL_HISTORY_HEADING_LOC = "maternalHistoryHeadingLoc";
	private static final String CONGENITAL_RUBELLA_HEADING_LOC = "congenitalRubellaHeadingLoc";

	private final ViewMode viewMode;
	private final Disease disease;

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(MATERNAL_HISTORY_HEADING_LOC) +
			fluidRowLocs(MaternalHistoryDto.CHILDREN_NUMBER, MaternalHistoryDto.AGE_AT_BIRTH, "") +
			fluidRowLocs(MaternalHistoryDto.RUBELLA_VACCINATION, MaternalHistoryDto.RUBELLA_VACCINATION_DATE, "") +
			fluidRowLocs(MaternalHistoryDto.RUBELLA, MaternalHistoryDto.RUBELLA_ONSET, MaternalHistoryDto.RUBELLA_MONTH) +
			fluidRowLocs(MaternalHistoryDto.MACULOPAPULAR_RASH, MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.SWOLLEN_LYMPHS, MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.OTHER_COMPLICATIONS, MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET, "") +
			loc(CONGENITAL_RUBELLA_HEADING_LOC) +
			fluidRowLocs(MaternalHistoryDto.CONGENITAL_RUBELLA, MaternalHistoryDto.CONGENITAL_RUBELLA_DATE, "");

	/** Matches {@link de.symeda.sormas.ui.epidata.EpiDataForm#CONGENITAL_RUBELLA_HTML_LAYOUT} field order (one row per field). */
	private static final String CRS_HTML_LAYOUT =
			loc(MATERNAL_HISTORY_HEADING_LOC) +
			fluidRowLocs(MaternalHistoryDto.CHILDREN_NUMBER, MaternalHistoryDto.AGE_AT_BIRTH, "") +
			fluidRowLocs(MaternalHistoryDto.RUBELLA_VACCINATION, MaternalHistoryDto.RUBELLA_VACCINATION_DATE, "") +
			fluidRowLocs(MaternalHistoryDto.RUBELLA, "", "") +
			fluidRowLocs(MaternalHistoryDto.MACULOPAPULAR_RASH, MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.SWOLLEN_LYMPHS, MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET, "") +
			fluidRowLocs(MaternalHistoryDto.OTHER_COMPLICATIONS, MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET, "") +
			loc(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED) +
			loc(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE) +
			loc(MaternalHistoryDto.RASH_EXPOSURE) +
			loc(MaternalHistoryDto.RASH_EXPOSURE_DATE) +
			loc(MaternalHistoryDto.GESTATIONAL_AGE_AT_EXPOSURE) +
			loc(MaternalHistoryDto.EXPOSURE_LOCATION_DESCRIPTION) +
			loc(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY) +
			loc(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE) +
			loc(MaternalHistoryDto.GESTATIONAL_AGE_AT_TRAVEL) +
			loc(MaternalHistoryDto.TRAVEL_LOCATION_DESCRIPTION);
	//@formatter:on

	public MaternalHistoryForm(ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		// Defer addFields until disease is set; super would run it before this constructor body (NPE in createHtmlLayout).
		super(
			MaternalHistoryDto.class,
			MaternalHistoryDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers(),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));
		this.viewMode = viewMode;
		this.disease = disease;
		addFields();
	}

	private static String epiDataCaption(String epiPropertyId) {
		return I18nProperties.getCaption(EpiDataDto.I18N_PREFIX + "." + epiPropertyId);
	}

	@Override
	protected void addFields() {

		Label maternalHistoryHeadingLabel = new Label(I18nProperties.getString(Strings.headingMaternalHistory));
		maternalHistoryHeadingLabel.addStyleName(H3);
		getContent().addComponent(maternalHistoryHeadingLabel, MATERNAL_HISTORY_HEADING_LOC);

		if (disease != Disease.CONGENITAL_RUBELLA) {
			Label congenitalRubellaHeadingLabel = new Label(I18nProperties.getString(Strings.headingCongenitalRubella));
			congenitalRubellaHeadingLabel.addStyleName(H3);
			getContent().addComponent(congenitalRubellaHeadingLabel, CONGENITAL_RUBELLA_HEADING_LOC);
		}

		if (disease == Disease.CONGENITAL_RUBELLA) {
			addCrsFields();
			return;
		}

		addDefaultFields();
	}

	private void addCrsFields() {

		TextField tfChildrenNumber = addField(MaternalHistoryDto.CHILDREN_NUMBER, TextField.class);
		tfChildrenNumber.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfChildrenNumber.getCaption()));
		TextField tfAgeAtBirth = addField(MaternalHistoryDto.AGE_AT_BIRTH, TextField.class);
		tfAgeAtBirth.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfAgeAtBirth.getCaption()));
		TextField tfRubellaMonth = addField(MaternalHistoryDto.RUBELLA_MONTH, TextField.class);
		tfRubellaMonth
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfRubellaMonth.getCaption()));

		addField(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED, NullableOptionGroup.class);
		getField(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED).setCaption(epiDataCaption(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED));
		addField(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE, DateField.class);
		getField(MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE).setCaption(epiDataCaption(EpiDataDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE));

		addField(MaternalHistoryDto.RASH_EXPOSURE, NullableOptionGroup.class);
		getField(MaternalHistoryDto.RASH_EXPOSURE).setCaption(epiDataCaption(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY));
		addField(MaternalHistoryDto.RASH_EXPOSURE_DATE, DateField.class);
		getField(MaternalHistoryDto.RASH_EXPOSURE_DATE).setCaption(epiDataCaption(EpiDataDto.MOTHER_EXPOSED_DURING_PREGNANCY_DATE));

		TextField tfGestationalAgeAtExposure = addField(MaternalHistoryDto.GESTATIONAL_AGE_AT_EXPOSURE, TextField.class);
		tfGestationalAgeAtExposure.setCaption(epiDataCaption(EpiDataDto.GESTATIONAL_AGE_AT_EXPOSURE));
		tfGestationalAgeAtExposure
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfGestationalAgeAtExposure.getCaption()));
		TextField tfExposureLocation = addField(MaternalHistoryDto.EXPOSURE_LOCATION_DESCRIPTION, TextField.class);
		tfExposureLocation.setCaption(epiDataCaption(EpiDataDto.EXPOSURE_LOCATION_DESCRIPTION));

		addField(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY, NullableOptionGroup.class);
		getField(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY).setCaption(epiDataCaption(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY));
		addField(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE, DateField.class);
		getField(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE).setCaption(epiDataCaption(EpiDataDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE));

		TextField tfGestationalAgeAtTravel = addField(MaternalHistoryDto.GESTATIONAL_AGE_AT_TRAVEL, TextField.class);
		tfGestationalAgeAtTravel.setCaption(epiDataCaption(EpiDataDto.GESTATIONAL_AGE_AT_TRAVEL));
		tfGestationalAgeAtTravel
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfGestationalAgeAtTravel.getCaption()));
		TextField tfTravelLocation = addField(MaternalHistoryDto.TRAVEL_LOCATION_DESCRIPTION, TextField.class);
		tfTravelLocation.setCaption(epiDataCaption(EpiDataDto.TRAVEL_LOCATION_DESCRIPTION));

		addFields(
			MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET,
			MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET,
			MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET,
			MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET,
			MaternalHistoryDto.RUBELLA_ONSET,
			MaternalHistoryDto.RUBELLA_VACCINATION_DATE);

		addField(MaternalHistoryDto.MACULOPAPULAR_RASH, NullableOptionGroup.class);
		addField(MaternalHistoryDto.SWOLLEN_LYMPHS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.OTHER_COMPLICATIONS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.RUBELLA, NullableOptionGroup.class);
		addField(MaternalHistoryDto.RUBELLA_VACCINATION, NullableOptionGroup.class);

		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET),
			MaternalHistoryDto.MACULOPAPULAR_RASH,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET),
			MaternalHistoryDto.SWOLLEN_LYMPHS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET),
			MaternalHistoryDto.ARTHRALGIA_ARTHRITIS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET),
			MaternalHistoryDto.OTHER_COMPLICATIONS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.RUBELLA_ONSET, MaternalHistoryDto.RUBELLA_MONTH),
			MaternalHistoryDto.RUBELLA,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.RUBELLA_VACCINATION_DATE),
			MaternalHistoryDto.RUBELLA_VACCINATION,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED_DATE,
			MaternalHistoryDto.MOTHER_RUBELLA_LAB_CONFIRMED,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.RASH_EXPOSURE_DATE,
			MaternalHistoryDto.RASH_EXPOSURE,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.GESTATIONAL_AGE_AT_EXPOSURE,
			MaternalHistoryDto.RASH_EXPOSURE,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.EXPOSURE_LOCATION_DESCRIPTION,
			MaternalHistoryDto.RASH_EXPOSURE,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE,
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.GESTATIONAL_AGE_AT_TRAVEL,
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			MaternalHistoryDto.TRAVEL_LOCATION_DESCRIPTION,
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Collections.singletonList(YesNoUnknown.YES),
			true);
	}

	private void addDefaultFields() {

		TextField tfChildrenNumber = addField(MaternalHistoryDto.CHILDREN_NUMBER, TextField.class);
		tfChildrenNumber.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfChildrenNumber.getCaption()));
		TextField tfAgeAtBirth = addField(MaternalHistoryDto.AGE_AT_BIRTH, TextField.class);
		tfAgeAtBirth.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfAgeAtBirth.getCaption()));
		TextField tfRubellaMonth = addField(MaternalHistoryDto.RUBELLA_MONTH, TextField.class);
		tfRubellaMonth
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfRubellaMonth.getCaption()));
		TextField tfGestationalAgeAtExposure = addField(MaternalHistoryDto.GESTATIONAL_AGE_AT_EXPOSURE, TextField.class);
		tfGestationalAgeAtExposure
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfGestationalAgeAtExposure.getCaption()));
		TextField tfGestationalAgeAtTravel = addField(MaternalHistoryDto.GESTATIONAL_AGE_AT_TRAVEL, TextField.class);
		tfGestationalAgeAtTravel
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfGestationalAgeAtTravel.getCaption()));
		addField(MaternalHistoryDto.EXPOSURE_LOCATION_DESCRIPTION, TextField.class);
		addField(MaternalHistoryDto.TRAVEL_LOCATION_DESCRIPTION, TextField.class);
		addField(MaternalHistoryDto.RASH_EXPOSURE, NullableOptionGroup.class);
		addField(MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY, NullableOptionGroup.class);

		addFields(
			MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET,
			MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET,
			MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET,
			MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET,
			MaternalHistoryDto.RUBELLA_ONSET,
			MaternalHistoryDto.RUBELLA_VACCINATION_DATE,
			MaternalHistoryDto.CONGENITAL_RUBELLA_DATE,
			MaternalHistoryDto.RASH_EXPOSURE_DATE,
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE);

		addField(MaternalHistoryDto.MACULOPAPULAR_RASH, NullableOptionGroup.class);
		addField(MaternalHistoryDto.SWOLLEN_LYMPHS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.OTHER_COMPLICATIONS, NullableOptionGroup.class);
		addField(MaternalHistoryDto.RUBELLA, NullableOptionGroup.class);
		addField(MaternalHistoryDto.RUBELLA_VACCINATION, NullableOptionGroup.class);
		addField(MaternalHistoryDto.CONGENITAL_RUBELLA, NullableOptionGroup.class);

		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET),
			MaternalHistoryDto.MACULOPAPULAR_RASH,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET),
			MaternalHistoryDto.SWOLLEN_LYMPHS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET),
			MaternalHistoryDto.ARTHRALGIA_ARTHRITIS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET),
			MaternalHistoryDto.OTHER_COMPLICATIONS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.RUBELLA_ONSET, MaternalHistoryDto.RUBELLA_MONTH),
			MaternalHistoryDto.RUBELLA,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.RUBELLA_VACCINATION_DATE),
			MaternalHistoryDto.RUBELLA_VACCINATION,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(MaternalHistoryDto.CONGENITAL_RUBELLA_DATE),
			MaternalHistoryDto.CONGENITAL_RUBELLA,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				MaternalHistoryDto.RASH_EXPOSURE_DATE,
				MaternalHistoryDto.GESTATIONAL_AGE_AT_EXPOSURE,
				MaternalHistoryDto.EXPOSURE_LOCATION_DESCRIPTION),
			MaternalHistoryDto.RASH_EXPOSURE,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY_DATE,
				MaternalHistoryDto.GESTATIONAL_AGE_AT_TRAVEL,
				MaternalHistoryDto.TRAVEL_LOCATION_DESCRIPTION),
			MaternalHistoryDto.MOTHER_TRAVELED_DURING_PREGNANCY,
			Arrays.asList(YesNoUnknown.YES),
			true);
	}

	@Override
	protected String createHtmlLayout() {
		String DISEASE_LAYOUT = "";
		switch (disease) {
			case CONGENITAL_RUBELLA:
				DISEASE_LAYOUT = CRS_HTML_LAYOUT;
				break;
			default:
				DISEASE_LAYOUT = HTML_LAYOUT;
				break;
		}
		return DISEASE_LAYOUT;
	}
}

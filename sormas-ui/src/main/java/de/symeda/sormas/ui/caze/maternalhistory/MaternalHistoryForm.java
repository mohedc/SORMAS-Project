package de.symeda.sormas.ui.caze.maternalhistory;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
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
	//@formatter:on

	public MaternalHistoryForm(ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction) {
		super(
			MaternalHistoryDto.class,
			MaternalHistoryDto.I18N_PREFIX,
			true,
			new FieldVisibilityCheckers(),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));
		this.viewMode = viewMode;
	}

	@Override
	protected void addFields() {

		Label maternalHistoryHeadingLabel = new Label(I18nProperties.getString(Strings.headingMaternalHistory));
		maternalHistoryHeadingLabel.addStyleName(H3);
		getContent().addComponent(maternalHistoryHeadingLabel, MATERNAL_HISTORY_HEADING_LOC);

		Label congenitalRubellaHeadingLabel = new Label(I18nProperties.getString(Strings.headingCongenitalRubella));
		congenitalRubellaHeadingLabel.addStyleName(H3);
		getContent().addComponent(congenitalRubellaHeadingLabel, CONGENITAL_RUBELLA_HEADING_LOC);

		TextField tfChildrenNumber = addField(MaternalHistoryDto.CHILDREN_NUMBER, TextField.class);
		tfChildrenNumber.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfChildrenNumber.getCaption()));
		TextField tfAgeAtBirth = addField(MaternalHistoryDto.AGE_AT_BIRTH, TextField.class);
		tfAgeAtBirth.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfAgeAtBirth.getCaption()));
		TextField tfRubellaMonth = addField(MaternalHistoryDto.RUBELLA_MONTH, TextField.class);
		tfRubellaMonth
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfRubellaMonth.getCaption()));

		addFields(
			MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET,
			MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET,
			MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET,
			MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET,
			MaternalHistoryDto.RUBELLA_ONSET,
			MaternalHistoryDto.RUBELLA_VACCINATION_DATE,
			MaternalHistoryDto.CONGENITAL_RUBELLA_DATE);

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
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}

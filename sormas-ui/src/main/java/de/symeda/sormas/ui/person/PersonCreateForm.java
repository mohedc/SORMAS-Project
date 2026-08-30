/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.time.Month;
import java.time.Period;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.person.*;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationAddressFormEmbed;
import de.symeda.sormas.ui.location.LocationCreateForm;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.SormasTextField;

public class PersonCreateForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = 639431574534995815L;

	private static final String PERSON_SEARCH_LOC = "personSearchLoc";
	private static final String ENTER_HOME_ADDRESS_NOW = "enterHomeAddressNow";
	private static final String HOME_ADDRESS_HEADER = "addressHeader";
	private static final String HOME_ADDRESS_LOC = "homeAddressLoc";

	private ComboBox birthDateDay;
	private CheckBox enterHomeAddressNow;
	private Label homeAddressHeader;
	private AbstractEditForm<LocationDto> homeAddressForm;
	private Button searchPersonButton;

	private PersonDto person;

	private final boolean showHomeAddressForm;
	private final boolean caseHomeAddressCoreLocationLayout;
	private final boolean showPresentCondition;
	private final boolean showSymptomsOnsetDate;
	private final boolean showPersonSearchButton;
	private SormasTextField nationalHealthIdField;
	private SormasTextField nationalityField;
	private Window warningSimilarPersons;
	private TextField approximateAgeField;
	private ComboBox approximateAgeTypeField;

	private static final String HTML_LAYOUT =
		"%s" + fluidRowLocs(PersonDto.OTHER_NAMES)
			+ fluidRow(
				fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
				fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE, PersonDto.APPROXIMATE_AGE_REFERENCE_DATE) + fluidRowLocs(PersonDto.APPROXIMATE_MONTH, PersonDto.APPROXIMATE_AGE_TYPE1, "") + fluidRowLocs(PersonDto.APPROXIMATE_DAY, PersonDto.APPROXIMATE_AGE_TYPE2, ""))
			+ fluidRowLocs(6, PersonDto.SEX, 6, PersonDto.NATIONALITY)
			+ fluidRowLocs(PersonDto.NATIONAL_HEALTH_ID, PersonDto.PASSPORT_NUMBER)
			+ fluidRowLocs(6, PersonDto.NATIONALITY)
			+ fluidRowLocs(PersonDto.BIRTH_COUNTRY)
			+ fluidRowLocs(PersonDto.PRESENT_CONDITION, SymptomsDto.ONSET_DATE) + fluidRowLocs(PersonDto.PHONE, PersonDto.EMAIL_ADDRESS)
			+ fluidRowLocs(ENTER_HOME_ADDRESS_NOW) + loc(HOME_ADDRESS_HEADER) + divsCss(VSPACE_3, fluidRowLocs(HOME_ADDRESS_LOC));

	private static final String NAME_ROW_WITH_PERSON_SEARCH = fluidRowLocs(6, PersonDto.FIRST_NAME, 4, PersonDto.LAST_NAME, 2, PERSON_SEARCH_LOC);
	private static final String NAME_ROW_WITHOUT_PERSON_SEARCH = fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME);

	public PersonCreateForm(boolean showHomeAddressForm, boolean showPresentCondition, boolean showSymptomsOnsetDate) {
		this(showHomeAddressForm, showPresentCondition, showSymptomsOnsetDate, true);
	}

	public PersonCreateForm(
		boolean showHomeAddressForm,
		boolean showPresentCondition,
		boolean showSymptomsOnsetDate,
		boolean showPersonSearchButton) {
		this(showHomeAddressForm, showPresentCondition, showSymptomsOnsetDate, showPersonSearchButton, false);
	}

	public PersonCreateForm(
		boolean showHomeAddressForm,
		boolean showPresentCondition,
		boolean showSymptomsOnsetDate,
		boolean showPersonSearchButton,
		boolean caseHomeAddressCoreLocationLayout) {

		super(
			PersonDto.class,
			PersonDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getDefault(false, FacadeProvider.getConfigFacade().getCountryLocale()));
		this.showHomeAddressForm = showHomeAddressForm;
		this.caseHomeAddressCoreLocationLayout = caseHomeAddressCoreLocationLayout;
		this.showPresentCondition = showPresentCondition;
		this.showSymptomsOnsetDate = showSymptomsOnsetDate;
		this.showPersonSearchButton = showPersonSearchButton;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return String.format(HTML_LAYOUT, showPersonSearchButton ? NAME_ROW_WITH_PERSON_SEARCH : NAME_ROW_WITHOUT_PERSON_SEARCH);
	}

	@Override
	protected void addFields() {

		addField(PersonDto.FIRST_NAME, TextField.class);
		addField(PersonDto.LAST_NAME, TextField.class);
		addField(PersonDto.OTHER_NAMES, TextField.class);

		if (showPersonSearchButton) {
			searchPersonButton = createPersonSearchButton(PERSON_SEARCH_LOC);
			getContent().addComponent(searchPersonButton, PERSON_SEARCH_LOC);
		}

		birthDateDay = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		birthDateDay.setCaption("");
		ComboBox birthDateMonth = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		birthDateMonth.setCaption("");
		DateHelper.getMonthsInYear()
			.forEach(month -> birthDateMonth.setItemCaption(month, de.symeda.sormas.api.Month.values()[month - 1].toString()));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		birthDateDay.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) birthDateMonth.getValue(), (Integer) e));
		birthDateMonth.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) e, (Integer) birthDateDay.getValue()));
		birthDateYear.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) e, (Integer) birthDateMonth.getValue(), (Integer) birthDateDay.getValue()));

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});

		approximateAgeField = addField(PersonDto.APPROXIMATE_AGE, TextField.class);
		approximateAgeField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, approximateAgeField.getCaption()));
		approximateAgeTypeField = addField(PersonDto.APPROXIMATE_AGE_TYPE, ComboBox.class);
		addField(PersonDto.APPROXIMATE_AGE_REFERENCE_DATE, DateField.class);

		TextField approximateMonthField = addField(PersonDto.APPROXIMATE_MONTH, TextField.class);
		approximateMonthField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, approximateMonthField.getCaption()));
		addField(PersonDto.APPROXIMATE_AGE_TYPE1, ComboBox.class);
		TextField approximateDayField = addField(PersonDto.APPROXIMATE_DAY, TextField.class);
		approximateDayField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, approximateDayField.getCaption()));
		addField(PersonDto.APPROXIMATE_AGE_TYPE2, ComboBox.class);

		ComboBox sex = addField(PersonDto.SEX, ComboBox.class);
		sex.removeItem(Sex.OTHER);
		sex.removeItem(Sex.UNKNOWN);

		addField(PersonDto.PASSPORT_NUMBER, TextField.class);

		nationalHealthIdField = addField(PersonDto.NATIONAL_HEALTH_ID, SormasTextField.class);
		nationalHealthIdField.setNullRepresentation("");

		nationalityField = addField(PersonDto.NATIONALITY, SormasTextField.class);
		nationalityField.setNullRepresentation("");

		List<de.symeda.sormas.api.infrastructure.country.CountryReferenceDto> countries = FacadeProvider.getCountryFacade().getAllActiveAsReference();
		addInfrastructureField(PersonDto.BIRTH_COUNTRY).addItems(countries);
		addInfrastructureField(PersonDto.CITIZENSHIP).addItems(countries);

		ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
		presentCondition.setVisible(showPresentCondition);
		FieldHelper.addSoftRequiredStyle(presentCondition, sex);

		if (showSymptomsOnsetDate) {
			addCustomField(
				SymptomsDto.ONSET_DATE,
				Date.class,
				DateField.class,
				I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
		}

		TextField phone = addCustomField(PersonDto.PHONE, String.class, TextField.class);
		phone.setCaption(I18nProperties.getCaption(Captions.Person_phone));
		TextField email = addCustomField(PersonDto.EMAIL_ADDRESS, String.class, TextField.class);
		email.setCaption(I18nProperties.getCaption(Captions.Person_emailAddress));

		phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption())));
		email.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, email.getCaption())));

		if (showHomeAddressForm) {
			addHomeAddressForm();
		}

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();
		hideValidationUntilNextCommit();
		setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void applyCaseCreationRequirements() {
		setRequired(true, PersonDto.PHONE);
		if (!getField(PersonDto.PHONE).isRequired()) {
			getField(PersonDto.PHONE).setRequired(true);
		}
		addCaseCreationAgeValidation();
	}

	private void addCaseCreationAgeValidation() {
		Validator ageOrBirthDateValidator = new Validator() {

			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (!isAgeOrBirthDateProvided()) {
					throw new InvalidValueException(I18nProperties.getValidationError(Validations.specifyAgeOrBirthDate));
				}
			}
		};

		getField(PersonDto.BIRTH_DATE_YYYY).addValidator(ageOrBirthDateValidator);
		approximateAgeField.addValidator(ageOrBirthDateValidator);

		approximateAgeField.addValueChangeListener(event -> {
			updateApproximateAgeTypeRequirement();
			updateAgeOrBirthDateRequirementIndicator();
		});
		getField(PersonDto.BIRTH_DATE_YYYY).addValueChangeListener(event -> {
			updateApproximateAgeTypeRequirement();
			updateAgeOrBirthDateRequirementIndicator();
		});
		approximateAgeTypeField.addValueChangeListener(event -> updateAgeOrBirthDateRequirementIndicator());
		updateApproximateAgeTypeRequirement();
		updateAgeOrBirthDateRequirementIndicator();
	}

	private void updateApproximateAgeTypeRequirement() {
		String ageValue = approximateAgeField.getValue();
		boolean ageProvided = StringUtils.isNotBlank(ageValue);
		boolean requireAgeTypeForMissingAgeOrBirthDate = !isAgeOrBirthDateProvided() && !ageProvided;
		setRequired(ageProvided || requireAgeTypeForMissingAgeOrBirthDate, PersonDto.APPROXIMATE_AGE_TYPE);
		if (ageProvided && approximateAgeTypeField.getValue() == null) {
			approximateAgeTypeField.setValue(ApproximateAgeType.YEARS);
		}
		if (!ageProvided && !requireAgeTypeForMissingAgeOrBirthDate) {
			approximateAgeTypeField.setValue(null);
		}
	}

	private void updateAgeOrBirthDateRequirementIndicator() {
		boolean required = !isAgeOrBirthDateProvided();
		setRequired(required, PersonDto.APPROXIMATE_AGE, PersonDto.BIRTH_DATE_YYYY);
	}

	private boolean isAgeOrBirthDateProvided() {
		Integer birthYear = (Integer) getField(PersonDto.BIRTH_DATE_YYYY).getValue();
		String ageValue = approximateAgeField.getValue();
		ApproximateAgeType ageType = (ApproximateAgeType) approximateAgeTypeField.getValue();
		return birthYear != null || (StringUtils.isNotBlank(ageValue) && ageType != null);
	}

	private void setItemCaptionsForMonths(AbstractSelect months) {

		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	private LocationAddressFormEmbed homeAddressCallbacks() {
		return (LocationAddressFormEmbed) homeAddressForm;
	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {

		Integer currentlySelected = (Integer) birthDateDay.getValue();
		birthDateDay.removeAllItems();
		birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		if (birthDateDay.containsId(currentlySelected)) {
			birthDateDay.setValue(currentlySelected);
		}
	}

	private void addHomeAddressForm() {

		enterHomeAddressNow = new CheckBox(I18nProperties.getCaption(Captions.caseDataEnterHomeAddressNow));
		enterHomeAddressNow.addStyleName(VSPACE_3);
		getContent().addComponent(enterHomeAddressNow, ENTER_HOME_ADDRESS_NOW);

		homeAddressHeader = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESS));
		homeAddressHeader.addStyleName(H3);
		getContent().addComponent(homeAddressHeader, HOME_ADDRESS_HEADER);
		homeAddressHeader.setVisible(false);

		if (caseHomeAddressCoreLocationLayout) {
			homeAddressForm = new LocationCreateForm(
				FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
				UiFieldAccessCheckers.getNoop());
		} else {
			homeAddressForm = new LocationEditForm(
				FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
				UiFieldAccessCheckers.getNoop());
		}
		homeAddressForm.setValue(new LocationDto());
		homeAddressForm.setCaption(null);
		homeAddressForm.setWidthFull();
		homeAddressCallbacks().setDisableFacilityAddressCheck(true);

		getContent().addComponent(homeAddressForm, HOME_ADDRESS_LOC);
		homeAddressForm.setVisible(false);

		enterHomeAddressNow.addValueChangeListener(e -> {
			boolean isChecked = (boolean) e.getProperty().getValue();
			homeAddressHeader.setVisible(isChecked);
			homeAddressForm.setVisible(isChecked);
			homeAddressCallbacks().setFacilityFieldsVisible(isChecked, true);
			if (!isChecked && person == null) {
				homeAddressForm.clear();
			}
		});
	}

	/**
	 * When {@code home} is true (case place of detection = home), show home address and require core location fields.
	 */
	public void applyPlaceOfDetectionHome(boolean home) {

		if (!showHomeAddressForm || homeAddressForm == null) {
			return;
		}
		if (home) {
			enterHomeAddressNow.setValue(true);
			homeAddressHeader.setVisible(true);
			homeAddressForm.setVisible(true);
			homeAddressCallbacks().setFacilityFieldsVisible(false, true);
			homeAddressCallbacks().setFieldsRequirement(
				true,
				LocationDto.REGION,
				LocationDto.DISTRICT,
				LocationDto.COMMUNITY,
				LocationDto.VILLAGE,
				LocationDto.NEAREST_HEALTH_FACILITY);
		} else {
			enterHomeAddressNow.setValue(false);
			homeAddressHeader.setVisible(false);
			homeAddressForm.setVisible(false);
			homeAddressCallbacks().setFieldsRequirement(
				false,
				LocationDto.REGION,
				LocationDto.DISTRICT,
				LocationDto.COMMUNITY,
				LocationDto.VILLAGE,
				LocationDto.NEAREST_HEALTH_FACILITY);
			if (person == null) {
				homeAddressForm.clear();
			}
		}
	}

	protected Button createPersonSearchButton(String personSearchLoc) {

		return ButtonHelper.createIconButtonWithCaption(personSearchLoc, StringUtils.EMPTY, VaadinIcons.SEARCH, clickEvent -> {
			VaadinIcons icon = (VaadinIcons) clickEvent.getButton().getIcon();
			if (icon == VaadinIcons.SEARCH) {
				PersonSearchField personSearchField = new PersonSearchField(null, I18nProperties.getString(Strings.infoSearchPerson));
				personSearchField.setWidth(1280, Unit.PIXELS);

				final CommitDiscardWrapperComponent<PersonSearchField> component = new CommitDiscardWrapperComponent<>(personSearchField);
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
				component.getCommitButton().setEnabled(false);
				component.addCommitListener(() -> {
					SimilarPersonDto pickedPerson = personSearchField.getValue();
					if (pickedPerson != null) {
						// add consumer
						person = FacadeProvider.getPersonFacade().getByUuid(pickedPerson.getUuid());
						setPerson(person);
						enablePersonFields(false, true);
						clickEvent.getButton().setIcon(VaadinIcons.CLOSE);
					}
				});

				personSearchField.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingSelectPerson));
			} else {
				person = null;
				setPerson(person);
				enablePersonFields(true);
				clickEvent.getButton().setIcon(VaadinIcons.SEARCH);
			}
		}, CssStyles.FORCE_CAPTION);
	}

	public void setPerson(PersonDto person) {
		setPerson(person, true);
	}

	public void setPerson(PersonDto person, boolean isNewPerson) {

		this.person = person;

		if (showHomeAddressForm) {
			enterHomeAddressNow.setEnabled(person == null || isNewPerson || LocationHelper.checkIsEmptyLocation(person.getAddress()));
			if (person == null || isNewPerson) {
				homeAddressForm.clear();
				homeAddressCallbacks().setFacilityFieldsVisible(false, true);
				homeAddressForm.setVisible(false);
				enterHomeAddressNow.setValue(person != null && person.getAddress() != null);
			} else {
				enterHomeAddressNow.setValue(false);
			}
		}

		if (person != null) {
			setValue(person);
			((TextField) getField(PersonDto.PHONE)).setValue(person.getPhone());
			((TextField) getField(PersonDto.EMAIL_ADDRESS)).setValue(person.getEmailAddress());
			if (homeAddressForm != null) {
				homeAddressForm.setValue(person.getAddress());
			}
		} else {
			setValue(new PersonDto());
			getField(PersonDto.PHONE).clear();
			getField(PersonDto.EMAIL_ADDRESS).clear();
			if (homeAddressForm != null) {
				homeAddressForm.clear();
			}
		}
	}

	public void transferDataToPerson(PersonDto person) {

		commit();
		PersonDto personCreated = getValue();

		person.setFirstName(personCreated.getFirstName());
		person.setLastName(personCreated.getLastName());
		person.setOtherNames(personCreated.getOtherNames());
		person.setBirthdateDD(personCreated.getBirthdateDD());
		person.setBirthdateMM(personCreated.getBirthdateMM());
		person.setBirthdateYYYY(personCreated.getBirthdateYYYY());
		person.setApproximateAge(personCreated.getApproximateAge());
		person.setApproximateAgeType(personCreated.getApproximateAgeType());
		person.setApproximateAgeReferenceDate(personCreated.getApproximateAgeReferenceDate());
		person.setSex(personCreated.getSex());
		person.setCitizenship(personCreated.getCitizenship());
		person.setBirthCountry(personCreated.getBirthCountry());
		person.setPresentCondition(personCreated.getPresentCondition());
		person.setNationalHealthId(personCreated.getNationalHealthId());
		person.setNationality(personCreated.getNationality());
		person.setPassportNumber(personCreated.getPassportNumber());

		if (StringUtils.isNotEmpty(getPhone())) {
			person.setPhone(getPhone());
		}
		if (StringUtils.isNotEmpty(getEmailAddress())) {
			person.setEmailAddress(getEmailAddress());
		}
		if (getHomeAddressForm() != null && getHomeAddressForm().getValue() != null) {
			person.setAddress(getHomeAddressForm().getValue());
		}
	}

	public void updateHomeAddress(PersonDto person) {

		commit();
		if (getHomeAddressForm() != null && getHomeAddressForm().getValue() != null) {
			person.setAddress(getHomeAddressForm().getValue());
		}
	}

	public void enablePersonFields(Boolean enabled) {
		enablePersonFields(enabled, false);
	}

	public void enablePersonFields(Boolean enabled, boolean alwaysEnableAddressFields) {

		getField(PersonDto.FIRST_NAME).setEnabled(enabled);
		getField(PersonDto.LAST_NAME).setEnabled(enabled);
		getField(PersonDto.BIRTH_DATE_DD).setEnabled(enabled);
		getField(PersonDto.BIRTH_DATE_MM).setEnabled(enabled);
		getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(enabled);
		getField(PersonDto.SEX).setEnabled(enabled);
		getField(PersonDto.PRESENT_CONDITION).setEnabled(enabled);
		getField(PersonDto.PHONE).setEnabled(enabled);
		getField(PersonDto.EMAIL_ADDRESS).setEnabled(enabled);
		getField(PersonDto.PASSPORT_NUMBER).setEnabled(enabled);
		getField(PersonDto.NATIONAL_HEALTH_ID).setEnabled(enabled);
		getField(PersonDto.NATIONALITY).setEnabled(enabled);
		if (homeAddressForm != null) {
			homeAddressForm.setEnabled(enabled || alwaysEnableAddressFields);
		}
		setRequired(enabled, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void setPersonalDetailsReadOnlyIfNotEmpty(boolean readOnly) {

		getField(PersonDto.FIRST_NAME).setEnabled(!readOnly);
		getField(PersonDto.LAST_NAME).setEnabled(!readOnly);
		searchPersonButton.setEnabled(!readOnly);
		if (getField(PersonDto.SEX).getValue() != null) {
			getField(PersonDto.SEX).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_MM).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_MM).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_DD).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_DD).setEnabled(!readOnly);
		}
		setRequired(!readOnly, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void setPersonDetailsReadOnly() {

		setEnabled(
			false,
			PersonDto.FIRST_NAME,
			PersonDto.LAST_NAME,
			PersonDto.SEX,
			PersonDto.BIRTH_DATE_YYYY,
			PersonDto.BIRTH_DATE_MM,
			PersonDto.BIRTH_DATE_DD,
			PersonDto.NATIONAL_HEALTH_ID,
			PersonDto.PASSPORT_NUMBER,
			PersonDto.NATIONALITY,
			PersonDto.PHONE,
			PersonDto.EMAIL_ADDRESS);

		searchPersonButton.setEnabled(false);

		setRequired(false, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public AbstractEditForm<LocationDto> getHomeAddressForm() {
		return homeAddressForm;
	}

	public void setSymptoms(SymptomsDto symptoms) {

		if (symptoms != null) {
			((DateField) getField(SymptomsDto.ONSET_DATE)).setValue(symptoms.getOnsetDate());
		} else {
			getField(SymptomsDto.ONSET_DATE).clear();
		}
	}

	public void updatePresentConditionEnum(Disease disease) {

		ComboBox presentConditionField = getField(PersonDto.PRESENT_CONDITION);
		PresentCondition currentValue = (PresentCondition) presentConditionField.getValue();
		List<PresentCondition> validValues;
		if (disease == null) {
			validValues = Arrays.asList(PresentCondition.values());
		} else {
			FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withDisease(disease);
			validValues = Arrays.stream(PresentCondition.values())
				.filter(c -> fieldVisibilityCheckers.isVisible(PresentCondition.class, c.name()))
				.collect(Collectors.toList());
			if (currentValue != null && !validValues.contains(currentValue)) {
				validValues.add(currentValue);
			}
		}
		FieldHelper.updateEnumData(presentConditionField, validValues);
	}

	public String getPhone() {
		return (String) getField(PersonDto.PHONE).getValue();
	}

	public String getEmailAddress() {
		return (String) getField(PersonDto.EMAIL_ADDRESS).getValue();
	}

	public Date getOnsetDate() {
		return (Date) getField(SymptomsDto.ONSET_DATE).getValue();
	}

	public PersonDto getSearchedPerson() {
		return person;
	}

	public void setSearchedPerson(PersonDto searchedPerson) {
		this.person = searchedPerson;
	}

	public Window getWarningSimilarPersons() {
		return warningSimilarPersons;
	}

	public SormasTextField getNationalHealthIdField() {
		return nationalHealthIdField;
	}

	private void updateReadyOnlyApproximateAge() {
		boolean readonly = false;
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			readonly = true;
		}

		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_MONTH).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE1).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_DAY).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE2).setReadOnly(readonly);
	}

	private Date calcBirthDateValue() {
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			Calendar birthDateCalendar = new GregorianCalendar();
			birthDateCalendar.set(
				(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue(),
				getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() != null
					? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() - 1
					: 0,
				getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue() != null
					? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue()
					: 1);
			return birthDateCalendar.getTime();
		}
		return null;
	}

	private void updateApproximateAge() {
		String approximateAge = null;
		ApproximateAgeType approximateAgeType = null;
		String approximateMonth = null;
		ApproximateAgeType approximateAgeType1 = null;
		String approximateDay = null;
		ApproximateAgeType approximateAgeType2 = null;

		Date birthDate = calcBirthDateValue();
		if (birthDate != null) {
			Period period = ApproximateAgeHelper.getApproximateAgePeriod(birthDate, null);
			approximateAge = String.valueOf(period.getYears());
			approximateAgeType = ApproximateAgeType.YEARS;
			approximateMonth = String.valueOf(period.getMonths());
			approximateAgeType1 = ApproximateAgeType.MONTHS;
			approximateDay = String.valueOf(period.getDays());
			approximateAgeType2 = ApproximateAgeType.DAYS;
		}

		setCalculatedAgeValue(PersonDto.APPROXIMATE_AGE, approximateAge);
		setCalculatedAgeValue(PersonDto.APPROXIMATE_AGE_TYPE, approximateAgeType);
		setCalculatedAgeValue(PersonDto.APPROXIMATE_MONTH, approximateMonth);
		setCalculatedAgeValue(PersonDto.APPROXIMATE_AGE_TYPE1, approximateAgeType1);
		setCalculatedAgeValue(PersonDto.APPROXIMATE_DAY, approximateDay);
		setCalculatedAgeValue(PersonDto.APPROXIMATE_AGE_TYPE2, approximateAgeType2);
	}

	/**
	 * The age fields are calculated from the date of birth, so they have to be written while they are read only. Whether they stay
	 * read only afterwards is decided by {@link #updateReadyOnlyApproximateAge()}, which runs right after every calculation.
	 */
	@SuppressWarnings("unchecked")
	private void setCalculatedAgeValue(String propertyId, Object value) {
		Field<Object> field = (Field<Object>) getFieldGroup().getField(propertyId);
		field.setReadOnly(false);
		field.setValue(value);
		field.setReadOnly(true);
	}
}

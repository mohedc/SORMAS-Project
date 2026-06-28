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

package de.symeda.sormas.app.person.edit;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import android.widget.TextView;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public final class PersonValidator {

	static void initializePersonValidation(final FragmentPersonEditLayoutBinding contentBinding) {
		ResultCallback<Boolean> deathDateCallback = () -> {
			Date birthDate = PersonEditFragment.calculateBirthDateValue(contentBinding);
			if (DateHelper.isDateBefore(contentBinding.personDeathDate.getValue(), birthDate)) {
				contentBinding.personDeathDate.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDate,
						contentBinding.personDeathDate.getCaption(),
						contentBinding.personBirthdateLabel.getText()));
				return true;
			}
			if (DateHelper.isDateAfter(contentBinding.personDeathDate.getValue(), contentBinding.personBurialDate.getValue())) {
				contentBinding.personDeathDate.enableErrorState(
						I18nProperties.getValidationError(
								Validations.beforeDate,
								contentBinding.personDeathDate.getCaption(),
								contentBinding.personBurialDate.getCaption()));
				return true;
			}

			return false;
		};

		ResultCallback<Boolean> burialDateCallback = () -> {
			Date birthDate = PersonEditFragment.calculateBirthDateValue(contentBinding);
			if (DateHelper.isDateBefore(contentBinding.personBurialDate.getValue(), birthDate)) {
				contentBinding.personBurialDate.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDate,
						contentBinding.personBurialDate.getCaption(),
						contentBinding.personBirthdateLabel.getText()));
				return true;
			}

			if (DateHelper.isDateBefore(contentBinding.personBurialDate.getValue(), contentBinding.personDeathDate.getValue())) {
				contentBinding.personBurialDate.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDate,
						contentBinding.personBurialDate.getCaption(),
						contentBinding.personDeathDate.getCaption()));
				return true;
			}

			return false;
		};

		ResultCallback<Boolean> approximateAgeCallback = () -> {
			if (ApproximateAgeType.YEARS.equals(contentBinding.personApproximateAgeType.getValue())
				&& !StringUtils.isEmpty(contentBinding.personApproximateAge.getValue())
				&& Integer.valueOf(contentBinding.personApproximateAge.getValue()) >= 150) {
				contentBinding.personApproximateAge.enableErrorState(I18nProperties.getValidationError(Validations.softApproximateAgeTooHigh));
				return true;
			}

			return false;
		};

		initializeBirthDateValidation(contentBinding.personBirthdateYYYY, contentBinding.personBirthdateMM, contentBinding.personBirthdateDD);

		contentBinding.personDeathDate.setValidationCallback(deathDateCallback);
		contentBinding.personBurialDate.setValidationCallback(burialDateCallback);
		contentBinding.personApproximateAge.setValidationCallback(approximateAgeCallback);

		contentBinding.personDeathDate.addValueChangedListener( v -> {
			if(!burialDateCallback.call()){
				contentBinding.personBurialDate.disableErrorState();
			}
		});

		contentBinding.personBurialDate.addValueChangedListener( v -> {
			if(!deathDateCallback.call()){
				contentBinding.personDeathDate.disableErrorState();
			}
		});

		contentBinding.personBirthdateYYYY.addValueChangedListener(v -> {
			if(!deathDateCallback.call()){
				contentBinding.personDeathDate.disableErrorState();
			}
			if(!burialDateCallback.call()){
				contentBinding.personBurialDate.disableErrorState();
			}
		});
		contentBinding.personBirthdateMM.addValueChangedListener(v -> {
			if(!deathDateCallback.call()){
				contentBinding.personDeathDate.disableErrorState();
			}
			if(!burialDateCallback.call()){
				contentBinding.personBurialDate.disableErrorState();
			}
		});
		contentBinding.personBirthdateDD.addValueChangedListener(v -> {
			if(!deathDateCallback.call()){
				contentBinding.personDeathDate.disableErrorState();
			}
			if(!burialDateCallback.call()){
				contentBinding.personBurialDate.disableErrorState();
			}
		});
	}

	public static void initializeBirthDateValidation(
		ControlSpinnerField personBirthdateYYYY,
		ControlSpinnerField personBirthdateMM,
		ControlSpinnerField personBirthdateDD) {

		ResultCallback<Boolean> birthDateCallback = () -> {
			Calendar calendar = Calendar.getInstance();
			calendar.setLenient(false);
			if (personBirthdateYYYY.getValue() != null) {
				calendar.set(Calendar.YEAR, (Integer) personBirthdateYYYY.getValue());
			}
			if (personBirthdateMM.getValue() != null) {
				calendar.set(Calendar.MONTH, ((Integer) personBirthdateMM.getValue()) - 1);
			}
			if (personBirthdateDD.getValue() != null) {
				calendar.set(Calendar.DAY_OF_MONTH, (Integer) personBirthdateDD.getValue());
			}

			if (DateHelper.isDateAfter(calendar.getTime(), new Date())) {
				personBirthdateYYYY.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
				personBirthdateMM.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
				personBirthdateDD.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
				return true;
			}

			return false;
		};

		personBirthdateYYYY.setValidationCallback(birthDateCallback);
		personBirthdateMM.setValidationCallback(birthDateCallback);
		personBirthdateDD.setValidationCallback(birthDateCallback);
	}

	public static void initializeCaseCreationValidation(final FragmentCaseNewLayoutBinding contentBinding) {
		ValidationHelper.initPhoneNumberValidator(contentBinding.personPhone);

		ResultCallback<Boolean> birthDateInFutureCallback = () -> isBirthDateInFuture(
			contentBinding.personBirthdateYYYY,
			contentBinding.personBirthdateMM,
			contentBinding.personBirthdateDD);

		ResultCallback<Boolean> ageOrBirthDateCallback = () -> validateAgeOrBirthDateCaseCreation(contentBinding);

		ResultCallback<Boolean> birthDateCombinedCallback = () -> birthDateInFutureCallback.call() | ageOrBirthDateCallback.call();

		contentBinding.personBirthdateYYYY.setValidationCallback(birthDateCombinedCallback);
		contentBinding.personBirthdateMM.setValidationCallback(birthDateCombinedCallback);
		contentBinding.personBirthdateDD.setValidationCallback(birthDateCombinedCallback);
		contentBinding.personApproximateAge.setValidationCallback(ageOrBirthDateCallback);
		contentBinding.personApproximateAgeType.setValidationCallback(ageOrBirthDateCallback);

		contentBinding.personApproximateAge.addValueChangedListener(field -> updateApproximateAgeTypeRequirement(contentBinding));
		contentBinding.personBirthdateYYYY.addValueChangedListener(field -> updateAgeOrBirthDateRequirementIndicator(contentBinding));
		contentBinding.personApproximateAge.addValueChangedListener(field -> updateAgeOrBirthDateRequirementIndicator(contentBinding));
		contentBinding.personApproximateAgeType.addValueChangedListener(field -> updateAgeOrBirthDateRequirementIndicator(contentBinding));
		updateApproximateAgeTypeRequirement(contentBinding);
		updateAgeOrBirthDateRequirementIndicator(contentBinding);
	}

	private static boolean isBirthDateInFuture(
		ControlSpinnerField personBirthdateYYYY,
		ControlSpinnerField personBirthdateMM,
		ControlSpinnerField personBirthdateDD) {

		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		if (personBirthdateYYYY.getValue() != null) {
			calendar.set(Calendar.YEAR, (Integer) personBirthdateYYYY.getValue());
		}
		if (personBirthdateMM.getValue() != null) {
			calendar.set(Calendar.MONTH, ((Integer) personBirthdateMM.getValue()) - 1);
		}
		if (personBirthdateDD.getValue() != null) {
			calendar.set(Calendar.DAY_OF_MONTH, (Integer) personBirthdateDD.getValue());
		}

		if (personBirthdateYYYY.getValue() != null && DateHelper.isDateAfter(calendar.getTime(), new Date())) {
			personBirthdateYYYY.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
			personBirthdateMM.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
			personBirthdateDD.enableErrorState(I18nProperties.getValidationError(Validations.birthDateInFuture));
			return true;
		}

		return false;
	}

	private static boolean validateAgeOrBirthDateCaseCreation(FragmentCaseNewLayoutBinding contentBinding) {
		if (isAgeOrBirthDateProvided(contentBinding)) {
			contentBinding.personBirthdateYYYY.disableErrorState();
			contentBinding.personBirthdateMM.disableErrorState();
			contentBinding.personBirthdateDD.disableErrorState();
			contentBinding.personApproximateAge.disableErrorState();
			contentBinding.personApproximateAgeType.disableErrorState();
			return false;
		}

		String errorMessage = I18nProperties.getValidationError(Validations.specifyAgeOrBirthDate);
		contentBinding.personBirthdateYYYY.enableErrorState(errorMessage);
		contentBinding.personBirthdateMM.enableErrorState(errorMessage);
		contentBinding.personBirthdateDD.enableErrorState(errorMessage);
		contentBinding.personApproximateAge.enableErrorState(errorMessage);
		contentBinding.personApproximateAgeType.enableErrorState(errorMessage);
		return true;
	}

	private static boolean isAgeOrBirthDateProvided(FragmentCaseNewLayoutBinding contentBinding) {
		Integer birthYear = (Integer) contentBinding.personBirthdateYYYY.getValue();
		String ageValue = contentBinding.personApproximateAge.getValue();
		ApproximateAgeType ageType = (ApproximateAgeType) contentBinding.personApproximateAgeType.getValue();
		return birthYear != null || (!StringUtils.isEmpty(ageValue) && ageType != null);
	}

	private static void updateApproximateAgeTypeRequirement(FragmentCaseNewLayoutBinding contentBinding) {
		String ageValue = contentBinding.personApproximateAge.getValue();
		if (StringUtils.isEmpty(ageValue)) {
			contentBinding.personApproximateAgeType.setRequired(false);
			contentBinding.personApproximateAgeType.setValue(null);
		} else {
			contentBinding.personApproximateAgeType.setRequired(true);
			if (contentBinding.personApproximateAgeType.getValue() == null) {
				contentBinding.personApproximateAgeType.setValue(ApproximateAgeType.YEARS);
			}
		}
	}

	private static void updateAgeOrBirthDateRequirementIndicator(FragmentCaseNewLayoutBinding contentBinding) {
		boolean ageOrBirthDateProvided = isAgeOrBirthDateProvided(contentBinding);
		boolean required = !ageOrBirthDateProvided;

		contentBinding.personApproximateAge.setRequired(required);
		updateBirthDateLabel(contentBinding.personBirthdateLabel, required);
	}

	private static void updateBirthDateLabel(TextView birthDateLabel, boolean required) {
		String caption = birthDateLabel.getResources().getString(R.string.caption_date_of_birth);
		if (required) {
			caption += " " + birthDateLabel.getResources().getString(R.string.indicator_required);
		}
		birthDateLabel.setText(caption);
	}

	public static void validateRequiredFieldsForCaseCreation(Person person) throws ValidationException {
		if (person == null) {
			throw new ValidationException(I18nProperties.getValidationError(Validations.validPerson));
		}
		if (StringUtils.isBlank(person.getPhone())) {
			throw new ValidationException(I18nProperties.getValidationError(Validations.specifyPrimaryPhoneNumber));
		}
		if (!DataHelper.isValidPhoneNumber(person.getPhone())) {
			throw new ValidationException(
				I18nProperties.getValidationError(Validations.validPhoneNumber, I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PHONE)));
		}
		boolean hasBirthDateYear = person.getBirthdateYYYY() != null;
		boolean hasApproximateAge = person.getApproximateAge() != null && person.getApproximateAgeType() != null;
		if (!hasBirthDateYear && !hasApproximateAge) {
			throw new ValidationException(I18nProperties.getValidationError(Validations.specifyAgeOrBirthDate));
		}
	}

}

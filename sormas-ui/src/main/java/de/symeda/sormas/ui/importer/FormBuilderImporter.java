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

package de.symeda.sormas.ui.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.fields.FormFieldIndexDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsCriteria;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import form builders.
 */
public class FormBuilderImporter extends DataImporter {

	protected final boolean allowOverwrite;

	public FormBuilderImporter(File inputFile, UserDto currentUser, boolean allowOverwrite, ValueSeparator csvSeparator) throws IOException {
		super(inputFile, false, currentUser, csvSeparator);
		this.allowOverwrite = allowOverwrite;
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		FormBuilderDto newFormBuilderDto = FormBuilderDto.build();

		boolean hasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			try {
				// If the cell entry is not empty, try to insert it into the current form builder object
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newFormBuilderDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		if (!hasImportError) {
			// Validate required fields
			if (newFormBuilderDto.getFormType() == null) {
				writeImportError(values, I18nProperties.getValidationError(Validations.importErrorInColumn, "formType") + ": " + I18nProperties.getValidationError(Validations.required));
				hasImportError = true;
			}
			if (newFormBuilderDto.getDisease() == null) {
				writeImportError(values, I18nProperties.getValidationError(Validations.importErrorInColumn, "disease") + ": " + I18nProperties.getValidationError(Validations.required));
				hasImportError = true;
			}
		}

		if (!hasImportError) {
			ImportLineResultDto<EntityDto> constraintErrors = validateConstraints(newFormBuilderDto);
			if (constraintErrors.isError()) {
				writeImportError(values, constraintErrors.getMessage());
				hasImportError = true;
			}
		}

		// Save the form builder into the database if the import has no errors
		if (!hasImportError) {
			try {
				FacadeProvider.getFormBuilderFacade().save(newFormBuilderDto, allowOverwrite);
				return ImportLineResult.SUCCESS;
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	/**
	 * Inserts the entry of a single cell into the form builder object.
	 */
	private void insertColumnEntryIntoData(FormBuilderDto newFormBuilderDto, String value, String[] entityPropertyPath)
		throws InvalidColumnException, ImportErrorException {

		String propertyName = entityPropertyPath[entityPropertyPath.length - 1];

		try {
			switch (propertyName) {
			case "uuid":
				newFormBuilderDto.setUuid(value);
				break;
			case "formType":
				try {
					FormType formType = FormType.valueOf(value);
					newFormBuilderDto.setFormType(formType);
				} catch (IllegalArgumentException e) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, propertyName) + ": Invalid enum value '" + value + "'");
				}
				break;
			case "disease":
				try {
					Disease disease = Disease.valueOf(value);
					newFormBuilderDto.setDisease(disease);
				} catch (IllegalArgumentException e) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, propertyName) + ": Invalid enum value '" + value + "'");
				}
				break;
			case "active":
				if (StringUtils.isNotBlank(value)) {
					Boolean active = Boolean.parseBoolean(value);
					newFormBuilderDto.setActive(active);
				}
				break;
			case "formFields":
				parseFormFields(newFormBuilderDto, value);
				break;
			default:
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			}
		} catch (ImportErrorException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error when trying to import form builder data: " + e.getMessage());
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
		}
	}

	/**
	 * Parses the comma-separated formFields string and resolves UUIDs.
	 * Supports both UUID format and fieldName format.
	 */
	private void parseFormFields(FormBuilderDto formBuilderDto, String formFieldsValue) throws ImportErrorException {
		if (StringUtils.isBlank(formFieldsValue)) {
			return;
		}

		String[] fieldIdentifiers = formFieldsValue.split(",");
		List<FormFieldReferenceDto> formFieldRefs = new ArrayList<>();

		for (int i = 0; i < fieldIdentifiers.length; i++) {
			String identifier = fieldIdentifiers[i].trim();
			if (StringUtils.isBlank(identifier)) {
				continue;
			}

			String fieldUuid = null;

			// Try to resolve as UUID first
			if (isValidUuid(identifier)) {
				// Check if UUID exists
				de.symeda.sormas.api.infrastructure.fields.FormFieldsDto fieldDto = FacadeProvider.getFormFieldFacade().getByUuid(identifier);
				if (fieldDto != null) {
					fieldUuid = identifier;
				} else {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, "formFields") + ": FormField with UUID '" + identifier + "' not found");
				}
			} else {
				// Try to resolve by fieldName
				fieldUuid = resolveFieldNameToUuid(identifier);
				if (fieldUuid == null) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, "formFields") + ": FormField with fieldName '" + identifier + "' not found");
				}
			}

			// Create FormFieldReferenceDto with displayOrder
			FormFieldReferenceDto refDto = new FormFieldReferenceDto(fieldUuid);
			refDto.setDisplayOrder(i); // Set displayOrder based on position (0-based)
			formFieldRefs.add(refDto);
		}

		formBuilderDto.setFormFields(formFieldRefs);
	}

	/**
	 * Checks if a string is a valid UUID format.
	 */
	private boolean isValidUuid(String str) {
		try {
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Resolves a fieldName to UUID by searching through FormFields.
	 */
	private String resolveFieldNameToUuid(String fieldName) {
		// Get all active form fields
		FormFieldsCriteria criteria = new FormFieldsCriteria();
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		List<FormFieldIndexDto> allFields = FacadeProvider.getFormFieldFacade().getIndexList(criteria, null, null, null);

		// Search for field with matching fieldName (case-insensitive)
		for (FormFieldIndexDto field : allFields) {
			if (field.getFieldName() != null && field.getFieldName().equalsIgnoreCase(fieldName)) {
				return field.getUuid();
			}
		}

		return null;
	}
}

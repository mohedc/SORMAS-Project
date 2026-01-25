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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import form fields.
 */
public class FormFieldImporter extends DataImporter {

	protected final boolean allowOverwrite;

	public FormFieldImporter(File inputFile, UserDto currentUser, boolean allowOverwrite, ValueSeparator csvSeparator) throws IOException {
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

		FormFieldsDto newFormFieldDto = FormFieldsDto.build();

		boolean hasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			try {
				// If the cell entry is not empty, try to insert it into the current form field object
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newFormFieldDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		if (!hasImportError) {
			// Validate required fields
			if (newFormFieldDto.getFormType() == null) {
				writeImportError(values, I18nProperties.getValidationError(Validations.importErrorInColumn, "formType") + ": " + I18nProperties.getValidationError(Validations.required));
				hasImportError = true;
			}
			if (StringUtils.isBlank(newFormFieldDto.getFieldName())) {
				writeImportError(values, I18nProperties.getValidationError(Validations.importErrorInColumn, "fieldName") + ": " + I18nProperties.getValidationError(Validations.required));
				hasImportError = true;
			}
		}

		if (!hasImportError) {
			ImportLineResultDto<EntityDto> constraintErrors = validateConstraints(newFormFieldDto);
			if (constraintErrors.isError()) {
				writeImportError(values, constraintErrors.getMessage());
				hasImportError = true;
			}
		}

		// Save the form field into the database if the import has no errors
		if (!hasImportError) {
			try {
				FacadeProvider.getFormFieldFacade().save(newFormFieldDto, allowOverwrite);
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
	 * Inserts the entry of a single cell into the form field object.
	 */
	private void insertColumnEntryIntoData(FormFieldsDto newFormFieldDto, String value, String[] entityPropertyPath)
		throws InvalidColumnException, ImportErrorException {

		String propertyName = entityPropertyPath[entityPropertyPath.length - 1];

		try {
			switch (propertyName) {
			case "formType":
				try {
					FormType formType = FormType.valueOf(value);
					newFormFieldDto.setFormType(formType);
				} catch (IllegalArgumentException e) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, propertyName) + ": Invalid enum value '" + value + "'");
				}
				break;
			case "fieldName":
				newFormFieldDto.setFieldName(value);
				break;
			case "description":
				newFormFieldDto.setDescription(value);
				break;
			case "active":
				if (StringUtils.isNotBlank(value)) {
					Boolean active = Boolean.parseBoolean(value);
					newFormFieldDto.setActive(active);
				}
				break;
			default:
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			}
		} catch (ImportErrorException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error when trying to import form field data: " + e.getMessage());
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
		}
	}
}

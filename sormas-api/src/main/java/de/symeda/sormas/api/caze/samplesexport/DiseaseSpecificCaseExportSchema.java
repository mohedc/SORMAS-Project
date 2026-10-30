/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.afpimmunization.AfpImmunizationDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.response.ResponseDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public final class DiseaseSpecificCaseExportSchema {

	public static final String BLOCK_CASE = "case";
	public static final String BLOCK_PERSON = "person";
	public static final String BLOCK_SYMPTOMS = "symptoms";
	public static final String BLOCK_HOSPITALIZATION = "hospitalization";
	public static final String BLOCK_EPI_DATA = "epiData";
	public static final String BLOCK_RESPONSE = "response";
	public static final String BLOCK_AFP_IMMUNIZATION = "afpImmunization";
	public static final String BLOCK_SAMPLE = "sample";
	public static final String BLOCK_PATHOGEN_TESTS = "pathogenTests";

	private static final Set<String> SKIP_PROPERTIES = Collections.unmodifiableSet(
		new HashSet<>(
			Arrays.asList(
				"uuid",
				"changeDate",
				"creationDate",
				"pseudonymized",
				"deleted",
				"deletionReason",
				"otherDeletionReason",
				"class",
				"caption",
				"i18nPrefix",
				"hasCaption",
				"inJurisdiction",
				"completeness")));

	private DiseaseSpecificCaseExportSchema() {
	}

	public static List<Column> build(Disease disease, String countryLocale) {
		if (!DiseaseSpecificExportFieldConfiguration.isSupported(disease)) {
			return Collections.emptyList();
		}

		FieldVisibilityCheckers visibility = FieldVisibilityCheckers.withDisease(disease);
		if (countryLocale != null && !countryLocale.isEmpty()) {
			visibility = visibility.andWithCountry(countryLocale);
		}
		List<Column> columns = new ArrayList<>();
		int order = 1;
		order = addBlock(columns, order, BLOCK_CASE, "", CaseDataDto.class, CaseDataDto.I18N_PREFIX, disease, visibility);
		order = addBlock(columns, order, BLOCK_PERSON, "person.", PersonDto.class, PersonDto.I18N_PREFIX, disease, visibility);
		order = addBlock(columns, order, BLOCK_SYMPTOMS, "symptoms.", SymptomsDto.class, SymptomsDto.I18N_PREFIX, disease, visibility);
		order = addBlock(
			columns,
			order,
			BLOCK_HOSPITALIZATION,
			"hospitalization.",
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			disease,
			visibility);
		if (DiseaseSpecificExportFieldConfiguration.hasEpiDataBlock(disease)) {
			order = addBlock(columns, order, BLOCK_EPI_DATA, "epiData.", EpiDataDto.class, EpiDataDto.I18N_PREFIX, disease, visibility);
		}
		if (DiseaseSpecificExportFieldConfiguration.hasResponseBlock(disease)) {
			order = addBlock(columns, order, BLOCK_RESPONSE, "response.", ResponseDto.class, ResponseDto.I18N_PREFIX, disease, visibility);
		}
		if (DiseaseSpecificExportFieldConfiguration.hasAfpImmunizationBlock(disease)) {
			order = addBlock(
				columns,
				order,
				BLOCK_AFP_IMMUNIZATION,
				"afpImmunization.",
				AfpImmunizationDto.class,
				AfpImmunizationDto.I18N_PREFIX,
				disease,
				visibility);
		}
		if (DiseaseSpecificExportFieldConfiguration.hasSampleBlock(disease)) {
			order = addBlock(columns, order, BLOCK_SAMPLE, "sample.", SampleDto.class, SampleDto.I18N_PREFIX, disease, visibility);
			order = addBlock(
				columns,
				order,
				BLOCK_PATHOGEN_TESTS,
				"pathogenTests.",
				PathogenTestDto.class,
				PathogenTestDto.I18N_PREFIX,
				disease,
				visibility);
		}
		return columns;
	}

	public static String toSeedCsv(Disease disease, String countryLocale) {
		StringBuilder csv = new StringBuilder();
		csv.append("disease,propertyPath,displayOrder,active\n");
		for (Column column : build(disease, countryLocale)) {
			csv.append(disease.name())
				.append(',')
				.append(column.getPropertyPath())
				.append(',')
				.append(column.getDisplayOrder())
				.append(",true\n");
		}
		return csv.toString();
	}

	public static Set<String> unionCatalogPaths(String countryLocale) {
		Set<String> paths = new LinkedHashSet<>();
		for (Disease disease : DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES) {
			for (Column column : build(disease, countryLocale)) {
				paths.add(column.getPropertyPath());
			}
		}
		return paths;
	}

	private static int addBlock(
		List<Column> columns,
		int order,
		String block,
		String pathPrefix,
		Class<?> dtoClass,
		String i18nPrefix,
		Disease disease,
		FieldVisibilityCheckers visibility) {

		List<PropertyDescriptor> descriptors = readDescriptors(dtoClass);
		descriptors.sort(Comparator.comparing(PropertyDescriptor::getName));
		Set<String> extra = pathPrefix.isEmpty() ? DiseaseSpecificExportFieldConfiguration.extraAllowCaseFields(disease) : Collections.emptySet();

		for (PropertyDescriptor descriptor : descriptors) {
			String name = descriptor.getName();
			if (SKIP_PROPERTIES.contains(name) || descriptor.getReadMethod() == null) {
				continue;
			}
			Class<?> type = descriptor.getPropertyType();
			if (!isExportableType(type)) {
				continue;
			}
			boolean visible = visibility.isVisible(dtoClass, name);
			if (!visible && !extra.contains(name)) {
				continue;
			}

			String propertyPath = pathPrefix + name;
			String caption = I18nProperties.getPrefixCaption(i18nPrefix, name, name);
			columns.add(new Column(block, dtoClass, propertyPath, name, caption, order++));
		}

		if (pathPrefix.isEmpty() && extra.contains("vaccinationDate") && columns.stream().noneMatch(c -> "vaccinationDate".equals(c.getPropertyPath()))) {
			columns.add(
				new Column(
					block,
					dtoClass,
					"vaccinationDate",
					CaseDataDto.LAST_VACCINATION_DATE,
					I18nProperties.getPrefixCaption(i18nPrefix, CaseDataDto.LAST_VACCINATION_DATE, "vaccinationDate"),
					order++));
		}
		return order;
	}

	private static List<PropertyDescriptor> readDescriptors(Class<?> dtoClass) {
		try {
			return Arrays.asList(Introspector.getBeanInfo(dtoClass, Object.class).getPropertyDescriptors());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isExportableType(Class<?> type) {
		if (type == null || type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			return false;
		}
		if (EntityDto.class.isAssignableFrom(type) && !ReferenceDto.class.isAssignableFrom(type)) {
			return false;
		}
		return type.isEnum()
			|| type == String.class
			|| Date.class.isAssignableFrom(type)
			|| Number.class.isAssignableFrom(type)
			|| type == Boolean.class
			|| type == boolean.class
			|| type == int.class
			|| type == long.class
			|| type == float.class
			|| type == double.class
			|| type == BigDecimal.class
			|| ReferenceDto.class.isAssignableFrom(type)
			|| CustomizableEnum.class.isAssignableFrom(type);
	}

	public static class Column {

		private final String schemaBlock;
		private final Class<?> entityClass;
		private final String propertyPath;
		private final String propertyName;
		private final String caption;
		private final int displayOrder;

		public Column(String schemaBlock, Class<?> entityClass, String propertyPath, String propertyName, String caption, int displayOrder) {
			this.schemaBlock = schemaBlock;
			this.entityClass = entityClass;
			this.propertyPath = propertyPath;
			this.propertyName = propertyName;
			this.caption = caption;
			this.displayOrder = displayOrder;
		}

		public String getSchemaBlock() {
			return schemaBlock;
		}

		public Class<?> getEntityClass() {
			return entityClass;
		}

		public String getPropertyPath() {
			return propertyPath;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public String getCaption() {
			return caption;
		}

		public int getDisplayOrder() {
			return displayOrder;
		}

		public Method getReadMethod() {
			try {
				return new PropertyDescriptor(propertyName, entityClass).getReadMethod();
			} catch (IntrospectionException e) {
				return null;
			}
		}
	}
}

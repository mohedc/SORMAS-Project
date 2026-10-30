/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.api.caze.samplesexport;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;

public final class DiseaseSpecificExportFieldConfiguration {

	public static final Set<Disease> SUPPORTED_DISEASES = Collections.unmodifiableSet(
		EnumSet.of(
			Disease.AFP,
			Disease.CSM,
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
			Disease.MEASLES,
			Disease.NEONATAL_TETANUS,
			Disease.RUBELLA,
			Disease.YELLOW_FEVER));

	private DiseaseSpecificExportFieldConfiguration() {
	}

	public static boolean isSupported(Disease disease) {
		return disease != null && SUPPORTED_DISEASES.contains(disease);
	}

	public static boolean hasSampleBlock(Disease disease) {
		return disease != Disease.NEONATAL_TETANUS;
	}

	public static boolean hasEpiDataBlock(Disease disease) {
		return disease != Disease.NEONATAL_TETANUS && disease != Disease.MEASLES;
	}

	public static boolean hasResponseBlock(Disease disease) {
		return disease == Disease.NEONATAL_TETANUS;
	}

	public static boolean hasAfpImmunizationBlock(Disease disease) {
		return disease == Disease.AFP;
	}

	public static Set<String> extraAllowCaseFields(Disease disease) {
		Set<String> extra = new LinkedHashSet<>();
		if (disease == Disease.YELLOW_FEVER || disease == Disease.CSM || disease == Disease.MEASLES || disease == Disease.RUBELLA) {
			extra.addAll(Arrays.asList(CaseDataDto.VACCINATION_STATUS, CaseDataDto.VACCINATED, CaseDataDto.LAST_VACCINATION_DATE, "vaccinationDate"));
		}
		if (disease == Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS) {
			extra.add(CaseDataDto.IDSR_DIAGNOSIS);
			extra.add(CaseDataDto.IDSR_DIAGNOSIS_DETAILS);
		}
		return extra;
	}
}

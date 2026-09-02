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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;

import java.util.Arrays;
import java.util.List;

public enum PathogenTestType {

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	ANTIBODY_DETECTION,
	@Diseases(value = {
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	ANTIGEN_DETECTION,
	@Diseases(value = {
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	RAPID_TEST,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	CULTURE,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	HISTOPATHOLOGY,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	ISOLATION,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.CSM,
		Disease.MEASLES,
		Disease.DENGUE,
		Disease.CONGENITAL_RUBELLA
		}, hide = true)
	IGM_SERUM_ANTIBODY,
	@Diseases(value = {
			Disease.MEASLES,
			Disease.DENGUE
		}, hide = true)
	SUSTAINED_IGG_LEVEL,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	IGG_SERUM_ANTIBODY,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	IGA_SERUM_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	INCUBATION_TIME,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	INDIRECT_FLUORESCENT_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	DIRECT_FLUORESCENT_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	MICROSCOPY,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	NEUTRALIZING_ANTIBODIES,
	@Diseases(value = {
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	PCR_RT_PCR,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	GRAM_STAIN,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CONGENITAL_RUBELLA,
		Disease.CSM }, hide = true)
	LATEX_AGGLUTINATION,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA,
		Disease.CSM }, hide = true)
	CQ_VALUE_DETECTION,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	SEQUENCING,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	DNA_MICROARRAY,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	TMA,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	IGRA,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	TST,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	BEIJINGGENOTYPING,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	SPOLIGOTYPING,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	MIRU_PATTERN_CODE,
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	ANTIBIOTIC_SUSCEPTIBILITY,
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	MULTILOCUS_SEQUENCE_TYPING,
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION})
	SLIDE_AGGLUTINATION,
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	WHOLE_GENOME_SEQUENCING,
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	SEROGROUPING,
	@Diseases(value = {
		Disease.CSM })
	CELL_COUNT,
	@Diseases(value = {
		Disease.CSM })
	WBC_COUNT,

	@Diseases(value = {
		Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
	P_FALICIPARUM,
	@Diseases(value = {
		Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
	P_VIVAX,
	@Diseases(value = {
		Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
	SHIGELLA,
	@Diseases(value = {
		Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS })
	LATEX,
	@Diseases(value = {
			Disease.AFP })
	WILD_POLIOVIRUS,
	@Diseases(value = {
			Disease.AFP })
	VDPV,
	@Diseases(value = {
			Disease.AFP })
	SABIN_STRAIN,
	@Diseases(value = {
			Disease.AFP })
	NON_POLIO_ENTEROVIRUS,

	@Diseases(value = {
		Disease.CSM })
	AGGLUTINATION_TEST,

	@Diseases(value = {
			Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS,
			Disease.MEASLES,
			Disease.RUBELLA,
			Disease.DENGUE
	})
	PCR,

	@Diseases(value = {
		Disease.CONGENITAL_RUBELLA })
	RUBELLA_VIRUS_ISOLATION,
	@Diseases(value = {
		Disease.CONGENITAL_RUBELLA })
	RUBELLA_IGM,
	@Diseases(value = {
		Disease.CONGENITAL_RUBELLA })
	RUBELLA_PCR,

	@Diseases(value = {
	 })
	INDIRECT_IGM_SEROLOGY,
	@Diseases(value = {
		 })
	CAPTURED_IGM_SEROLOGY,
	@Diseases(value = {
			Disease.MEASLES,
			Disease.RUBELLA,
			Disease.DENGUE
	})
	IGM_SEREOLOGY,

	@Diseases(value = {
		Disease.YELLOW_FEVER,
		Disease.MEASLES,
		Disease.CSM,
		Disease.CONGENITAL_RUBELLA }, hide = true)
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String toString(PathogenTestType value, String details) {
		if (value == null) {
			return "";
		}

		if (value == PathogenTestType.OTHER) {
			return DataHelper.toStringNullable(details);
		}

		return value.toString();
	}
}

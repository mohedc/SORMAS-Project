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
package de.symeda.sormas.api.caze;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.followup.FollowUpStartDateType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;

public final class CaseLogic {

	public static final String GAMBIA_COUNTRY_EPID_CODE = "GAM";
	public static final int GAMBIA_EPID_SERIAL_MIN_DIGITS = 4;

	private static final Map<Disease, String> GAMBIA_DISEASE_EPID_CODES;

	static {
		Map<Disease, String> codes = new EnumMap<>(Disease.class);
		codes.put(Disease.AFP, "AFP");
		codes.put(Disease.CORONAVIRUS, "CVD");
		codes.put(Disease.CONGENITAL_RUBELLA, "CRS");
		codes.put(Disease.IMMEDIATE_CASE_BASED_FORM_OTHER_CONDITIONS, "IDS");
		codes.put(Disease.MEASLES, "MSL");
		codes.put(Disease.CSM, "CSF");
		codes.put(Disease.NEONATAL_TETANUS, "NNT");
		codes.put(Disease.YELLOW_FEVER, "YFA");
		GAMBIA_DISEASE_EPID_CODES = Collections.unmodifiableMap(codes);
	}

	private CaseLogic() {
		// Hide Utility Class Constructor
	}

	// Country, region, district, and optional disease code are 3 alphanumerics; year is 2 digits; count is the trailing number.
	// Accepts both the legacy format (GMB-CEN-JAN-26-013) and the Gambia format (GAM-WR1-KNH-AFP-26-0001).
	private static final String EPID_PATTERN_COMPLETE = "([A-Z0-9]{3}-){3,4}[0-9]{2}-[0-9]+";
	private static final String EPID_PATTERN_PREFIX = "([A-Z0-9]{3}-){3,4}[0-9]{2}-";

	public static void validateInvestigationDoneAllowed(CaseDataDto caze) throws ValidationException {
		// No-op: unclassified cases are no longer supported.
	}

	public static Date getStartDate(CaseDataDto caseDto) {
		return getStartDate(caseDto.getSymptoms().getOnsetDate(), caseDto.getReportDate());
	}

	public static Date getStartDate(Date onsetDate, Date reportDate) {
		return onsetDate != null ? onsetDate : reportDate;
	}

	public static FollowUpPeriodDto getFollowUpStartDate(CaseDataDto caseDto, List<SampleDto> samples) {
		return getFollowUpStartDate(caseDto.getSymptoms().getOnsetDate(), caseDto.getReportDate(), samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date onsetDate, Date reportDate, List<SampleDto> samples) {

		if (onsetDate != null) {
			return new FollowUpPeriodDto(onsetDate, FollowUpStartDateType.SYMPTOM_ONSET_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date onsetDate, Date reportDate, Date earliestSampleDate) {

		if (onsetDate != null) {
			return new FollowUpPeriodDto(onsetDate, FollowUpStartDateType.SYMPTOM_ONSET_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, earliestSampleDate);
	}

	public static Date getEndDate(CaseDataDto caseDto) {
		return getEndDate(caseDto.getSymptoms().getOnsetDate(), caseDto.getReportDate(), caseDto.getFollowUpUntil());
	}

	public static Date getEndDate(Date onsetDate, Date reportDate, Date followUpUntil) {
		return followUpUntil != null ? followUpUntil : onsetDate != null ? onsetDate : reportDate;
	}

	public static boolean isEpidNumberPrefix(String s) {

		if (StringUtils.isEmpty(s)) {
			return false;
		}

		return Pattern.matches(EPID_PATTERN_PREFIX, s);
	}

	public static boolean isCompleteEpidNumber(String s) {

		if (StringUtils.isEmpty(s)) {
			return false;
		}

		return Pattern.matches(EPID_PATTERN_COMPLETE, s);
	}

	public static String getGambiaDiseaseEpidCode(Disease disease) {
		return disease == null ? null : GAMBIA_DISEASE_EPID_CODES.get(disease);
	}

	public static boolean isGambiaCountryEpidCode(String code) {
		if (StringUtils.isBlank(code)) {
			return false;
		}

		String normalized = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ENGLISH);
		return GAMBIA_COUNTRY_EPID_CODE.equals(normalized) || "GMB".equals(normalized);
	}

	/**
	 * Gambia EPID format applies when the server locale is Gambia, or when infrastructure epid codes already
	 * use GAM/GMB (e.g. region epid {@code GMB-NBW}).
	 */
	public static boolean usesGambiaEpidFormat(boolean configuredGambia, String regionEpidCode, String countryEpidPrefix) {
		if (configuredGambia || isGambiaCountryEpidCode(countryEpidPrefix)) {
			return true;
		}

		if (StringUtils.isBlank(regionEpidCode)) {
			return false;
		}

		String value = regionEpidCode.trim().toUpperCase(Locale.ENGLISH);
		int dash = value.indexOf('-');
		String countryPart = dash > 0 ? value.substring(0, dash) : value;
		return isGambiaCountryEpidCode(countryPart);
	}

	public static boolean hasGambiaDiseaseEpidCode(Disease disease) {
		return getGambiaDiseaseEpidCode(disease) != null;
	}

	public static String formatEpidSerial(int serial, int minDigits) {
		return String.format(Locale.ENGLISH, "%0" + minDigits + "d", serial);
	}

	public static String buildGambiaEpidNumberPrefix(String regionCode, String districtCode, String diseaseCode, String year) {
		StringBuilder prefix = new StringBuilder();
		prefix.append(GAMBIA_COUNTRY_EPID_CODE)
			.append('-')
			.append(regionCode)
			.append('-')
			.append(districtCode)
			.append('-')
			.append(diseaseCode)
			.append('-');
		if (year != null) {
			prefix.append(year).append('-');
		}
		return prefix.toString();
	}

	public static String buildGambiaEpidNumber(String regionCode, String districtCode, String diseaseCode, String year, int serial) {
		return buildGambiaEpidNumberPrefix(regionCode, districtCode, diseaseCode, year)
			+ formatEpidSerial(serial, GAMBIA_EPID_SERIAL_MIN_DIGITS);
	}

	public static String buildGambiaEpidLikePattern(String diseaseCode, String year) {
		return "___-___-___-" + diseaseCode + "-" + year + "-%";
	}

	public static int parseEpidSerial(String epidNumber) {
		if (StringUtils.isBlank(epidNumber)) {
			return -1;
		}

		int lastDash = epidNumber.lastIndexOf('-');
		if (lastDash < 0 || lastDash == epidNumber.length() - 1) {
			return -1;
		}

		Integer serial = DataHelper.tryParseInt(epidNumber.substring(lastDash + 1).replaceAll("\\D", ""));
		return serial != null ? serial : -1;
	}

	/**
	 * Extracts 3-character region and district codes from a concatenated district epid code such as
	 * {@code GMB-CRR-JAN} or {@code CRR-JAN}.
	 */
	public static String[] extractRegionAndDistrictEpidCodes(String fullEpidCode) {
		if (StringUtils.isBlank(fullEpidCode)) {
			return null;
		}

		String[] parts = fullEpidCode.toUpperCase(Locale.ENGLISH).split("-");
		String regionPart;
		String districtPart;
		if (parts.length >= 3) {
			regionPart = parts[parts.length - 2];
			districtPart = parts[parts.length - 1];
		} else if (parts.length == 2) {
			regionPart = parts[0];
			districtPart = parts[1];
		} else {
			return null;
		}

		String regionCode = toEpidCodePart(regionPart);
		String districtCode = toEpidCodePart(districtPart);
		if (StringUtils.isAnyBlank(regionCode, districtCode) || regionCode.length() != 3 || districtCode.length() != 3) {
			return null;
		}

		return new String[] {
			regionCode,
			districtCode };
	}

	public static String toEpidCodePart(String value) {
		if (value == null) {
			return null;
		}

		String normalized = value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ENGLISH);
		if (normalized.length() >= 3) {
			return normalized.substring(0, 3);
		}

		return StringUtils.isBlank(normalized) ? null : normalized;
	}

	/**
	 * Handles the hospitalization change of a case.
	 *
	 * @param caze
	 *            The new CaseDataDto for which the facility change should be handled.
	 * @param oldCase
	 *            The Dto of the existing case being changed.
	 * @param isTransfer
	 *            Indicates if the old case is transferred (both from or to a hospital).
	 */
	public static void handleHospitalization(CaseDataDto caze, CaseDataDto oldCase, boolean isTransfer) {
		// todo (@JonasCir) I feel this whole class or at least this method should be absorbed by the case EJB
		// case is already in a hospital and is transferred from it (discharge or other hospital)...
		if (isTransfer && FacilityType.HOSPITAL.equals(oldCase.getFacilityType())) {
			// therefore add the old hospitalization to the list of previous ones
			PreviousHospitalizationDto prevHosp = PreviousHospitalizationDto.build(oldCase);
			caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}

		// clear everything if a case is transferred or discharged from a hospital
		if (isTransfer || !FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			// set everything but previous hospitalization to null
			try {
				PropertyDescriptor[] pds = Introspector.getBeanInfo(HospitalizationDto.class, EntityDto.class).getPropertyDescriptors();

				for (PropertyDescriptor pd : pds) {
					// Skip properties without a read or write method
					if (pd.getWriteMethod() == null
						|| HospitalizationDto.HOSPITALIZED_PREVIOUSLY.equals(pd.getName())
						|| HospitalizationDto.PREVIOUS_HOSPITALIZATIONS.equals(pd.getName())) {
						continue;
					}

					pd.getWriteMethod().invoke(caze.getHospitalization(), (Object) null);
				}
			} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException("Exception when trying to fill dto: " + e.getMessage(), e.getCause());
			}
		}

		// case gets transferred to a hospital
		if (isTransfer && FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			caze.getHospitalization().setAdmissionDate(new Date());
		}
	}

	/**
	 * Calculates the follow-up until date of the case based on its start date (onset contact or report date), the follow-up duration of
	 * the disease, the current follow-up until date and the date of the last cooperative visit.
	 *
	 * @param ignoreOverwrite
	 *            Ignores current follow-up until date and whether or not follow-up until has been overwritten.
	 */
	public static FollowUpPeriodDto calculateFollowUpUntilDate(
		CaseDataDto caze,
		FollowUpPeriodDto followUpPeriod,
		List<VisitDto> visits,
		int followUpDuration,
		boolean ignoreOverwrite,
		boolean allowFreeOverwrite) {

		Date overwriteUntilDate = !ignoreOverwrite && caze.isOverwriteFollowUpUntil() ? caze.getFollowUpUntil() : null;
		return FollowUpLogic.calculateFollowUpUntilDate(followUpPeriod, overwriteUntilDate, visits, followUpDuration, allowFreeOverwrite);
	}

	public static RegionReferenceDto getRegionWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleRegion();
		}

		return caze.getRegion();
	}

	public static DistrictReferenceDto getDistrictWithFallback(CaseDataDto caze) {
		if (caze.getDistrict() == null) {
			return caze.getResponsibleDistrict();
		}

		return caze.getDistrict();
	}

	public static CommunityReferenceDto getCommunityWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleCommunity();
		}

		return caze.getCommunity();
	}

	public static ReinfectionStatus calculateReinfectionStatus(Map<ReinfectionDetail, Boolean> reinfectionDetails) {

		if (reinfectionDetails == null) {
			return null;
		}

		if (reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCES_NOT_MATCHING, false)) {
			return ReinfectionStatus.CONFIRMED;
		}

		if (!(reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, false))
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_COPY_NUMBER_ABOVE_THRESHOLD, false)) {
			return ReinfectionStatus.PROBABLE;
		}

		if ((reinfectionDetails.getOrDefault(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, false)
			|| reinfectionDetails.getOrDefault(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, false))
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_COPY_NUMBER_BELOW_THRESHOLD, false)) {
			return ReinfectionStatus.POSSIBLE;
		}

		return null;
	}
}

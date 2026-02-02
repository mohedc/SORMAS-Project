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
package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING })
public class EpiDataDto extends PseudonymizableDto {

	private static final long serialVersionUID = 6292411396563549093L;

	public static final String I18N_PREFIX = "EpiData";

	public static final String EXPOSURE_DETAILS_KNOWN = "exposureDetailsKnown";
	public static final String ACTIVITY_AS_CASE_DETAILS_KNOWN = "activityAsCaseDetailsKnown";
	public static final String CONTACT_WITH_SOURCE_CASE_KNOWN = "contactWithSourceCaseKnown";
	public static final String EXPOSURES = "exposures";
	public static final String ACTIVITIES_AS_CASE = "activitiesAsCase";
	public static final String AREA_INFECTED_ANIMALS = "areaInfectedAnimals";
	public static final String HIGH_TRANSMISSION_RISK_AREA = "highTransmissionRiskArea";
	public static final String LARGE_OUTBREAKS_AREA = "largeOutbreaksArea";
	public static final String TRAVEL_HISTORY_KNOWN = "travelHistoryKnown";
	public static final String TRAVEL_LOCATION = "travelLocation";
	public static final String MOTHER_RUBELLA_LAB_CONFIRMED = "motherRubellaLabConfirmed";
	public static final String MOTHER_RUBELLA_LAB_CONFIRMED_DATE = "motherRubellaLabConfirmedDate";
	public static final String MOTHER_EXPOSED_DURING_PREGNANCY = "motherExposedDuringPregnancy";
	public static final String MOTHER_EXPOSED_DURING_PREGNANCY_DATE = "motherExposedDuringPregnancyDate";
	public static final String GESTATIONAL_AGE_AT_EXPOSURE = "gestationalAgeAtExposure";
	public static final String EXPOSURE_LOCATION_DESCRIPTION = "exposureLocationDescription";
	public static final String MOTHER_TRAVELED_DURING_PREGNANCY = "motherTraveledDuringPregnancy";
	public static final String MOTHER_TRAVELED_DURING_PREGNANCY_DATE = "motherTraveledDuringPregnancyDate";
	public static final String GESTATIONAL_AGE_AT_TRAVEL = "gestationalAgeAtTravel";
	public static final String TRAVEL_LOCATION_DESCRIPTION = "travelLocationDescription";
	public static final String RECENT_TRAVEL_OUTBREAK = "recentTravelOutbreak";
	public static final String CONTACT_SIMILAR_SYMPTOMS = "contactSimilarOutbreak";
	public static final String CONTACT_SICK_ANIMALS = "contactSickAnimals";

	private YesNoUnknown exposureDetailsKnown;
	private YesNoUnknown activityAsCaseDetailsKnown;
	private YesNoUnknown contactWithSourceCaseKnown;
	private YesNoUnknown highTransmissionRiskArea;
	private YesNoUnknown largeOutbreaksArea;
	private YesNo recentTravelOutbreak;
	private YesNo contactSimilarOutbreak;
	private YesNo contactSickAnimals;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown areaInfectedAnimals;

	@Valid
	private List<ExposureDto> exposures = new ArrayList<>();

	@Valid
	private List<ActivityAsCaseDto> activitiesAsCase = new ArrayList<>();

	@Diseases({
		Disease.MEASLES })
	private YesNo travelHistoryKnown;

	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private LocationDto travelLocation;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private YesNoUnknown motherRubellaLabConfirmed;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private Date motherRubellaLabConfirmedDate;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private YesNoUnknown motherExposedDuringPregnancy;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private Date motherExposedDuringPregnancyDate;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private Integer gestationalAgeAtExposure;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String exposureLocationDescription;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private YesNoUnknown motherTraveledDuringPregnancy;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private Date motherTraveledDuringPregnancyDate;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private Integer gestationalAgeAtTravel;

	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String travelLocationDescription;

	public YesNoUnknown getExposureDetailsKnown() {
		return exposureDetailsKnown;
	}

	public void setExposureDetailsKnown(YesNoUnknown exposureDetailsKnown) {
		this.exposureDetailsKnown = exposureDetailsKnown;
	}

	public YesNoUnknown getActivityAsCaseDetailsKnown() {
		return activityAsCaseDetailsKnown;
	}

	public void setActivityAsCaseDetailsKnown(YesNoUnknown activityAsCaseDetailsKnown) {
		this.activityAsCaseDetailsKnown = activityAsCaseDetailsKnown;
	}

	@ImportIgnore
	public List<ExposureDto> getExposures() {
		return exposures;
	}

	public void setExposures(List<ExposureDto> exposures) {
		this.exposures = exposures;
	}

	@ImportIgnore
	public List<ActivityAsCaseDto> getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(List<ActivityAsCaseDto> activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	public void setContactWithSourceCaseKnown(YesNoUnknown contactWithSourceCaseKnown) {
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
	}

	public YesNoUnknown getHighTransmissionRiskArea() {
		return highTransmissionRiskArea;
	}

	public void setHighTransmissionRiskArea(YesNoUnknown highTransmissionRiskArea) {
		this.highTransmissionRiskArea = highTransmissionRiskArea;
	}

	public YesNoUnknown getLargeOutbreaksArea() {
		return largeOutbreaksArea;
	}

	public void setLargeOutbreaksArea(YesNoUnknown largeOutbreaksArea) {
		this.largeOutbreaksArea = largeOutbreaksArea;
	}

	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}

	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}

	public YesNo getTravelHistoryKnown() {
		return travelHistoryKnown;
	}

	public void setTravelHistoryKnown(YesNo travelHistoryKnown) {
		this.travelHistoryKnown = travelHistoryKnown;
	}

	public LocationDto getTravelLocation() {
		return travelLocation;
	}

	public void setTravelLocation(LocationDto travelLocation) {
		this.travelLocation = travelLocation;
	}

	public YesNoUnknown getMotherRubellaLabConfirmed() {
		return motherRubellaLabConfirmed;
	}

	public void setMotherRubellaLabConfirmed(YesNoUnknown motherRubellaLabConfirmed) {
		this.motherRubellaLabConfirmed = motherRubellaLabConfirmed;
	}

	public Date getMotherRubellaLabConfirmedDate() {
		return motherRubellaLabConfirmedDate;
	}

	public void setMotherRubellaLabConfirmedDate(Date motherRubellaLabConfirmedDate) {
		this.motherRubellaLabConfirmedDate = motherRubellaLabConfirmedDate;
	}

	public YesNoUnknown getMotherExposedDuringPregnancy() {
		return motherExposedDuringPregnancy;
	}

	public void setMotherExposedDuringPregnancy(YesNoUnknown motherExposedDuringPregnancy) {
		this.motherExposedDuringPregnancy = motherExposedDuringPregnancy;
	}

	public Date getMotherExposedDuringPregnancyDate() {
		return motherExposedDuringPregnancyDate;
	}

	public void setMotherExposedDuringPregnancyDate(Date motherExposedDuringPregnancyDate) {
		this.motherExposedDuringPregnancyDate = motherExposedDuringPregnancyDate;
	}

	public Integer getGestationalAgeAtExposure() {
		return gestationalAgeAtExposure;
	}

	public void setGestationalAgeAtExposure(Integer gestationalAgeAtExposure) {
		this.gestationalAgeAtExposure = gestationalAgeAtExposure;
	}

	public String getExposureLocationDescription() {
		return exposureLocationDescription;
	}

	public void setExposureLocationDescription(String exposureLocationDescription) {
		this.exposureLocationDescription = exposureLocationDescription;
	}

	public YesNoUnknown getMotherTraveledDuringPregnancy() {
		return motherTraveledDuringPregnancy;
	}

	public void setMotherTraveledDuringPregnancy(YesNoUnknown motherTraveledDuringPregnancy) {
		this.motherTraveledDuringPregnancy = motherTraveledDuringPregnancy;
	}

	public Date getMotherTraveledDuringPregnancyDate() {
		return motherTraveledDuringPregnancyDate;
	}

	public void setMotherTraveledDuringPregnancyDate(Date motherTraveledDuringPregnancyDate) {
		this.motherTraveledDuringPregnancyDate = motherTraveledDuringPregnancyDate;
	}

	public Integer getGestationalAgeAtTravel() {
		return gestationalAgeAtTravel;
	}

	public void setGestationalAgeAtTravel(Integer gestationalAgeAtTravel) {
		this.gestationalAgeAtTravel = gestationalAgeAtTravel;
	}

	public String getTravelLocationDescription() {
		return travelLocationDescription;
	}

	public void setTravelLocationDescription(String travelLocationDescription) {
		this.travelLocationDescription = travelLocationDescription;
	}

	public YesNo getRecentTravelOutbreak() {
		return recentTravelOutbreak;
	}

	public void setRecentTravelOutbreak(YesNo recentTravelOutbreak) {
		this.recentTravelOutbreak = recentTravelOutbreak;
	}

	public YesNo getContactSimilarOutbreak() {
		return contactSimilarOutbreak;
	}

	public void setContactSimilarOutbreak(YesNo contactSimilarOutbreak) {
		this.contactSimilarOutbreak = contactSimilarOutbreak;
	}

	public YesNo getContactSickAnimals() {
		return contactSickAnimals;
	}

	public void setContactSickAnimals(YesNo contactSickAnimals) {
		this.contactSickAnimals = contactSickAnimals;
	}

	public static EpiDataDto build() {

		EpiDataDto epiData = new EpiDataDto();
		epiData.setUuid(DataHelper.createUuid());
		epiData.setTravelLocation(LocationDto.build());
		return epiData;
	}

	@Override
	public EpiDataDto clone() throws CloneNotSupportedException {
		EpiDataDto clone = (EpiDataDto) super.clone();
		List<ActivityAsCaseDto> activityAsCaseDtos = new ArrayList<>();
		for (ActivityAsCaseDto activityAsCase : getActivitiesAsCase()) {
			activityAsCaseDtos.add(activityAsCase.clone());
		}
		clone.getActivitiesAsCase().clear();
		clone.getActivitiesAsCase().addAll(activityAsCaseDtos);

		List<ExposureDto> exposureDtos = new ArrayList<>();
		for (ExposureDto exposure : getExposures()) {
			exposureDtos.add(exposure.clone());
		}
		clone.getExposures().clear();
		clone.getExposures().addAll(exposureDtos);

		clone.setTravelLocation((LocationDto) getTravelLocation().clone());

		return clone;
	}
}

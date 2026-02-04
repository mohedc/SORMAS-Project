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
package de.symeda.sormas.backend.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.location.Location;

@Entity
public class EpiData extends AbstractDomainObject {

	private static final long serialVersionUID = -8294812479501735785L;

	public static final String TABLE_NAME = "epidata";

	public static final String CONTACT_WITH_SOURCE_CASE_KNOWN = "contactWithSourceCaseKnown";
	public static final String EXPOSURES = "exposures";
	public static final String ACTIVITIES_AS_CASE = "activitiesAsCase";

	private YesNoUnknown exposureDetailsKnown;
	private YesNoUnknown activityAsCaseDetailsKnown;
	private YesNoUnknown contactWithSourceCaseKnown;
	private YesNoUnknown highTransmissionRiskArea;
	private YesNoUnknown largeOutbreaksArea;
	private YesNoUnknown areaInfectedAnimals;
	private YesNo travelHistoryKnown;

	private List<Exposure> exposures = new ArrayList<>();
	private List<ActivityAsCase> activitiesAsCase = new ArrayList<>();
	@NotExposedToApi
	private Date changeDateOfEmbeddedLists;
	private Location travelLocation;

	private YesNoUnknown motherRubellaLabConfirmed;
	private Date motherRubellaLabConfirmedDate;
	private YesNoUnknown motherExposedDuringPregnancy;
	private Date motherExposedDuringPregnancyDate;
	private Integer gestationalAgeAtExposure;
	private String exposureLocationDescription;
	private YesNoUnknown motherTraveledDuringPregnancy;
	private Date motherTraveledDuringPregnancyDate;
	private Integer gestationalAgeAtTravel;
	private String travelLocationDescription;
	private YesNo recentTravelOutbreak;
	private YesNo contactSimilarOutbreak;
	private YesNo contactSickAnimals;
	private String place;
	private String durationMonths;
	private String durationDays;
	private String place2;
	private String durationMonths2;
	private String durationDays2;
	private String place3;
	private String durationMonths3;
	private String durationDays3;
	private String place4;
	private String durationMonths4;
	private String durationDays4;

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getExposureDetailsKnown() {
		return exposureDetailsKnown;
	}

	public void setExposureDetailsKnown(YesNoUnknown exposureDetailsKnown) {
		this.exposureDetailsKnown = exposureDetailsKnown;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getActivityAsCaseDetailsKnown() {
		return activityAsCaseDetailsKnown;
	}

	public void setActivityAsCaseDetailsKnown(YesNoUnknown activityAsCaseDetailsKnown) {
		this.activityAsCaseDetailsKnown = activityAsCaseDetailsKnown;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Exposure.EPI_DATA)
	public List<Exposure> getExposures() {
		return exposures;
	}

	public void setExposures(List<Exposure> exposures) {
		this.exposures = exposures;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Exposure.EPI_DATA)
	public List<ActivityAsCase> getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(List<ActivityAsCase> activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	/**
	 * This change date has to be set whenever exposures are modified
	 */
	public Date getChangeDateOfEmbeddedLists() {
		return changeDateOfEmbeddedLists;
	}

	public void setChangeDateOfEmbeddedLists(Date changeDateOfEmbeddedLists) {
		this.changeDateOfEmbeddedLists = changeDateOfEmbeddedLists;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}

	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHighTransmissionRiskArea() {
		return highTransmissionRiskArea;
	}

	public void setHighTransmissionRiskArea(YesNoUnknown highTransmissionRiskArea) {
		this.highTransmissionRiskArea = highTransmissionRiskArea;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLargeOutbreaksArea() {
		return largeOutbreaksArea;
	}

	public void setLargeOutbreaksArea(YesNoUnknown largeOutbreaksArea) {
		this.largeOutbreaksArea = largeOutbreaksArea;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	public void setContactWithSourceCaseKnown(YesNoUnknown contactWithSourceCaseKnown) {
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getTravelHistoryKnown() {
		return travelHistoryKnown;
	}

	public void setTravelHistoryKnown(YesNo travelHistoryKnown) {
		this.travelHistoryKnown = travelHistoryKnown;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Location getTravelLocation() {
		if (travelLocation == null) {
			travelLocation = new Location();
		}
		return travelLocation;
	}

	public void setTravelLocation(Location travelLocation) {
		this.travelLocation = travelLocation;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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
	@Enumerated(EnumType.STRING)
	public YesNo getRecentTravelOutbreak() {
		return recentTravelOutbreak;
	}

	public void setRecentTravelOutbreak(YesNo recentTravelOutbreak) {
		this.recentTravelOutbreak = recentTravelOutbreak;
	}
	@Enumerated(EnumType.STRING)
	public YesNo getContactSimilarOutbreak() {
		return contactSimilarOutbreak;
	}

	public void setContactSimilarOutbreak(YesNo contactSimilarOutbreak) {
		this.contactSimilarOutbreak = contactSimilarOutbreak;
	}
	@Enumerated(EnumType.STRING)
	public YesNo getContactSickAnimals() {
		return contactSickAnimals;
	}
	public void setContactSickAnimals(YesNo contactSickAnimals) {
		this.contactSickAnimals = contactSickAnimals;
	}
	@Column
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	@Column
	public String getDurationMonths() {
		return durationMonths;
	}
	public void setDurationMonths(String durationMonths) {
		this.durationMonths = durationMonths;
	}
	@Column
	public String getDurationDays() {
		return durationDays;
	}
	public void setDurationDays(String durationDays) {
		this.durationDays = durationDays;
	}
	@Column
	public String getPlace2() {
		return place2;
	}
	public void setPlace2(String place2) {
		this.place2 = place2;
	}
	@Column
	public String getDurationMonths2() {
		return durationMonths2;
	}
	public void setDurationMonths2(String durationMonths2) {
		this.durationMonths2 = durationMonths2;
	}
	@Column
	public String getDurationDays2() {
		return durationDays2;
	}
	public void setDurationDays2(String durationDays2) {
		this.durationDays2 = durationDays2;
	}
	@Column
	public String getPlace3() {
		return place3;
	}
	public void setPlace3(String place3) {
		this.place3 = place3;
	}
	@Column
	public String getDurationMonths3() {
		return durationMonths3;
	}
	public void setDurationMonths3(String durationMonths3) {
		this.durationMonths3 = durationMonths3;
	}
	@Column
	public String getDurationDays3() {
		return durationDays3;
	}
	public void setDurationDays3(String durationDays3) {
		this.durationDays3 = durationDays3;
	}
	@Column
	public String getPlace4() {
		return place4;
	}
	public void setPlace4(String place4) {
		this.place4 = place4;
	}
	@Column
	public String getDurationMonths4() {
		return durationMonths4;
	}
	public void setDurationMonths4(String durationMonths4) {
		this.durationMonths4 = durationMonths4;
	}
	@Column
	public String getDurationDays4() {
		return durationDays4;
	}
	public void setDurationDays4(String durationDays4) {
		this.durationDays4 = durationDays4;
	}
}

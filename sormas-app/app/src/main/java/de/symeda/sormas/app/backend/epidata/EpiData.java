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

package de.symeda.sormas.app.backend.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.location.Location;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

@Entity(name = EpiData.TABLE_NAME)
@DatabaseTable(tableName = EpiData.TABLE_NAME)
@EmbeddedAdo
public class EpiData extends PseudonymizableAdo {

	private static final long serialVersionUID = -8294812479501735785L;

	public static final String TABLE_NAME = "epidata";
	public static final String I18N_PREFIX = "EpiData";

	@Enumerated(EnumType.STRING)
	private YesNoUnknown exposureDetailsKnown;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown activityAsCaseDetailsKnown;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown contactWithSourceCaseKnown;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown highTransmissionRiskArea;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown largeOutbreaksArea;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown areaInfectedAnimals;
	@Enumerated(EnumType.STRING)
	private YesNo travelHistoryKnown;

	private List<Exposure> exposures = new ArrayList<>();

	private List<ActivityAsCase> activitiesAsCase = new ArrayList<>();

	@com.j256.ormlite.field.DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Location travelLocation;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown motherRubellaLabConfirmed;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date motherRubellaLabConfirmedDate;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown motherExposedDuringPregnancy;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date motherExposedDuringPregnancyDate;
	@DatabaseField
	private Integer gestationalAgeAtExposure;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String exposureLocationDescription;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown motherTraveledDuringPregnancy;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date motherTraveledDuringPregnancyDate;
	@DatabaseField
	private Integer gestationalAgeAtTravel;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String travelLocationDescription;
	@Enumerated(EnumType.STRING)
	private YesNo recentTravelOutbreak;
	@Enumerated(EnumType.STRING)
	private YesNo contactSimilarOutbreak;
	@Enumerated(EnumType.STRING)
	private YesNo contactSickAnimals;

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

	public List<Exposure> getExposures() {
		return exposures;
	}

	public void setExposures(List<Exposure> exposures) {
		this.exposures = exposures;
	}

	public List<ActivityAsCase> getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(List<ActivityAsCase> activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	public YesNo getTravelHistoryKnown() {
		return travelHistoryKnown;
	}

	public void setTravelHistoryKnown(YesNo travelHistoryKnown) {
		this.travelHistoryKnown = travelHistoryKnown;
	}

	public Location getTravelLocation() {
		return travelLocation;
	}

	public void setTravelLocation(Location travelLocation) {
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

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}

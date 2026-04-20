package de.symeda.sormas.backend.caze.maternalhistory;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Entity
public class MaternalHistory extends AbstractDomainObject {

	private static final long serialVersionUID = -5534360436146186436L;

	public static final String TABLE_NAME = "maternalhistory";

	private Integer childrenNumber;
	private Integer ageAtBirth;
	private YesNoUnknown conjunctivitis;
	private Date conjunctivitisOnset;
	private Integer conjunctivitisMonth;
	private YesNoUnknown maculopapularRash;
	private Date maculopapularRashOnset;
	private Integer maculopapularRashMonth;
	private YesNoUnknown swollenLymphs;
	private Date swollenLymphsOnset;
	private Integer swollenLymphsMonth;
	private YesNoUnknown arthralgiaArthritis;
	private Date arthralgiaArthritisOnset;
	private Integer arthralgiaArthritisMonth;
	private YesNoUnknown otherComplications;
	private Date otherComplicationsOnset;
	private Integer otherComplicationsMonth;
	private String otherComplicationsDetails;
	private YesNoUnknown rubella;
	private Date rubellaOnset;
	private YesNoUnknown rubellaVaccination;
	private Date rubellaVaccinationDate;
	private Integer rubellaMonth;
	private YesNoUnknown congenitalRubella;
	private Date congenitalRubellaDate;
	private YesNoUnknown rashExposure;
	private Date rashExposureDate;
	private Integer rashExposureMonth;
	private Region rashExposureRegion;
	private District rashExposureDistrict;
	private Community rashExposureCommunity;
	private Integer gestationalAgeAtExposure;
	private String exposureLocationDescription;
	private YesNoUnknown motherTraveledDuringPregnancy;
	private Date motherTraveledDuringPregnancyDate;
	private Integer gestationalAgeAtTravel;
	private String travelLocationDescription;

	public Integer getChildrenNumber() {
		return childrenNumber;
	}

	public void setChildrenNumber(Integer childrenNumber) {
		this.childrenNumber = childrenNumber;
	}

	public Integer getAgeAtBirth() {
		return ageAtBirth;
	}

	public void setAgeAtBirth(Integer ageAtBirth) {
		this.ageAtBirth = ageAtBirth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(YesNoUnknown conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getConjunctivitisOnset() {
		return conjunctivitisOnset;
	}

	public void setConjunctivitisOnset(Date conjunctivitisOnset) {
		this.conjunctivitisOnset = conjunctivitisOnset;
	}

	public Integer getConjunctivitisMonth() {
		return conjunctivitisMonth;
	}

	public void setConjunctivitisMonth(Integer conjunctivitisMonth) {
		this.conjunctivitisMonth = conjunctivitisMonth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getMaculopapularRash() {
		return maculopapularRash;
	}

	public void setMaculopapularRash(YesNoUnknown maculopapularRash) {
		this.maculopapularRash = maculopapularRash;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getMaculopapularRashOnset() {
		return maculopapularRashOnset;
	}

	public void setMaculopapularRashOnset(Date maculopapularRashOnset) {
		this.maculopapularRashOnset = maculopapularRashOnset;
	}

	public Integer getMaculopapularRashMonth() {
		return maculopapularRashMonth;
	}

	public void setMaculopapularRashMonth(Integer maculopapularRashMonth) {
		this.maculopapularRashMonth = maculopapularRashMonth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSwollenLymphs() {
		return swollenLymphs;
	}

	public void setSwollenLymphs(YesNoUnknown swollenLymphs) {
		this.swollenLymphs = swollenLymphs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSwollenLymphsOnset() {
		return swollenLymphsOnset;
	}

	public void setSwollenLymphsOnset(Date swollenLymphsOnset) {
		this.swollenLymphsOnset = swollenLymphsOnset;
	}

	public Integer getSwollenLymphsMonth() {
		return swollenLymphsMonth;
	}

	public void setSwollenLymphsMonth(Integer swollenLymphsMonth) {
		this.swollenLymphsMonth = swollenLymphsMonth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getArthralgiaArthritis() {
		return arthralgiaArthritis;
	}

	public void setArthralgiaArthritis(YesNoUnknown arthralgiaArthritis) {
		this.arthralgiaArthritis = arthralgiaArthritis;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getArthralgiaArthritisOnset() {
		return arthralgiaArthritisOnset;
	}

	public void setArthralgiaArthritisOnset(Date arthralgiaArthritisOnset) {
		this.arthralgiaArthritisOnset = arthralgiaArthritisOnset;
	}

	public Integer getArthralgiaArthritisMonth() {
		return arthralgiaArthritisMonth;
	}

	public void setArthralgiaArthritisMonth(Integer arthralgiaArthritisMonth) {
		this.arthralgiaArthritisMonth = arthralgiaArthritisMonth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getOtherComplications() {
		return otherComplications;
	}

	public void setOtherComplications(YesNoUnknown otherComplications) {
		this.otherComplications = otherComplications;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOtherComplicationsOnset() {
		return otherComplicationsOnset;
	}

	public void setOtherComplicationsOnset(Date otherComplicationsOnset) {
		this.otherComplicationsOnset = otherComplicationsOnset;
	}

	public Integer getOtherComplicationsMonth() {
		return otherComplicationsMonth;
	}

	public void setOtherComplicationsMonth(Integer otherComplicationsMonth) {
		this.otherComplicationsMonth = otherComplicationsMonth;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getOtherComplicationsDetails() {
		return otherComplicationsDetails;
	}

	public void setOtherComplicationsDetails(String otherComplicationsDetails) {
		this.otherComplicationsDetails = otherComplicationsDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRubella() {
		return rubella;
	}

	public void setRubella(YesNoUnknown rubella) {
		this.rubella = rubella;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRubellaOnset() {
		return rubellaOnset;
	}

	public void setRubellaOnset(Date rubellaOnset) {
		this.rubellaOnset = rubellaOnset;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRubellaVaccination() {
		return rubellaVaccination;
	}

	public void setRubellaVaccination(YesNoUnknown rubellaVaccination) {
		this.rubellaVaccination = rubellaVaccination;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRubellaVaccinationDate() {
		return rubellaVaccinationDate;
	}

	public void setRubellaVaccinationDate(Date rubellaVaccinationDate) {
		this.rubellaVaccinationDate = rubellaVaccinationDate;
	}

	public Integer getRubellaMonth() {
		return rubellaMonth;
	}

	public void setRubellaMonth(Integer rubellaMonth) {
		this.rubellaMonth = rubellaMonth;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCongenitalRubella() {
		return congenitalRubella;
	}

	public void setCongenitalRubella(YesNoUnknown congenitalRubella) {
		this.congenitalRubella = congenitalRubella;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCongenitalRubellaDate() {
		return congenitalRubellaDate;
	}

	public void setCongenitalRubellaDate(Date congenitalRubellaDate) {
		this.congenitalRubellaDate = congenitalRubellaDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRashExposure() {
		return rashExposure;
	}

	public void setRashExposure(YesNoUnknown rashExposure) {
		this.rashExposure = rashExposure;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRashExposureDate() {
		return rashExposureDate;
	}

	public void setRashExposureDate(Date rashExposureDate) {
		this.rashExposureDate = rashExposureDate;
	}

	public Integer getRashExposureMonth() {
		return rashExposureMonth;
	}

	public void setRashExposureMonth(Integer rashExposureMonth) {
		this.rashExposureMonth = rashExposureMonth;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Region getRashExposureRegion() {
		return rashExposureRegion;
	}

	public void setRashExposureRegion(Region rashExposureRegion) {
		this.rashExposureRegion = rashExposureRegion;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public District getRashExposureDistrict() {
		return rashExposureDistrict;
	}

	public void setRashExposureDistrict(District rashExposureDistrict) {
		this.rashExposureDistrict = rashExposureDistrict;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Community getRashExposureCommunity() {
		return rashExposureCommunity;
	}

	public void setRashExposureCommunity(Community rashExposureCommunity) {
		this.rashExposureCommunity = rashExposureCommunity;
	}

	public Integer getGestationalAgeAtExposure() {
		return gestationalAgeAtExposure;
	}

	public void setGestationalAgeAtExposure(Integer gestationalAgeAtExposure) {
		this.gestationalAgeAtExposure = gestationalAgeAtExposure;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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

	@Temporal(TemporalType.TIMESTAMP)
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTravelLocationDescription() {
		return travelLocationDescription;
	}

	public void setTravelLocationDescription(String travelLocationDescription) {
		this.travelLocationDescription = travelLocationDescription;
	}

}

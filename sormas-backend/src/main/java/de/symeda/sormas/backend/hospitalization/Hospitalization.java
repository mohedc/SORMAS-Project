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
package de.symeda.sormas.backend.hospitalization;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.utils.InpatOutpat;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.facility.Facility;

@Entity
public class Hospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = -8576270649634034244L;

	public static final String TABLE_NAME = "hospitalization";

	public static final String ADMITTED_TO_HEALTH_FACILITY = "admittedToHealthFacility";
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String LEFT_AGAINST_ADVICE = "leftAgainstAdvice";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	public static final String PREVIOUS_HOSPITALIZATIONS = "previousHospitalizations";
	public static final String INTENSIVE_CARE_UNIT = "intensiveCareUnit";
	public static final String INTENSIVE_CARE_UNIT_START = "intensiveCareUnitStart";
	public static final String INTENSIVE_CARE_UNIT_END = "intensiveCareUnitEnd";
	public static final String DESCRIPTION = "description";
	public static final String SEEN_AT_HEALTH_FACILITY = "seenAtHealthFacility";
	public static final String DATE_FIRST_SEEN_AT_HEALTH_FACILITY = "dateFirstSeenAtHealthFacility";
	public static final String DATE_OF_DISEASE_ONSET = "dateOfDiseaseOnset";
	public static final String DATE_HEALTH_FACILITY_NOTIFIED_DISTRICT = "dateHealthFacilityNotifiedDistrict";
	public static final String SERIAL_NUMBER_IN_CONSULTATION_REGISTER = "serialNumberInConsultationRegister";
	public static final String DATE_OF_CONSULTATION_AT_HEALTH_FACILITY = "dateOfConsultationAtHealthFacility";
	public static final String DATE_HEALTH_REGION_NOTIFIED = "dateHealthRegionNotified";
	public static final String ADMITTED_TO_DIFFERENT_HEALTH_FACILITY = "admittedToDifferentHealthFacility";
	public static final String ADMISSION_HEALTH_FACILITY = "admissionHealthFacility";
	public static final String ADMISSION_HEALTH_FACILITY_DETAILS = "admissionHealthFacilityDetails";

	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown leftAgainstAdvice;

	private YesNoUnknown hospitalizedPreviously;
	private Date changeDateOfEmbeddedLists;
	private List<PreviousHospitalization> previousHospitalizations = new ArrayList<PreviousHospitalization>();
	private YesNoUnknown intensiveCareUnit;
	private Date intensiveCareUnitStart;
	private Date intensiveCareUnitEnd;
	private HospitalizationReasonType hospitalizationReason;
	private String otherHospitalizationReason;
	private String description;
	private String hospitalRecordNumber;
	private InpatOutpat selectInpatientOutpatient;
	private String address;
	private YesNoUnknown seenAtHealthFacility;
	private Date dateFirstSeenAtHealthFacility;
	private Date dateOfDiseaseOnset;
	private Date dateHealthFacilityNotifiedDistrict;
	private String serialNumberInConsultationRegister;
	private Date dateOfConsultationAtHealthFacility;
	private Date dateHealthRegionNotified;
	private YesNo admittedToDifferentHealthFacility;
	private Facility admissionHealthFacility;
	private String admissionHealthFacilityDetails;

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIsolated() {
		return isolated;
	}

	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}

	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = PreviousHospitalization.HOSPITALIZATION)
	public List<PreviousHospitalization> getPreviousHospitalizations() {
		return previousHospitalizations;
	}

	public void setPreviousHospitalizations(List<PreviousHospitalization> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	/**
	 * This change date has to be set whenever one of the embedded lists is modified: !oldList.equals(newList)
	 * 
	 * @return
	 */
	public Date getChangeDateOfEmbeddedLists() {
		return changeDateOfEmbeddedLists;
	}

	public void setChangeDateOfEmbeddedLists(Date changeDateOfEmbeddedLists) {
		this.changeDateOfEmbeddedLists = changeDateOfEmbeddedLists;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLeftAgainstAdvice() {
		return leftAgainstAdvice;
	}

	public void setLeftAgainstAdvice(YesNoUnknown leftAgainstAdvice) {
		this.leftAgainstAdvice = leftAgainstAdvice;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIntensiveCareUnit() {
		return intensiveCareUnit;
	}

	public void setIntensiveCareUnit(YesNoUnknown intensiveCareUnit) {
		this.intensiveCareUnit = intensiveCareUnit;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getIntensiveCareUnitStart() {
		return intensiveCareUnitStart;
	}

	public void setIntensiveCareUnitStart(Date intensiveCareUnitStart) {
		this.intensiveCareUnitStart = intensiveCareUnitStart;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getIntensiveCareUnitEnd() {
		return intensiveCareUnitEnd;
	}

	public void setIntensiveCareUnitEnd(Date intensiveCareUnitEnd) {
		this.intensiveCareUnitEnd = intensiveCareUnitEnd;
	}

	@Enumerated(EnumType.STRING)
	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType reasonForHospitalization) {
		this.hospitalizationReason = reasonForHospitalization;
	}

	public String getOtherHospitalizationReason() {
		return otherHospitalizationReason;
	}

	public void setOtherHospitalizationReason(String otherReasonForHospitalization) {
		this.otherHospitalizationReason = otherReasonForHospitalization;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHospitalRecordNumber() {
		return hospitalRecordNumber;
	}

	public void setHospitalRecordNumber(String hospitalRecordNumber) {
		this.hospitalRecordNumber = hospitalRecordNumber;
	}
	@Enumerated(EnumType.STRING)
	public InpatOutpat getSelectInpatientOutpatient() {
		return selectInpatientOutpatient;
	}

	public void setSelectInpatientOutpatient(InpatOutpat selectInpatientOutpatient) {
		this.selectInpatientOutpatient = selectInpatientOutpatient;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSeenAtHealthFacility() {
		return seenAtHealthFacility;
	}

	public void setSeenAtHealthFacility(YesNoUnknown seenAtHealthFacility) {
		this.seenAtHealthFacility = seenAtHealthFacility;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateFirstSeenAtHealthFacility() {
		return dateFirstSeenAtHealthFacility;
	}

	public void setDateFirstSeenAtHealthFacility(Date dateFirstSeenAtHealthFacility) {
		this.dateFirstSeenAtHealthFacility = dateFirstSeenAtHealthFacility;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateOfDiseaseOnset() {
		return dateOfDiseaseOnset;
	}

	public void setDateOfDiseaseOnset(Date dateOfDiseaseOnset) {
		this.dateOfDiseaseOnset = dateOfDiseaseOnset;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateHealthFacilityNotifiedDistrict() {
		return dateHealthFacilityNotifiedDistrict;
	}

	public void setDateHealthFacilityNotifiedDistrict(Date dateHealthFacilityNotifiedDistrict) {
		this.dateHealthFacilityNotifiedDistrict = dateHealthFacilityNotifiedDistrict;
	}

	public String getSerialNumberInConsultationRegister() {
		return serialNumberInConsultationRegister;
	}

	public void setSerialNumberInConsultationRegister(String serialNumberInConsultationRegister) {
		this.serialNumberInConsultationRegister = serialNumberInConsultationRegister;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateOfConsultationAtHealthFacility() {
		return dateOfConsultationAtHealthFacility;
	}

	public void setDateOfConsultationAtHealthFacility(Date dateOfConsultationAtHealthFacility) {
		this.dateOfConsultationAtHealthFacility = dateOfConsultationAtHealthFacility;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateHealthRegionNotified() {
		return dateHealthRegionNotified;
	}

	public void setDateHealthRegionNotified(Date dateHealthRegionNotified) {
		this.dateHealthRegionNotified = dateHealthRegionNotified;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getAdmittedToDifferentHealthFacility() {
		return admittedToDifferentHealthFacility;
	}

	public void setAdmittedToDifferentHealthFacility(YesNo admittedToDifferentHealthFacility) {
		this.admittedToDifferentHealthFacility = admittedToDifferentHealthFacility;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "admissionhealthfacility_id")
	public Facility getAdmissionHealthFacility() {
		return admissionHealthFacility;
	}

	public void setAdmissionHealthFacility(Facility admissionHealthFacility) {
		this.admissionHealthFacility = admissionHealthFacility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getAdmissionHealthFacilityDetails() {
		return admissionHealthFacilityDetails;
	}

	public void setAdmissionHealthFacilityDetails(String admissionHealthFacilityDetails) {
		this.admissionHealthFacilityDetails = admissionHealthFacilityDetails;
	}
}

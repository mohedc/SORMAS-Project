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

package de.symeda.sormas.app.backend.hospitalization;

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

import de.symeda.sormas.api.hospitalization.AccommodationType;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.InpatOutpat;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

@Entity(name = Hospitalization.TABLE_NAME)
@DatabaseTable(tableName = Hospitalization.TABLE_NAME)
@EmbeddedAdo
public class Hospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = -8576270649634034244L;

	public static final String TABLE_NAME = "hospitalizations";
	public static final String I18N_PREFIX = "CaseHospitalization";

	@Enumerated(EnumType.STRING)
	private YesNoUnknown admittedToHealthFacility;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date admissionDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dischargeDate;
	@Enumerated(EnumType.STRING)
	@Deprecated
	private AccommodationType accommodation;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown isolated;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date isolationDate;
	@Column(length = CHARACTER_LIMIT_BIG)
	private String description;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown leftAgainstAdvice;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown hospitalizedPreviously;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown intensiveCareUnit;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date intensiveCareUnitStart;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date intensiveCareUnitEnd;

	@Enumerated(EnumType.STRING)
	private HospitalizationReasonType hospitalizationReason;

	@Column(columnDefinition = "text")
	private String otherHospitalizationReason;

	@Enumerated(EnumType.STRING)
	private InpatOutpat selectInpatientOutpatient;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown seenAtHealthFacility;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dateFirstSeenAtHealthFacility;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dateHealthFacilityNotifiedDistrict;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String hospitalRecordNumber;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String serialNumberInConsultationRegister;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dateOfConsultationAtHealthFacility;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dateHealthRegionNotified;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dateOfDiseaseOnset;
	@Column(length = CHARACTER_LIMIT_BIG)
	private String address;

	@Enumerated(EnumType.STRING)
	private YesNo admittedToDifferentHealthFacility;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Region admissionRegion;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District admissionDistrict;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility admissionHealthFacility;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String admissionHealthFacilityDetails;

	// just for reference, not persisted in DB
	private List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public YesNoUnknown getIsolated() {
		return isolated;
	}

	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}

	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}

	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}

	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public YesNoUnknown getIntensiveCareUnit() {
		return intensiveCareUnit;
	}

	public void setIntensiveCareUnit(YesNoUnknown intensiveCareUnit) {
		this.intensiveCareUnit = intensiveCareUnit;
	}

	public Date getIntensiveCareUnitStart() {
		return intensiveCareUnitStart;
	}

	public void setIntensiveCareUnitStart(Date intensiveCareUnitStart) {
		this.intensiveCareUnitStart = intensiveCareUnitStart;
	}

	public Date getIntensiveCareUnitEnd() {
		return intensiveCareUnitEnd;
	}

	public void setIntensiveCareUnitEnd(Date intensiveCareUnitEnd) {
		this.intensiveCareUnitEnd = intensiveCareUnitEnd;
	}

	/**
	 * NOTE: This is only initialized when the hospitalization is retrieved using {@link HospitalizationDao}
	 * 
	 * @return
	 */
	public List<PreviousHospitalization> getPreviousHospitalizations() {
		return previousHospitalizations;
	}

	public void setPreviousHospitalizations(List<PreviousHospitalization> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Deprecated
	public AccommodationType getAccommodation() {
		return accommodation;
	}

	@Deprecated
	public void setAccommodation(AccommodationType accommodation) {
		this.accommodation = accommodation;
	}

	public YesNoUnknown getLeftAgainstAdvice() {
		return leftAgainstAdvice;
	}

	public void setLeftAgainstAdvice(YesNoUnknown leftAgainstAdvice) {
		this.leftAgainstAdvice = leftAgainstAdvice;
	}

	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType hospitalizationReason) {
		this.hospitalizationReason = hospitalizationReason;
	}

	public String getOtherHospitalizationReason() {
		return otherHospitalizationReason;
	}

	public void setOtherHospitalizationReason(String otherHospitalizationReason) {
		this.otherHospitalizationReason = otherHospitalizationReason;
	}

	public InpatOutpat getSelectInpatientOutpatient() {
		return selectInpatientOutpatient;
	}

	public void setSelectInpatientOutpatient(InpatOutpat selectInpatientOutpatient) {
		this.selectInpatientOutpatient = selectInpatientOutpatient;
	}

	public YesNoUnknown getSeenAtHealthFacility() {
		return seenAtHealthFacility;
	}

	public void setSeenAtHealthFacility(YesNoUnknown seenAtHealthFacility) {
		this.seenAtHealthFacility = seenAtHealthFacility;
	}

	public Date getDateFirstSeenAtHealthFacility() {
		return dateFirstSeenAtHealthFacility;
	}

	public void setDateFirstSeenAtHealthFacility(Date dateFirstSeenAtHealthFacility) {
		this.dateFirstSeenAtHealthFacility = dateFirstSeenAtHealthFacility;
	}

	public Date getDateHealthFacilityNotifiedDistrict() {
		return dateHealthFacilityNotifiedDistrict;
	}

	public void setDateHealthFacilityNotifiedDistrict(Date dateHealthFacilityNotifiedDistrict) {
		this.dateHealthFacilityNotifiedDistrict = dateHealthFacilityNotifiedDistrict;
	}

	public String getHospitalRecordNumber() {
		return hospitalRecordNumber;
	}

	public void setHospitalRecordNumber(String hospitalRecordNumber) {
		this.hospitalRecordNumber = hospitalRecordNumber;
	}

	public String getSerialNumberInConsultationRegister() {
		return serialNumberInConsultationRegister;
	}

	public void setSerialNumberInConsultationRegister(String serialNumberInConsultationRegister) {
		this.serialNumberInConsultationRegister = serialNumberInConsultationRegister;
	}

	public Date getDateOfConsultationAtHealthFacility() {
		return dateOfConsultationAtHealthFacility;
	}

	public void setDateOfConsultationAtHealthFacility(Date dateOfConsultationAtHealthFacility) {
		this.dateOfConsultationAtHealthFacility = dateOfConsultationAtHealthFacility;
	}

	public Date getDateHealthRegionNotified() {
		return dateHealthRegionNotified;
	}

	public void setDateHealthRegionNotified(Date dateHealthRegionNotified) {
		this.dateHealthRegionNotified = dateHealthRegionNotified;
	}

	public Date getDateOfDiseaseOnset() {
		return dateOfDiseaseOnset;
	}

	public void setDateOfDiseaseOnset(Date dateOfDiseaseOnset) {
		this.dateOfDiseaseOnset = dateOfDiseaseOnset;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public YesNo getAdmittedToDifferentHealthFacility() {
		return admittedToDifferentHealthFacility;
	}

	public void setAdmittedToDifferentHealthFacility(YesNo admittedToDifferentHealthFacility) {
		this.admittedToDifferentHealthFacility = admittedToDifferentHealthFacility;
	}

	public Region getAdmissionRegion() {
		return admissionRegion;
	}

	public void setAdmissionRegion(Region admissionRegion) {
		this.admissionRegion = admissionRegion;
	}

	public District getAdmissionDistrict() {
		return admissionDistrict;
	}

	public void setAdmissionDistrict(District admissionDistrict) {
		this.admissionDistrict = admissionDistrict;
	}

	public Facility getAdmissionHealthFacility() {
		return admissionHealthFacility;
	}

	public void setAdmissionHealthFacility(Facility admissionHealthFacility) {
		this.admissionHealthFacility = admissionHealthFacility;
	}

	public String getAdmissionHealthFacilityDetails() {
		return admissionHealthFacilityDetails;
	}

	public void setAdmissionHealthFacilityDetails(String admissionHealthFacilityDetails) {
		this.admissionHealthFacilityDetails = admissionHealthFacilityDetails;
	}
}

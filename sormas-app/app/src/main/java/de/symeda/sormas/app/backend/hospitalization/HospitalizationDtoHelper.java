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
import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class HospitalizationDtoHelper extends AdoDtoHelper<Hospitalization, HospitalizationDto> {

	private PreviousHospitalizationDtoHelper previousHospitalizationDtoHelper;

	public HospitalizationDtoHelper() {
		previousHospitalizationDtoHelper = new PreviousHospitalizationDtoHelper();
	}

	@Override
	protected Class<Hospitalization> getAdoClass() {
		return Hospitalization.class;
	}

	@Override
	protected Class<HospitalizationDto> getDtoClass() {
		return HospitalizationDto.class;
	}

	@Override
	protected Call<List<HospitalizationDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<HospitalizationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<HospitalizationDto> hospitalizationDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(Hospitalization a, HospitalizationDto b) {

		a.setAdmittedToHealthFacility(b.getAdmittedToHealthFacility());
		a.setAdmissionDate(b.getAdmissionDate());
		a.setDischargeDate(b.getDischargeDate());
		a.setIsolated(b.getIsolated());
		a.setIsolationDate(b.getIsolationDate());
		a.setDescription(b.getDescription());
		a.setLeftAgainstAdvice(b.getLeftAgainstAdvice());
		a.setIntensiveCareUnit(b.getIntensiveCareUnit());
		a.setIntensiveCareUnitStart(b.getIntensiveCareUnitStart());
		a.setIntensiveCareUnitEnd(b.getIntensiveCareUnitEnd());
		a.setHospitalizedPreviously(b.getHospitalizedPreviously());
		a.setHospitalizationReason(b.getHospitalizationReason());
		a.setOtherHospitalizationReason(b.getOtherHospitalizationReason());
		a.setSelectInpatientOutpatient(b.getSelectInpatientOutpatient());
		a.setHospitalRecordNumber(b.getHospitalRecordNumber());
		a.setSeenAtHealthFacility(b.getSeenAtHealthFacility());
		a.setDateFirstSeenAtHealthFacility(b.getDateFirstSeenAtHealthFacility());
		a.setDateHealthFacilityNotifiedDistrict(b.getDateHealthFacilityNotifiedDistrict());
		a.setSerialNumberInConsultationRegister(b.getSerialNumberInConsultationRegister());
		a.setDateOfConsultationAtHealthFacility(b.getDateOfConsultationAtHealthFacility());
		a.setDateHealthRegionNotified(b.getDateHealthRegionNotified());
		a.setDateOfDiseaseOnset(b.getDateOfDiseaseOnset());
		a.setAddress(b.getAddress());
		a.setAdmittedToDifferentHealthFacility(b.getAdmittedToDifferentHealthFacility());
		if (b.getAdmissionRegion() != null) {
			a.setAdmissionRegion(DatabaseHelper.getRegionDao().queryUuid(b.getAdmissionRegion().getUuid()));
		} else {
			a.setAdmissionRegion(null);
		}
		if (b.getAdmissionDistrict() != null) {
			a.setAdmissionDistrict(DatabaseHelper.getDistrictDao().queryUuid(b.getAdmissionDistrict().getUuid()));
		} else {
			a.setAdmissionDistrict(null);
		}
		if (b.getAdmissionHealthFacility() != null) {
			a.setAdmissionHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(b.getAdmissionHealthFacility().getUuid()));
		} else {
			a.setAdmissionHealthFacility(null);
		}
		a.setAdmissionHealthFacilityDetails(b.getAdmissionHealthFacilityDetails());

		// It would be better to merge with the existing hospitalizations
		List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
		if (!b.getPreviousHospitalizations().isEmpty()) {
			for (PreviousHospitalizationDto prevHospDto : b.getPreviousHospitalizations()) {
				PreviousHospitalization prevHosp = previousHospitalizationDtoHelper.fillOrCreateFromDto(null, prevHospDto);
				prevHosp.setHospitalization(a);
				previousHospitalizations.add(prevHosp);
			}
		}
		a.setPreviousHospitalizations(previousHospitalizations);
	}

	@Override
	public void fillInnerFromAdo(HospitalizationDto a, Hospitalization b) {

		a.setAdmittedToHealthFacility(b.getAdmittedToHealthFacility());
		a.setAdmissionDate(b.getAdmissionDate());
		a.setDischargeDate(b.getDischargeDate());
		a.setIsolated(b.getIsolated());
		a.setIsolationDate(b.getIsolationDate());
		a.setDescription(b.getDescription());
		a.setLeftAgainstAdvice(b.getLeftAgainstAdvice());
		a.setIntensiveCareUnit(b.getIntensiveCareUnit());
		a.setIntensiveCareUnitStart(b.getIntensiveCareUnitStart());
		a.setIntensiveCareUnitEnd(b.getIntensiveCareUnitEnd());
		a.setHospitalizedPreviously(b.getHospitalizedPreviously());
		a.setHospitalizationReason(b.getHospitalizationReason());
		a.setOtherHospitalizationReason(b.getOtherHospitalizationReason());
		a.setSelectInpatientOutpatient(b.getSelectInpatientOutpatient());
		a.setHospitalRecordNumber(b.getHospitalRecordNumber());
		a.setSeenAtHealthFacility(b.getSeenAtHealthFacility());
		a.setDateFirstSeenAtHealthFacility(b.getDateFirstSeenAtHealthFacility());
		a.setDateHealthFacilityNotifiedDistrict(b.getDateHealthFacilityNotifiedDistrict());
		a.setSerialNumberInConsultationRegister(b.getSerialNumberInConsultationRegister());
		a.setDateOfConsultationAtHealthFacility(b.getDateOfConsultationAtHealthFacility());
		a.setDateHealthRegionNotified(b.getDateHealthRegionNotified());
		a.setDateOfDiseaseOnset(b.getDateOfDiseaseOnset());
		a.setAddress(b.getAddress());
		a.setAdmittedToDifferentHealthFacility(b.getAdmittedToDifferentHealthFacility());
		if (b.getAdmissionRegion() != null) {
			Region region = DatabaseHelper.getRegionDao().queryForId(b.getAdmissionRegion().getId());
			a.setAdmissionRegion(RegionDtoHelper.toReferenceDto(region));
		} else {
			a.setAdmissionRegion(null);
		}
		if (b.getAdmissionDistrict() != null) {
			District district = DatabaseHelper.getDistrictDao().queryForId(b.getAdmissionDistrict().getId());
			a.setAdmissionDistrict(DistrictDtoHelper.toReferenceDto(district));
		} else {
			a.setAdmissionDistrict(null);
		}
		if (b.getAdmissionHealthFacility() != null) {
			Facility facility = DatabaseHelper.getFacilityDao().queryForId(b.getAdmissionHealthFacility().getId());
			a.setAdmissionHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
		} else {
			a.setAdmissionHealthFacility(null);
		}
		a.setAdmissionHealthFacilityDetails(b.getAdmissionHealthFacilityDetails());

		List<PreviousHospitalizationDto> previousHospitalizationDtos = new ArrayList<>();
		for (PreviousHospitalization prevHosp : b.getPreviousHospitalizations()) {
			PreviousHospitalizationDto prevHospDto = previousHospitalizationDtoHelper.adoToDto(prevHosp);
			previousHospitalizationDtos.add(prevHospDto);
		}
		a.setPreviousHospitalizations(previousHospitalizationDtos);
	}

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}

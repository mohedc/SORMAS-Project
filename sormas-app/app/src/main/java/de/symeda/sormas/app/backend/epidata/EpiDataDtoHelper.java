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
import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.exposure.ExposureDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class EpiDataDtoHelper extends AdoDtoHelper<EpiData, EpiDataDto> {

	private final ExposureDtoHelper exposureDtoHelper;
	private final ActivityAsCaseDtoHelper activityAsCaseDtoHelper;
	private final LocationDtoHelper locationDtoHelper;

	public EpiDataDtoHelper() {
		exposureDtoHelper = new ExposureDtoHelper();
		activityAsCaseDtoHelper = new ActivityAsCaseDtoHelper();
		locationDtoHelper = new LocationDtoHelper();
	}

	@Override
	protected Class<EpiData> getAdoClass() {
		return EpiData.class;
	}

	@Override
	protected Class<EpiDataDto> getDtoClass() {
		return EpiDataDto.class;
	}

	@Override
	protected Call<List<EpiDataDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<EpiDataDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<EpiDataDto> epiDataDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(EpiData target, EpiDataDto source) {

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setActivityAsCaseDetailsKnown(source.getActivityAsCaseDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());
		target.setTravelHistoryKnown(source.getTravelHistoryKnown());
		target.setTravelLocation(locationDtoHelper.fillOrCreateFromDto(target.getTravelLocation(), source.getTravelLocation()));

		List<Exposure> exposures = new ArrayList<>();
		if (!source.getExposures().isEmpty()) {
			for (ExposureDto exposureDto : source.getExposures()) {
				Exposure exposure = exposureDtoHelper.fillOrCreateFromDto(null, exposureDto);
				exposure.setEpiData(target);
				exposures.add(exposure);
			}
		}
		target.setExposures(exposures);

		List<ActivityAsCase> activitiesAsCase = new ArrayList<>();
		if (!source.getActivitiesAsCase().isEmpty()) {
			for (ActivityAsCaseDto activityAsCaseDto : source.getActivitiesAsCase()) {
				ActivityAsCase activityAsCase = activityAsCaseDtoHelper.fillOrCreateFromDto(null, activityAsCaseDto);
				activityAsCase.setEpiData(target);
				activitiesAsCase.add(activityAsCase);
			}
		}
		target.setActivitiesAsCase(activitiesAsCase);

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(EpiDataDto target, EpiData source) {

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setActivityAsCaseDetailsKnown(source.getActivityAsCaseDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());
		target.setTravelHistoryKnown(source.getTravelHistoryKnown());

		List<ExposureDto> exposureDtos = new ArrayList<>();
		if (!source.getExposures().isEmpty()) {
			for (Exposure exposure : source.getExposures()) {
				ExposureDto exposureDto = exposureDtoHelper.adoToDto(exposure);
				exposureDtos.add(exposureDto);
			}
		}
		target.setExposures(exposureDtos);

		List<ActivityAsCaseDto> activityAsCaseDtos = new ArrayList<>();
		if (!source.getActivitiesAsCase().isEmpty()) {
			for (ActivityAsCase activityAsCase : source.getActivitiesAsCase()) {
				ActivityAsCaseDto exposureDto = activityAsCaseDtoHelper.adoToDto(activityAsCase);
				activityAsCaseDtos.add(exposureDto);
			}
		}
		target.setActivitiesAsCase(activityAsCaseDtos);

		if (source.getTravelLocation() != null) {
			Location travelLocation = DatabaseHelper.getLocationDao().queryForId(source.getTravelLocation().getId());
			target.setTravelLocation(locationDtoHelper.adoToDto(travelLocation));
		} else {
			target.setTravelLocation(null);
		}

		target.setPseudonymized(source.isPseudonymized());
	}

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}

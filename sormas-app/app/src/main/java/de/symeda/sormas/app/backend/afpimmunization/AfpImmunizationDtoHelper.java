/*
 * SORMAS(R) - Surveillance Outbreak Response Management & Analysis System
 * Copyright (C) 2016-2026 Helmholtz-Zentrum fuer Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.app.backend.afpimmunization;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.afpimmunization.AfpImmunizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class AfpImmunizationDtoHelper extends AdoDtoHelper<AfpImmunization, AfpImmunizationDto> {

	@Override
	protected Class<AfpImmunization> getAdoClass() {
		return AfpImmunization.class;
	}

	@Override
	protected Class<AfpImmunizationDto> getDtoClass() {
		return AfpImmunizationDto.class;
	}

	@Override
	protected Call<List<AfpImmunizationDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)
		throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<AfpImmunizationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<AfpImmunizationDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(AfpImmunization target, AfpImmunizationDto source) {
		target.setTotalNumberDoses(source.getTotalNumberDoses());
		target.setOpvDoseAtBirth(source.getOpvDoseAtBirth());
		target.setSecondDose(source.getSecondDose());
		target.setFourthDose(source.getFourthDose());
		target.setFirstDose(source.getFirstDose());
		target.setThirdDose(source.getThirdDose());
		target.setLastDose(source.getLastDose());
		target.setTotalOpvDosesReceivedThroughSia(source.getTotalOpvDosesReceivedThroughSia());
		target.setTotalOpvDosesReceivedThroughRi(source.getTotalOpvDosesReceivedThroughRi());
		target.setDateLastOpvDosesReceivedThroughSia(source.getDateLastOpvDosesReceivedThroughSia());
		target.setTotalIpvDosesReceivedThroughSia(source.getTotalIpvDosesReceivedThroughSia());
		target.setTotalIpvDosesReceivedThroughRi(source.getTotalIpvDosesReceivedThroughRi());
		target.setDateLastIpvDosesReceivedThroughSia(source.getDateLastIpvDosesReceivedThroughSia());
		target.setSourceRiVaccinationInformation(source.getSourceRiVaccinationInformation());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	protected void fillInnerFromAdo(AfpImmunizationDto target, AfpImmunization source) {
		target.setTotalNumberDoses(source.getTotalNumberDoses());
		target.setOpvDoseAtBirth(source.getOpvDoseAtBirth());
		target.setSecondDose(source.getSecondDose());
		target.setFourthDose(source.getFourthDose());
		target.setFirstDose(source.getFirstDose());
		target.setThirdDose(source.getThirdDose());
		target.setLastDose(source.getLastDose());
		target.setTotalOpvDosesReceivedThroughSia(source.getTotalOpvDosesReceivedThroughSia());
		target.setTotalOpvDosesReceivedThroughRi(source.getTotalOpvDosesReceivedThroughRi());
		target.setDateLastOpvDosesReceivedThroughSia(source.getDateLastOpvDosesReceivedThroughSia());
		target.setTotalIpvDosesReceivedThroughSia(source.getTotalIpvDosesReceivedThroughSia());
		target.setTotalIpvDosesReceivedThroughRi(source.getTotalIpvDosesReceivedThroughRi());
		target.setDateLastIpvDosesReceivedThroughSia(source.getDateLastIpvDosesReceivedThroughSia());
		target.setSourceRiVaccinationInformation(source.getSourceRiVaccinationInformation());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}
}

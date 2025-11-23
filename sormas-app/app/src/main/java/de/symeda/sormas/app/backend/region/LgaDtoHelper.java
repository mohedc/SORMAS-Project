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

package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.infrastructure.lga.LgaDto;
import de.symeda.sormas.api.infrastructure.lga.LgaReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class LgaDtoHelper extends AdoDtoHelper<Lga, LgaDto> {

	@Override
	protected Class<Lga> getAdoClass() {
		return Lga.class;
	}

	@Override
	protected Class<LgaDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<LgaDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getLgaFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<LgaDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getLgaFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<LgaDto> lgaDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	public void fillInnerFromDto(Lga ado, LgaDto dto) {
		ado.setName(dto.getName());
		ado.setEpidCode(dto.getEpidCode());
		ado.setCountry(DatabaseHelper.getCountryDao().getByReferenceDto(dto.getCountry()));
		ado.setArea(DatabaseHelper.getAreaDao().getByReferenceDto(dto.getArea()));
		ado.setArchived(dto.isArchived());
		ado.setDefaultInfrastructure(dto.isDefaultInfrastructure());
	}

	@Override
	public void fillInnerFromAdo(LgaDto lgaDto, Lga lga) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

	public static LgaReferenceDto toReferenceDto(Lga ado) {
		if (ado == null) {
			return null;
		}
		LgaReferenceDto dto = new LgaReferenceDto(ado.getUuid());

		return dto;
	}
}


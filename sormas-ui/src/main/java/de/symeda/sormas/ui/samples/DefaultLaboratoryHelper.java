/*
 * SORMASÂ® - Surveillance Outbreak Response Management & Analysis System
 * Copyright Â© 2016-2023 Helmholtz-Zentrum fÃ¼r Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;

public final class DefaultLaboratoryHelper {

	private static final String DEFAULT_LABORATORY_NAME = "national public health laboratory";
	private static final String DEFAULT_LABORATORY_ABBREVIATION = "nphl";

	private DefaultLaboratoryHelper() {
	}

	public static void setDefaultLaboratory(SampleDto sample) {
		FacilityReferenceDto defaultLaboratory = getDefaultLaboratory();
		if (defaultLaboratory != null) {
			sample.setLab(defaultLaboratory);
		}
	}

	public static void setDefaultLaboratory(PathogenTestDto pathogenTest) {
		FacilityReferenceDto defaultLaboratory = getDefaultLaboratory();
		if (defaultLaboratory != null) {
			pathogenTest.setLab(defaultLaboratory);
			pathogenTest.setLabDetails(null);
		}
	}

	private static FacilityReferenceDto getDefaultLaboratory() {
		List<FacilityReferenceDto> laboratories = FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false);
		return laboratories.stream().filter(DefaultLaboratoryHelper::isDefaultLaboratory).findFirst().orElse(null);
	}

	private static boolean isDefaultLaboratory(FacilityReferenceDto laboratory) {
		String caption = StringUtils.defaultString(laboratory.getCaption()).toLowerCase(Locale.ENGLISH);
		return caption.contains(DEFAULT_LABORATORY_NAME) || caption.contains(DEFAULT_LABORATORY_ABBREVIATION);
	}
}

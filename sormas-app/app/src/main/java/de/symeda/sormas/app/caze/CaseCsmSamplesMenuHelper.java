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

package de.symeda.sormas.app.caze;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNo;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public final class CaseCsmSamplesMenuHelper {

	private CaseCsmSamplesMenuHelper() {
	}

	public static boolean isSamplesMenuEnabled(Case caze) {
		if (caze == null || caze.getDisease() != Disease.CSM) {
			return true;
		}
		return YesNo.YES.equals(caze.getCsfSampleCollected());
	}

	public static void configureSamplesMenuItem(List<PageMenuItem> menuItems, boolean enabled) {
		PageMenuItem samplesMenuItem = menuItems.get(CaseSection.SAMPLES.ordinal());
		if (samplesMenuItem == null) {
			return;
		}
		samplesMenuItem.setEnabled(enabled);
		if (!enabled) {
			samplesMenuItem.setDisabledReason(I18nProperties.getCaption(Captions.sampleSelectYesForCsfCollected));
		} else {
			samplesMenuItem.setDisabledReason(null);
		}
	}
}

/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Culture or PCR result checkboxes on pathogen test laboratory forms.
 */
public enum CulturePcrFinding {

	NMA,
	NMC,
	NMW,
	NMY,
	NMB,
	NMX,
	NM_INDETERMINATE,
	S_PNEUMONIAE,
	HIB,
	H_INFLUENZAE_INDETERMINATE,
	STREP_B,
	OTHER_GERMS,
	CONTAMINATED,
	NEGATIVE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static boolean triggersAntibiogram(CulturePcrFinding finding) {
		return finding == NMA
			|| finding == NMC
			|| finding == NMW
			|| finding == NMY
			|| finding == NMB
			|| finding == NMX
			|| finding == HIB
			|| finding == STREP_B;
	}
}

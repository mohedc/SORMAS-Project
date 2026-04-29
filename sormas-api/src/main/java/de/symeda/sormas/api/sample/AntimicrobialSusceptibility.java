/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Antimicrobial susceptibility outcome (e.g. culture antibiogram on pathogen tests).
 */
public enum AntimicrobialSusceptibility {

	SENSITIVE,
	RESISTANT,
	INTERMEDIATE,
	NOT_DONE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

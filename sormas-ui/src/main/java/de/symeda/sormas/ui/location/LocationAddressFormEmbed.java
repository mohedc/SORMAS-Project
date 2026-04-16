/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package de.symeda.sormas.ui.location;

/**
 * Callbacks used when a location form is embedded in {@link de.symeda.sormas.ui.person.PersonCreateForm}
 * (home address block). Implemented by {@link LocationEditForm} and {@link LocationCreateForm}.
 */
public interface LocationAddressFormEmbed {

	void setFieldsRequirement(boolean required, String... fieldIds);

	void setFacilityFieldsVisible(boolean visible, boolean clearOnHidden);

	void setDisableFacilityAddressCheck(boolean disableFacilityAddressCheck);
}

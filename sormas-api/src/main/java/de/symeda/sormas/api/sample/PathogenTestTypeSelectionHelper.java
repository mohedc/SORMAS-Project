/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Stable ordering for multi-selected {@link PathogenTestType} values used with
 * {@link PathogenTestDto#getSelectedPathogenTestTypes()} to derive the legacy single {@link PathogenTestDto#getTestType()}.
 */
public final class PathogenTestTypeSelectionHelper {

	private PathogenTestTypeSelectionHelper() {
	}

	/**
	 * Panel types offered in multi-select UI (e.g. CSM laboratory form), in display / sync order.
	 */
	public static final List<PathogenTestType> MULTI_SELECT_PANEL_ORDER = Collections.unmodifiableList(
		Arrays.asList(
			PathogenTestType.CELL_COUNT,
			PathogenTestType.GRAM_STAIN,
			PathogenTestType.LATEX,
			PathogenTestType.RAPID_TEST,
			PathogenTestType.CULTURE,
			PathogenTestType.PCR,
			PathogenTestType.OTHER));

	/**
	 * Picks the first selected type in {@link #MULTI_SELECT_PANEL_ORDER} so {@link PathogenTestDto#getTestType()} stays deterministic.
	 */
	public static PathogenTestType derivePrimaryTestType(Set<PathogenTestType> selected) {
		if (selected == null || selected.isEmpty()) {
			return null;
		}
		for (PathogenTestType p : MULTI_SELECT_PANEL_ORDER) {
			if (selected.contains(p)) {
				return p;
			}
		}
		return null;
	}
}

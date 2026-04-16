/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package de.symeda.sormas.ui.location;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.data.Buffered.SourceException;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

/**
 * Minimal location editor for case creation home address: region, district, community, village,
 * nearest health facility only (no country, street, GPS, facility picker, etc.).
 */
public class LocationCreateForm extends AbstractEditForm<LocationDto> implements LocationAddressFormEmbed {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT =
		fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT, LocationDto.COMMUNITY)
			+ fluidRowLocs(LocationDto.VILLAGE, LocationDto.NEAREST_HEALTH_FACILITY, "");

	private ComboBox region;
	private ComboBox district;
	private ComboBox community;

	public LocationCreateForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(LocationDto.class, LocationDto.I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		region = addInfrastructureField(LocationDto.REGION);
		district = addInfrastructureField(LocationDto.DISTRICT);
		community = addInfrastructureField(LocationDto.COMMUNITY);
		community.setNullSelectionAllowed(true);

		addField(LocationDto.VILLAGE, TextField.class);
		addField(LocationDto.NEAREST_HEALTH_FACILITY, TextField.class);

		region.addValueChangeListener(
			e -> FieldHelper.updateItems(
				district,
				e.getProperty().getValue() != null
					? FacadeProvider.getDistrictFacade().getAllActiveByRegion(((RegionReferenceDto) e.getProperty().getValue()).getUuid())
					: null));
		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		});

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (!isEditableAllowed(LocationDto.COMMUNITY)) {
			setEnabled(false, LocationDto.REGION, LocationDto.DISTRICT);
		}

		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
	}

	public void setFieldsRequirement(boolean required, String... fieldIds) {
		setRequired(required, fieldIds);
	}

	/**
	 * No facility fields in this form; kept for API compatibility with {@link PersonCreateForm}.
	 */
	public void setFacilityFieldsVisible(boolean visible, boolean clearOnHidden) {
		// no-op
	}

	/**
	 * No facility-driven address override; kept for API compatibility with {@link PersonCreateForm}.
	 */
	public void setDisableFacilityAddressCheck(boolean disableFacilityAddressCheck) {
		// no-op
	}

	@Override
	public void setValue(LocationDto newFieldValue) throws Property.ReadOnlyException, ConversionException {
		super.setValue(newFieldValue);
		discard();
	}

	@Override
	protected void setInternalValue(LocationDto newValue) {
		super.setInternalValue(newValue);
		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
		if (getValue() != null && UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}

	private void hideAndFillJurisdictionFields() {

		region.setVisible(false);
		district.setVisible(false);
		community.setVisible(false);
		if (region.getValue() == null) {
			region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		}
		if (district.getValue() == null) {
			district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		}
		if (community.getValue() == null) {
			community.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
		}
	}

}

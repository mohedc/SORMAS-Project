/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DiseaseSpecificExportField.TABLE_NAME)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
	"disease",
	"propertypath" }))
public class DiseaseSpecificExportField extends AbstractDomainObject {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "diseasespecificexportfield";

	public static final String DISEASE = "disease";
	public static final String PROPERTY_PATH = "propertyPath";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String ACTIVE = "active";
	public static final String SCHEMA_BLOCK = "schemaBlock";

	private Disease disease;
	private String propertyPath;
	private int displayOrder;
	private boolean active;
	private String schemaBlock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(name = "propertypath", nullable = false, length = 512)
	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	@Column(nullable = false)
	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(length = 64)
	public String getSchemaBlock() {
		return schemaBlock;
	}

	public void setSchemaBlock(String schemaBlock) {
		this.schemaBlock = schemaBlock;
	}
}

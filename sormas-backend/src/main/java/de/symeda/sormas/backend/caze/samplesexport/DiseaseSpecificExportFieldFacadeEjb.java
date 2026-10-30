/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificCaseExportSchema;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldDto;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "DiseaseSpecificExportFieldFacade")
@RightsAllowed(UserRight._MANAGE_PUBLIC_EXPORT_CONFIGURATION)
public class DiseaseSpecificExportFieldFacadeEjb implements DiseaseSpecificExportFieldFacade {

	@EJB
	private DiseaseSpecificExportFieldService fieldService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public List<DiseaseSpecificExportFieldDto> getByDisease(Disease disease) {
		return fieldService.getByDisease(disease).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public void saveActivePaths(Disease disease, Set<String> activePropertyPaths) {
		Set<String> active = activePropertyPaths == null ? java.util.Collections.emptySet() : activePropertyPaths;
		for (DiseaseSpecificExportField field : fieldService.getByDisease(disease)) {
			field.setActive(active.contains(field.getPropertyPath()));
			fieldService.ensurePersisted(field);
		}
	}

	@Override
	public byte[] exportCsv(Disease disease) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
			CSVWriter writer = CSVUtils.createCSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), ',')) {
			writer.writeNext(new String[] {
				"disease",
				"propertyPath",
				"displayOrder",
				"active" });
			for (DiseaseSpecificExportField field : fieldService.getByDisease(disease)) {
				writer.writeNext(new String[] {
					field.getDisease().name(),
					field.getPropertyPath(),
					String.valueOf(field.getDisplayOrder()),
					String.valueOf(field.isActive()) });
			}
			writer.flush();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void importCsv(byte[] csvContent) {
		List<String> unknown = fieldService.importCsvAndUpdateActive(csvContent);
		if (CollectionUtils.isNotEmpty(unknown)) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageDiseaseSpecificExportUnknownPaths) + " " + String.join(", ", unknown));
		}
	}

	@Override
	public List<String> getActivePropertyPaths(Disease disease) {
		return fieldService.getActivePropertyPaths(disease);
	}

	@Override
	public boolean isSupported(Disease disease) {
		return DiseaseSpecificExportFieldConfiguration.isSupported(disease);
	}

	private DiseaseSpecificExportFieldDto toDto(DiseaseSpecificExportField source) {
		DiseaseSpecificExportFieldDto target = new DiseaseSpecificExportFieldDto();
		DtoHelper.fillDto(target, source);
		target.setDisease(source.getDisease());
		target.setPropertyPath(source.getPropertyPath());
		target.setDisplayOrder(source.getDisplayOrder());
		target.setActive(source.isActive());
		target.setSchemaBlock(source.getSchemaBlock());
		Map<String, DiseaseSpecificCaseExportSchema.Column> catalog = DiseaseSpecificCaseExportSchema.build(source.getDisease(), configFacade.getCountryLocale())
			.stream()
			.collect(Collectors.toMap(DiseaseSpecificCaseExportSchema.Column::getPropertyPath, c -> c, (a, b) -> a));
		DiseaseSpecificCaseExportSchema.Column column = catalog.get(source.getPropertyPath());
		target.setCaption(column != null ? column.getCaption() : source.getPropertyPath());
		return target;
	}

	@LocalBean
	@Stateless
	public static class DiseaseSpecificExportFieldFacadeEjbLocal extends DiseaseSpecificExportFieldFacadeEjb {
	}
}

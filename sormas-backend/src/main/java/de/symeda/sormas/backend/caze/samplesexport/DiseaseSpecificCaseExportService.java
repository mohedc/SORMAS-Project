/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificCaseExportSchema;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;

@Stateless
@LocalBean
public class DiseaseSpecificCaseExportService {

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public void writeExport(
		CaseCriteria criteria,
		Collection<String> selectedRows,
		List<String> propertyPaths,
		Disease disease,
		File file,
		IntConsumer rowsWrittenCallback)
		throws Exception {

		List<DiseaseSpecificCaseExportSchema.Column> catalog = DiseaseSpecificCaseExportSchema.build(disease, configFacade.getCountryLocale());
		Map<String, DiseaseSpecificCaseExportSchema.Column> byPath =
			catalog.stream().collect(Collectors.toMap(DiseaseSpecificCaseExportSchema.Column::getPropertyPath, c -> c, (a, b) -> a));
		List<DiseaseSpecificCaseExportSchema.Column> columns = new ArrayList<>();
		LinkedHashSet<String> orderedPaths = new LinkedHashSet<>();
		orderedPaths.add(DiseaseSpecificCaseExportSchema.PATH_ID);
		orderedPaths.add(DiseaseSpecificCaseExportSchema.PATH_UUID);
		if (propertyPaths != null) {
			for (String path : propertyPaths) {
				if (StringUtils.isNotBlank(path)) {
					orderedPaths.add(path.trim());
				}
			}
		}
		for (String path : orderedPaths) {
			DiseaseSpecificCaseExportSchema.Column column = byPath.get(path);
			if (column != null) {
				columns.add(column);
			} else {
				columns.add(new DiseaseSpecificCaseExportSchema.Column("case", CaseDataDto.class, path, path, path, columns.size() + 1));
			}
		}

		try (CSVWriter writer = CSVUtils.createCSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), configFacade.getCsvSeparator())) {
			writer.writeNext(columns.stream().map(c -> DataHelper.getHumanClassName(c.getEntityClass())).toArray(String[]::new));
			writer.writeNext(columns.stream().map(DiseaseSpecificCaseExportSchema.Column::getPropertyPath).toArray(String[]::new));
			String[] captions = columns.stream().map(this::captionOf).toArray(String[]::new);
			if (captions.length > 0) {
				captions[0] = "#" + captions[0];
			}
			writer.writeNext(captions, false);

			int pageSize = Math.max(1, configFacade.getStepSizeForCsvExport());
			int first = 0;
			while (true) {
				List<String> caseUuids = nextCaseUuids(criteria, selectedRows, first, pageSize);
				if (caseUuids.isEmpty()) {
					break;
				}
				List<CaseDataDto> cases = caseFacade.getByUuids(caseUuids);
				Map<String, Long> caseIdsByUuid = loadCaseIds(criteria, selectedRows, first, pageSize, caseUuids);
				Map<String, PersonDto> persons = loadPersons(cases);
				Map<String, List<SampleDto>> samplesByCase = CollectionUtils.isEmpty(cases)
					? Collections.emptyMap()
					: sampleFacade.getByCaseUuids(caseUuids).stream().collect(Collectors.groupingBy(s -> s.getAssociatedCase().getUuid()));
				List<String> sampleUuids =
					samplesByCase.values().stream().flatMap(List::stream).map(SampleDto::getUuid).collect(Collectors.toList());
				Map<String, List<PathogenTestDto>> testsBySample = sampleUuids.isEmpty()
					? Collections.emptyMap()
					: pathogenTestFacade.getBySampleUuids(sampleUuids).stream().collect(Collectors.groupingBy(t -> t.getSample().getUuid()));

				int rowsThisPage = 0;
				for (CaseDataDto caze : cases) {
					PersonDto person = caze.getPerson() == null ? null : persons.get(caze.getPerson().getUuid());
					Long caseId = caseIdsByUuid.get(caze.getUuid());
					List<SampleDto> samples = samplesByCase.getOrDefault(caze.getUuid(), Collections.emptyList());
					if (samples.isEmpty()) {
						writer.writeNext(buildRow(columns, caze, caseId, person, null, Collections.emptyList()));
						rowsThisPage++;
					} else {
						for (SampleDto sample : samples) {
							writer.writeNext(
								buildRow(columns, caze, caseId, person, sample, testsBySample.getOrDefault(sample.getUuid(), Collections.emptyList())));
							rowsThisPage++;
						}
					}
				}
				writer.flush();
				if (rowsWrittenCallback != null) {
					rowsWrittenCallback.accept(rowsThisPage);
				}
				if (CollectionUtils.isNotEmpty(selectedRows) || caseUuids.size() < pageSize) {
					break;
				}
				first += pageSize;
			}
		}
	}

	private List<String> nextCaseUuids(CaseCriteria criteria, Collection<String> selectedRows, int first, int max) {
		if (CollectionUtils.isNotEmpty(selectedRows)) {
			if (first > 0) {
				return Collections.emptyList();
			}
			return new ArrayList<>(selectedRows);
		}
		List<CaseIndexDto> index = caseFacade.getIndexList(criteria, first, max, null);
		return index.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList());
	}

	private Map<String, PersonDto> loadPersons(List<CaseDataDto> cases) {
		List<String> personUuids = cases.stream().filter(c -> c.getPerson() != null).map(c -> c.getPerson().getUuid()).distinct().collect(Collectors.toList());
		if (personUuids.isEmpty()) {
			return Collections.emptyMap();
		}
		return personFacade.getByUuids(personUuids).stream().collect(Collectors.toMap(PersonDto::getUuid, p -> p));
	}

	private Map<String, Long> loadCaseIds(
		CaseCriteria criteria,
		Collection<String> selectedRows,
		int first,
		int max,
		List<String> caseUuids) {
		if (CollectionUtils.isEmpty(caseUuids)) {
			return Collections.emptyMap();
		}
		List<CaseIndexDto> index;
		if (CollectionUtils.isNotEmpty(selectedRows)) {
			index = caseFacade.getIndexList(criteria, null, null, null)
				.stream()
				.filter(c -> caseUuids.contains(c.getUuid()))
				.collect(Collectors.toList());
		} else {
			index = caseFacade.getIndexList(criteria, first, max, null);
		}
		return index.stream().collect(Collectors.toMap(CaseIndexDto::getUuid, CaseIndexDto::getId, (a, b) -> a));
	}

	private String[] buildRow(
		List<DiseaseSpecificCaseExportSchema.Column> columns,
		CaseDataDto caze,
		Long caseId,
		PersonDto person,
		SampleDto sample,
		List<PathogenTestDto> tests) {

		String[] row = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			row[i] = DataHelper.valueToString(resolve(columns.get(i).getPropertyPath(), caze, caseId, person, sample, tests));
		}
		return row;
	}

	private Object resolve(String path, CaseDataDto caze, Long caseId, PersonDto person, SampleDto sample, List<PathogenTestDto> tests) {
		try {
			if (DiseaseSpecificCaseExportSchema.PATH_ID.equals(path)) {
				return caseId;
			}
			if (DiseaseSpecificCaseExportSchema.PATH_UUID.equals(path)) {
				return caze == null ? null : caze.getUuid();
			}
			if ("vaccinationDate".equals(path)) {
				return caze == null ? null : caze.getLastVaccinationDate();
			}
			if (path.startsWith("person.")) {
				return person == null ? null : PropertyUtils.getProperty(person, path.substring("person.".length()));
			}
			if (path.startsWith("symptoms.")) {
				return caze == null || caze.getSymptoms() == null ? null : PropertyUtils.getProperty(caze.getSymptoms(), path.substring("symptoms.".length()));
			}
			if (path.startsWith("hospitalization.")) {
				return caze == null || caze.getHospitalization() == null
					? null
					: PropertyUtils.getProperty(caze.getHospitalization(), path.substring("hospitalization.".length()));
			}
			if (path.startsWith("epiData.")) {
				return caze == null || caze.getEpiData() == null ? null : PropertyUtils.getProperty(caze.getEpiData(), path.substring("epiData.".length()));
			}
			if (path.startsWith("response.")) {
				return caze == null || caze.getResponse() == null ? null : PropertyUtils.getProperty(caze.getResponse(), path.substring("response.".length()));
			}
			if (path.startsWith("afpImmunization.")) {
				return caze == null || caze.getAfpImmunization() == null
					? null
					: PropertyUtils.getProperty(caze.getAfpImmunization(), path.substring("afpImmunization.".length()));
			}
			if (path.startsWith("sample.")) {
				return sample == null ? null : PropertyUtils.getProperty(sample, path.substring("sample.".length()));
			}
			if (path.startsWith("pathogenTests.")) {
				String property = path.substring("pathogenTests.".length());
				if (tests == null || tests.isEmpty()) {
					return "";
				}
				return tests.stream().map(t -> {
					try {
						return DataHelper.valueToString(PropertyUtils.getProperty(t, property));
					} catch (Exception e) {
						return "";
					}
				}).collect(Collectors.joining(" | "));
			}
			return caze == null ? null : PropertyUtils.getProperty(caze, path);
		} catch (Exception e) {
			return "";
		}
	}

	private String captionOf(DiseaseSpecificCaseExportSchema.Column column) {
		if (StringUtils.isNotBlank(column.getCaption())) {
			return column.getCaption();
		}
		return I18nProperties.findPrefixCaption(column.getPropertyName(), CaseDataDto.I18N_PREFIX);
	}
}

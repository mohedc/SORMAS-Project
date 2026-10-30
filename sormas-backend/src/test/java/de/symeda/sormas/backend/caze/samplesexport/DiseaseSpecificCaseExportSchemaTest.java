/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.backend.caze.samplesexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificCaseExportSchema;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.api.i18n.I18nProperties;

public class DiseaseSpecificCaseExportSchemaTest {

	private static final String LOCALE = "en-GM";

	@BeforeAll
	public static void initI18n() {
		I18nProperties.setUserLanguage(Language.EN);
	}

	@Test
	public void testSupportedDiseasesHaveCatalogsAndNntHasNoSampleBlock() {
		for (Disease disease : DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES) {
			List<DiseaseSpecificCaseExportSchema.Column> columns = DiseaseSpecificCaseExportSchema.build(disease, LOCALE);
			assertFalse(columns.isEmpty(), "Schema is empty for " + disease);
			boolean hasSample = columns.stream().anyMatch(c -> DiseaseSpecificCaseExportSchema.BLOCK_SAMPLE.equals(c.getSchemaBlock()));
			boolean hasPathogen = columns.stream().anyMatch(c -> DiseaseSpecificCaseExportSchema.BLOCK_PATHOGEN_TESTS.equals(c.getSchemaBlock()));
			if (disease == Disease.NEONATAL_TETANUS) {
				assertFalse(hasSample);
				assertFalse(hasPathogen);
				assertTrue(columns.stream().anyMatch(c -> DiseaseSpecificCaseExportSchema.BLOCK_RESPONSE.equals(c.getSchemaBlock())));
			} else {
				assertTrue(hasSample, "Missing sample block for " + disease);
				assertTrue(hasPathogen, "Missing pathogen test block for " + disease);
			}
			if (disease == Disease.AFP) {
				assertTrue(columns.stream().anyMatch(c -> DiseaseSpecificCaseExportSchema.BLOCK_AFP_IMMUNIZATION.equals(c.getSchemaBlock())));
			}
		}
	}

	@Test
	public void testSeedCsvMatchesSchemaOrder() throws IOException {
		Path dir = Paths.get("src/main/resources/disease-specific-export-fields");
		Files.createDirectories(dir);
		for (Disease disease : DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES) {
			String expected = DiseaseSpecificCaseExportSchema.toSeedCsv(disease, LOCALE);
			Path file = dir.resolve(disease.name() + ".csv");
			Files.write(file, expected.getBytes(StandardCharsets.UTF_8));
			try (InputStream in = getClass().getResourceAsStream("/disease-specific-export-fields/" + disease.name() + ".csv")) {
				if (in != null) {
					String classpath = IOUtils.toString(in, StandardCharsets.UTF_8);
					assertEquals(normalize(expected), normalize(classpath), "Classpath seed CSV differs from schema for " + disease);
				}
			}
			List<String> expectedPaths = DiseaseSpecificCaseExportSchema.build(disease, LOCALE)
				.stream()
				.map(DiseaseSpecificCaseExportSchema.Column::getPropertyPath)
				.collect(Collectors.toList());
			List<String> csvPaths = Files.readAllLines(file, StandardCharsets.UTF_8)
				.stream()
				.skip(1)
				.filter(line -> !line.isEmpty())
				.map(line -> line.split(",", -1)[1])
				.collect(Collectors.toList());
			assertEquals(expectedPaths, csvPaths, "Seed CSV order does not match schema for " + disease);
		}
	}

	private static String normalize(String value) {
		return value.replace("\r\n", "\n");
	}
}

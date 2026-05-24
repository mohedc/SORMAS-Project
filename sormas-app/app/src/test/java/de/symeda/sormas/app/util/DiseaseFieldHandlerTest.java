package de.symeda.sormas.app.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.formfield.FormField;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DiseaseFieldHandlerTest {

	@Test
	public void testFormFieldMatchesSymptomsFieldNameVariants() {
		assertThat(DiseaseFieldHandler.formFieldMatchesViewResource("symptoms_fever", "fever"), is(true));
		assertThat(DiseaseFieldHandler.formFieldMatchesViewResource("symptoms_fever", "symptoms_fever"), is(true));
	}

	@Test
	public void testFormFieldMatchingDoesNotStripCaseDataPrefix() {
		assertThat(DiseaseFieldHandler.formFieldMatchesViewResource("caseData_vaccineType", "caseData_vaccineType"), is(true));
		assertThat(DiseaseFieldHandler.formFieldMatchesViewResource("caseData_vaccineType", "vaccineType"), is(false));
	}

	@Test
	public void testReorderFieldsForDiseaseKeepsContainerVisibleForNestedSelectedField() {
		Resources resources = mock(Resources.class);
		when(resources.getResourceEntryName(R.id.caseData_vaccineType)).thenReturn("caseData_vaccineType");
		when(resources.getResourceEntryName(R.id.caseData_vaccinationRecordType_numberOfVaccinationDoses_layout))
			.thenReturn("caseData_vaccinationRecordType_numberOfVaccinationDoses_layout");
		when(resources.getResourceEntryName(R.id.caseData_numberOfVaccinationDoses)).thenReturn("caseData_numberOfVaccinationDoses");

		Context context = mock(Context.class);
		when(context.getResources()).thenReturn(resources);

		DiseaseFieldHandler handler = new DiseaseFieldHandler(context);
		LinearLayout mainContent = new LinearLayout(RuntimeEnvironment.getApplication());

		TextView directField = new TextView(RuntimeEnvironment.getApplication());
		directField.setId(R.id.caseData_vaccineType);
		mainContent.addView(directField);

		LinearLayout nestedRow = new LinearLayout(RuntimeEnvironment.getApplication());
		nestedRow.setId(R.id.caseData_vaccinationRecordType_numberOfVaccinationDoses_layout);
		TextView nestedField = new TextView(RuntimeEnvironment.getApplication());
		nestedField.setId(R.id.caseData_numberOfVaccinationDoses);
		nestedRow.addView(nestedField);
		mainContent.addView(nestedRow);

		handler.reorderFieldsForDisease(Collections.singletonList(formField("caseData_numberOfVaccinationDoses")), mainContent);

		assertThat(directField.getVisibility(), is(View.GONE));
		assertThat(nestedRow.getVisibility(), is(View.VISIBLE));
		assertThat(nestedField.getVisibility(), is(View.VISIBLE));
	}

	private static FormField formField(String fieldName) {
		FormField formField = new FormField();
		formField.setFieldName(fieldName);
		return formField;
	}
}

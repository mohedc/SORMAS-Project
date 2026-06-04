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

	@Test
	public void testReorderFieldsForDiseaseKeepsUnconfiguredDependencyFieldAfterAnchor() {
		Resources resources = mock(Resources.class);
		when(resources.getResourceEntryName(1001)).thenReturn("maternalHistory_rubellaLayout");
		when(resources.getResourceEntryName(1002)).thenReturn("maternalHistory_rubella");
		when(resources.getResourceEntryName(1003)).thenReturn("maternalHistory_rubellaMonth");
		when(resources.getResourceEntryName(1004)).thenReturn("maternalHistory_childrenNumber");

		Context context = mock(Context.class);
		when(context.getResources()).thenReturn(resources);

		DiseaseFieldHandler handler = new DiseaseFieldHandler(context);
		LinearLayout mainContent = new LinearLayout(RuntimeEnvironment.getApplication());

		LinearLayout rubellaLayout = new LinearLayout(RuntimeEnvironment.getApplication());
		rubellaLayout.setId(1001);
		TextView rubellaField = new TextView(RuntimeEnvironment.getApplication());
		rubellaField.setId(1002);
		rubellaLayout.addView(rubellaField);

		TextView rubellaMonth = new TextView(RuntimeEnvironment.getApplication());
		rubellaMonth.setId(1003);

		TextView childrenNumber = new TextView(RuntimeEnvironment.getApplication());
		childrenNumber.setId(1004);

		mainContent.addView(rubellaLayout);
		mainContent.addView(rubellaMonth);
		mainContent.addView(childrenNumber);

		handler.reorderFieldsForDisease(
			java.util.Arrays.asList(formField("maternalHistory_rubella"), formField("maternalHistory_childrenNumber")),
			mainContent);

		assertThat(mainContent.getChildAt(0), is(rubellaLayout));
		assertThat(mainContent.getChildAt(1), is(rubellaMonth));
		assertThat(mainContent.getChildAt(2), is(childrenNumber));
		assertThat(rubellaLayout.getVisibility(), is(View.VISIBLE));
		assertThat(rubellaMonth.getVisibility(), is(View.GONE));
		assertThat(childrenNumber.getVisibility(), is(View.VISIBLE));
	}

	private static FormField formField(String fieldName) {
		FormField formField = new FormField();
		formField.setFieldName(fieldName);
		return formField;
	}
}

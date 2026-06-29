package de.symeda.sormas.api.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class PersonHelperTest {

	@Test
	public void testValidateRequiredFieldsForCaseCreationRequiresPrimaryPhone() {
		PersonDto person = PersonDto.build();
		person.setBirthdateYYYY(1990);

		assertThrows(ValidationRuntimeException.class, () -> PersonHelper.validateRequiredFieldsForCaseCreation(person));

		person.setPhone("123456789");
		assertDoesNotThrow(() -> PersonHelper.validateRequiredFieldsForCaseCreation(person));
	}
}

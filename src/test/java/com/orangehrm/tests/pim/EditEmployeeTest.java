package com.orangehrm.tests.pim;

import com.orangehrm.data.TestDataFactory;
import com.orangehrm.models.Gender;
import com.orangehrm.models.PersonalDetails;
import com.orangehrm.pages.pim.PersonalDetailsPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

@Feature("Employee management")
@Story("Edit employee")
public class EditEmployeeTest extends PimBaseTest {

    private static final String NEW_MIDDLE_NAME = "Edited";
    // The day is above 12, so a date saved with day and month swapped cannot go unnoticed.
    private static final LocalDate NEW_DATE_OF_BIRTH = LocalDate.of(1990, 12, 25);
    private static final Gender NEW_GENDER = Gender.FEMALE;

    @Test(groups = "regression", description = "PIM-08: Edit Personal Details")
    public void shouldKeepEditedPersonalDetailsAfterReload() {
        PersonalDetailsPage details = createEmployeesAsAdmin(TestDataFactory.randomEmployee());
        PersonalDetails before = details.getPersonalDetails();
        PersonalDetails expected = before
                .withMiddleName(NEW_MIDDLE_NAME)
                .withDateOfBirth(NEW_DATE_OF_BIRTH)
                .withGender(NEW_GENDER);
        assertNotEquals(before, expected, "The edit should change the current values");

        String message = details
                .enterMiddleName(NEW_MIDDLE_NAME)
                .enterDateOfBirth(NEW_DATE_OF_BIRTH)
                .selectGender(NEW_GENDER)
                .save();

        assertEquals(message, "Successfully Updated");
        assertEquals(details.reload().getPersonalDetails(), expected,
                "Edited fields should be stored and other fields should keep their values");
    }
}

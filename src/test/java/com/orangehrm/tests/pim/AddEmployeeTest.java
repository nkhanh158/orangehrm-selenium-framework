package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.flows.EmployeeFlow;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.PersonalDetailsPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AddEmployeeTest extends BaseTest {

    @Test(groups = "smoke", description = "PIM-01: Add employee with required fields only")
    public void shouldCreateEmployeeWithRequiredFieldsOnly() {
        Employee employee = TestDataFactory.randomEmployee();
        cleanupAfterTest(() -> new EmployeeFlow(driver).deleteEmployeeIfExists(employee.employeeId()));

        PersonalDetailsPage details = loginAsAdmin()
                .sidebar().goToPim()
                .clickAdd()
                .addEmployee(employee);

        assertEquals(details.toast().getMessage(), "Successfully Saved");
        assertTrue(details.isLoaded(), "Personal Details page should open after saving");
        assertEquals(details.getEmployee(), employee);
    }
}

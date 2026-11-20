package com.orangehrm.flows;

import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import com.orangehrm.pages.pim.PersonalDetailsPage;
import org.openqa.selenium.WebDriver;

/**
 * Business-level employee operations composed of several page steps. Used for test setup and cleanup,
 * where the individual UI steps are not what the test verifies.
 */
public class EmployeeFlow {

    private final WebDriver driver;

    public EmployeeFlow(WebDriver driver) {
        this.driver = driver;
    }

    public PersonalDetailsPage createEmployee(Employee employee) {
        PersonalDetailsPage details = EmployeeListPage.open(driver)
                .clickAdd()
                .addEmployee(employee);
        details.getEmployee();
        return details;
    }

    /**
     * Deletes the employee if it exists. Safe to call when the employee was never created or already deleted.
     */
    public void deleteEmployeeIfExists(String employeeId) {
        EmployeeListPage list = EmployeeListPage.open(driver).searchByEmployeeId(employeeId);
        if (list.table().hasRowWithCell(employeeId)) {
            list.deleteEmployee(employeeId);
        }
    }
}

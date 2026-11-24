package com.orangehrm.flows;

import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import com.orangehrm.pages.pim.PersonalDetailsPage;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Business-level employee operations composed of several page steps. Used for test setup and cleanup,
 * where the individual UI steps are not what the test verifies.
 */
public class EmployeeFlow {

    private static final Logger LOG = LogManager.getLogger(EmployeeFlow.class);

    private final WebDriver driver;

    public EmployeeFlow(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Create employee {employee.employeeId}")
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
    @Step("Delete employee {employeeId} if it exists")
    public void deleteEmployeeIfExists(String employeeId) {
        EmployeeListPage list = EmployeeListPage.open(driver).searchByEmployeeId(employeeId);
        if (list.table().hasRowWithCell(employeeId)) {
            list.deleteEmployee(employeeId);
        } else {
            LOG.info("Employee {} not found, nothing to delete", employeeId);
        }
    }
}

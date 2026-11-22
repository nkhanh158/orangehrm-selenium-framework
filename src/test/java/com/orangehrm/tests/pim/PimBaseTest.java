package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.flows.EmployeeFlow;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.PersonalDetailsPage;

public abstract class PimBaseTest extends BaseTest {

    /**
     * Logs in as Admin and creates the employees as test setup. Each employee is deleted after the test,
     * also when the test fails or setup stops halfway.
     *
     * @return Personal Details of the last created employee
     */
    protected PersonalDetailsPage createEmployeesAsAdmin(Employee... employees) {
        EmployeeFlow flow = new EmployeeFlow(driver);
        loginAsAdmin();
        PersonalDetailsPage details = null;
        for (Employee employee : employees) {
            cleanupAfterTest(() -> flow.deleteEmployeeIfExists(employee.employeeId()));
            details = flow.createEmployee(employee);
        }
        return details;
    }
}

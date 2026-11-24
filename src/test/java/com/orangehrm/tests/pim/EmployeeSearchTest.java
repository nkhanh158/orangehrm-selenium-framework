package com.orangehrm.tests.pim;

import com.orangehrm.data.TestDataFactory;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EmployeeSearchTest extends PimBaseTest {

    private static final String STATUS = "Full-Time Permanent";

    @Test(groups = {"smoke", "regression"}, description = "PIM-06: Search employee by name using autocomplete")
    public void shouldFindEmployeeBySelectedNameSuggestion() {
        Employee employee = TestDataFactory.randomEmployee();
        createEmployeesAsAdmin(employee);
        EmployeeListPage list = EmployeeListPage.open(driver);

        List<String> suggestions = list.employeeName().type(employee.lastName()).getSuggestions();
        assertEquals(suggestions, List.of(employee.fullName()), "Suggestions for the unique last name");

        list.employeeName().select(employee.fullName());
        list.clickSearch();

        assertEquals(list.table().getRowCount(), 1, "Exactly one employee should match");
        assertTrue(list.table().hasRowWithCell(employee.employeeId()), "The matching row should be the employee");
    }

    @Test(groups = "regression", description = "PIM-07: Search by Employee Id")
    public void shouldFindExactlyOneEmployeeById() {
        Employee employee = TestDataFactory.randomEmployee();
        createEmployeesAsAdmin(employee);

        EmployeeListPage list = EmployeeListPage.open(driver).searchByEmployeeId(employee.employeeId());

        assertEquals(list.table().getColumnValues("Id"), List.of(employee.employeeId()));
    }

    @Test(groups = "regression", description = "PIM-07: Filter employees by Employment Status")
    public void shouldListOnlyEmployeesWithSelectedEmploymentStatus() {
        EmployeeListPage list = loginAsAdmin()
                .sidebar().goToPim()
                .filterByEmploymentStatus(STATUS);

        List<String> statuses = list.table().getColumnValues("Employment Status");

        assertFalse(statuses.isEmpty(), "Shared demo data should contain employees with status " + STATUS);
        assertTrue(statuses.stream().allMatch(STATUS::equals), "Unexpected statuses in result: " + statuses);
    }
}

package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.data.ExcelReader;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.flows.EmployeeFlow;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class EmployeeImportTest extends BaseTest {

    private static final String EMPLOYEES_FILE = "testdata/employees.xlsx";
    private static final String EMPLOYEES_SHEET = "employees";

    @Test(groups = "regression", dataProvider = "employeesFromExcel",
            description = "PIM-13: Create employees from an Excel file")
    public void shouldCreateEmployeeFromExcelRow(String description, Employee employee) {
        EmployeeFlow flow = new EmployeeFlow(driver);
        cleanupAfterTest(() -> flow.deleteEmployeeIfExists(employee.employeeId()));
        loginAsAdmin();

        assertEquals(flow.createEmployee(employee).getEmployee(), employee, "Saved names should be unchanged");

        EmployeeListPage list = EmployeeListPage.open(driver).searchByEmployeeId(employee.employeeId());
        assertTrue(list.table().hasRowWithCell(employee.employeeId()), "Employee should be found by Employee Id");
    }

    /**
     * Names come from Excel; the last name and Employee Id are generated so that every run creates unique,
     * removable records on a shared environment.
     */
    @DataProvider
    public Object[][] employeesFromExcel() {
        return ExcelReader.readSheet(EMPLOYEES_FILE, EMPLOYEES_SHEET).stream()
                .map(row -> new Object[] {row.get("description"), toEmployee(row)})
                .toArray(Object[][]::new);
    }

    private static Employee toEmployee(Map<String, String> row) {
        return new Employee(row.get("firstName"), row.get("middleName"),
                TestDataFactory.uniqueSuffix(), TestDataFactory.uniqueEmployeeId());
    }
}

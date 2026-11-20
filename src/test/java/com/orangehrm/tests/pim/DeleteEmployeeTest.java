package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.flows.EmployeeFlow;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DeleteEmployeeTest extends BaseTest {

    @Test(groups = "smoke", description = "PIM-09: Delete an employee from its table row")
    public void shouldDeleteOnlyTheEmployeeInTheSelectedRow() {
        String marker = TestDataFactory.uniqueSuffix();
        Employee target = TestDataFactory.randomEmployee(marker);
        Employee other = TestDataFactory.randomEmployee(marker);
        EmployeeFlow flow = new EmployeeFlow(driver);

        loginAsAdmin();
        for (Employee employee : new Employee[] {target, other}) {
            cleanupAfterTest(() -> flow.deleteEmployeeIfExists(employee.employeeId()));
            flow.createEmployee(employee);
        }

        EmployeeListPage list = EmployeeListPage.open(driver).searchByName(marker);
        assertEquals(list.table().getRowCount(), 2, "Both employees sharing the marker should be listed");

        String message = list.deleteEmployee(target.employeeId());

        assertEquals(message, "Successfully Deleted");
        assertFalse(list.table().hasRowWithCell(target.employeeId()), "Deleted employee should not be listed");
        assertTrue(list.table().hasRowWithCell(other.employeeId()), "Employee in another row should not be deleted");
    }
}

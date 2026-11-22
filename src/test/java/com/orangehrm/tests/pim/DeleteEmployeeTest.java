package com.orangehrm.tests.pim;

import com.orangehrm.data.TestDataFactory;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DeleteEmployeeTest extends PimBaseTest {

    @Test(groups = "smoke", description = "PIM-09: Delete an employee from its table row")
    public void shouldDeleteOnlyTheEmployeeInTheSelectedRow() {
        String marker = TestDataFactory.uniqueSuffix();
        Employee target = TestDataFactory.randomEmployee(marker);
        Employee other = TestDataFactory.randomEmployee(marker);
        createEmployeesAsAdmin(target, other);

        EmployeeListPage list = EmployeeListPage.open(driver).searchByName(marker);
        assertEquals(list.table().getRowCount(), 2, "Both employees sharing the marker should be listed");

        String message = list.deleteEmployee(target.employeeId());

        assertEquals(message, "Successfully Deleted");
        assertFalse(list.table().hasRowWithCell(target.employeeId()), "Deleted employee should not be listed");
        assertTrue(list.table().hasRowWithCell(other.employeeId()), "Employee in another row should not be deleted");
    }

    @Test(groups = "regression", description = "PIM-10: Delete several employees using checkboxes")
    public void shouldDeleteOnlyTheSelectedEmployees() {
        String marker = TestDataFactory.uniqueSuffix();
        Employee first = TestDataFactory.randomEmployee(marker);
        Employee second = TestDataFactory.randomEmployee(marker);
        Employee kept = TestDataFactory.randomEmployee(marker);
        createEmployeesAsAdmin(first, second, kept);

        EmployeeListPage list = EmployeeListPage.open(driver).searchByName(marker);
        assertEquals(list.table().getRowCount(), 3, "All employees sharing the marker should be listed");

        String message = list.deleteSelectedEmployees(first.employeeId(), second.employeeId());

        assertEquals(message, "Successfully Deleted");
        assertEquals(list.table().getColumnValues("Id"), List.of(kept.employeeId()),
                "Only the employee that was not selected should remain");
    }
}

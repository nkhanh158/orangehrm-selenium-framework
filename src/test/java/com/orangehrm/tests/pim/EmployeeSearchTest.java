package com.orangehrm.tests.pim;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EmployeeSearchTest extends BaseTest {

    private static final String STATUS = "Full-Time Permanent";

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

package com.orangehrm.tests.pim;

import com.orangehrm.components.DataTableComponent;
import com.orangehrm.components.DataTableComponent.SortOrder;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.orangehrm.components.PaginationComponent.PAGE_SIZE;
import static org.testng.Assert.assertEquals;

public class EmployeeTableTest extends PimBaseTest {

    private static final String NAME_COLUMN = "First (& Middle) Name";
    // Created out of order and in mixed case: a case-sensitive sort would put "Bravo" first.
    private static final List<String> FIRST_NAMES = List.of("charlie", "alpha", "Bravo");

    @Test(groups = "regression", description = "PIM-11: Sort by name")
    public void shouldSortByFirstNameIgnoringCase() {
        String marker = TestDataFactory.uniqueSuffix();
        createEmployeesAsAdmin(FIRST_NAMES.stream()
                .map(firstName -> new Employee(firstName, "", marker, TestDataFactory.uniqueEmployeeId()))
                .toArray(Employee[]::new));
        DataTableComponent table = EmployeeListPage.open(driver).searchByName(marker).table();

        // Descending first: the list is already sorted ascending by name by default.
        table.sortBy(NAME_COLUMN, SortOrder.DESCENDING);
        assertEquals(table.getColumnValues(NAME_COLUMN),
                FIRST_NAMES.stream().sorted(String.CASE_INSENSITIVE_ORDER.reversed()).toList(), "Descending");

        table.sortBy(NAME_COLUMN, SortOrder.ASCENDING);
        assertEquals(table.getColumnValues(NAME_COLUMN),
                FIRST_NAMES.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList(), "Ascending");
    }

    @Test(groups = "regression", description = "PIM-12: Pagination")
    public void shouldListEveryRecordAcrossPages() {
        DataTableComponent table = loginAsAdmin().sidebar().goToPim().table();
        int total = table.getTotalRecords();
        if (total <= PAGE_SIZE) {
            throw new SkipException("Needs more than " + PAGE_SIZE + " employees to have a second page, found " + total);
        }
        int expectedPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;

        List<Integer> rowsPerPage = new ArrayList<>(List.of(table.getRowCount()));
        while (table.pagination().hasNextPage() && rowsPerPage.size() <= expectedPages) {
            table.pagination().goToNextPage();
            rowsPerPage.add(table.getRowCount());
        }

        String summary = "Rows per page " + rowsPerPage + " for " + total + " records";
        assertEquals(rowsPerPage.size(), expectedPages, summary);
        assertEquals(rowsPerPage.stream().mapToInt(Integer::intValue).sum(), total, summary);
    }
}

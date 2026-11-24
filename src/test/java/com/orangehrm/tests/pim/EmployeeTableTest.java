package com.orangehrm.tests.pim;

import com.orangehrm.components.DataTableComponent.SortOrder;
import com.orangehrm.components.DataTableComponent;
import com.orangehrm.data.TestDataFactory;
import com.orangehrm.models.Employee;
import com.orangehrm.pages.pim.EmployeeListPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.orangehrm.components.PaginationComponent.PAGE_SIZE;
import static org.testng.Assert.assertEquals;

@Feature("Employee management")
@Story("Employee list")
public class EmployeeTableTest extends PimBaseTest {

    private static final String NAME_COLUMN = "First (& Middle) Name";
    private static final int MAX_PAGE_WALKS = 3;
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

    @Test(groups = {"regression", "serial"}, description = "PIM-12: Pagination")
    public void shouldListEveryRecordAcrossPages() {
        loginAsAdmin();
        List<PageWalk> unstableWalks = new ArrayList<>();
        for (int attempt = 1; attempt <= MAX_PAGE_WALKS; attempt++) {
            PageWalk walk = walkAllPages(EmployeeListPage.open(driver).table());
            if (walk.totalAtStart() <= PAGE_SIZE) {
                throw new SkipException("Needs more than " + PAGE_SIZE + " employees to have a second page, found "
                        + walk.totalAtStart());
            }
            if (walk.totalAtStart() != walk.totalAtEnd()) {
                unstableWalks.add(walk);
                continue;
            }
            int total = walk.totalAtStart();
            assertEquals(walk.rowsPerPage().size(), (total + PAGE_SIZE - 1) / PAGE_SIZE, walk.toString());
            assertEquals(walk.rowsPerPage().stream().mapToInt(Integer::intValue).sum(), total, walk.toString());
            return;
        }
        throw new SkipException("Employees were added or deleted by someone else during every walk: " + unstableWalks);
    }

    /**
     * Visits every page. The record count is read again at the end: on a shared environment a different count
     * means the data changed while the pages were visited, so the row counts cannot be compared with it.
     */
    private static PageWalk walkAllPages(DataTableComponent table) {
        int totalAtStart = table.getTotalRecords();
        int maxPages = totalAtStart / PAGE_SIZE + 1;
        List<Integer> rowsPerPage = new ArrayList<>(List.of(table.getRowCount()));
        while (table.pagination().hasNextPage() && rowsPerPage.size() <= maxPages) {
            table.pagination().goToNextPage();
            rowsPerPage.add(table.getRowCount());
        }
        return new PageWalk(totalAtStart, rowsPerPage, table.getTotalRecords());
    }

    private record PageWalk(int totalAtStart, List<Integer> rowsPerPage, int totalAtEnd) {
    }
}

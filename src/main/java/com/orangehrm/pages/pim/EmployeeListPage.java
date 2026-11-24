package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.components.AutocompleteComponent;
import com.orangehrm.components.DataTableComponent;
import com.orangehrm.components.DropdownComponent;
import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;

public class EmployeeListPage extends AuthenticatedPage {

    private static final String PATH = "/web/index.php/pim/viewEmployeeList";

    private final By employeeIdInput = inputByLabel("Employee Id");
    private final By searchButton = By.cssSelector(".oxd-table-filter button[type='submit']");
    private final By addButton = By.xpath("//button[normalize-space()='Add']");

    private final DataTableComponent table;
    private final DropdownComponent employmentStatus;
    private final AutocompleteComponent employeeName;

    public EmployeeListPage(WebDriver driver) {
        super(driver);
        this.table = new DataTableComponent(driver);
        this.employmentStatus = new DropdownComponent(driver, "Employment Status");
        this.employeeName = new AutocompleteComponent(driver,
                By.cssSelector(".oxd-table-filter .oxd-autocomplete-text-input input"));
    }

    /**
     * Opens the list by URL. Used for setup and cleanup, where navigation through the UI is not under test.
     */
    public static EmployeeListPage open(WebDriver driver) {
        EmployeeListPage page = new EmployeeListPage(driver);
        page.log.info("Open employee list");
        driver.get(ConfigReader.baseUrl() + PATH);
        return page;
    }

    public DataTableComponent table() {
        return table;
    }

    public AutocompleteComponent employeeName() {
        return employeeName;
    }

    public AddEmployeePage clickAdd() {
        log.info("Click Add");
        click(addButton);
        return new AddEmployeePage(driver);
    }

    /**
     * Searches by typed (partial) name without picking an autocomplete suggestion.
     */
    public EmployeeListPage searchByName(String name) {
        log.info("Search employees by name '{}'", name);
        employeeName.type(name);
        return clickSearch();
    }

    public EmployeeListPage searchByEmployeeId(String employeeId) {
        log.info("Search employees by Employee Id {}", employeeId);
        type(employeeIdInput, employeeId);
        return clickSearch();
    }

    public EmployeeListPage filterByEmploymentStatus(String status) {
        log.info("Filter employees by Employment Status '{}'", status);
        employmentStatus.select(status);
        return clickSearch();
    }

    /**
     * Deletes the employee in the row with the given Employee Id and waits for the table to reload.
     *
     * @return the confirmation toast message, read before the toast disappears
     */
    public String deleteEmployee(String employeeId) {
        log.info("Delete employee {} from its row", employeeId);
        return table.reloadAfter(() -> {
            table.clickDeleteInRow(employeeId).confirm();
            return toast().getMessage();
        });
    }

    /**
     * Ticks the rows with the given Employee Ids, deletes them with "Delete Selected" and waits for the table
     * to reload.
     *
     * @return the confirmation toast message
     */
    public String deleteSelectedEmployees(String... employeeIds) {
        log.info("Delete selected employees {}", Arrays.toString(employeeIds));
        for (String employeeId : employeeIds) {
            table.selectRow(employeeId);
        }
        return table.reloadAfter(() -> {
            table.clickDeleteSelected().confirm();
            return toast().getMessage();
        });
    }

    /**
     * Applies the current filters and waits for the table to reload.
     */
    public EmployeeListPage clickSearch() {
        table.reloadAfter(() -> click(searchButton));
        return this;
    }
}

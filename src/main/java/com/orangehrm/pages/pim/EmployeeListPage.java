package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.components.AutocompleteComponent;
import com.orangehrm.components.DataTableComponent;
import com.orangehrm.components.DropdownComponent;
import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        driver.get(ConfigReader.baseUrl() + PATH);
        return new EmployeeListPage(driver);
    }

    public DataTableComponent table() {
        return table;
    }

    public AutocompleteComponent employeeName() {
        return employeeName;
    }

    public AddEmployeePage clickAdd() {
        click(addButton);
        return new AddEmployeePage(driver);
    }

    /**
     * Searches by typed (partial) name without picking an autocomplete suggestion.
     */
    public EmployeeListPage searchByName(String name) {
        employeeName.type(name);
        return clickSearch();
    }

    public EmployeeListPage searchByEmployeeId(String employeeId) {
        type(employeeIdInput, employeeId);
        return clickSearch();
    }

    public EmployeeListPage filterByEmploymentStatus(String status) {
        employmentStatus.select(status);
        return clickSearch();
    }

    /**
     * Deletes the employee in the row with the given Employee Id and waits for the table to reload.
     *
     * @return the confirmation toast message, read before the toast disappears
     */
    public String deleteEmployee(String employeeId) {
        return table.reloadAfter(() -> {
            table.clickDeleteInRow(employeeId).confirm();
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

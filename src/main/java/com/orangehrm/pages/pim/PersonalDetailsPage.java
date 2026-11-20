package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.models.Employee;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PersonalDetailsPage extends AuthenticatedPage {

    private static final String URL_FRAGMENT = "/pim/viewPersonalDetails";

    private final By firstNameInput = By.name("firstName");
    private final By middleNameInput = By.name("middleName");
    private final By lastNameInput = By.name("lastName");
    private final By employeeIdInput = inputByLabel("Employee Id");

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(firstNameInput);
    }

    /**
     * Reads the employee shown in the form. The form is rendered empty first and filled asynchronously,
     * so this waits for the required First Name to be populated.
     *
     * @throws IllegalStateException if the browser is not on the Personal Details page, e.g. because saving
     *                               a new employee has not finished. The Add Employee form uses the same
     *                               field locators, so reading without this check would return stale input.
     */
    public Employee getEmployee() {
        if (!waitForUrlContains(URL_FRAGMENT)) {
            throw new IllegalStateException("Personal Details page is not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
        wait.until(d -> !getValue(firstNameInput).isEmpty());
        return new Employee(
                getValue(firstNameInput),
                getValue(middleNameInput),
                getValue(lastNameInput),
                getValue(employeeIdInput));
    }
}

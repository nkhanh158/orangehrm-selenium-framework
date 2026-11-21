package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.models.Employee;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class PersonalDetailsPage extends AuthenticatedPage {

    private static final String URL_FRAGMENT = "/pim/viewPersonalDetails";

    private final By firstNameInput = By.name("firstName");
    private final By middleNameInput = By.name("middleName");
    private final By lastNameInput = By.name("lastName");
    private static final String DEFAULT_PICTURE_PATH = "/web/images/default-photo.png";
    private static final String SAME_IMAGE_SCRIPT = """
            const done = arguments[arguments.length - 1];
            const shown = document.querySelector('.orangehrm-edit-employee-image img').src;
            const bytes = url => fetch(url).then(r => r.arrayBuffer()).then(b => new Uint8Array(b));
            Promise.all([bytes(shown), bytes(arguments[0])])
              .then(([a, b]) => done(a.length === b.length && a.every((value, i) => value === b[i])))
              .catch(error => done(String(error)));
            """;

    private final By employeeIdInput = inputByLabel("Employee Id");
    private final By profilePicture = By.cssSelector(".orangehrm-edit-employee-image img");

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Compares the displayed picture with OrangeHRM's default photo byte by byte. The URL and size cannot tell
     * them apart: every employee's picture is served from {@code /pim/viewPhoto/...} and scaled to 200x200.
     * Both files are fetched inside the browser so that the request uses the logged-in session.
     */
    public boolean hasDefaultProfilePicture() {
        waitForVisible(profilePicture);
        Object same = ((JavascriptExecutor) driver).executeAsyncScript(SAME_IMAGE_SCRIPT, DEFAULT_PICTURE_PATH);
        if (!(same instanceof Boolean result)) {
            throw new IllegalStateException("Cannot compare profile pictures: " + same);
        }
        return result;
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

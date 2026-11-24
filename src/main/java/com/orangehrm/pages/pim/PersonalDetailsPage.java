package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.components.DatePickerComponent;
import com.orangehrm.components.DropdownComponent;
import com.orangehrm.models.Employee;
import com.orangehrm.models.Gender;
import com.orangehrm.models.PersonalDetails;
import com.orangehrm.utils.XPathUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.Arrays;

public class PersonalDetailsPage extends AuthenticatedPage {

    private static final String URL_FRAGMENT = "/pim/viewPersonalDetails";
    private static final String DEFAULT_PICTURE_PATH = "/web/images/default-photo.png";
    private static final String SAME_IMAGE_SCRIPT = """
            const done = arguments[arguments.length - 1];
            const shown = document.querySelector('.orangehrm-edit-employee-image img').src;
            const bytes = url => fetch(url).then(r => r.arrayBuffer()).then(b => new Uint8Array(b));
            Promise.all([bytes(shown), bytes(arguments[0])])
              .then(([a, b]) => done(a.length === b.length && a.every((value, i) => value === b[i])))
              .catch(error => done(String(error)));
            """;

    private final By firstNameInput = By.name("firstName");
    private final By middleNameInput = By.name("middleName");
    private final By lastNameInput = By.name("lastName");
    private final By employeeIdInput = inputByLabel("Employee Id");
    private final By otherIdInput = inputByLabel("Other Id");
    private final By profilePicture = By.cssSelector(".orangehrm-edit-employee-image img");
    // The page has a second form (Custom Fields) with its own Save button.
    private final By saveButton = By.xpath("//form[.//input[@name='firstName']]//button[@type='submit']");

    private final DropdownComponent nationality;
    private final DropdownComponent maritalStatus;
    private final DatePickerComponent dateOfBirth;

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
        this.nationality = new DropdownComponent(driver, "Nationality");
        this.maritalStatus = new DropdownComponent(driver, "Marital Status");
        this.dateOfBirth = new DatePickerComponent(driver, "Date of Birth");
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
        waitForSpinnerToDisappear();
        return new Employee(
                getValue(firstNameInput),
                getValue(middleNameInput),
                getValue(lastNameInput),
                getValue(employeeIdInput));
    }

    public PersonalDetails getPersonalDetails() {
        Employee employee = getEmployee();
        return new PersonalDetails(
                employee,
                getValue(otherIdInput),
                nationality.getSelected(),
                maritalStatus.getSelected(),
                dateOfBirth.get(),
                getSelectedGender());
    }

    @Step("Enter Middle Name '{middleName}'")
    public PersonalDetailsPage enterMiddleName(String middleName) {
        type(middleNameInput, middleName);
        return this;
    }

    @Step("Enter Date of Birth {date}")
    public PersonalDetailsPage enterDateOfBirth(LocalDate date) {
        dateOfBirth.set(date);
        return this;
    }

    /**
     * Clicks the label: the radio input itself is hidden behind a styled span.
     */
    @Step("Select Gender {gender}")
    public PersonalDetailsPage selectGender(Gender gender) {
        click(genderLabel(gender));
        return this;
    }

    /**
     * Saves the Personal Details form.
     *
     * @return the confirmation toast message, which is the signal that the update has been stored
     */
    @Step("Save personal details")
    public String save() {
        click(saveButton);
        return toast().getMessage();
    }

    /**
     * Reloads the page so that values are read back from the server rather than from the edited form.
     */
    @Step("Reload page")
    public PersonalDetailsPage reload() {
        driver.navigate().refresh();
        return new PersonalDetailsPage(driver);
    }

    private Gender getSelectedGender() {
        return Arrays.stream(Gender.values())
                .filter(gender -> driver.findElement(genderRadio(gender)).isSelected())
                .findFirst()
                .orElse(null);
    }

    private static By genderLabel(Gender gender) {
        return By.xpath(fieldGroup("Gender") + "//label[normalize-space()=" + XPathUtils.literal(gender.label()) + "]");
    }

    private static By genderRadio(Gender gender) {
        return By.xpath(fieldGroup("Gender") + "//label[normalize-space()=" + XPathUtils.literal(gender.label())
                + "]//input[@type='radio']");
    }
}

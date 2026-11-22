package com.orangehrm.pages.pim;

import com.orangehrm.base.AuthenticatedPage;
import com.orangehrm.models.Employee;
import com.orangehrm.models.LoginDetails;
import com.orangehrm.utils.XPathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;

public class AddEmployeePage extends AuthenticatedPage {

    private static final String URL_FRAGMENT = "/pim/addEmployee";

    /**
     * Fields that show their own validation message. First, Middle and Last Name share one label,
     * so each field is located by its input rather than by label.
     */
    public enum Field {
        FIRST_NAME("//input[@name='firstName']"),
        MIDDLE_NAME("//input[@name='middleName']"),
        LAST_NAME("//input[@name='lastName']"),
        EMPLOYEE_ID(fieldGroup("Employee Id") + "//input");

        private final String inputXPath;

        Field(String inputXPath) {
            this.inputXPath = inputXPath;
        }

        private By input() {
            return By.xpath(inputXPath);
        }

        /**
         * Validation message of the field, found in the closest input group around its input.
         * {@code [1]} picks the nearest ancestor because the three name inputs share one outer group.
         */
        private By error() {
            return By.xpath(inputXPath + "/ancestor::div[" + XPathUtils.hasClass("oxd-input-group") + "][1]"
                    + "//span[contains(@class,'oxd-input-field-error-message')]");
        }
    }

    private final By pictureInput = By.cssSelector(".orangehrm-employee-image input[type='file']");
    private final By picturePreview = By.cssSelector(".orangehrm-employee-image img.employee-image");
    private final By loginDetailsSwitch = By.xpath(
            "//p[normalize-space()='Create Login Details']/following-sibling::div//span[contains(@class,'oxd-switch-input')]");
    private final By usernameInput = inputByLabel("Username");
    private final By passwordInput = inputByLabel("Password");
    private final By confirmPasswordInput = inputByLabel("Confirm Password");
    private final By saveButton = By.cssSelector("button[type='submit']");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public PersonalDetailsPage addEmployee(Employee employee) {
        return enterEmployee(employee).save();
    }

    public AddEmployeePage enterEmployee(Employee employee) {
        enter(Field.FIRST_NAME, employee.firstName());
        enter(Field.MIDDLE_NAME, employee.middleName());
        enter(Field.LAST_NAME, employee.lastName());
        if (!employee.employeeId().isEmpty()) {
            enter(Field.EMPLOYEE_ID, employee.employeeId());
        }
        return this;
    }

    public AddEmployeePage enter(Field field, String value) {
        type(field.input(), value);
        return this;
    }

    /**
     * Selects a picture through the hidden file input; sending the path is how a user's file choice is simulated.
     */
    public AddEmployeePage uploadPicture(Path picture) {
        driver.findElement(pictureInput).sendKeys(picture.toString());
        return this;
    }

    /**
     * True once the selected picture is shown; the preview is an inline data URL instead of the default image.
     */
    public boolean isPicturePreviewed() {
        try {
            return wait.until(d -> getAttribute(picturePreview, "src").startsWith("data:image"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public AddEmployeePage enterLoginDetails(LoginDetails login) {
        click(loginDetailsSwitch);
        type(usernameInput, login.username());
        type(passwordInput, login.password());
        type(confirmPasswordInput, login.password());
        return this;
    }

    public PersonalDetailsPage save() {
        click(saveButton);
        return new PersonalDetailsPage(driver);
    }

    /**
     * Clicks Save when validation is expected to keep the form open.
     */
    public AddEmployeePage saveExpectingErrors() {
        click(saveButton);
        return this;
    }

    /**
     * Waits for the validation message of the field. Messages may come from the server, e.g. a duplicate
     * Employee Id, so they can appear after a short delay.
     */
    public String getError(Field field) {
        return getText(field.error());
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(Field.FIRST_NAME.input());
    }
}

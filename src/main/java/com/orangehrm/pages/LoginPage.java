package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import com.orangehrm.models.LoginErrors;
import com.orangehrm.utils.XPathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class LoginPage extends BasePage {

    private static final String URL_FRAGMENT = "/auth/login";
    private static final String DASHBOARD_URL_FRAGMENT = "/dashboard";

    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By forgotPasswordLink = By.cssSelector(".orangehrm-login-forgot p");
    private final By alertMessage = By.cssSelector(".oxd-alert-content-text");
    private final By anyFieldError = By.cssSelector(".oxd-input-field-error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Logs in and waits for the redirect to the Dashboard. Navigating away earlier would cancel the login request.
     *
     * @throws IllegalStateException if the Dashboard is not reached, e.g. because the credentials were rejected
     */
    public DashboardPage loginAs(String username, String password) {
        enterCredentials(username, password);
        if (!waitForUrlContains(DASHBOARD_URL_FRAGMENT)) {
            throw new IllegalStateException("Login as '" + username + "' did not reach the Dashboard. Current URL: "
                    + driver.getCurrentUrl());
        }
        return new DashboardPage(driver);
    }

    public LoginPage loginExpectingError(String username, String password) {
        enterCredentials(username, password);
        return this;
    }

    /**
     * Waits until the attempt has been rejected, either by the server (alert) or by client-side
     * validation (field errors), then reads every error currently shown.
     */
    public LoginErrors getErrors() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(alertMessage),
                ExpectedConditions.visibilityOfElementLocated(anyFieldError)));
        return new LoginErrors(textIfPresent(alertMessage), textIfPresent(fieldError("Username")),
                textIfPresent(fieldError("Password")));
    }

    public ForgotPasswordPage clickForgotPassword() {
        click(forgotPasswordLink);
        return new ForgotPasswordPage(driver);
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(loginButton);
    }

    private void enterCredentials(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }

    private String textIfPresent(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.isEmpty() ? "" : elements.get(0).getText().trim();
    }

    private static By fieldError(String label) {
        return By.xpath("//label[normalize-space()=" + XPathUtils.literal(label)
                + "]/ancestor::div[contains(@class,'oxd-input-group')]"
                + "//span[contains(@class,'oxd-input-field-error-message')]");
    }
}

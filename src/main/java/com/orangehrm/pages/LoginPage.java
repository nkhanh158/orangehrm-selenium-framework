package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final String URL_FRAGMENT = "/auth/login";

    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage loginAs(String username, String password) {
        enterCredentials(username, password);
        return new DashboardPage(driver);
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(loginButton);
    }

    private void enterCredentials(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }
}

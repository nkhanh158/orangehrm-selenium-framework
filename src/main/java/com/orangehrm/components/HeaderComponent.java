package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Top bar shared by every page after login.
 */
public class HeaderComponent extends BasePage {

    private static final String LOGIN_URL_FRAGMENT = "/auth/login";

    private final By userDropdown = By.cssSelector(".oxd-userdropdown-tab");
    private final By userName = By.cssSelector(".oxd-userdropdown-name");
    private final By logoutLink = By.xpath("//ul[contains(@class,'oxd-dropdown-menu')]//a[normalize-space()='Logout']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    /**
     * Name of the logged-in user as shown in the top bar, e.g. "John Doe".
     */
    public String getUserName() {
        return getText(userName);
    }

    /**
     * Logs out and waits until the server has ended the session and redirected to the Login page.
     * Navigating away earlier would cancel the logout request.
     */
    public LoginPage logout() {
        click(userDropdown);
        click(logoutLink);
        wait.until(ExpectedConditions.urlContains(LOGIN_URL_FRAGMENT));
        return new LoginPage(driver);
    }
}

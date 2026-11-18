package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Top bar shared by every page after login.
 */
public class HeaderComponent extends BasePage {

    private final By userDropdown = By.cssSelector(".oxd-userdropdown-tab");
    private final By logoutLink = By.xpath("//ul[contains(@class,'oxd-dropdown-menu')]//a[normalize-space()='Logout']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public LoginPage logout() {
        click(userDropdown);
        click(logoutLink);
        return new LoginPage(driver);
    }
}

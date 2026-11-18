package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import com.orangehrm.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private static final String URL_FRAGMENT = "/dashboard";

    private final By pageTitle = By.cssSelector(".oxd-topbar-header-breadcrumb h6");

    private final HeaderComponent header;

    public DashboardPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(pageTitle);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }
}

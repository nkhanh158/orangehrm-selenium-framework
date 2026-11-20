package com.orangehrm.pages;

import com.orangehrm.base.AuthenticatedPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends AuthenticatedPage {

    private static final String URL_FRAGMENT = "/dashboard";

    private final By pageTitle = By.cssSelector(".oxd-topbar-header-breadcrumb h6");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(pageTitle);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }
}

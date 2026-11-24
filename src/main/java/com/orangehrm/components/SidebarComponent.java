package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.pages.pim.EmployeeListPage;
import com.orangehrm.utils.XPathUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Side menu shared by every page after login. All menu locators live here.
 */
public class SidebarComponent extends BasePage {

    private final By searchInput = By.cssSelector(".oxd-main-menu-search input");
    private final By menuItemNames = By.cssSelector(".oxd-main-menu-item--name");

    public SidebarComponent(WebDriver driver) {
        super(driver);
    }

    public EmployeeListPage goToPim() {
        openMenu("PIM");
        return new EmployeeListPage(driver);
    }

    @Step("Search menu for '{text}'")
    public SidebarComponent search(String text) {
        type(searchInput, text);
        return this;
    }

    public List<String> getMenuItems() {
        waitForVisible(searchInput);
        return getTexts(menuItemNames);
    }

    @Step("Open menu {name}")
    private void openMenu(String name) {
        click(By.xpath("//a[contains(@class,'oxd-main-menu-item')][.//span[normalize-space()="
                + XPathUtils.literal(name) + "]]"));
    }
}

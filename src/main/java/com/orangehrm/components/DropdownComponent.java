package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.utils.XPathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * OrangeHRM select box identified by its field label, e.g. "Employment Status".
 */
public class DropdownComponent extends BasePage {

    private final By selectedText;

    public DropdownComponent(WebDriver driver, String label) {
        super(driver);
        this.selectedText = By.xpath("//label[normalize-space()=" + XPathUtils.literal(label)
                + "]/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
    }

    public void select(String option) {
        selectCustomDropdown(selectedText, option);
    }

    public String getSelected() {
        return getText(selectedText);
    }
}

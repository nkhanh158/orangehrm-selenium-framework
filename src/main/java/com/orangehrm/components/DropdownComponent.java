package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * OrangeHRM select box identified by its field label, e.g. "Employment Status".
 */
public class DropdownComponent extends BasePage {

    private static final String NO_SELECTION = "-- Select --";

    private final By selectedText;

    public DropdownComponent(WebDriver driver, String label) {
        super(driver);
        this.selectedText = By.xpath(fieldGroup(label) + "//div[contains(@class,'oxd-select-text-input')]");
    }

    public void select(String option) {
        log.debug("Select option '{}'", option);
        selectCustomDropdown(selectedText, option);
    }

    /**
     * Selected option, or an empty string when the placeholder "-- Select --" is shown.
     */
    public String getSelected() {
        String text = getText(selectedText);
        return NO_SELECTION.equals(text) ? "" : text;
    }
}

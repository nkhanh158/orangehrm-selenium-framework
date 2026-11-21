package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.utils.XPathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * OrangeHRM autocomplete input, e.g. "Employee Name". Suggestions are loaded from the server after typing;
 * a "Searching...." placeholder option is shown until they arrive.
 */
public class AutocompleteComponent extends BasePage {

    private static final String SEARCHING = "Searching....";
    private static final String OPTION = "//div[@role='listbox']//div[@role='option']";

    private final By input;
    private final By options = By.xpath(OPTION);

    public AutocompleteComponent(WebDriver driver, By input) {
        super(driver);
        this.input = input;
    }

    public AutocompleteComponent type(String text) {
        type(input, text);
        return this;
    }

    /**
     * Suggestions currently offered, waiting until the server has answered.
     */
    public List<String> getSuggestions() {
        return wait.until(driver -> {
            List<String> texts = getTexts(options);
            return texts.isEmpty() || texts.contains(SEARCHING) ? null : texts;
        });
    }

    public void select(String suggestion) {
        getSuggestions();
        click(By.xpath(OPTION + "[normalize-space()=" + XPathUtils.literal(suggestion) + "]"));
    }
}

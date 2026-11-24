package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * OrangeHRM date field identified by its label, e.g. "Date of Birth".
 * <p>
 * The date format is an application setting (Admin > Localization) and the input shows it as its placeholder,
 * e.g. {@code yyyy-dd-mm}. Dates are typed and read in that format, so the component keeps working when the
 * setting changes.
 */
public class DatePickerComponent extends BasePage {

    private final By input;

    public DatePickerComponent(WebDriver driver, String label) {
        super(driver);
        this.input = By.xpath(fieldGroup(label) + "//div[contains(@class,'oxd-date-input')]//input");
    }

    public void set(LocalDate date) {
        String text = format().format(date);
        log.debug("Set date {} as '{}'", date, text);
        type(input, text);
    }

    /**
     * Date shown in the field, or {@code null} when it is empty.
     */
    public LocalDate get() {
        String value = getValue(input);
        return value.isEmpty() ? null : LocalDate.parse(value, format());
    }

    /**
     * Converts the placeholder to a Java pattern. In the placeholder {@code mm} means month, which is
     * {@code MM} in Java ({@code mm} would be minutes).
     */
    private DateTimeFormatter format() {
        return DateTimeFormatter.ofPattern(getAttribute(input, "placeholder").replace("mm", "MM"));
    }
}

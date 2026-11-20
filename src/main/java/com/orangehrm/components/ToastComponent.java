package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Notification shown in the bottom-left corner after an action. It disappears after a few seconds,
 * so it must be read right after the action that triggers it.
 */
public class ToastComponent extends BasePage {

    private final By messages = By.cssSelector(".oxd-toast .oxd-text--toast-message");

    public ToastComponent(WebDriver driver) {
        super(driver);
    }

    /**
     * Waits for a toast and returns the text of the most recent one.
     */
    public String getMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(messages));
        List<String> texts = getTexts(messages);
        return texts.isEmpty() ? "" : texts.get(texts.size() - 1);
    }
}

package com.orangehrm.base;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Common UI actions. Every action waits explicitly for the element state it needs;
 * implicit waits are never used.
 */
public abstract class BasePage {

    private static final By LOADERS = By.cssSelector(".oxd-loading-spinner, .oxd-form-loader");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.timeoutSeconds()));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitForSpinnerToDisappear();
        wait.until(WaitUtils.clickSucceeds(locator));
    }

    /**
     * Replaces the field value. Select-all + delete is used instead of {@link WebElement#clear()}
     * because clear() does not trigger the input events Vue listens to.
     */
    protected void type(By locator, String text) {
        waitForSpinnerToDisappear();
        WebElement element = waitForVisible(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return element.isDisplayed() ? element.getText().trim() : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected List<String> getTexts(By locator) {
        return wait.until(WaitUtils.textsOf(locator));
    }

    protected String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getDomAttribute(attribute);
    }

    protected boolean isDisplayed(By locator) {
        try {
            waitForVisible(locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isNotDisplayed(By locator) {
        try {
            return wait.until(WaitUtils.noVisibleElements(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean waitForUrlContains(String fragment) {
        try {
            return wait.until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void waitForSpinnerToDisappear() {
        wait.until(WaitUtils.noVisibleElements(LOADERS));
    }

    protected void scrollIntoView(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }
}

package com.orangehrm.base;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.utils.WaitUtils;
import com.orangehrm.utils.XPathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    // Named after the concrete page or component, e.g. "LoginPage".
    protected final Logger log = LogManager.getLogger(getClass());
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
        log.debug("Click {}", locator);
        waitForSpinnerToDisappear();
        wait.until(WaitUtils.clickSucceeds(locator));
    }

    /**
     * Replaces the field value. Select-all + delete is used instead of {@link WebElement#clear()}
     * because clear() does not trigger the input events Vue listens to.
     * The value is not logged because it may be a password.
     */
    protected void type(By locator, String text) {
        log.debug("Type into {}", locator);
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

    /**
     * Current value of an input. Uses the DOM property because the HTML attribute does not change when the user types.
     */
    protected String getValue(By locator) {
        return waitForVisible(locator).getDomProperty("value");
    }

    /**
     * Selects an option of an OrangeHRM dropdown, which is built from divs rather than a native {@code <select>}.
     */
    protected void selectCustomDropdown(By dropdown, String option) {
        click(dropdown);
        click(By.xpath("//div[@role='listbox']//div[@role='option'][normalize-space()="
                + XPathUtils.literal(option) + "]"));
    }

    /**
     * XPath of the input group (label, control and validation message) of the form field with the given label.
     * The label sits inside {@code oxd-input-group__label-wrapper}, so the class is matched as a whole token,
     * and {@code [1]} picks the closest group because groups can be nested.
     */
    protected static String fieldGroup(String label) {
        return "//label[normalize-space()=" + XPathUtils.literal(label)
                + "]/ancestor::div[" + XPathUtils.hasClass("oxd-input-group") + "][1]";
    }

    /**
     * Input that belongs to the form field with the given label, e.g. "Employee Id".
     */
    protected static By inputByLabel(String label) {
        return By.xpath(fieldGroup(label) + "//input");
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

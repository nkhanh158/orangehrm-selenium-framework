package com.orangehrm.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

/**
 * Custom {@link ExpectedCondition}s for a Vue based UI that re-renders elements frequently.
 * <p>
 * A condition is re-evaluated until it returns a value other than {@code null} or {@code false},
 * so returning {@code null} from a catch block means "not ready yet, try again".
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    /**
     * Clicks the element once it is displayed and enabled, retrying when it goes stale
     * or is temporarily covered by an overlay.
     */
    public static ExpectedCondition<Boolean> clickSucceeds(By locator) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    if (!element.isDisplayed() || !element.isEnabled()) {
                        return false;
                    }
                    element.click();
                    return true;
                } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element to be clicked: " + locator;
            }
        };
    }

    /**
     * Waits until none of the elements matching the locator are visible. Also true when none exist.
     */
    public static ExpectedCondition<Boolean> noVisibleElements(By locator) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return driver.findElements(locator).stream().noneMatch(WebElement::isDisplayed);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "no visible elements: " + locator;
            }
        };
    }

    /**
     * Waits until the number of elements matching the locator differs from {@code previousCount},
     * e.g. table rows after a delete. Returns the new count.
     */
    public static ExpectedCondition<Integer> numberOfElementsToChangeFrom(By locator, int previousCount) {
        return new ExpectedCondition<>() {
            @Override
            public Integer apply(WebDriver driver) {
                int current = driver.findElements(locator).size();
                return current != previousCount ? current : null;
            }

            @Override
            public String toString() {
                return "number of elements " + locator + " to change from " + previousCount;
            }
        };
    }

    /**
     * Reads the visible text of all matching elements, re-reading the whole list if any element goes stale.
     */
    public static ExpectedCondition<List<String>> textsOf(By locator) {
        return new ExpectedCondition<>() {
            @Override
            public List<String> apply(WebDriver driver) {
                try {
                    return driver.findElements(locator).stream()
                            .map(element -> element.getText().trim())
                            .toList();
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "texts of " + locator;
            }
        };
    }
}

package com.orangehrm.failures;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.List;

/**
 * Finds server errors behind a failure. The browser's Resource Timing API keeps the status of the page and of every
 * request it made, including the API calls of the single page app, so a 5xx can be found after the fact.
 */
public final class ServerErrors {

    private static final int MAX_REPORTED = 5;
    private static final String SCRIPT = """
            return performance.getEntriesByType('navigation')
                .concat(performance.getEntriesByType('resource'))
                .filter(entry => entry.responseStatus >= 500)
                .map(entry => entry.responseStatus + ' ' + entry.name);
            """;

    private ServerErrors() {
    }

    /**
     * Returns e.g. "HTTP 500 https://host/web/index.php/api/v2/pim/employees" for each failed response of the
     * current page. Empty when there were none, or when the browser cannot tell: the browser has gone, or it does
     * not report response status (only Chromium based browsers do).
     */
    public static List<String> seenBy(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor javascript)) {
            return List.of();
        }
        try {
            Object result = javascript.executeScript(SCRIPT);
            if (!(result instanceof List<?> entries)) {
                return List.of();
            }
            return entries.stream().limit(MAX_REPORTED).map(entry -> "HTTP " + entry).toList();
        } catch (WebDriverException e) {
            return List.of();
        }
    }
}

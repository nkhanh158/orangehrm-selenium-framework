package com.orangehrm.listeners;

import com.orangehrm.base.BaseTest;
import com.orangehrm.failures.FailureClassification;
import com.orangehrm.failures.FailureType;
import com.orangehrm.failures.ServerErrors;
import com.orangehrm.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Logs the start and outcome of every test. When a test fails, classifies the failure and, for a UI test, attaches
 * evidence to the Allure report.
 */
public class TestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("START {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASS  {} ({} ms)", testName(result), duration(result));
    }

    /**
     * A failed attempt that is run again is reported as skipped. {@link RetryAnalyzer} has already logged it.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        if (!result.wasRetried()) {
            LOG.warn("SKIP  {}: {}", testName(result), reason(result));
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String type = FailureClassification.recordedOn(result.getThrowable())
                .map(FailureType::label)
                .orElse("Unclassified");
        LOG.error("FAIL  {} ({} ms): {}", testName(result), duration(result), type, result.getThrowable());
    }

    /**
     * Runs right after the test method, while its browser is still open and before the retry decision and
     * {@link #onTestFailure}. Evidence cannot be attached in onTestFailure: Allure's own listener may already have
     * closed the test result by then.
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod() || result.getStatus() != ITestResult.FAILURE || result.getThrowable() == null) {
            return;
        }
        WebDriver driver = result.getInstance() instanceof BaseTest test ? test.getDriver() : null;
        FailureClassification.record(result.getThrowable(),
                () -> driver == null ? List.of() : ServerErrors.seenBy(driver));
        if (driver != null) {
            attachEvidence(driver, result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName());
        }
    }

    /**
     * Attaches what the browser showed when the test failed: a screenshot, the URL and the page source (the DOM,
     * which shows why a locator did not match). Each piece is captured separately, so one failing capture,
     * e.g. because the browser has crashed, does not prevent the others.
     */
    private static void attachEvidence(WebDriver driver, String name) {
        try {
            Path screenshot = ScreenshotUtils.capture(driver, name);
            try (InputStream image = Files.newInputStream(screenshot)) {
                Allure.addAttachment("Screenshot", "image/png", image, ".png");
            }
            LOG.info("Screenshot saved: {}", screenshot);
        } catch (WebDriverException | IOException e) {
            LOG.warn("Could not capture screenshot for {}: {}", name, e.getMessage());
        }
        try {
            Allure.addAttachment("URL", "text/uri-list", driver.getCurrentUrl());
            Allure.addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
        } catch (WebDriverException e) {
            LOG.warn("Could not read URL or page source for {}: {}", name, e.getMessage());
        }
    }

    /**
     * "LoginTest.shouldRejectInvalidLogin [Empty password]": parameters identify the data set of a data-driven test.
     */
    static String testName(ITestResult result) {
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();
        return parameters.length == 0 ? name : name + " " + Arrays.toString(parameters);
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    private static String reason(ITestResult result) {
        Throwable throwable = result.getThrowable();
        return throwable == null ? "no reason given" : throwable.getMessage();
    }
}

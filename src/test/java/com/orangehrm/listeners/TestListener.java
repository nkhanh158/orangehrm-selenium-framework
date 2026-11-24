package com.orangehrm.listeners;

import com.orangehrm.base.BaseTest;
import com.orangehrm.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Logs the start and outcome of every test and captures a screenshot when a UI test fails.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("START {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASS  {} ({} ms)", testName(result), duration(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIP  {}: {}", testName(result), reason(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAIL  {} ({} ms)", testName(result), duration(result), result.getThrowable());
        captureScreenshot(result);
    }

    private static void captureScreenshot(ITestResult result) {
        if (!(result.getInstance() instanceof BaseTest test)) {
            return;
        }
        WebDriver driver = test.getDriver();
        if (driver == null) {
            return;
        }
        String name = result.getTestClass().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName();
        try {
            Path screenshot = ScreenshotUtils.capture(driver, name);
            LOG.info("Screenshot saved: {}", screenshot);
        } catch (WebDriverException e) {
            LOG.warn("Could not capture screenshot for {}: {}", name, e.getMessage());
        }
    }

    /**
     * "LoginTest.shouldRejectInvalidLogin [Empty password]": parameters identify the data set of a data-driven test.
     */
    private static String testName(ITestResult result) {
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

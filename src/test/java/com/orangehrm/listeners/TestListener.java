package com.orangehrm.listeners;

import com.orangehrm.base.BaseTest;
import com.orangehrm.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.nio.file.Path;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
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
            Reporter.log("Screenshot saved: " + screenshot, true);
        } catch (WebDriverException e) {
            Reporter.log("Could not capture screenshot for " + name + ": " + e.getMessage(), true);
        }
    }
}

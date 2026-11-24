package com.orangehrm.base;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.driver.DriverFactory;
import com.orangehrm.listeners.AllureResultsListener;
import com.orangehrm.listeners.TestListener;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.ArrayDeque;
import java.util.Deque;

@Listeners({TestListener.class, AllureResultsListener.class})
public abstract class BaseTest {

    private static final Logger LOG = LogManager.getLogger(BaseTest.class);
    private static final String LOGOUT_PATH = "/web/index.php/auth/logout";

    protected WebDriver driver;

    private final Deque<Runnable> cleanupActions = new ArrayDeque<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        cleanupActions.clear();
        driver = DriverFactory.createDriver(ConfigReader.browser(), ConfigReader.headless());
        driver.get(ConfigReader.baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        runCleanupActions();
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected DashboardPage loginAsAdmin() {
        return new LoginPage(driver).loginAs(ConfigReader.adminUsername(), ConfigReader.adminPassword());
    }

    /**
     * Ends the current session, whoever is logged in, and logs in as Admin. Used by cleanup after a test
     * has switched to a user without admin rights.
     * <p>
     * The session is ended through the logout URL rather than by deleting cookies: after deleting them,
     * every second login failed with "CSRF token validation failed".
     */
    protected DashboardPage startNewAdminSession() {
        LOG.info("End the current session and log in as Admin");
        driver.get(ConfigReader.baseUrl() + LOGOUT_PATH);
        return loginAsAdmin();
    }

    /**
     * Registers an action that removes data created by the test. Actions run after the test, in reverse order,
     * even when the test fails. A failing cleanup is reported but never hides the test result.
     */
    protected void cleanupAfterTest(Runnable action) {
        cleanupActions.push(action);
    }

    private void runCleanupActions() {
        if (!cleanupActions.isEmpty()) {
            LOG.info("Cleanup: {} action(s)", cleanupActions.size());
        }
        while (!cleanupActions.isEmpty()) {
            try {
                cleanupActions.pop().run();
            } catch (RuntimeException e) {
                LOG.warn("Cleanup failed", e);
            }
        }
    }
}

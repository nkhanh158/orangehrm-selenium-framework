package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.data.JsonReader;
import com.orangehrm.models.LoginAttempt;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseTest {

    private static final String INVALID_LOGIN_DATA = "testdata/login-data.json";

    @Test(groups = "smoke", description = "AUTH-01: Login with valid admin credentials")
    public void shouldOpenDashboardWhenCredentialsAreValid() {
        DashboardPage dashboard = new LoginPage(driver)
                .loginAs(ConfigReader.adminUsername(), ConfigReader.adminPassword());

        assertTrue(dashboard.isLoaded(), "Dashboard page should be loaded after login");
        assertEquals(dashboard.getPageTitle(), "Dashboard");
    }

    @Test(groups = "regression", dataProvider = "invalidLogins", description = "AUTH-02: Login with invalid credentials")
    public void shouldRejectInvalidLogin(LoginAttempt attempt) {
        LoginPage loginPage = new LoginPage(driver)
                .loginExpectingError(resolve(attempt.username()), resolve(attempt.password()));

        assertEquals(loginPage.getErrors(), attempt.expected());
        assertTrue(loginPage.isLoaded(), "User should stay on the Login page");
    }

    @DataProvider
    public Object[][] invalidLogins() {
        return JsonReader.readList(INVALID_LOGIN_DATA, LoginAttempt.class).stream()
                .map(attempt -> new Object[] {attempt})
                .toArray(Object[][]::new);
    }

    /**
     * Replaces placeholders so the data file holds no environment-specific credentials.
     */
    private static String resolve(String value) {
        Map<String, String> placeholders = Map.of(
                "${adminUsername}", ConfigReader.adminUsername(),
                "${adminPasswordUpperCase}", ConfigReader.adminPassword().toUpperCase(Locale.ROOT),
                "${adminPassword}", ConfigReader.adminPassword());
        String resolved = value;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            resolved = resolved.replace(placeholder.getKey(), placeholder.getValue());
        }
        return resolved;
    }
}

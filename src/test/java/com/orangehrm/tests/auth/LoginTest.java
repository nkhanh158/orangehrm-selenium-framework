package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseTest {

    @Test(groups = "smoke", description = "AUTH-01: Login with valid admin credentials")
    public void shouldOpenDashboardWhenCredentialsAreValid() {
        DashboardPage dashboard = new LoginPage(driver)
                .loginAs(ConfigReader.adminUsername(), ConfigReader.adminPassword());

        assertTrue(dashboard.isLoaded(), "Dashboard page should be loaded after login");
        assertEquals(dashboard.getPageTitle(), "Dashboard");
    }

    @Test(groups = "smoke", description = "AUTH-03: Logout from the header menu")
    public void shouldReturnToLoginPageAfterLogout() {
        LoginPage loginPage = new LoginPage(driver)
                .loginAs(ConfigReader.adminUsername(), ConfigReader.adminPassword())
                .header()
                .logout();

        assertTrue(loginPage.isLoaded(), "Login page should be displayed after logout");
    }
}

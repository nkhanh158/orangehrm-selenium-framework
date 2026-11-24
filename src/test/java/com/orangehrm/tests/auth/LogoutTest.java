package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LogoutTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "AUTH-03: Logout from the header menu")
    public void shouldReturnToLoginPageAfterLogout() {
        LoginPage loginPage = loginAsAdmin().header().logout();

        assertTrue(loginPage.isLoaded(), "Login page should be displayed after logout");
    }

    @Test(groups = "regression", description = "AUTH-04: Browser Back after logout")
    public void shouldNotReturnToInternalPageWithBrowserBackAfterLogout() {
        loginAsAdmin().header().logout();

        driver.navigate().back();
        // Back may show the page from the browser's back/forward cache without contacting the server.
        // Reloading makes the server decide whether the session is still valid.
        driver.navigate().refresh();

        assertTrue(new LoginPage(driver).isLoaded(), "Internal page should not be accessible after logout");
    }
}

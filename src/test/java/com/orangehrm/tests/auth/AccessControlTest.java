package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.pim.EmployeeListPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class AccessControlTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "AUTH-05: Direct access to an internal page without a session")
    public void shouldRedirectToLoginWhenOpeningInternalPageWithoutSession() {
        EmployeeListPage.open(driver);

        assertTrue(new LoginPage(driver).isLoaded(), "Unauthenticated user should be redirected to the Login page");
    }
}

package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.pim.EmployeeListPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Feature("Authentication")
@Story("Access control")
public class AccessControlTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "AUTH-05: Direct access to an internal page without a session")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRedirectToLoginWhenOpeningInternalPageWithoutSession() {
        EmployeeListPage.open(driver);

        assertTrue(new LoginPage(driver).isLoaded(), "Unauthenticated user should be redirected to the Login page");
    }
}

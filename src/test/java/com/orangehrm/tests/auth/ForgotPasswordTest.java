package com.orangehrm.tests.auth;

import com.orangehrm.base.BaseTest;
import com.orangehrm.pages.ForgotPasswordPage;
import com.orangehrm.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Authentication")
@Story("Password reset")
public class ForgotPasswordTest extends BaseTest {

    @Test(groups = "regression", description = "AUTH-06: Forgot password link opens the Reset Password page")
    public void shouldOpenResetPasswordPageFromLoginPage() {
        ForgotPasswordPage forgotPassword = new LoginPage(driver).clickForgotPassword();

        assertTrue(forgotPassword.isLoaded(), "Reset Password page should open");
        assertEquals(forgotPassword.getTitle(), "Reset Password");
    }

    @Test(groups = "regression", description = "AUTH-06: Cancel on the Reset Password page returns to Login")
    public void shouldReturnToLoginPageWhenResetIsCancelled() {
        LoginPage loginPage = new LoginPage(driver).clickForgotPassword().cancel();

        assertTrue(loginPage.isLoaded(), "Login page should open after cancelling the reset");
    }
}

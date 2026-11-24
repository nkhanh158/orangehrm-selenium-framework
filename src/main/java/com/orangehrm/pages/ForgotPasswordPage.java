package com.orangehrm.pages;

import com.orangehrm.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ForgotPasswordPage extends BasePage {

    private static final String URL_FRAGMENT = "/auth/requestPasswordResetCode";

    private final By title = By.cssSelector(".orangehrm-forgot-password-title");
    private final By cancelButton = By.cssSelector(".orangehrm-forgot-password-button--cancel");

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return waitForUrlContains(URL_FRAGMENT) && isDisplayed(title);
    }

    public String getTitle() {
        return getText(title);
    }

    @Step("Cancel password reset")
    public LoginPage cancel() {
        click(cancelButton);
        return new LoginPage(driver);
    }
}

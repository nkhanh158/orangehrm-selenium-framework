package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * "Are you Sure?" dialog shown before deleting records.
 */
public class ConfirmDialogComponent extends BasePage {

    private final By dialog = By.cssSelector(".orangehrm-dialog-popup");
    private final By message = By.cssSelector(".orangehrm-dialog-popup .oxd-text--card-body");
    private final By confirmButton = By.cssSelector(".orangehrm-modal-footer .oxd-button--label-danger");
    private final By cancelButton = By.cssSelector(".orangehrm-modal-footer .oxd-button--ghost");

    public ConfirmDialogComponent(WebDriver driver) {
        super(driver);
    }

    public String getMessage() {
        return getText(message);
    }

    public void confirm() {
        log.debug("Confirm dialog");
        click(confirmButton);
        isNotDisplayed(dialog);
    }

    public void cancel() {
        log.debug("Cancel dialog");
        click(cancelButton);
        isNotDisplayed(dialog);
    }
}

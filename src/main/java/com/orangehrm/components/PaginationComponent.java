package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page navigation under a list table. It is rendered only when there is more than one page, and the
 * "next" arrow is missing on the last page.
 */
public class PaginationComponent extends BasePage {

    /**
     * Rows per page in OrangeHRM list tables.
     */
    public static final int PAGE_SIZE = 50;

    private final By navigation = By.cssSelector("nav[aria-label='Pagination Navigation']");
    private final By nextButton = By.xpath(
            "//nav[@aria-label='Pagination Navigation']//button[i[contains(@class,'bi-chevron-right')]]");

    private final DataTableComponent table;

    PaginationComponent(WebDriver driver, DataTableComponent table) {
        super(driver);
        this.table = table;
    }

    /**
     * Uses the record count to decide whether navigation must be there, then waits for it, so the answer
     * does not depend on how fast the navigation is rendered after the table.
     */
    public boolean hasNextPage() {
        if (table.getTotalRecords() <= PAGE_SIZE) {
            return false;
        }
        waitForVisible(navigation);
        return !driver.findElements(nextButton).isEmpty();
    }

    @Step("Go to next page")
    public void goToNextPage() {
        table.reloadAfter(() -> click(nextButton));
    }
}

package com.orangehrm.components;

import com.orangehrm.base.BasePage;
import com.orangehrm.utils.XPathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OrangeHRM list table: a header row, one card per record, a "(n) Records Found" label and pagination.
 */
public class DataTableComponent extends BasePage {

    public enum SortOrder {
        ASCENDING("Ascending"),
        DESCENDING("Descending");

        private final String label;

        SortOrder(String label) {
            this.label = label;
        }
    }

    private static final Pattern RECORD_COUNT = Pattern.compile("\\((\\d+)\\)");
    private static final String ROW = "//div[contains(@class,'oxd-table-card')]";

    private final By headers = By.cssSelector(".oxd-table-header .oxd-table-th");
    private final By rows = By.cssSelector(".oxd-table-body .oxd-table-card");
    // Shows "(n) Records Found", or "(n) Records Selected" while rows are checked.
    private final By recordCount = By.cssSelector(".orangehrm-horizontal-padding .oxd-text--span");
    private final By deleteSelectedButton = By.xpath("//button[normalize-space()='Delete Selected']");

    private final PaginationComponent pagination;

    public DataTableComponent(WebDriver driver) {
        super(driver);
        this.pagination = new PaginationComponent(driver, this);
    }

    public PaginationComponent pagination() {
        return pagination;
    }

    /**
     * Runs an action that reloads the table (search, delete, sort) and waits until the new data is rendered.
     * The current record label is removed from the DOM when the table reloads, so its staleness is the signal.
     */
    public void reloadAfter(Runnable action) {
        reloadAfter(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Same as {@link #reloadAfter(Runnable)} for an action that returns a value, e.g. a toast message
     * that has to be read before it disappears.
     */
    public <T> T reloadAfter(Supplier<T> action) {
        WebElement oldLabel = waitForVisible(recordCount);
        T result = action.get();
        wait.until(ExpectedConditions.stalenessOf(oldLabel));
        waitForSpinnerToDisappear();
        waitForVisible(recordCount);
        return result;
    }

    public int getTotalRecords() {
        Matcher matcher = RECORD_COUNT.matcher(getText(recordCount));
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    public int getRowCount() {
        waitForVisible(recordCount);
        return driver.findElements(rows).size();
    }

    public List<String> getColumnValues(String header) {
        waitForVisible(headers);
        int column = getTexts(headers).indexOf(header);
        if (column < 0) {
            throw new IllegalArgumentException("Column '" + header + "' not found. Columns: " + getTexts(headers));
        }
        return getTexts(By.xpath(ROW + "//div[@role='cell'][" + (column + 1) + "]"));
    }

    public boolean hasRowWithCell(String cellText) {
        return !driver.findElements(cell(cellText)).isEmpty();
    }

    /**
     * Clicks the trash icon in the row that has a cell with exactly {@code cellText}.
     */
    public ConfirmDialogComponent clickDeleteInRow(String cellText) {
        click(actionButtonInRow(cellText, "bi-trash"));
        return new ConfirmDialogComponent(driver);
    }

    /**
     * Ticks the checkbox of the row that has a cell with exactly {@code cellText}. The checkbox input is hidden,
     * so its styled span is clicked.
     */
    public void selectRow(String cellText) {
        click(By.xpath(row(cellText) + "//span[contains(@class,'oxd-checkbox-input')]"));
    }

    /**
     * Clicks "Delete Selected", which is shown only while at least one row is checked.
     */
    public ConfirmDialogComponent clickDeleteSelected() {
        click(deleteSelectedButton);
        return new ConfirmDialogComponent(driver);
    }

    /**
     * Sorts by the column through the menu in its header and waits for the table to reload.
     */
    public void sortBy(String header, SortOrder order) {
        // text()[...] matches the header's own text; the hidden menu inside it adds "AscendingDescending".
        String headerCell = "//div[@role='columnheader'][text()[normalize-space()=" + XPathUtils.literal(header) + "]]";
        click(By.xpath(headerCell + "//i[contains(@class,'oxd-table-header-sort-icon')]"));
        By option = By.xpath(headerCell + "//li[normalize-space()=" + XPathUtils.literal(order.label) + "]");
        reloadAfter(() -> click(option));
    }

    private String row(String cellText) {
        return ROW + "//div[@role='cell'][normalize-space()=" + XPathUtils.literal(cellText) + "]"
                + "/ancestor::div[@role='row']";
    }

    private By cell(String cellText) {
        return By.xpath(ROW + "//div[@role='cell'][normalize-space()=" + XPathUtils.literal(cellText) + "]");
    }

    /**
     * cell → up to its row (ancestor axis) → down to the button that holds the given icon.
     */
    private By actionButtonInRow(String cellText, String iconClass) {
        return By.xpath(row(cellText) + "//button[i[contains(@class,'" + iconClass + "')]]");
    }
}

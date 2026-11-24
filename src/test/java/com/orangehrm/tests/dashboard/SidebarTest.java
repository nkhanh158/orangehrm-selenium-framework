package com.orangehrm.tests.dashboard;

import com.orangehrm.base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Navigation")
@Story("Side menu")
public class SidebarTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "DASH-02: Side menu search filters the menu items")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldShowOnlyMenuItemsMatchingSearchTerm() {
        String term = "ti";

        List<String> items = loginAsAdmin().sidebar().search(term).getMenuItems();

        assertFalse(items.isEmpty(), "At least one menu item should match '" + term + "'");
        assertTrue(items.stream().allMatch(item -> item.toLowerCase(Locale.ROOT).contains(term)),
                "Every visible item should contain '" + term + "': " + items);
    }
}

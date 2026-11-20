package com.orangehrm.tests.dashboard;

import com.orangehrm.base.BaseTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SidebarTest extends BaseTest {

    @Test(groups = "regression", description = "DASH-02: Side menu search filters the menu items")
    public void shouldShowOnlyMenuItemsMatchingSearchTerm() {
        String term = "ti";

        List<String> items = loginAsAdmin().sidebar().search(term).getMenuItems();

        assertFalse(items.isEmpty(), "At least one menu item should match '" + term + "'");
        assertTrue(items.stream().allMatch(item -> item.toLowerCase(Locale.ROOT).contains(term)),
                "Every visible item should contain '" + term + "': " + items);
    }
}

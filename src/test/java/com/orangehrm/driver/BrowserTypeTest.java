package com.orangehrm.driver;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@Feature("Framework")
@Story("Browser")
@Test(groups = "framework")
public class BrowserTypeTest {

    @Test
    public void shouldParseBrowserIgnoringCase() {
        assertEquals(BrowserType.from(" Firefox "), BrowserType.FIREFOX);
    }

    @Test
    public void shouldRejectUnknownBrowser() {
        assertThrows(IllegalArgumentException.class, () -> BrowserType.from("safari"));
    }
}

package com.orangehrm.driver;

import com.orangehrm.base.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = "framework")
public class DriverFactoryTest extends BaseTest {

    @Test
    public void shouldOpenBaseUrlInConfiguredBrowser() {
        assertTrue(driver.getTitle().contains("OrangeHRM"),
                "Unexpected page title: " + driver.getTitle());
    }
}

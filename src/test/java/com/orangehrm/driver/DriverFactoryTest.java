package com.orangehrm.driver;

import com.orangehrm.base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Feature("Framework")
@Story("Browser")
@Test(groups = "framework")
public class DriverFactoryTest extends BaseTest {

    @Test
    public void shouldOpenBaseUrlInConfiguredBrowser() {
        assertTrue(driver.getTitle().contains("OrangeHRM"),
                "Unexpected page title: " + driver.getTitle());
    }
}

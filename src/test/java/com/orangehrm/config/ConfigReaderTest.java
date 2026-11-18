package com.orangehrm.config;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class ConfigReaderTest {

    private String originalTimeout;

    @BeforeMethod(alwaysRun = true)
    public void saveOverrides() {
        originalTimeout = System.getProperty("timeout");
    }

    @AfterMethod(alwaysRun = true)
    public void restoreOverrides() {
        if (originalTimeout == null) {
            System.clearProperty("timeout");
        } else {
            System.setProperty("timeout", originalTimeout);
        }
    }

    @Test
    public void shouldParseEnvironmentIgnoringCase() {
        assertEquals(Environment.from(" Demo "), Environment.DEMO);
    }

    @Test
    public void shouldReadRequiredValuesFromPropertiesFile() {
        assertTrue(ConfigReader.baseUrl().startsWith("http"));
        assertTrue(ConfigReader.timeoutSeconds() > 0);
    }

    @Test
    public void shouldPreferSystemPropertyOverPropertiesFile() {
        System.setProperty("timeout", "30");
        assertEquals(ConfigReader.timeoutSeconds(), 30);
    }

    @Test
    public void shouldReturnDefaultWhenKeyIsMissing() {
        assertEquals(ConfigReader.get("notExistingKey", "fallback"), "fallback");
    }

    @Test
    public void shouldFailWhenRequiredKeyIsMissing() {
        assertThrows(IllegalStateException.class, () -> ConfigReader.get("notExistingKey"));
    }

    @Test
    public void shouldConvertKeyToEnvironmentVariableName() {
        assertEquals(ConfigReader.toEnvVarName("adminPassword"), "ADMIN_PASSWORD");
        assertEquals(ConfigReader.toEnvVarName("db.url"), "DB_URL");
    }

    @Test
    public void shouldRejectUnknownEnvironment() {
        assertThrows(IllegalArgumentException.class, () -> Environment.from("production"));
    }
}

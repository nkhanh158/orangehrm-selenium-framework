package com.orangehrm.config;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

@Feature("Framework")
@Story("Configuration")
@Test(groups = {"framework", "serial"})
public class ConfigReaderTest {

    private static final List<String> OVERRIDDEN_KEYS = List.of("timeout", "retryCount");

    private final Map<String, String> originalValues = new HashMap<>();

    @BeforeMethod(alwaysRun = true)
    public void saveOverrides() {
        originalValues.clear();
        OVERRIDDEN_KEYS.forEach(key -> originalValues.put(key, System.getProperty(key)));
    }

    @AfterMethod(alwaysRun = true)
    public void restoreOverrides() {
        originalValues.forEach((key, value) -> {
            if (value == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, value);
            }
        });
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
    public void shouldReadRetryCountOverride() {
        System.setProperty("retryCount", "0");
        assertEquals(ConfigReader.retryCount(), 0);
    }

    @Test
    public void shouldRejectNegativeRetryCount() {
        System.setProperty("retryCount", "-1");
        assertThrows(IllegalStateException.class, ConfigReader::retryCount);
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

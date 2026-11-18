package com.orangehrm.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

/**
 * Resolves configuration values with the following precedence:
 * JVM system property ({@code -Dkey=value}) &gt; environment variable ({@code KEY}) &gt; environment properties file.
 * <p>
 * Environment variable names are derived from keys in UPPER_SNAKE_CASE, e.g. {@code adminPassword -> ADMIN_PASSWORD}.
 */
public final class ConfigReader {

    private static final Environment ENVIRONMENT = resolveEnvironment();
    private static final Properties PROPERTIES = loadProperties(ENVIRONMENT);

    private ConfigReader() {
    }

    public static Environment environment() {
        return ENVIRONMENT;
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static int timeoutSeconds() {
        return getInt("timeout");
    }

    public static String adminUsername() {
        return get("adminUsername");
    }

    public static String adminPassword() {
        return get("adminPassword");
    }

    public static String get(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing config '" + key + "' for environment " + ENVIRONMENT
                            + ". Set it in " + ENVIRONMENT.configFile()
                            + ", via -D" + key + " or env variable " + toEnvVarName(key) + ".");
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = resolve(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Config '" + key + "' must be an integer but was '" + value + "'.", e);
        }
    }

    private static String resolve(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(toEnvVarName(key));
        }
        if (isBlank(value)) {
            value = PROPERTIES.getProperty(key);
        }
        return isBlank(value) ? null : value.trim();
    }

    private static Environment resolveEnvironment() {
        String value = System.getProperty(FrameworkConstants.ENV_KEY);
        if (isBlank(value)) {
            value = System.getenv(toEnvVarName(FrameworkConstants.ENV_KEY));
        }
        return isBlank(value) ? FrameworkConstants.DEFAULT_ENV : Environment.from(value);
    }

    private static Properties loadProperties(Environment environment) {
        String path = environment.configFile();
        InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Config file not found on classpath: " + path);
        }
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config file: " + path, e);
        }
        return properties;
    }

    static String toEnvVarName(String key) {
        return key.replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .replace('.', '_')
                .toUpperCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

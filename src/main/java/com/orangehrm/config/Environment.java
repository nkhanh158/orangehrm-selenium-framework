package com.orangehrm.config;

import java.util.Arrays;
import java.util.Locale;

public enum Environment {
    DEMO;

    public String configFile() {
        return FrameworkConstants.CONFIG_DIR + name().toLowerCase(Locale.ROOT) + ".properties";
    }

    public static Environment from(String value) {
        return Arrays.stream(values())
                .filter(env -> env.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown environment '" + value + "'. Supported: " + Arrays.toString(values())));
    }
}

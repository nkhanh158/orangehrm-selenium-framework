package com.orangehrm.listeners;

import com.orangehrm.config.ConfigReader;
import io.qameta.allure.util.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Writes {@code environment.properties} to the Allure results directory before the suite starts, so that the
 * report shows where and how the tests ran.
 */
public class AllureEnvironmentListener implements ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(AllureEnvironmentListener.class);
    private static final String RESULTS_DIRECTORY_KEY = "allure.results.directory";

    @Override
    public void onStart(ISuite suite) {
        Properties environment = new Properties();
        environment.setProperty("Environment", ConfigReader.environment().toString());
        environment.setProperty("Base URL", ConfigReader.baseUrl());
        environment.setProperty("Browser", ConfigReader.browser().toString());
        environment.setProperty("Headless", String.valueOf(ConfigReader.headless()));
        environment.setProperty("Suite", suite.getName());
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("OS", System.getProperty("os.name"));

        Path directory = Path.of(PropertiesUtils.loadAllureProperties()
                .getProperty(RESULTS_DIRECTORY_KEY, "allure-results"));
        try {
            Files.createDirectories(directory);
            try (Writer writer = Files.newBufferedWriter(directory.resolve("environment.properties"),
                    StandardCharsets.UTF_8)) {
                environment.store(writer, null);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write Allure environment to " + directory, e);
        }
        LOG.debug("Allure environment written to {}", directory);
    }
}

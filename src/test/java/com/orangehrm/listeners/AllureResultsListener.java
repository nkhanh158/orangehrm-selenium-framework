package com.orangehrm.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orangehrm.config.ConfigReader;
import com.orangehrm.failures.FailureClassification;
import com.orangehrm.failures.FailureType;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Writes the files Allure reads next to the test results before the suite starts: {@code environment.properties},
 * which shows where and how the tests ran, and {@code categories.json}, which groups failures by
 * {@link FailureType}.
 */
public class AllureResultsListener implements ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(AllureResultsListener.class);
    private static final String RESULTS_DIRECTORY_KEY = "allure.results.directory";

    @Override
    public void onStart(ISuite suite) {
        Path directory = Path.of(PropertiesUtils.loadAllureProperties()
                .getProperty(RESULTS_DIRECTORY_KEY, "allure-results"));
        try {
            Files.createDirectories(directory);
            writeEnvironment(directory.resolve("environment.properties"), suite);
            new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(directory.resolve("categories.json").toFile(), categories());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write Allure environment and categories to " + directory, e);
        }
        LOG.debug("Allure environment and categories written to {}", directory);
    }

    private static void writeEnvironment(Path file, ISuite suite) throws IOException {
        Properties environment = new Properties();
        environment.setProperty("Environment", ConfigReader.environment().toString());
        environment.setProperty("Base URL", ConfigReader.baseUrl());
        environment.setProperty("Browser", ConfigReader.browser().toString());
        environment.setProperty("Headless", String.valueOf(ConfigReader.headless()));
        environment.setProperty("Retry count", String.valueOf(ConfigReader.retryCount()));
        environment.setProperty("Suite", suite.getName());
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("OS", System.getProperty("os.name"));
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            environment.store(writer, null);
        }
    }

    /**
     * One category per failure type, matched on the classification printed in the stack trace. Retried attempts
     * are skipped results, so skipped is matched as well.
     */
    private static List<Map<String, Object>> categories() {
        return Arrays.stream(FailureType.values()).map(type -> {
            Map<String, Object> category = new LinkedHashMap<>();
            category.put("name", type.label());
            category.put("matchedStatuses", List.of("failed", "broken", "skipped"));
            category.put("traceRegex", "(?s).*" + Pattern.quote(FailureClassification.class.getSimpleName()
                    + ": " + type.label()) + ".*");
            return category;
        }).toList();
    }
}

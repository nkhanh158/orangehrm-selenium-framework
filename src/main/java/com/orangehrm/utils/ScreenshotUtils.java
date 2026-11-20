package com.orangehrm.utils;

import com.orangehrm.config.FrameworkConstants;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    /**
     * Saves a PNG screenshot of the current page and returns its path.
     */
    public static Path capture(WebDriver driver, String name) {
        byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String fileName = sanitize(name) + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png";
        try {
            Files.createDirectories(FrameworkConstants.SCREENSHOT_DIR);
            return Files.write(FrameworkConstants.SCREENSHOT_DIR.resolve(fileName), image);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save screenshot " + fileName, e);
        }
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

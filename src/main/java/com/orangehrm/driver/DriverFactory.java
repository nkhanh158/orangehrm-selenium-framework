package com.orangehrm.driver;

import com.orangehrm.config.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates configured {@link WebDriver} instances. Driver binaries are resolved by Selenium Manager.
 */
public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browser, boolean headless) {
        LOG.info("Start {}{}", browser, headless ? " (headless)" : "");
        Path downloadDir = createDownloadDir();
        WebDriver driver = switch (browser) {
            case CHROME -> new ChromeDriver(chromiumOptions(new ChromeOptions(), headless, downloadDir));
            case EDGE -> new EdgeDriver(chromiumOptions(new EdgeOptions(), headless, downloadDir));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless, downloadDir));
        };
        driver.manage().window().setSize(
                new Dimension(FrameworkConstants.WINDOW_WIDTH, FrameworkConstants.WINDOW_HEIGHT));
        return driver;
    }

    private static <T extends ChromiumOptions<T>> T chromiumOptions(T options, boolean headless, Path downloadDir) {
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=" + FrameworkConstants.WINDOW_WIDTH + "," + FrameworkConstants.WINDOW_HEIGHT);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir.toString());
        prefs.put("download.prompt_for_download", false);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        // Prevents the "password found in a data breach" dialog for the public demo credentials.
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless, Path downloadDir) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", downloadDir.toString());
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/pdf,application/octet-stream,image/png,image/jpeg,text/plain");
        options.addPreference("pdfjs.disabled", true);
        options.addPreference("signon.rememberSignons", false);
        return options;
    }

    private static Path createDownloadDir() {
        try {
            return Files.createDirectories(FrameworkConstants.DOWNLOAD_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create download directory " + FrameworkConstants.DOWNLOAD_DIR, e);
        }
    }
}

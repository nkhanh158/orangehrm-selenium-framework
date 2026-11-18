package com.orangehrm.config;

import com.orangehrm.driver.BrowserType;

import java.nio.file.Path;

public final class FrameworkConstants {

    public static final String CONFIG_DIR = "config/";
    public static final String ENV_KEY = "env";
    public static final Environment DEFAULT_ENV = Environment.DEMO;

    public static final BrowserType DEFAULT_BROWSER = BrowserType.CHROME;
    public static final boolean DEFAULT_HEADLESS = false;
    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
    public static final Path DOWNLOAD_DIR = Path.of("target", "downloads").toAbsolutePath();

    private FrameworkConstants() {
    }
}

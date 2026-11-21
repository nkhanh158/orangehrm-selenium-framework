package com.orangehrm.data;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Locates files that tests hand to the browser, e.g. images to upload.
 */
public final class TestFiles {

    private TestFiles() {
    }

    /**
     * Absolute path of a classpath resource such as {@code testdata/avatar.png}. File inputs need a real path
     * on disk, not a stream.
     */
    public static Path path(String classpathResource) {
        URL url = TestFiles.class.getClassLoader().getResource(classpathResource);
        if (url == null) {
            throw new IllegalStateException("Test file not found on classpath: " + classpathResource);
        }
        try {
            return Path.of(url.toURI()).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid test file location: " + url, e);
        }
    }
}

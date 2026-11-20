package com.orangehrm.data;

import java.io.InputStream;

final class ResourceLoader {

    private ResourceLoader() {
    }

    static InputStream open(String classpathResource) {
        InputStream stream = ResourceLoader.class.getClassLoader().getResourceAsStream(classpathResource);
        if (stream == null) {
            throw new IllegalStateException("Test data file not found on classpath: " + classpathResource);
        }
        return stream;
    }
}

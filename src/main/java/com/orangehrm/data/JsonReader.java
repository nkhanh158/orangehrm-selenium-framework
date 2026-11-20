package com.orangehrm.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Reads JSON test data from the classpath into model objects.
 */
public final class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private JsonReader() {
    }

    /**
     * Reads a JSON array, e.g. {@code testdata/login-data.json}, into a list of {@code type}.
     */
    public static <T> List<T> readList(String classpathResource, Class<T> type) {
        try (InputStream stream = ResourceLoader.open(classpathResource)) {
            return readList(stream, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read JSON test data: " + classpathResource, e);
        }
    }

    static <T> List<T> readList(InputStream stream, Class<T> type) throws IOException {
        CollectionType listType = MAPPER.getTypeFactory().constructCollectionType(List.class, type);
        return MAPPER.readValue(stream, listType);
    }
}

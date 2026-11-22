package com.orangehrm.utils;

public final class XPathUtils {

    private XPathUtils() {
    }

    /**
     * Quotes a value as an XPath string literal. XPath 1.0 has no escape character, so a value containing
     * both quote types (e.g. {@code O'Neil "Jr"}) is built with {@code concat()}.
     */
    public static String literal(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }

    /**
     * Predicate that matches elements having the CSS class as a whole token. {@code contains(@class, 'x')}
     * would also match {@code x__wrapper} or {@code x-icon}.
     */
    public static String hasClass(String className) {
        return "contains(concat(' ', normalize-space(@class), ' '), ' " + className + " ')";
    }
}

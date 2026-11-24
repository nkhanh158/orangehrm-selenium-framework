package com.orangehrm.failures;

/**
 * A first guess at why a test failed, made from the exception and the state of the browser. It tells where to look
 * first; it is not a verdict.
 */
public enum FailureType {

    SERVER_ERROR("Suspected environment problem: server error", true),
    BROWSER_OR_NETWORK("Suspected environment problem: browser or network", true),
    TIMEOUT("Timed out waiting: slow environment or wrong locator", true),
    PRODUCT_DEFECT("Suspected product defect", false),
    TEST_DEFECT("Suspected test defect", false);

    private final String label;
    private final boolean retryable;

    FailureType(String label, boolean retryable) {
        this.label = label;
        this.retryable = retryable;
    }

    public String label() {
        return label;
    }

    /**
     * Whether running the test again may give a different result. An assertion or a script error fails the same
     * way every time, and retrying it would only hide the defect.
     */
    public boolean retryable() {
        return retryable;
    }
}

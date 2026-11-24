package com.orangehrm.failures;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class FailureClassifier {

    /** Chrome reports failed page loads as e.g. "unknown error: net::ERR_CONNECTION_RESET". */
    private static final String CHROME_NETWORK_ERROR = "net::ERR_";

    private FailureClassifier() {
    }

    /**
     * Classifies a failure.
     *
     * @param failure      what the test threw
     * @param serverErrors server responses with a 5xx status seen by the page when the test failed. A server error
     *                     explains a failed assertion too, e.g. when a save did not reach the database.
     */
    public static FailureType classify(Throwable failure, List<String> serverErrors) {
        if (!serverErrors.isEmpty()) {
            return FailureType.SERVER_ERROR;
        }
        List<Throwable> chain = causeChain(failure);
        if (chain.stream().anyMatch(FailureClassifier::isBrowserOrNetworkFailure)) {
            return FailureType.BROWSER_OR_NETWORK;
        }
        if (chain.stream().anyMatch(TimeoutException.class::isInstance)) {
            return FailureType.TIMEOUT;
        }
        return failure instanceof AssertionError ? FailureType.PRODUCT_DEFECT : FailureType.TEST_DEFECT;
    }

    private static boolean isBrowserOrNetworkFailure(Throwable throwable) {
        if (throwable instanceof NoSuchSessionException
                || throwable instanceof SessionNotCreatedException
                || throwable instanceof UnreachableBrowserException
                || throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof UnknownHostException) {
            return true;
        }
        return throwable instanceof WebDriverException
                && throwable.getMessage() != null
                && throwable.getMessage().contains(CHROME_NETWORK_ERROR);
    }

    /**
     * The failure and its causes. Selenium often wraps the exception that tells what went wrong.
     */
    private static List<Throwable> causeChain(Throwable failure) {
        List<Throwable> chain = new ArrayList<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Throwable current = failure;
        while (current != null && seen.add(current)) {
            chain.add(current);
            current = current.getCause();
        }
        return chain;
    }
}

package com.orangehrm.failures;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Framework")
@Story("Failure classification")
@Test(groups = "framework")
public class FailureClassifierTest {

    private static final List<String> NO_SERVER_ERRORS = List.of();

    /**
     * Named, because the message of a Selenium exception spans several lines and would make an unreadable test name.
     */
    private record Failure(String name, Throwable exception, FailureType expected) {
        @Override
        public String toString() {
            return name;
        }
    }

    @DataProvider
    public Object[][] failures() {
        return new Object[][]{
                {new Failure("Assertion", new AssertionError("expected [Saved] but found [Failed]"),
                        FailureType.PRODUCT_DEFECT)},
                {new Failure("No such element", new NoSuchElementException("no such element"),
                        FailureType.TEST_DEFECT)},
                {new Failure("Stale element", new StaleElementReferenceException("stale element"),
                        FailureType.TEST_DEFECT)},
                {new Failure("Illegal state", new IllegalStateException("did not reach the Dashboard"),
                        FailureType.TEST_DEFECT)},
                {new Failure("Timeout", new TimeoutException("Expected condition failed"),
                        FailureType.TIMEOUT)},
                {new Failure("Session gone", new NoSuchSessionException("invalid session id"),
                        FailureType.BROWSER_OR_NETWORK)},
                {new Failure("Page load network error",
                        new WebDriverException("unknown error: net::ERR_CONNECTION_RESET"),
                        FailureType.BROWSER_OR_NETWORK)},
                {new Failure("Connection refused as cause",
                        new WebDriverException("Could not start a new session", new ConnectException("refused")),
                        FailureType.BROWSER_OR_NETWORK)},
        };
    }

    @Test(dataProvider = "failures")
    public void shouldClassifyFailureByException(Failure failure) {
        assertEquals(FailureClassifier.classify(failure.exception(), NO_SERVER_ERRORS), failure.expected());
    }

    @Test
    public void shouldBlameServerErrorEvenWhenAnAssertionFailed() {
        AssertionError failure = new AssertionError("employee not found");

        assertEquals(FailureClassifier.classify(failure, List.of("HTTP 500 https://host/api/v2/pim/employees")),
                FailureType.SERVER_ERROR);
    }

    @Test
    public void shouldRetryOnlyEnvironmentFailures() {
        assertTrue(FailureType.SERVER_ERROR.retryable());
        assertTrue(FailureType.BROWSER_OR_NETWORK.retryable());
        assertTrue(FailureType.TIMEOUT.retryable());
        assertFalse(FailureType.PRODUCT_DEFECT.retryable());
        assertFalse(FailureType.TEST_DEFECT.retryable());
    }

    @Test
    public void shouldRecordClassificationInStackTraceOnce() {
        AssertionError failure = new AssertionError("expected [1] but found [2]");

        FailureClassification.record(failure, () -> NO_SERVER_ERRORS);
        FailureType second = FailureClassification.record(failure, () -> List.of("HTTP 503 https://host/"));

        assertEquals(second, FailureType.PRODUCT_DEFECT);
        assertEquals(failure.getSuppressed().length, 1);
        assertEquals(failure.getMessage(), "expected [1] but found [2]");
        assertEquals(failure.getSuppressed()[0].getMessage(), FailureType.PRODUCT_DEFECT.label());
    }

    @Test
    public void shouldNameServerErrorsInClassification() {
        AssertionError failure = new AssertionError("employee not found");

        FailureClassification.record(failure, () -> List.of("HTTP 500 https://host/api"));

        assertEquals(failure.getSuppressed()[0].getMessage(),
                FailureType.SERVER_ERROR.label() + ": HTTP 500 https://host/api");
    }
}

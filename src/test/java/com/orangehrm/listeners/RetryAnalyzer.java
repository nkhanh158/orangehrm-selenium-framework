package com.orangehrm.listeners;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.failures.FailureClassification;
import com.orangehrm.failures.FailureClassifier;
import com.orangehrm.failures.FailureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.List;

/**
 * Runs a test again when its failure looks caused by the environment, up to {@code retryCount} times. Failures of
 * the application or of the script are never retried. TestNG reports the failed attempt as skipped, and the log and
 * the Allure report show it next to the final result.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);

    private int retries;

    @Override
    public boolean retry(ITestResult result) {
        Throwable failure = result.getThrowable();
        if (failure == null) {
            return false;
        }
        FailureType type = FailureClassification.recordedOn(failure)
                .orElseGet(() -> FailureClassifier.classify(failure, List.of()));
        int maxRetries = ConfigReader.retryCount();
        if (!type.retryable() || retries >= maxRetries) {
            return false;
        }
        retries++;
        RetriedAttemptListener.markRetried(failure);
        LOG.warn("RETRY {} ({}/{}): {}", TestListener.testName(result), retries, maxRetries, type.label());
        return true;
    }
}

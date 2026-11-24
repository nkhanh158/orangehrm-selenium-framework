package com.orangehrm.listeners;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;

/**
 * Makes retries visible in the Allure report:
 * <ul>
 *     <li>a failed attempt that is run again is reported as failed or broken. TestNG marks it as skipped, so the
 *     report would show that nothing went wrong. The report lists it under the retries of the final attempt and
 *     counts only the final attempt in its statistics.</li>
 *     <li>a test that passed only when run again is marked flaky, so it stands out. A test that failed on every
 *     attempt is not flaky; it fails.</li>
 * </ul>
 * TestNG runs a retried test again right away on the same thread, so the state is kept per thread.
 * <p>
 * Registered in {@code META-INF/services/io.qameta.allure.listener.TestLifecycleListener}.
 */
public class RetriedAttemptListener implements TestLifecycleListener {

    private static final ThreadLocal<Status> RETRIED_ATTEMPT_STATUS = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> RETRY_PENDING = new ThreadLocal<>();

    /**
     * Called by {@link RetryAnalyzer} when it decides to run the test again. The attempt's Allure result is stopped
     * right after, on the same thread.
     */
    static void markRetried(Throwable failure) {
        RETRIED_ATTEMPT_STATUS.set(ResultsUtils.getStatus(failure).orElse(Status.BROKEN));
    }

    @Override
    public void beforeTestStop(TestResult result) {
        Status retriedStatus = RETRIED_ATTEMPT_STATUS.get();
        RETRIED_ATTEMPT_STATUS.remove();
        boolean afterRetry = Boolean.TRUE.equals(RETRY_PENDING.get());
        RETRY_PENDING.remove();

        if (retriedStatus != null && result.getStatus() == Status.SKIPPED) {
            result.setStatus(retriedStatus);
            RETRY_PENDING.set(true);
            return;
        }
        if (afterRetry && result.getStatus() == Status.PASSED) {
            if (result.getStatusDetails() == null) {
                result.setStatusDetails(new StatusDetails());
            }
            result.getStatusDetails().setFlaky(true);
        }
    }
}

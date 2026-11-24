package com.orangehrm.failures;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Records the {@link FailureType} on the failure itself, as a suppressed exception. It is printed with the stack
 * trace ("Suppressed: ...FailureClassification: Suspected product defect"), so the Surefire report, the log and
 * Allure's categories all see it, while the original exception and its message stay unchanged.
 */
public final class FailureClassification extends Exception {

    private final transient FailureType type;

    private FailureClassification(FailureType type, List<String> serverErrors) {
        super(serverErrors.isEmpty() ? type.label() : type.label() + ": " + String.join(", ", serverErrors),
                null, false, false);
        this.type = type;
    }

    /**
     * Classifies the failure and records the result on it.
     *
     * @param serverErrors reads the 5xx responses seen by the page; only called when the browser is still open
     */
    public static FailureType record(Throwable failure, Supplier<List<String>> serverErrors) {
        Optional<FailureType> recorded = recordedOn(failure);
        if (recorded.isPresent()) {
            return recorded.get();
        }
        List<String> errors = serverErrors.get();
        FailureType type = FailureClassifier.classify(failure, errors);
        failure.addSuppressed(new FailureClassification(type, errors));
        return type;
    }

    public static Optional<FailureType> recordedOn(Throwable failure) {
        if (failure == null) {
            return Optional.empty();
        }
        for (Throwable suppressed : failure.getSuppressed()) {
            if (suppressed instanceof FailureClassification classification) {
                return Optional.of(classification.type);
            }
        }
        return Optional.empty();
    }
}

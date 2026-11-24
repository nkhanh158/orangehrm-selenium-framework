package com.orangehrm.listeners;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Writes every Allure step to the log, so that a step is described once, by {@code @Step}, and appears both in
 * the report and in the log. Registered through
 * {@code META-INF/services/io.qameta.allure.listener.StepLifecycleListener}.
 */
public class StepLogger implements StepLifecycleListener {

    private static final Logger LOG = LogManager.getLogger(StepLogger.class);

    @Override
    public void beforeStepStart(StepResult result) {
        LOG.info(result.getName());
    }
}

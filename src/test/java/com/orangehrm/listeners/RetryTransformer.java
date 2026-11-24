package com.orangehrm.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.DisabledRetryAnalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Gives every test the {@link RetryAnalyzer}, unless the test names its own.
 * <p>
 * Registered in {@code META-INF/services/org.testng.ITestNGListener}: an annotation transformer has to be known
 * before TestNG reads the annotations, so it cannot be declared with {@code @Listeners}.
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Class<?> analyzer = annotation.getRetryAnalyzerClass();
        if (analyzer == null || analyzer == DisabledRetryAnalyzer.class) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}

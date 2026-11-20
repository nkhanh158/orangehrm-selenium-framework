package com.orangehrm.data;

import com.orangehrm.models.Employee;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random test data. Every generated record carries a unique marker so that tests never collide
 * with each other or with data created by other users of a shared environment.
 */
public final class TestDataFactory {

    public static final String AUTO_PREFIX = "Auto";

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final int EMPLOYEE_ID_LENGTH = 10;

    // Faker is not thread-safe; one instance per thread keeps parallel runs safe.
    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private TestDataFactory() {
    }

    /**
     * Returns a marker such as {@code Auto_20261110_153045_7f3a}.
     */
    public static String uniqueSuffix() {
        String random = String.format("%04x", ThreadLocalRandom.current().nextInt(0x10000));
        return AUTO_PREFIX + "_" + LocalDateTime.now().format(TIMESTAMP) + "_" + random;
    }

    /**
     * Random employee whose last name is a unique marker, so it can be searched for and cleaned up reliably.
     */
    public static Employee randomEmployee() {
        return new Employee(FAKER.get().name().firstName(), "", uniqueSuffix(), uniqueEmployeeId());
    }

    /**
     * Random employee with the given last name, e.g. to create several employees that share one marker.
     */
    public static Employee randomEmployee(String lastName) {
        return new Employee(FAKER.get().name().firstName(), "", lastName, uniqueEmployeeId());
    }

    public static Employee randomEmployeeWithMiddleName() {
        Faker faker = FAKER.get();
        return new Employee(faker.name().firstName(), faker.name().firstName(), uniqueSuffix(), uniqueEmployeeId());
    }

    /**
     * Numeric employee id of the maximum length OrangeHRM accepts (10 characters).
     */
    public static String uniqueEmployeeId() {
        long bound = (long) Math.pow(10, EMPLOYEE_ID_LENGTH - 1);
        return String.valueOf(ThreadLocalRandom.current().nextLong(bound, bound * 10));
    }
}

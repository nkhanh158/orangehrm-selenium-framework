# OrangeHRM Selenium Framework

Test automation framework for [OrangeHRM Open Source](https://opensource-demo.orangehrmlive.com) built with Selenium WebDriver 4, TestNG and Maven.

> Work in progress.

## Tech Stack

- Java 17
- Maven
- Selenium WebDriver 4 (drivers resolved by Selenium Manager)
- TestNG
- Log4j 2
- Allure Report 2
- Datafaker, Jackson and Apache POI for test data

## Prerequisites

- JDK 17+
- Maven 3.9+
- Google Chrome, Mozilla Firefox or Microsoft Edge

## Getting Started

```bash
git clone https://github.com/nkhanh158/orangehrm-selenium-framework.git
cd orangehrm-selenium-framework
mvn clean test
```

## Running Tests

### Test suites

| Profile | Suite file | Content |
| --- | --- | --- |
| `regression` (default) | `testng-regression.xml` | Every test, one at a time |
| `smoke` | `testng-smoke.xml` | Framework self-tests and the `smoke` group: the main user journeys, under 3 minutes |
| `parallel` | `testng-parallel.xml` | Every test, three test classes at a time; the `serial` group runs afterwards on its own |

```bash
mvn clean test -Psmoke
mvn clean test -Pregression -Dbrowser=edge -Dheadless=true
```

Groups: `smoke` (also in `regression`), `regression`, `framework` (tests of the framework itself, no application
needed except for the driver check) and `serial` (tests that change or count shared state and must not run alongside
others).

### Options

| Option | Values | Default | Example |
| --- | --- | --- | --- |
| `env` | `demo` | `demo` | `-Denv=demo` |
| `browser` | `chrome`, `firefox`, `edge` | `chrome` | `-Dbrowser=firefox` |
| `headless` | `true`, `false` | `false` | `-Dheadless=true` |

```bash
# Run a single test class (overrides the suite file)
mvn clean test -Dtest=LoginTest

# Run headless in Edge
mvn clean test -Dbrowser=edge -Dheadless=true
```

Any configuration key can be overridden by a JVM system property (`-DbaseUrl=...`) or an environment variable (`BASE_URL`), in that order of precedence over the environment properties file.

### Allure report

Tests write Allure results to `target/allure-results/`. The Maven plugin downloads the Allure command line and
builds the report from them:

```bash
mvn clean test -Psmoke
mvn allure:serve      # builds the report and opens it in the browser
mvn allure:report     # builds it to target/site/allure-maven-plugin/
```

The report groups tests by feature and story, shows each test as a tree of steps (`@Step` on page methods) and
lists the environment the tests ran in. A failed UI test has a screenshot, its URL and the page source attached.
Screenshots are also saved to `target/screenshots/`.

### Logs

Each test logs its start, its main steps and its outcome to the console and to `logs/automation.log`, which rolls
over to `logs/automation-yyyy-MM-dd.log` every day (kept for 14 days). Clicks and typing are logged at DEBUG level,
by locator only, so that typed passwords never appear in a log:

```bash
mvn clean test -Psmoke -DlogLevel=DEBUG
```

Selenium (java.util.logging) and TestNG (SLF4J) log through Log4j as well, so all output follows `log4j2.xml`.

## Design Decisions

### Explicit waits only

The framework never sets an implicit wait. Every interaction in `BasePage` waits explicitly for the state it needs (visible, clickable, loaders gone). Mixing implicit and explicit waits makes timeouts unpredictable: an explicit wait for an element to *disappear* calls `findElement` internally, which is then held by the implicit wait on every poll. `Thread.sleep()` is not used anywhere.

### Stale elements

OrangeHRM is a Vue application that re-renders tables and forms frequently. Page objects store `By` locators rather than `WebElement` references and look elements up on each action; actions that can still hit a re-render (`click`, `getText`, reading table texts) retry on `StaleElementReferenceException` within the explicit wait timeout.

### Surefire 3.5.x

Surefire 3.6 runs TestNG on the JUnit Platform, which cannot read TestNG suite XML files. The project stays on
the 3.5 line so that suites, groups and parallel settings are defined in TestNG's own format.

### Allure Java 2.x

Allure Java 3 was released in September 2026 with a rewritten lifecycle. The project stays on the maintained 2.x
line, which most documentation and existing projects are based on.

Two consequences of 2.x are handled in the framework:

- Evidence of a failure is attached in `afterInvocation`, which runs before Allure's own listener closes the test
  result in `onTestFailure`.
- `@Step` stores every argument in the results files, even when a parameter is hidden in the report. Login methods
  therefore report their step with `Allure.step(...)`, which records no arguments, so passwords are never written
  to `target/allure-results/`.

### One description per step

A step is described once, by `@Step`. A step listener writes each step to the log, so the report and the log show
the same steps without repeating the text in a `log.info` call.

### Parallel execution

`BaseTest` keeps the WebDriver in an instance field, and TestNG creates one instance per test class. Test classes
can therefore run in parallel, but methods of the same class would share one driver, so the parallel suite uses
`parallel="classes"`.

## Test Cases

Manual test case specifications are documented in [docs/test-cases.md](docs/test-cases.md).

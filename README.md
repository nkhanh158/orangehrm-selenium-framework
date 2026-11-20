# OrangeHRM Selenium Framework

Test automation framework for [OrangeHRM Open Source](https://opensource-demo.orangehrmlive.com) built with Selenium WebDriver 4, TestNG and Maven.

> Work in progress.

## Tech Stack

- Java 17
- Maven
- Selenium WebDriver 4 (drivers resolved by Selenium Manager)
- TestNG

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

| Option | Values | Default | Example |
| --- | --- | --- | --- |
| `env` | `demo` | `demo` | `-Denv=demo` |
| `browser` | `chrome`, `firefox`, `edge` | `chrome` | `-Dbrowser=firefox` |
| `headless` | `true`, `false` | `false` | `-Dheadless=true` |

```bash
# Run a single test class
mvn clean test -Dtest=LoginTest

# Run headless in Edge
mvn clean test -Dbrowser=edge -Dheadless=true
```

Any configuration key can be overridden by a JVM system property (`-DbaseUrl=...`) or an environment variable (`BASE_URL`), in that order of precedence over the environment properties file.

Screenshots of failed tests are saved to `target/screenshots/`.

## Test Cases

Manual test case specifications are documented in [docs/test-cases.md](docs/test-cases.md).

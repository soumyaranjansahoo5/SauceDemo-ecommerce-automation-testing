<<<<<<< HEAD
# SauceDemo-ecommerce-automation-testing
=======
# E-Commerce End-to-End Automation Framework

**Author:** Soumyaranjan Sahoo  
**Stack:** Selenium WebDriver · Java · TestNG · REST Assured · Postman · Maven · POM

---

## Project Structure

```
ecommerce-automation/
├── pom.xml
├── src/
│   ├── main/java/com/ecommerce/
│   │   ├── config/
│   │   │   └── ConfigReader.java          ← reads config.properties
│   │   ├── pages/
│   │   │   ├── BasePage.java              ← parent POM class
│   │   │   ├── LoginPage.java
│   │   │   ├── ProductsPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   └── utils/
│   │       ├── DriverManager.java         ← thread-safe WebDriver
│   │       ├── WaitUtils.java             ← explicit wait helpers
│   │       ├── ScreenshotUtils.java       ← failure screenshots
│   │       └── ExtentReportManager.java   ← HTML report
│   └── test/
│       ├── java/com/ecommerce/
│       │   ├── tests/
│       │   │   ├── BaseTest.java          ← @BeforeMethod / @AfterMethod
│       │   │   ├── LoginTest.java         ← 6 login scenarios
│       │   │   ├── ProductTest.java       ← 8 product/sorting scenarios
│       │   │   ├── CartTest.java          ← 5 cart scenarios
│       │   │   └── CheckoutTest.java      ← 6 checkout/e2e scenarios
│       │   └── api/
│       │       ├── ApiBaseTest.java       ← REST Assured base spec
│       │       ├── ProductApiTest.java    ← 9 product API tests
│       │       ├── CartApiTest.java       ← 8 cart API tests
│       │       └── OrderApiTest.java      ← 9 user/order API tests
│       └── resources/
│           ├── config.properties
│           └── testng.xml
└── test-output/
    ├── ExtentReport.html
    └── screenshots/
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 11 or higher |
| Maven | 3.8+ |
| Chrome | Latest (WebDriverManager auto-downloads driver) |

---

## Setup

```bash
# 1. Clone / unzip the project
cd ecommerce-automation

# 2. Install dependencies
mvn clean install -DskipTests
```

---

## Running Tests

### Run full suite
```bash
mvn test
```

### Run smoke tests only
```bash
mvn test -Dgroups=smoke
```

### Run API tests only
```bash
mvn test -Dgroups=api
```

### Run end-to-end tests only
```bash
mvn test -Dgroups=e2e
```

### Run in a different browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### Run headless (for CI/CD)
```bash
# Edit config.properties: headless=true
mvn test
```

---

## Test Coverage

### UI Tests (Selenium + TestNG)

| Module | Test Class | Scenarios |
|--------|-----------|-----------|
| Login | LoginTest | 6 |
| Products & Search | ProductTest | 8 |
| Cart Management | CartTest | 5 |
| Checkout (E2E) | CheckoutTest | 6 |
| **Total UI** | | **25** |

### API Tests (REST Assured)

| Endpoint Group | Test Class | Scenarios |
|---------------|-----------|-----------|
| Products API | ProductApiTest | 9 |
| Cart API | CartApiTest | 8 |
| Orders / Auth API | OrderApiTest | 9 |
| **Total API** | | **26** |

**Grand Total: 51 automated test scenarios**

---

## Test Groups (TestNG)

| Group | Description |
|-------|-------------|
| `smoke` | Critical path — run after every deployment |
| `regression` | Full suite — covers all functional flows |
| `api` | REST Assured API tests only |
| `e2e` | Full end-to-end journey tests |
| `performance` | Response time assertions |

---

## Test Applications Used

| Layer | Application | URL |
|-------|-------------|-----|
| UI (Selenium) | SauceDemo | https://www.saucedemo.com |
| API (REST Assured) | FakeStoreAPI | https://fakestoreapi.com |

---

## Reports

After execution, open the HTML report:

```
test-output/ExtentReport.html
```

Screenshots for failed tests are saved in:
```
test-output/screenshots/
```

---

## Key Design Decisions

- **Page Object Model (POM)** — every page has its own class; tests contain zero locator code
- **ThreadLocal WebDriver** — safe for parallel test execution with `parallel="methods"` in TestNG
- **WebDriverManager** — no manual chromedriver/geckodriver downloads needed
- **ConfigReader singleton** — all config values from one `.properties` file, no hardcoding in tests
- **ExtentReports** — dark-themed HTML report auto-attached with screenshots on failure
- **REST Assured RequestSpecification** — shared base spec (headers, base URI, logging) defined once in `ApiBaseTest`
- **Group annotations** — every test tagged for selective execution in CI pipelines
>>>>>>> f488ffa (Initial commit - SauceDemo automation framework)

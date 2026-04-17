package utils;

import config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe DriverManager using ThreadLocal — supports parallel execution.
 * Automatically manages driver binaries via WebDriverManager.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverManager() {}

    public static void initDriver() {
        String browser = config.getBrowser().toLowerCase().trim();
        boolean headless = config.isHeadless();
        WebDriver webDriver;

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (headless) ffOptions.addArguments("--headless");
                webDriver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless");
                webDriver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                }
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                webDriver = new ChromeDriver(chromeOptions);
                break;
        }

        webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()))
                .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        webDriver.manage().window().maximize();

        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException(
                    "WebDriver not initialized. Call DriverManager.initDriver() first.");
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}

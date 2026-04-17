package utils;

import config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Reusable explicit wait utility methods.
 * All waits default to the configured explicit.wait timeout.
 */
public class WaitUtils {

    private static final int TIMEOUT = ConfigReader.getInstance().getExplicitWait();

    private WaitUtils() {}

    private static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return getWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(WebDriver driver, WebElement element) {
        return getWait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickability(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForTextPresent(WebDriver driver, WebElement element, String text) {
        return getWait(driver).until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static WebDriver waitForUrl(WebDriver driver, String urlFragment) {
        getWait(driver).until(ExpectedConditions.urlContains(urlFragment));
        return driver;
    }
}

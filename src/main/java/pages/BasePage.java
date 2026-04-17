package pages;

import utils.DriverManager;
import utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Base Page — every Page Object inherits from this.
 * Centralises common interactions so child pages stay lean.
 */
public class BasePage {

    protected WebDriver driver;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    public void navigateTo(String url) {
        driver.get(url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // ── Element interactions ─────────────────────────────────────────────────

    protected void click(WebElement element) {
        WaitUtils.waitForClickability(driver, element).click();
    }

    protected void click(By locator) {
        WaitUtils.waitForClickability(driver, locator).click();
    }

    protected void type(WebElement element, String text) {
        WaitUtils.waitForVisibility(driver, element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        WaitUtils.waitForVisibility(driver, element);
        return element.getText().trim();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void selectByVisibleText(WebElement element, String text) {
        new Select(element).selectByVisibleText(text);
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}

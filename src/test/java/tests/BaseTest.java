package tests;

import config.ConfigReader;
import utils.DriverManager;
import utils.ExtentReportManager;
import utils.ScreenshotUtils;
import com.aventstack.extentreports.Status;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest — all UI test classes extend this.
 * Handles driver lifecycle, ExtentReports logging, and screenshot on failure.
 */
public class BaseTest {

    protected ConfigReader config = ConfigReader.getInstance();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }
        DriverManager.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = ScreenshotUtils.capture(
                    DriverManager.getDriver(), result.getName());

            ExtentReportManager.getTest()
                    .log(Status.FAIL, "Test FAILED: " + result.getThrowable())
                    .addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReportManager.getTest().log(Status.PASS, "Test PASSED");
        } else {
            ExtentReportManager.getTest().log(Status.SKIP, "Test SKIPPED");
        }
        DriverManager.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void flushReports() {
        ExtentReportManager.flushReport();
    }
}

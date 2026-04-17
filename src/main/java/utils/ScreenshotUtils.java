package utils;

import config.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Captures full-page screenshots and saves them to the configured directory.
 * Returns the absolute file path for attaching to ExtentReports.
 */
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR =
            ConfigReader.getInstance().getScreenshotPath();

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and returns the file path.
     *
     * @param driver   active WebDriver
     * @param testName name used in the filename
     * @return absolute path to the saved screenshot file
     */
    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = SCREENSHOT_DIR + fileName;

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(filePath);
            dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }

        return new File(filePath).getAbsolutePath();
    }
}

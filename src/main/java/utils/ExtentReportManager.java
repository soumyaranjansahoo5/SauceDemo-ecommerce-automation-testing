package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import config.ConfigReader;

/**
 * Singleton Extent Reports manager.
 * Call getTest() after createTest() to log steps.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = ConfigReader.getInstance().getReportPath();
            new java.io.File(reportPath).getParentFile().mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("E-Commerce Automation Report");
            spark.config().setReportName("Test Execution Report");
            spark.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Framework", "Selenium + TestNG + REST Assured");
            extent.setSystemInfo("Browser", ConfigReader.getInstance().getBrowser());
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Tester", "Soumyaranjan Sahoo");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = getInstance().createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flushReport() {
        if (extent != null) extent.flush();
    }
}

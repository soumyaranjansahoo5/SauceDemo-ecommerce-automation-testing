package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton ConfigReader — loads config.properties once and provides
 * typed getters used throughout the framework.
 */
public class ConfigReader {

    private static Properties properties;
    private static ConfigReader instance;

    private static final String CONFIG_PATH =
            "src/test/resources/config.properties";

    private ConfigReader() {
        loadProperties();
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot load config.properties from: " + CONFIG_PATH, e);
        }
    }

    public String getBaseUrl()         { return properties.getProperty("base.url"); }
    public String getApiBaseUrl()      { return properties.getProperty("api.base.url"); }
    public String getBrowser()         { return properties.getProperty("browser", "chrome"); }
    public boolean isHeadless()        { return Boolean.parseBoolean(properties.getProperty("headless", "false")); }
    public int getImplicitWait()       { return Integer.parseInt(properties.getProperty("implicit.wait", "10")); }
    public int getExplicitWait()       { return Integer.parseInt(properties.getProperty("explicit.wait", "15")); }
    public int getPageLoadTimeout()    { return Integer.parseInt(properties.getProperty("page.load.timeout", "30")); }
    public String getValidUsername()   { return properties.getProperty("valid.username"); }
    public String getValidPassword()   { return properties.getProperty("valid.password"); }
    public String getLockedUsername()  { return properties.getProperty("locked.username"); }
    public String getInvalidUsername() { return properties.getProperty("invalid.username"); }
    public String getInvalidPassword() { return properties.getProperty("invalid.password"); }
    public String getReportPath()      { return properties.getProperty("report.path"); }
    public String getScreenshotPath()  { return properties.getProperty("screenshot.path"); }
}

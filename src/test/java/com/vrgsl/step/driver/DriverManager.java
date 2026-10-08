package com.vrgsl.step.driver;

import com.vrgsl.step.helper.ConfigHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DriverManager {

    private static final ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return threadDriver.get();
    }

    public static synchronized WebDriver createDriver() {
        if (threadDriver.get() != null) {
            return threadDriver.get();
        }

        String defaultBrowser = ConfigHelper.getString("browser", "chrome");
        String browser = System.getProperty("browser", defaultBrowser).toLowerCase();

        boolean remoteDebugEnabled = ConfigHelper.getBoolean("remoteDebug.enabled", true);
        String remoteHost = ConfigHelper.getString("remoteDebug.host", "127.0.0.1");
        int remotePort = ConfigHelper.getInt("remoteDebug.port", 9222);

        boolean isRemoteDebugOpen = false;
        if (remoteDebugEnabled && browser.contains("chrome")) {
            try (Socket socket = new Socket(remoteHost, remotePort)) {
                isRemoteDebugOpen = true;
            } catch (Exception ignored) {}
        }

        WebDriver driver;
        if (isRemoteDebugOpen) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", remoteHost + ":" + remotePort);
            driver = new ChromeDriver(options);
        } else if (browser.contains("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            List<String> ffArgs = ConfigHelper.getList("firefox.args");
            for (String arg : ffArgs) {
                options.addArguments(arg);
            }
            if (ConfigHelper.getBoolean("firefox.headless", false)) {
                options.addArguments("-headless");
            }
            driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();

            // Load arguments from YAML
            List<String> chromeArgs = ConfigHelper.getList("chrome.args");
            for (String arg : chromeArgs) {
                options.addArguments(arg);
            }

            if (ConfigHelper.getBoolean("chrome.headless", false)) {
                options.addArguments("--headless=new");
            }

            // User data directory
            String rawUserDataDir = ConfigHelper.getString("chrome.userDataDir", "");
            if (!rawUserDataDir.isEmpty()) {
                options.addArguments("--user-data-dir=" + rawUserDataDir);
            }

            // Preferences
            Map<String, Object> prefs = ConfigHelper.getMap("chrome.prefs");
            if (!prefs.isEmpty()) {
                options.setExperimentalOption("prefs", prefs);
            }

            // Exclude switches
            List<String> excludeSwitches = ConfigHelper.getList("chrome.excludeSwitches");
            if (!excludeSwitches.isEmpty()) {
                options.setExperimentalOption("excludeSwitches", excludeSwitches);
            }

            // Automation extension
            boolean useAutomationExtension = ConfigHelper.getBoolean("chrome.useAutomationExtension", false);
            options.setExperimentalOption("useAutomationExtension", useAutomationExtension);

            driver = new ChromeDriver(options);

            // Stealth script injection
            boolean stealthEnabled = ConfigHelper.getBoolean("chrome.stealth.enabled", true);
            if (stealthEnabled) {
                String stealthJs = ConfigHelper.getString("chrome.stealth.script", "");
                if (!stealthJs.isEmpty()) {
                    ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",
                            Map.of("source", stealthJs));
                }
            }
        }

        // Timeouts
        int pageLoadTimeout = ConfigHelper.getInt("timeouts.pageLoadTimeout", 30);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        threadDriver.set(driver);
        return driver;
    }

    public static synchronized void quitDriver() {
        WebDriver driver = threadDriver.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
            threadDriver.remove();
        }
    }
}

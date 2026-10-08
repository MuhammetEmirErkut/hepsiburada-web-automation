package com.vrgsl.step.driver;

import com.thoughtworks.gauge.*;
import com.vrgsl.step.helper.ConfigHelper;
import com.vrgsl.step.helper.ElementHelper;
import com.vrgsl.step.helper.ValueHelper;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class DriverHooks {

    @BeforeSuite
    public void beforeSuite() {
        ConfigHelper.loadConfigurations();
        ElementHelper.initElementList();
        ValueHelper.initValueList();
    }

    @BeforeScenario
    public void beforeScenario(ExecutionContext context) {
        DriverManager.createDriver();
        Gauge.writeMessage("🚀 Started Scenario: " + context.getCurrentScenario().getName());
    }

    @AfterScenario
    public void afterScenario(ExecutionContext context) {
        try {
            if (context.getCurrentScenario().getIsFailing()) {
                WebDriver driver = DriverManager.getDriver();
                if (driver instanceof TakesScreenshot takesScreenshot) {
                    byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                    Gauge.captureScreenshot();
                }
                Gauge.writeMessage("❌ Scenario Failed: " + context.getCurrentScenario().getName());
            } else {
                Gauge.writeMessage("✅ Scenario Passed: " + context.getCurrentScenario().getName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}

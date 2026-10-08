package com.vrgsl.step.helper;

import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CdpHelper {

    private static final Random random = new Random();

    public static void humanClick(WebDriver driver, WebElement element) {
        if (driver instanceof ChromeDriver chromeDriver) {
            Rectangle rect = element.getRect();
            int x = rect.getX() + rect.getWidth() / 2;
            int y = rect.getY() + rect.getHeight() / 2;

            Map<String, Object> move = new HashMap<>();
            move.put("type", "mouseMoved");
            move.put("x", x);
            move.put("y", y);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", move);

            pause(100 + random.nextInt(100));

            Map<String, Object> down = new HashMap<>();
            down.put("type", "mousePressed");
            down.put("button", "left");
            down.put("clickCount", 1);
            down.put("x", x);
            down.put("y", y);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", down);

            pause(80 + random.nextInt(70));

            Map<String, Object> up = new HashMap<>();
            up.put("type", "mouseReleased");
            up.put("button", "left");
            up.put("clickCount", 1);
            up.put("x", x);
            up.put("y", y);
            chromeDriver.executeCdpCommand("Input.dispatchMouseEvent", up);
        } else {
            new Actions(driver).moveToElement(element).pause(Duration.ofMillis(200)).click().perform();
        }
    }

    public static void humanType(WebDriver driver, WebElement element, String text) {
        humanClick(driver, element);
        pause(200 + random.nextInt(150));
        element.clear();

        if (driver instanceof ChromeDriver chromeDriver) {
            for (char c : text.toCharArray()) {
                Map<String, Object> params = new HashMap<>();
                params.put("type", "char");
                params.put("text", String.valueOf(c));
                chromeDriver.executeCdpCommand("Input.dispatchKeyEvent", params);
                pause(60 + random.nextInt(80));
            }
        } else {
            element.sendKeys(text);
        }
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}

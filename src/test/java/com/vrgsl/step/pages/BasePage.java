package com.vrgsl.step.pages;

import com.vrgsl.step.helper.ConfigHelper;
import com.vrgsl.step.helper.ElementHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor js;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigHelper.getInt("timeouts.explicitWait", 15);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public WebElement findElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findElement(String elementKey) {
        return findElement(ElementHelper.getBy(elementKey));
    }

    public WebElement findVisibleElement(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public WebElement findVisibleElement(String elementKey) {
        return findVisibleElement(ElementHelper.getBy(elementKey));
    }

    public List<WebElement> findElements(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    public List<WebElement> findElements(String elementKey) {
        return findElements(ElementHelper.getBy(elementKey));
    }

    public void click(String elementKey) {
        By by = ElementHelper.getBy(elementKey);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
            element.click();
        } catch (ElementClickInterceptedException | TimeoutException e) {
            WebElement element = findElement(by);
            js.executeScript("arguments[0].click();", element);
        }
    }

    public void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    public void hover(String elementKey) {
        WebElement element = findVisibleElement(elementKey);
        actions.moveToElement(element).perform();
    }

    public void typeText(String elementKey, String text) {
        WebElement element = findVisibleElement(elementKey);
        element.clear();
        element.sendKeys(text);
    }

    public void humanType(String elementKey, String text) {
        WebElement element = findVisibleElement(elementKey);
        element.click();
        element.clear();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(40 + (long) (Math.random() * 40));
            } catch (InterruptedException ignored) {}
        }
    }

    public String getText(String elementKey) {
        return findVisibleElement(elementKey).getText().trim();
    }

    public boolean isElementVisible(String elementKey, int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(ElementHelper.getBy(elementKey))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollToElement(String elementKey) {
        WebElement element = findElement(elementKey);
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void switchToLastTab() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}
        Set<String> handles = driver.getWindowHandles();
        List<String> handleList = new ArrayList<>(handles);
        driver.switchTo().window(handleList.get(handleList.size() - 1));
    }

    public void dismissCookiesIfPresent() {
        try {
            if (isElementVisible("cookieAcceptButton", 3)) {
                click("cookieAcceptButton");
            }
        } catch (Exception ignored) {}
    }

    public void waitForPageLoad() {
        try {
            wait.until(d -> js.executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }
}

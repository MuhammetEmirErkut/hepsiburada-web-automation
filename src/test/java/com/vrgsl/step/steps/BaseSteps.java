package com.vrgsl.step.steps;

import com.thoughtworks.gauge.Step;
import com.vrgsl.step.driver.DriverManager;
import com.vrgsl.step.helper.ConfigHelper;
import com.vrgsl.step.helper.ElementHelper;
import com.vrgsl.step.helper.StoreHelper;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseSteps {

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    protected WebDriverWait getDefaultWait() {
        int timeout = ConfigHelper.getInt("timeouts.explicitWait", 15);
        return getWait(timeout);
    }

    protected Actions getActions() {
        return new Actions(getDriver());
    }

    protected JavascriptExecutor getJs() {
        return (JavascriptExecutor) getDriver();
    }

    public WebElement findElement(By by) {
        return getDefaultWait().until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findVisibleElement(By by) {
        return getDefaultWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public List<WebElement> findElements(By by) {
        return getDefaultWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    // ==========================================
    // Navigation Steps
    // ==========================================

    @Step({"Go to url <url>", "<url> adresine git"})
    public void goToUrl(String url) {
        getDriver().get(url);
    }

    @Step({"Refresh page", "Sayfayı yenile"})
    public void refreshPage() {
        getDriver().navigate().refresh();
    }

    @Step({"Go back", "Önceki sayfaya git"})
    public void goBack() {
        getDriver().navigate().back();
    }

    @Step({"Go forward", "Sonraki sayfaya git"})
    public void goForward() {
        getDriver().navigate().forward();
    }

    // ==========================================
    // Wait Steps
    // ==========================================

    @Step({"Wait <seconds> seconds", "<seconds> saniye bekle"})
    public void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ignored) {}
    }

    @Step({"Wait until element <key> is visible", "<key> elementi görünür olana kadar bekle"})
    public void waitUntilVisible(String key) {
        getDefaultWait().until(ExpectedConditions.visibilityOfElementLocated(ElementHelper.getBy(key)));
    }

    @Step({"Wait until element <key> is clickable", "<key> elementi tıklanabilir olana kadar bekle"})
    public void waitUntilClickable(String key) {
        getDefaultWait().until(ExpectedConditions.elementToBeClickable(ElementHelper.getBy(key)));
    }

    @Step({"Wait until element <key> is invisible", "<key> elementi kaybolana kadar bekle"})
    public void waitUntilInvisible(String key) {
        getDefaultWait().until(ExpectedConditions.invisibilityOfElementLocated(ElementHelper.getBy(key)));
    }

    // ==========================================
    // Interaction Steps
    // ==========================================

    @Step({"Click element <key>", "<key> elementine tıkla"})
    public void clickElement(String key) {
        By by = ElementHelper.getBy(key);
        try {
            WebElement element = getDefaultWait().until(ExpectedConditions.elementToBeClickable(by));
            element.click();
        } catch (ElementClickInterceptedException | TimeoutException e) {
            WebElement element = findElement(by);
            getJs().executeScript("arguments[0].click();", element);
        }
    }

    @Step({"Click element <key> with param <param>", "<key> elementine <param> parametresiyle tıkla"})
    public void clickElementWithParam(String key, String param) {
        By by = ElementHelper.getBy(key, param);
        try {
            WebElement element = getDefaultWait().until(ExpectedConditions.elementToBeClickable(by));
            element.click();
        } catch (Exception e) {
            WebElement element = findElement(by);
            getJs().executeScript("arguments[0].click();", element);
        }
    }

    @Step({"Click element <key> if displayed", "<key> elementine varsa tıkla"})
    public void clickIfDisplayed(String key) {
        try {
            By by = ElementHelper.getBy(key);
            int shortWaitSeconds = ConfigHelper.getInt("timeouts.shortWait", 3);
            WebElement element = getWait(shortWaitSeconds).until(ExpectedConditions.elementToBeClickable(by));
            element.click();
        } catch (Exception ignored) {}
    }

    @Step({"Force click element <key> with JS", "<key> elementine JS ile tıkla"})
    public void forceClickJs(String key) {
        WebElement element = findElement(ElementHelper.getBy(key));
        getJs().executeScript("arguments[0].click();", element);
    }

    @Step({"Double click element <key>", "<key> elementine çift tıkla"})
    public void doubleClick(String key) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        getActions().doubleClick(element).perform();
    }

    @Step({"Right click element <key>", "<key> elementine sağ tıkla"})
    public void rightClick(String key) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        getActions().contextClick(element).perform();
    }

    @Step({"Hover element <key>", "<key> elementinin üzerine gel"})
    public void hoverElement(String key) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        getActions().moveToElement(element).perform();
    }

    @Step({"Write <text> to element <key>", "<key> elementine <text> değerini yaz"})
    public void writeText(String key, String text) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        element.clear();
        element.sendKeys(text);
    }

    @Step({"Write <text> to element <key> and press ENTER", "<key> elementine <text> değerini yaz ve ENTER bas"})
    public void writeTextAndEnter(String key, String text) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        element.clear();
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
    }

    @Step({"Clear text in element <key>", "<key> elementinin metnini temizle"})
    public void clearText(String key) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        element.clear();
    }

    @Step({"Select <text> from dropdown <key>", "<key> dropdownından <text> seç"})
    public void selectFromDropdown(String key, String text) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    // ==========================================
    // Scroll Steps
    // ==========================================

    @Step({"Scroll to element <key>", "<key> elementine kaydır"})
    public void scrollToElement(String key) {
        WebElement element = findElement(ElementHelper.getBy(key));
        getJs().executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    @Step({"Scroll to bottom of page", "Sayfanın en altına kaydır"})
    public void scrollToBottom() {
        getJs().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @Step({"Scroll to top of page", "Sayfanın en üstüne kaydır"})
    public void scrollToTop() {
        getJs().executeScript("window.scrollTo(0, 0);");
    }

    // ==========================================
    // Assertion Steps
    // ==========================================

    @Step({"Check if element <key> is displayed", "<key> elementinin görünür olduğunu kontrol et"})
    public void checkIfElementDisplayed(String key) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        Assertions.assertThat(element.isDisplayed())
                .as("Element [%s] should be visible", key)
                .isTrue();
    }

    @Step({"Check if element <key> is not displayed", "<key> elementinin görünür olmadığını kontrol et"})
    public void checkIfElementNotDisplayed(String key) {
        int shortWait = ConfigHelper.getInt("timeouts.shortWait", 3);
        try {
            boolean visible = getWait(shortWait).until(ExpectedConditions.visibilityOfElementLocated(ElementHelper.getBy(key))).isDisplayed();
            Assertions.assertThat(visible).as("Element [%s] should NOT be visible", key).isFalse();
        } catch (Exception ignored) {
            // Element not visible as expected
        }
    }

    @Step({"Check if element <key> contains text <text>", "<key> elementinin <text> metnini içerdiğini kontrol et"})
    public void checkIfElementContainsText(String key, String text) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        Assertions.assertThat(element.getText().trim())
                .as("Element [%s] text should contain [%s]", key, text)
                .containsIgnoringCase(text);
    }

    @Step({"Check if element <key> text equals <text>", "<key> elementinin metninin <text> olduğunu doğrula"})
    public void checkIfElementTextEquals(String key, String text) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        Assertions.assertThat(element.getText().trim())
                .as("Element [%s] text should equal [%s]", key, text)
                .isEqualTo(text);
    }

    @Step({"Check if current url contains <expected>", "Mevcut adresin <expected> içerdiğini kontrol et"})
    public void checkUrlContains(String expected) {
        Assertions.assertThat(getDriver().getCurrentUrl())
                .as("Current URL should contain [%s]", expected)
                .contains(expected);
    }

    @Step({"Check if current title contains <expected>", "Sayfa başlığının <expected> içerdiğini kontrol et"})
    public void checkTitleContains(String expected) {
        Assertions.assertThat(getDriver().getTitle())
                .as("Page title should contain [%s]", expected)
                .contains(expected);
    }

    // ==========================================
    // Store & Context Steps
    // ==========================================

    @Step({"Save text of element <key> to scenario store with key <storeKey>", "<key> elementinin metnini <storeKey> anahtarıyla senaryo hafızasına kaydet"})
    public void saveTextToStore(String key, String storeKey) {
        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        String text = element.getText().trim();
        StoreHelper.putScenario(storeKey, text);
    }

    @Step({"Verify element <key> text contains stored value <storeKey>", "<key> elementinin <storeKey> hafıza değerini içerdiğini doğrula"})
    public void verifyElementContainsStoredValue(String key, String storeKey) {
        String storedValue = StoreHelper.getScenarioString(storeKey, "");
        Assertions.assertThat(storedValue)
                .as("Stored value for key [%s] should not be empty", storeKey)
                .isNotEmpty();

        WebElement element = findVisibleElement(ElementHelper.getBy(key));
        Assertions.assertThat(element.getText().trim())
                .as("Element [%s] text should contain stored value [%s]", key, storedValue)
                .containsIgnoringCase(storedValue);
    }

    // ==========================================
    // Window, Tab, IFrame & Alert Steps
    // ==========================================

    @Step({"Switch to new tab", "Yeni sekmeye geç", "Son sekmeye geç"})
    public void switchToLastTab() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}
        Set<String> handles = getDriver().getWindowHandles();
        List<String> handleList = new ArrayList<>(handles);
        getDriver().switchTo().window(handleList.get(handleList.size() - 1));
    }

    @Step({"Close current tab and switch to main tab", "Mevcut sekmeyi kapat ve ana sekmeye geç"})
    public void closeTabAndSwitchToMain() {
        Set<String> handles = getDriver().getWindowHandles();
        List<String> handleList = new ArrayList<>(handles);
        if (handleList.size() > 1) {
            getDriver().close();
            getDriver().switchTo().window(handleList.get(0));
        }
    }

    @Step({"Switch to iframe <key>", "<key> iframe alanına geç"})
    public void switchToIframe(String key) {
        WebElement iframe = findElement(ElementHelper.getBy(key));
        getDriver().switchTo().frame(iframe);
    }

    @Step({"Switch to default content", "Varsayılan sayfaya dön"})
    public void switchToDefaultContent() {
        getDriver().switchTo().defaultContent();
    }

    @Step({"Accept alert", "Uyarıyı kabul et"})
    public void acceptAlert() {
        getDefaultWait().until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Step({"Dismiss alert", "Uyarıyı reddet"})
    public void dismissAlert() {
        getDefaultWait().until(ExpectedConditions.alertIsPresent()).dismiss();
    }
}

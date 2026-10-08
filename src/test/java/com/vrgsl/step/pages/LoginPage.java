package com.vrgsl.step.pages;

import com.vrgsl.step.helper.CdpHelper;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String email) {
        dismissCookiesIfPresent();

        WebElement emailElem = findVisibleElement("usernameInput");
        scrollToElement(emailElem);
        CdpHelper.humanType(driver, emailElem, email);
        CdpHelper.pause(400);

        List<WebElement> passInputs = findElements("passwordInput");
        if (passInputs.isEmpty() || !passInputs.get(0).isDisplayed()) {
            click("loginSubmitButton");
        }
    }

    public void enterPassword(String password) {
        WebElement passElem = findVisibleElement("passwordInput");
        scrollToElement(passElem);
        CdpHelper.humanType(driver, passElem, password);
        CdpHelper.pause(400);

        try {
            click("emailLoginSubmitButton");
        } catch (Exception e) {
            passElem.sendKeys(Keys.ENTER);
        }
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
    }

    public boolean isRedirectedAwayFromLogin() {
        for (int i = 0; i < 40; i++) {
            if (!driver.getCurrentUrl().contains("giris.hepsiburada.com")) {
                return true;
            }
            CdpHelper.pause(1000);
        }
        return false;
    }

    public String getErrorMessageIfPresent() {
        try {
            List<WebElement> errors = findElements("loginErrorMessage");
            for (WebElement err : errors) {
                if (err.isDisplayed() && !err.getText().isBlank()) {
                    return err.getText().trim();
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}

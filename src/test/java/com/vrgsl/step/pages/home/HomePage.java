package com.vrgsl.step.pages.home;

import com.vrgsl.step.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void openHomePage(String url) {
        navigateTo(url);
        dismissCookiesIfPresent();
    }

    public void navigateToLoginPage() {
        hover("myAccountDropdown");
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        click("loginButton");
    }

    public void searchProduct(String keyword) {
        dismissCookiesIfPresent();
        try {
            click("searchBoxContainer");
            Thread.sleep(500);

            WebElement activeInput = findVisibleElement("searchBoxInput");
            activeInput.clear();
            activeInput.sendKeys(keyword);
            Thread.sleep(300);
            activeInput.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            WebElement input = findElement("searchBoxInput");
            input.clear();
            input.sendKeys(keyword);
            input.sendKeys(Keys.ENTER);
        }
    }

    public boolean isUserLoggedIn() {
        try {
            List<WebElement> headers = findElements("accountUsernameHeader");
            for (WebElement h : headers) {
                if (h.isDisplayed() && !h.getText().trim().equalsIgnoreCase("Giriş Yap") && !h.getText().trim().isEmpty()) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return !driver.getCurrentUrl().contains("giris.hepsiburada.com");
    }
}

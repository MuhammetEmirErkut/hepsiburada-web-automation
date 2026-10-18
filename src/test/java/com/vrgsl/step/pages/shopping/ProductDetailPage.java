package com.vrgsl.step.pages.shopping;

import com.vrgsl.step.pages.BasePage;
import com.vrgsl.step.helper.CdpHelper;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public void switchToProductDetailTab() {
        switchToLastTab();
        waitForPageLoad();
        dismissCookiesIfPresent();
    }

    public void clickAddToCart() {
        dismissCookiesIfPresent();
        CdpHelper.pause(1000);
        scrollToElement("addToCartButton");
        click("addToCartButton");
    }

    public boolean isAddedToCartConfirmationVisible() {
        return isElementVisible("cartAddedToastModal", 10);
    }

    public void navigateToCartPage() {
        CdpHelper.pause(1000);
        if (isElementVisible("goToCartButtonInModal", 5)) {
            click("goToCartButtonInModal");
        } else {
            click("headerCartIcon");
        }
    }
}

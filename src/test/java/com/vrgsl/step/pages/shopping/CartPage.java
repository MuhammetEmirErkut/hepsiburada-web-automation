package com.vrgsl.step.pages.shopping;

import com.vrgsl.step.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageLoaded() {
        return isElementVisible("cartContainer", 10) || !findElements("cartItemTitles").isEmpty();
    }

    public boolean isProductPresentInCart(String expectedProductName) {
        List<WebElement> items = findElements("cartItemTitles");
        if (items.isEmpty()) {
            return false;
        }

        String normalizedExpected = expectedProductName.toLowerCase().replaceAll("[^a-zA-Z0-9çğıöşü]", "");
        String[] words = expectedProductName.toLowerCase().split("\\s+");

        for (WebElement item : items) {
            String itemText = item.getText().toLowerCase();
            String normalizedItem = itemText.replaceAll("[^a-zA-Z0-9çğıöşü]", "");
            if (normalizedItem.contains(normalizedExpected) || normalizedExpected.contains(normalizedItem)) {
                return true;
            }
            int matchCount = 0;
            for (String w : words) {
                if (w.length() > 3 && itemText.contains(w)) {
                    matchCount++;
                }
            }
            if (matchCount >= 2) {
                return true;
            }
        }
        return false;
    }
}

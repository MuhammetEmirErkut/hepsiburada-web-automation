package com.vrgsl.step.pages;

import com.vrgsl.step.helper.ElementHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage extends BasePage {

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isResultsListDisplayed() {
        return findElements("productCards").size() > 0;
    }

    /**
     * 2. satır 1. ürünü bulur, başlığını okur ve tıklar.
     * Standart 4'lü grid yapısında 2. satır 1. ürün index 4 (5. ürün) olur.
     */
    public String selectFirstProductInSecondRow() {
        List<WebElement> products = findElements("productCards");
        if (products.size() < 5) {
            throw new IllegalStateException("Arama sonuçlarında 2. satır 1. ürünü seçmek için en az 5 ürün bulunmalıdır.");
        }

        int targetIndex = 4; // 2. satır, 1. ürün
        WebElement targetProduct = products.get(targetIndex);

        scrollToElement(targetProduct);

        String productTitle = "";
        try {
            WebElement titleElem = targetProduct.findElement(ElementHelper.getBy("productTitleInCard"));
            productTitle = titleElem.getText().trim();
        } catch (Exception e) {
            productTitle = targetProduct.getText().split("\n")[0];
        }

        click(targetProduct);
        return productTitle;
    }
}

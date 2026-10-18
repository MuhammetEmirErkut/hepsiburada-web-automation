package com.vrgsl.step.pages.shopping;

import com.vrgsl.step.pages.BasePage;
import com.vrgsl.step.helper.ElementHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchResultsPage extends BasePage {

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isResultsListDisplayed() {
        return findElements("productCards").size() > 0;
    }

    /**
     * Finds and clicks the first product in the second row dynamically using CSS Grid template columns.
     */
    public String selectFirstProductInSecondRow() {
        WebElement targetCard = getElementInGrid("productListContainer", "productCards", 2, 1);
        scrollToElement(targetCard);

        WebElement titleElem = targetCard.findElement(ElementHelper.getBy("productTitleInCard"));
        String productTitle = titleElem.getText().trim();

        WebElement productLink = targetCard.findElement(ElementHelper.getBy("productCardLink"));
        click(productLink);

        return productTitle;
    }
}

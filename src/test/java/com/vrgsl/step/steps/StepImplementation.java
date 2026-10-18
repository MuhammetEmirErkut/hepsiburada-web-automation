package com.vrgsl.step.steps;

import com.thoughtworks.gauge.Step;
import com.vrgsl.step.helper.ConfigHelper;
import com.vrgsl.step.helper.StoreHelper;
import com.vrgsl.step.helper.ValueHelper;
import com.vrgsl.step.pages.BasePage;
import com.vrgsl.step.pages.home.HomePage;
import com.vrgsl.step.pages.login.LoginPage;
import com.vrgsl.step.pages.shopping.CartPage;
import com.vrgsl.step.pages.shopping.ProductDetailPage;
import com.vrgsl.step.pages.shopping.SearchResultsPage;
import org.assertj.core.api.Assertions;

public class StepImplementation extends BaseSteps {

    private static final String SELECTED_PRODUCT_NAME_KEY = "SELECTED_PRODUCT_NAME";

    // Page Object Factory
    private HomePage homePage() {
        return new HomePage(getDriver());
    }

    private LoginPage loginPage() {
        return new LoginPage(getDriver());
    }

    private SearchResultsPage searchResultsPage() {
        return new SearchResultsPage(getDriver());
    }

    private ProductDetailPage productDetailPage() {
        return new ProductDetailPage(getDriver());
    }

    private CartPage cartPage() {
        return new CartPage(getDriver());
    }

    @Step({"Open Hepsiburada home page", "Hepsiburada anasayfasını aç"})
    public void openHomePage() {
        String baseUrl = ConfigHelper.getString("baseUrl", "https://www.hepsiburada.com");
        homePage().openHomePage(baseUrl);
    }

    @Step({"Open Hepsiburada home page <urlKey>", "<urlKey> Hepsiburada anasayfasını aç"})
    public void openHomePage(String urlKey) {
        String resolvedUrl = ConfigHelper.getString(urlKey, ValueHelper.getValue(urlKey));
        homePage().openHomePage(resolvedUrl);
    }

    @Step({"Navigate to login page", "Giriş yap sayfasına git"})
    public void navigateToLogin() {
        homePage().navigateToLoginPage();
    }

    @Step({"Enter email <email>", "E-posta gir <email>", "<email> e-postasını gir"})
    public void enterEmail(String email) {
        String resolvedEmail = ValueHelper.getValue(email);
        loginPage().enterEmail(resolvedEmail);
    }

    @Step({"Enter password <password>", "Şifre gir <password>", "<password> şifresini gir"})
    public void enterPassword(String password) {
        String resolvedPassword = ValueHelper.getValue(password);
        loginPage().enterPassword(resolvedPassword);
    }

    @Step({"Login with username <username> and password <password>", "<username> kullanıcı adı ve <password> şifresi ile giriş yap"})
    public void login(String username, String password) {
        enterEmail(username);
        enterPassword(password);
    }

    @Step({"Login with default credentials", "Varsayılan kullanıcı ile giriş yap"})
    public void loginWithDefaultCredentials() {
        login("defaultUserEmail", "defaultUserPassword");
    }

    @Step({"Verify that user is successfully logged in", "Kullanıcının başarılı bir şekilde giriş yaptığını doğrula"})
    public void verifyLogin() {
        boolean redirected = loginPage().isRedirectedAwayFromLogin();
        String detectedError = loginPage().getErrorMessageIfPresent();

        homePage().dismissCookiesIfPresent();

        Assertions.assertThat(redirected)
                .as("User should be logged in successfully. Current URL: [%s]. Error: [%s]",
                        getDriver().getCurrentUrl(),
                        detectedError.isEmpty() ? "None" : detectedError)
                .isTrue();

        Assertions.assertThat(homePage().isUserLoggedIn())
                .as("User login state should be confirmed on the page.")
                .isTrue();
    }

    @Step({"Search for product <keyword>", "<keyword> ürününü ara"})
    public void searchProduct(String keyword) {
        String resolvedKeyword = ValueHelper.getValue(keyword);
        homePage().searchProduct(resolvedKeyword);
    }

    @Step({"Verify search results are displayed", "Arama sonuçlarının görüntülendiğini doğrula"})
    public void verifySearchResults() {
        Assertions.assertThat(searchResultsPage().isResultsListDisplayed())
                .as("Search results should be displayed and product list should be visible.")
                .isTrue();
    }

    @Step({"Select the first product in the second row and navigate to product details", "İkinci satır ilk ürünü seç ve detay sayfasına git"})
    public void selectSecondRowFirstProduct() {
        String productTitle = searchResultsPage().selectFirstProductInSecondRow();

        // Save product title to Scenario DataStore for cart verification
        StoreHelper.putScenario(SELECTED_PRODUCT_NAME_KEY, productTitle);

        // Switch to product detail tab
        productDetailPage().switchToProductDetailTab();
    }

    @Step({"Add product to cart and verify confirmation", "Ürünü sepete ekle ve onay mesajını doğrula"})
    public void addProductToCart() {
        productDetailPage().clickAddToCart();
        Assertions.assertThat(productDetailPage().isAddedToCartConfirmationVisible())
                .as("Add-to-cart confirmation toast/modal should be visible.")
                .isTrue();
    }

    @Step({"Navigate to shopping cart page", "Sepetim sayfasına git"})
    public void navigateToCart() {
        productDetailPage().navigateToCartPage();
        Assertions.assertThat(cartPage().isCartPageLoaded())
                .as("Cart page should load successfully.")
                .isTrue();
    }

    @Step({"Verify that the selected product is present in the cart", "Seçilen ürünün sepette olduğunu doğrula"})
    public void verifyProductInCart() {
        String expectedProductName = StoreHelper.getScenarioString(SELECTED_PRODUCT_NAME_KEY, "");

        Assertions.assertThat(expectedProductName)
                .as("Stored selected product name should not be empty.")
                .isNotEmpty();

        boolean isPresent = cartPage().isProductPresentInCart(expectedProductName);

        Assertions.assertThat(isPresent)
                .as("Added product [%s] should be present in the cart.", expectedProductName)
                .isTrue();
    }
}

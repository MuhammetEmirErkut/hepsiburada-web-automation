package com.vrgsl.step.steps;

import com.thoughtworks.gauge.Step;
import com.vrgsl.step.helper.ConfigHelper;
import com.vrgsl.step.helper.StoreHelper;
import com.vrgsl.step.helper.ValueHelper;
import com.vrgsl.step.pages.*;
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
                .as("Kullanıcı başarılı şekilde login olmalıdır. Mevcut URL: [%s]. Hata: [%s]",
                        getDriver().getCurrentUrl(),
                        detectedError.isEmpty() ? "Yok" : detectedError)
                .isTrue();

        Assertions.assertThat(homePage().isUserLoggedIn())
                .as("Kullanıcının giriş yaptığı sayfada doğrulanmalıdır.")
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
                .as("Arama sonuçları kullanıcı girdisine uygun gelmeli ve ürün listesi görünür olmalıdır.")
                .isTrue();
    }

    @Step({"Select the first product in the second row and navigate to product details", "İkinci satır ilk ürünü seç ve detay sayfasına git"})
    public void selectSecondRowFirstProduct() {
        String productTitle = searchResultsPage().selectFirstProductInSecondRow();

        // Ürün adını sonraki adımda sepette doğrulamak için Gauge Scenario DataStore'a kaydet
        StoreHelper.putScenario(SELECTED_PRODUCT_NAME_KEY, productTitle);

        // 2. satır -> 1. ürün sayfasına yönlen (yeni sekmeye geç)
        productDetailPage().switchToProductDetailTab();
    }

    @Step({"Add product to cart and verify confirmation", "Ürünü sepete ekle ve onay mesajını doğrula"})
    public void addProductToCart() {
        productDetailPage().clickAddToCart();
        Assertions.assertThat(productDetailPage().isAddedToCartConfirmationVisible())
                .as("Sepete eklendiğine dair toast / onay mesajı görünür olmalıdır.")
                .isTrue();
    }

    @Step({"Navigate to shopping cart page", "Sepetim sayfasına git"})
    public void navigateToCart() {
        productDetailPage().navigateToCartPage();
        Assertions.assertThat(cartPage().isCartPageLoaded())
                .as("Sepet ekranı başarıyla yüklenmelidir.")
                .isTrue();
    }

    @Step({"Verify that the selected product is present in the cart", "Seçilen ürünün sepette olduğunu doğrula"})
    public void verifyProductInCart() {
        String expectedProductName = StoreHelper.getScenarioString(SELECTED_PRODUCT_NAME_KEY, "");

        Assertions.assertThat(expectedProductName)
                .as("Hafızadaki seçilen ürün adı boş olmamalıdır.")
                .isNotEmpty();

        boolean isPresent = cartPage().isProductPresentInCart(expectedProductName);

        Assertions.assertThat(isPresent)
                .as("Eklenen ürünün adı [%s] sepette görünür ve eşleşmiş olmalıdır.", expectedProductName)
                .isTrue();
    }
}

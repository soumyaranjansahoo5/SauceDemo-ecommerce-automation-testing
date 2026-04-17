package tests;

import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CartTest — cart management: add, remove, persist, and navigation.
 */
public class CartTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.open(config.getBaseUrl());
        productsPage = loginPage.loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ── TC-C01: Navigate to cart ──────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-C01: Cart page should be accessible from product listing")
    public void testNavigateToCart() {
        ExtentReportManager.createTest("TC-C01: Navigate to Cart",
                "Verify cart page loads correctly");

        CartPage cartPage = productsPage.goToCart();

        Assert.assertTrue(cartPage.isCartPageDisplayed(),
                "Cart page title should be 'Your Cart'");
        Assert.assertTrue(cartPage.getCurrentUrl().contains("cart"),
                "URL should contain 'cart'");
    }

    // ── TC-C02: Cart is empty by default ─────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-C02: Cart should be empty for a fresh session")
    public void testCartEmptyOnFreshLogin() {
        ExtentReportManager.createTest("TC-C02: Empty Cart",
                "Verify new session starts with empty cart");

        CartPage cartPage = productsPage.goToCart();

        Assert.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty on fresh login");
        Assert.assertEquals(cartPage.getCartItemCount(), 0,
                "Cart item count should be 0");
    }

    // ── TC-C03: Added items appear in cart ────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-C03: Items added on product page should appear in cart")
    public void testAddedItemsAppearInCart() {
        ExtentReportManager.createTest("TC-C03: Items in Cart",
                "Verify added products appear in cart page");

        productsPage.addProductToCartByName("Sauce Labs Backpack");
        productsPage.addProductToCartByName("Sauce Labs Bike Light");

        CartPage cartPage = productsPage.goToCart();

        Assert.assertEquals(cartPage.getCartItemCount(), 2,
                "Cart should contain 2 items");
        Assert.assertTrue(cartPage.isItemInCart("Sauce Labs Backpack"),
                "Backpack should be in cart");
        Assert.assertTrue(cartPage.isItemInCart("Sauce Labs Bike Light"),
                "Bike Light should be in cart");

        ExtentReportManager.getTest().info("Cart contents: " + cartPage.getCartItemNames());
    }

    // ── TC-C04: Continue shopping ─────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-C04: Continue Shopping button returns to products page")
    public void testContinueShopping() {
        ExtentReportManager.createTest("TC-C04: Continue Shopping",
                "Verify Continue Shopping button navigates back to products");

        productsPage.addProductToCartByName("Sauce Labs Backpack");
        CartPage cartPage = productsPage.goToCart();

        ProductsPage returnPage = cartPage.continueShopping();

        Assert.assertTrue(returnPage.isProductsPageDisplayed(),
                "Should return to Products page");
        Assert.assertEquals(returnPage.getCartItemCount(), 1,
                "Cart count should persist after returning to products");
    }

    // ── TC-C05: Prices match between pages ───────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-C05: Product prices shown in cart must match product listing prices")
    public void testCartPricesMatchProductPrices() {
        ExtentReportManager.createTest("TC-C05: Price Consistency",
                "Verify prices in cart match product listing prices");

        productsPage.sortBy("Name (A to Z)");
        // Add the cheapest product (Sauce Labs Bike Light — $9.99)
        productsPage.addProductToCartByName("Sauce Labs Bike Light");

        CartPage cartPage = productsPage.goToCart();
        double cartPrice = cartPage.getCartItemPrices().get(0);

        Assert.assertEquals(cartPrice, 9.99, 0.001,
                "Cart price should match product listing price of $9.99");
    }
}

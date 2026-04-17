package tests;

import pages.LoginPage;
import pages.ProductsPage;
import utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ProductTest — product catalogue, sorting, and add-to-cart from listing.
 */
public class ProductTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.open(config.getBaseUrl());
        productsPage = loginPage.loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ── TC-P01: Product page loads ────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-P01: Products page should display 6 products")
    public void testProductPageLoads() {
        ExtentReportManager.createTest("TC-P01: Product Page Loads",
                "Verify products page shows correct product count");

        Assert.assertTrue(productsPage.isProductsPageDisplayed(),
                "Products page should be displayed");
        Assert.assertEquals(productsPage.getProductCount(), 6,
                "Should display exactly 6 products");

        ExtentReportManager.getTest().info("Product count: " + productsPage.getProductCount());
    }

    // ── TC-P02: Sort A→Z ─────────────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P02: Sort by Name A-Z should order products alphabetically")
    public void testSortByNameAZ() {
        ExtentReportManager.createTest("TC-P02: Sort Name A-Z",
                "Verify products sort A to Z correctly");

        productsPage.sortBy("Name (A to Z)");
        List<String> names = productsPage.getAllProductNames();

        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(
                    names.get(i).compareToIgnoreCase(names.get(i + 1)) <= 0,
                    "Products should be in A-Z order. Found: " + names.get(i) +
                            " before " + names.get(i + 1));
        }
        ExtentReportManager.getTest().info("Sorted names: " + names);
    }

    // ── TC-P03: Sort Z→A ─────────────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P03: Sort by Name Z-A should reverse alphabetical order")
    public void testSortByNameZA() {
        ExtentReportManager.createTest("TC-P03: Sort Name Z-A",
                "Verify products sort Z to A correctly");

        productsPage.sortBy("Name (Z to A)");
        List<String> names = productsPage.getAllProductNames();

        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(
                    names.get(i).compareToIgnoreCase(names.get(i + 1)) >= 0,
                    "Products should be in Z-A order");
        }
    }

    // ── TC-P04: Sort price low→high ───────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P04: Sort by Price Low to High should order by price ascending")
    public void testSortByPriceLowHigh() {
        ExtentReportManager.createTest("TC-P04: Sort Price Low-High",
                "Verify products sort by price ascending");

        productsPage.sortBy("Price (low to high)");
        List<Double> prices = productsPage.getAllProductPrices();

        for (int i = 0; i < prices.size() - 1; i++) {
            Assert.assertTrue(prices.get(i) <= prices.get(i + 1),
                    "Prices should be ascending. Found: " +
                            prices.get(i) + " before " + prices.get(i + 1));
        }
        ExtentReportManager.getTest().info("Sorted prices: " + prices);
    }

    // ── TC-P05: Sort price high→low ───────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P05: Sort by Price High to Low should order by price descending")
    public void testSortByPriceHighLow() {
        ExtentReportManager.createTest("TC-P05: Sort Price High-Low",
                "Verify products sort by price descending");

        productsPage.sortBy("Price (high to low)");
        List<Double> prices = productsPage.getAllProductPrices();

        for (int i = 0; i < prices.size() - 1; i++) {
            Assert.assertTrue(prices.get(i) >= prices.get(i + 1),
                    "Prices should be descending");
        }
    }

    // ── TC-P06: Add single item to cart ──────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-P06: Adding a product should increment cart badge count")
    public void testAddSingleProductToCart() {
        ExtentReportManager.createTest("TC-P06: Add Product to Cart",
                "Verify cart badge updates when product is added");

        productsPage.addProductToCartByName("Sauce Labs Backpack");

        Assert.assertEquals(productsPage.getCartItemCount(), 1,
                "Cart badge should show 1 after adding one item");
    }

    // ── TC-P07: Add multiple items ────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P07: Adding multiple products should correctly update cart count")
    public void testAddMultipleProductsToCart() {
        ExtentReportManager.createTest("TC-P07: Add Multiple Products",
                "Verify cart count updates correctly for multiple additions");

        productsPage.addProductToCartByName("Sauce Labs Backpack");
        productsPage.addProductToCartByName("Sauce Labs Bike Light");
        productsPage.addProductToCartByName("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(productsPage.getCartItemCount(), 3,
                "Cart badge should show 3 after adding three items");
    }

    // ── TC-P08: Remove from listing ───────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-P08: Removing a product from the listing should decrement cart count")
    public void testRemoveProductFromListing() {
        ExtentReportManager.createTest("TC-P08: Remove Product From Listing",
                "Verify cart count decrements when item removed from product page");

        productsPage.addProductToCartByName("Sauce Labs Backpack");
        productsPage.addProductToCartByName("Sauce Labs Bike Light");
        Assert.assertEquals(productsPage.getCartItemCount(), 2, "Cart should have 2 items");

        productsPage.removeProductFromCartByName("Sauce Labs Backpack");
        Assert.assertEquals(productsPage.getCartItemCount(), 1,
                "Cart should have 1 item after removal");
    }
}

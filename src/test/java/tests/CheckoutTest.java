package tests;

import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CheckoutTest — end-to-end checkout flow tests:
 *   form validation, successful order placement, boundary values.
 */
public class CheckoutTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void loginAndAddProduct() {
        LoginPage loginPage = new LoginPage();
        loginPage.open(config.getBaseUrl());
        productsPage = loginPage.loginAs(config.getValidUsername(), config.getValidPassword());
        productsPage.addProductToCartByName("Sauce Labs Backpack");
    }

    // ── TC-CHK01: Full successful checkout ───────────────────────────────────

    @Test(groups = {"smoke", "regression", "e2e"},
          description = "TC-CHK01: Full end-to-end checkout should show order confirmation")
    public void testSuccessfulCheckout() {
        ExtentReportManager.createTest("TC-CHK01: Successful Checkout",
                "End-to-end checkout from cart to confirmation");

        CartPage cartPage = productsPage.goToCart();
        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertEquals(checkoutPage.getPageTitle(), "Checkout: Your Information",
                "Step 1 title should match");

        checkoutPage.submitCustomerInfo("Soumyaranjan", "Sahoo", "751001");
        Assert.assertEquals(checkoutPage.getPageTitle(), "Checkout: Overview",
                "Step 2 title should match");

        ExtentReportManager.getTest().info("Order total: " + checkoutPage.getTotal());

        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderConfirmed(),
                "Order confirmation should be displayed");
        Assert.assertEquals(checkoutPage.getConfirmationHeader(),
                "Thank you for your order!", "Confirmation header mismatch");

        ExtentReportManager.getTest().info("Order placed. Confirmation text: " +
                checkoutPage.getConfirmationText());
    }

    // ── TC-CHK02: Empty first name validation ────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-CHK02: Empty first name should trigger validation error")
    public void testCheckoutEmptyFirstName() {
        ExtentReportManager.createTest("TC-CHK02: Empty First Name Validation",
                "Verify checkout form rejects empty first name");

        CheckoutPage checkoutPage = productsPage.goToCart()
                .proceedToCheckout();

        checkoutPage.submitCustomerInfo("", "Sahoo", "751001");

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
                "Error should be shown for empty first name");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name is required"),
                "Error message should mention First Name");
    }

    // ── TC-CHK03: Empty last name validation ─────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-CHK03: Empty last name should trigger validation error")
    public void testCheckoutEmptyLastName() {
        ExtentReportManager.createTest("TC-CHK03: Empty Last Name Validation",
                "Verify checkout form rejects empty last name");

        CheckoutPage checkoutPage = productsPage.goToCart()
                .proceedToCheckout();

        checkoutPage.submitCustomerInfo("Soumyaranjan", "", "751001");

        Assert.assertTrue(checkoutPage.isErrorDisplayed(), "Error should appear");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("Last Name is required"),
                "Error should mention Last Name");
    }

    // ── TC-CHK04: Empty postal code validation ────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-CHK04: Empty postal code should trigger validation error")
    public void testCheckoutEmptyPostalCode() {
        ExtentReportManager.createTest("TC-CHK04: Empty Postal Code Validation",
                "Verify checkout form rejects empty postal code");

        CheckoutPage checkoutPage = productsPage.goToCart()
                .proceedToCheckout();

        checkoutPage.submitCustomerInfo("Soumyaranjan", "Sahoo", "");

        Assert.assertTrue(checkoutPage.isErrorDisplayed(), "Error should appear");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("Postal Code is required"),
                "Error should mention Postal Code");
    }

    // ── TC-CHK05: Cancel from step 1 ─────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-CHK05: Cancel on checkout step 2 returns to products page")
    public void testCancelCheckout() {
        ExtentReportManager.createTest("TC-CHK05: Cancel Checkout",
                "Verify cancel on overview returns to products");

        CheckoutPage checkoutPage = productsPage.goToCart()
                .proceedToCheckout();

        checkoutPage.submitCustomerInfo("Soumyaranjan", "Sahoo", "751001");

        ProductsPage returnPage = checkoutPage.cancelCheckout();

        Assert.assertTrue(returnPage.isProductsPageDisplayed(),
                "Cancel should return to Products page");
    }

    // ── TC-CHK06: Back to products after order ───────────────────────────────

    @Test(groups = {"e2e"},
          description = "TC-CHK06: Back to Products button on confirmation should work")
    public void testBackToProductsAfterOrder() {
        ExtentReportManager.createTest("TC-CHK06: Back to Products Post-Order",
                "Verify navigation back to products after order confirmation");

        CheckoutPage checkoutPage = productsPage.goToCart()
                .proceedToCheckout();

        checkoutPage.submitCustomerInfo("Soumyaranjan", "Sahoo", "751001");
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderConfirmed(), "Order should be confirmed");

        ProductsPage homePage = checkoutPage.goBackToProducts();

        Assert.assertTrue(homePage.isProductsPageDisplayed(),
                "Should navigate back to products page");
        Assert.assertEquals(homePage.getCartItemCount(), 0,
                "Cart should be empty after order completion");
    }
}

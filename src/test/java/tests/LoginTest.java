package tests;

import pages.LoginPage;
import pages.ProductsPage;
import utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * LoginTest — covers all login scenarios:
 *   - Valid credentials → redirect to products
 *   - Invalid credentials → error message
 *   - Locked user → error message
 *   - Empty fields → validation
 *   - Logout flow
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void initPage() {
        loginPage = new LoginPage();
        loginPage.open(config.getBaseUrl());
    }

    // ── TC-L01: Valid login ───────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-L01: Valid credentials should redirect to Products page")
    public void testValidLogin() {
        ExtentReportManager.createTest("TC-L01: Valid Login",
                "Verify successful login with valid credentials");

        ProductsPage productsPage = loginPage.loginAs(
                config.getValidUsername(), config.getValidPassword());

        ExtentReportManager.getTest().info("Navigated to: " + productsPage.getCurrentUrl());

        Assert.assertTrue(productsPage.isProductsPageDisplayed(),
                "Products page should be displayed after login");
        Assert.assertTrue(productsPage.getCurrentUrl().contains("inventory"),
                "URL should contain 'inventory' after login");

        ExtentReportManager.getTest().info("Products page title: " + productsPage.getPageTitle());
    }

    // ── TC-L02: Invalid credentials ──────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-L02: Invalid credentials should show error message")
    public void testInvalidLogin() {
        ExtentReportManager.createTest("TC-L02: Invalid Login",
                "Verify error message for invalid username/password");

        LoginPage resultPage = loginPage.loginExpectingFailure(
                config.getInvalidUsername(), config.getInvalidPassword());

        Assert.assertTrue(resultPage.isErrorDisplayed(),
                "Error message should be displayed");
        Assert.assertTrue(resultPage.getErrorMessage()
                        .contains("Username and password do not match"),
                "Error should state credentials mismatch");

        ExtentReportManager.getTest().info("Error shown: " + resultPage.getErrorMessage());
    }

    // ── TC-L03: Locked user ───────────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-L03: Locked-out user should see specific error")
    public void testLockedOutUser() {
        ExtentReportManager.createTest("TC-L03: Locked User Login",
                "Verify locked_out_user cannot login");

        LoginPage resultPage = loginPage.loginExpectingFailure(
                config.getLockedUsername(), config.getValidPassword());

        Assert.assertTrue(resultPage.isErrorDisplayed(),
                "Error message should be visible");
        Assert.assertTrue(resultPage.getErrorMessage()
                        .contains("locked out"),
                "Error should indicate user is locked out");
    }

    // ── TC-L04: Empty username ────────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-L04: Empty username field should show validation error")
    public void testEmptyUsernameField() {
        ExtentReportManager.createTest("TC-L04: Empty Username",
                "Verify error when username is blank");

        loginPage.enterPassword(config.getValidPassword());
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Validation error should appear");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"),
                "Error should say Username is required");
    }

    // ── TC-L05: Empty password ────────────────────────────────────────────────

    @Test(groups = {"regression"},
          description = "TC-L05: Empty password field should show validation error")
    public void testEmptyPasswordField() {
        ExtentReportManager.createTest("TC-L05: Empty Password",
                "Verify error when password is blank");

        loginPage.enterUsername(config.getValidUsername());
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Validation error should appear");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password is required"),
                "Error should say Password is required");
    }

    // ── TC-L06: Logout ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"},
          description = "TC-L06: Logout should redirect back to login page")
    public void testLogout() {
        ExtentReportManager.createTest("TC-L06: Logout",
                "Verify logout redirects to login page");

        ProductsPage productsPage = loginPage.loginAs(
                config.getValidUsername(), config.getValidPassword());

        Assert.assertTrue(productsPage.isProductsPageDisplayed(),
                "Should be on Products page after login");

        productsPage.logout();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Should return to Login page after logout");
        Assert.assertTrue(loginPage.getCurrentUrl().equals(config.getBaseUrl() + "/"),
                "URL should be base URL after logout");
    }
}

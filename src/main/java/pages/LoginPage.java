package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — covers the SauceDemo login screen.
 * Locators use @FindBy; PageFactory initialised in BasePage.
 */
public class LoginPage extends BasePage {

    // ── Locators ─────────────────────────────────────────────────────────────

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(css = ".login_logo")
    private WebElement loginLogo;

    // ── Actions ──────────────────────────────────────────────────────────────

    public LoginPage open(String baseUrl) {
        navigateTo(baseUrl);
        return this;
    }

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public ProductsPage clickLogin() {
        click(loginButton);
        return new ProductsPage();
    }

    /**
     * Full login flow - returns ProductsPage on success.
     */
    public ProductsPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    /**
     * Login expecting failure (e.g. invalid creds, locked user).
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(loginLogo);
    }
}

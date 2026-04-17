package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutPage — covers three checkout steps:
 *   Step 1: Customer info form
 *   Step 2: Order overview / summary
 *   Step 3: Order confirmation (complete)
 */
public class CheckoutPage extends BasePage {

    // ── Step 1 — Customer Info ────────────────────────────────────────────────

    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // ── Step 2 — Overview ────────────────────────────────────────────────────

    @FindBy(css = ".summary_subtotal_label")
    private WebElement subtotalLabel;

    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    // ── Step 3 — Confirmation ────────────────────────────────────────────────

    @FindBy(css = ".complete-header")
    private WebElement confirmationHeader;

    @FindBy(css = ".complete-text")
    private WebElement confirmationText;

    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;

    @FindBy(css = ".title")
    private WebElement pageTitle;

    // ── Actions — Step 1 ─────────────────────────────────────────────────────

    public CheckoutPage enterFirstName(String name) {
        type(firstNameField, name);
        return this;
    }

    public CheckoutPage enterLastName(String name) {
        type(lastNameField, name);
        return this;
    }

    public CheckoutPage enterPostalCode(String code) {
        type(postalCodeField, code);
        return this;
    }

    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    public CheckoutPage fillCustomerInfo(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    public CheckoutPage submitCustomerInfo(String firstName, String lastName, String postalCode) {
        fillCustomerInfo(firstName, lastName, postalCode);
        clickContinue();
        return this;
    }

    // ── Actions — Step 2 ─────────────────────────────────────────────────────

    public String getSubtotal() {
        return getText(subtotalLabel);
    }

    public String getTax() {
        return getText(taxLabel);
    }

    public String getTotal() {
        return getText(totalLabel);
    }

    public CheckoutPage clickFinish() {
        click(finishButton);
        return this;
    }

    public ProductsPage cancelCheckout() {
        click(cancelButton);
        return new ProductsPage();
    }

    // ── Actions — Step 3 ─────────────────────────────────────────────────────

    public String getConfirmationHeader() {
        return getText(confirmationHeader);
    }

    public String getConfirmationText() {
        return getText(confirmationText);
    }

    public ProductsPage goBackToProducts() {
        click(backToProductsButton);
        return new ProductsPage();
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isOrderConfirmed() {
        return isDisplayed(confirmationHeader) &&
                getText(confirmationHeader).equalsIgnoreCase("Thank you for your order!");
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }
}

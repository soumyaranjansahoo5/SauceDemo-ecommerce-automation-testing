package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage — shopping cart / bag page.
 */
public class CartPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> itemPrices;

    @FindBy(css = ".cart_quantity")
    private List<WebElement> itemQuantities;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    // ── Actions ──────────────────────────────────────────────────────────────

    public int getCartItemCount() {
        return cartItems.size();
    }

    public List<String> getCartItemNames() {
        return itemNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getCartItemPrices() {
        return itemPrices.stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    public boolean isItemInCart(String productName) {
        return getCartItemNames().contains(productName);
    }

    public ProductsPage continueShopping() {
        click(continueShoppingButton);
        return new ProductsPage();
    }

    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutPage();
    }

    public boolean isCartPageDisplayed() {
        return isDisplayed(pageTitle) && getText(pageTitle).equals("Your Cart");
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
}

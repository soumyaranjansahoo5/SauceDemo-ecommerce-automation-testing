package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductsPage — the main inventory/catalogue page after login.
 */
public class ProductsPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".inventory_item")
    private List<WebElement> productItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    @FindBy(css = "[data-test='product_sort_container']")
    private WebElement sortDropdown;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenu;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    // ── Actions ──────────────────────────────────────────────────────────────

    public String getPageTitle() {
        return getText(pageTitle);
    }

    public int getProductCount() {
        return productItems.size();
    }

    public List<String> getAllProductNames() {
        return productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getAllProductPrices() {
        return productPrices.stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    public void addProductToCartByName(String productName) {
        // Build the data-test attribute dynamically from the product name
        String dataTest = "add-to-cart-" +
                productName.toLowerCase().replace(" ", "-").replace("(", "").replace(")", "");
        click(By.cssSelector("[data-test='" + dataTest + "']"));
    }

    public void removeProductFromCartByName(String productName) {
        String dataTest = "remove-" +
                productName.toLowerCase().replace(" ", "-").replace("(", "").replace(")", "");
        click(By.cssSelector("[data-test='" + dataTest + "']"));
    }

    public void sortBy(String option) {
        selectByVisibleText(sortDropdown, option);
    }

    public CartPage goToCart() {
        click(cartIcon);
        return new CartPage();
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(getText(cartBadge));
        } catch (Exception e) {
            return 0;
        }
    }

    public void logout() {
        click(hamburgerMenu);
        click(logoutLink);
    }

    public boolean isProductsPageDisplayed() {
        return isDisplayed(pageTitle) && getText(pageTitle).equals("Products");
    }
}

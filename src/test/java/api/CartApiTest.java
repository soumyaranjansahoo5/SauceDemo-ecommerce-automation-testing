package api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * CartApiTest — REST Assured tests for /carts endpoints.
 * Covers GET, POST (add), PUT (update), DELETE cart operations.
 */
public class CartApiTest extends ApiBaseTest {

    private static final String CARTS_ENDPOINT   = "/carts";
    private static final String CART_BY_ID       = "/carts/{id}";
    private static final String USER_CARTS       = "/carts/user/{userId}";

    // ── TC-API-C01: Get all carts ────────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-C01: GET /carts should return 200 with list of carts")
    public void testGetAllCarts() {
        given()
            .spec(requestSpec)
        .when()
            .get(CARTS_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$", not(empty()))
            .body("size()", greaterThan(0));
    }

    // ── TC-API-C02: Get cart by ID ────────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-C02: GET /carts/{id} should return cart with correct fields")
    public void testGetCartById() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(CART_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("userId", notNullValue())
            .body("date", notNullValue())
            .body("products", not(empty()))
            .body("products[0].productId", notNullValue())
            .body("products[0].quantity", greaterThan(0));
    }

    // ── TC-API-C03: Get carts for a specific user ─────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-C03: GET /carts/user/{userId} should return that user's carts")
    public void testGetCartsByUserId() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("userId", 1)
        .when()
            .get(USER_CARTS)
        .then()
            .statusCode(200)
            .extract().response();

        List<Integer> userIds = response.jsonPath().getList("userId");
        Assert.assertFalse(userIds.isEmpty(), "Should return at least one cart for user 1");
        userIds.forEach(uid ->
            Assert.assertEquals(uid, Integer.valueOf(1),
                    "All carts should belong to userId 1, got: " + uid));
    }

    // ── TC-API-C04: Add item to cart (POST) ───────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-C04: POST /carts should create a new cart and return 200 with ID")
    public void testAddItemToCart() {
        // Build product item
        Map<String, Object> product = new HashMap<>();
        product.put("productId", 5);
        product.put("quantity", 2);

        Map<String, Object> cartBody = new HashMap<>();
        cartBody.put("userId", 3);
        cartBody.put("date", "2024-06-15");
        cartBody.put("products", List.of(product));

        Response response = given()
            .spec(requestSpec)
            .body(cartBody)
        .when()
            .post(CARTS_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", notNullValue())
            .extract().response();

        int cartId = response.jsonPath().getInt("id");
        Assert.assertTrue(cartId > 0, "New cart should have a valid ID, got: " + cartId);
    }

    // ── TC-API-C05: Update cart (PUT) ────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-C05: PUT /carts/{id} should update cart and return updated data")
    public void testUpdateCart() {
        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1);
        product.put("quantity", 3);

        Map<String, Object> updatedCart = new HashMap<>();
        updatedCart.put("userId", 1);
        updatedCart.put("date", "2024-07-20");
        updatedCart.put("products", List.of(product));

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
            .body(updatedCart)
        .when()
            .put(CART_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1));
    }

    // ── TC-API-C06: Delete cart ───────────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-C06: DELETE /carts/{id} should return 200 with deleted cart data")
    public void testDeleteCart() {
        given()
            .spec(requestSpec)
            .pathParam("id", 6)
        .when()
            .delete(CART_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(6));
    }

    // ── TC-API-C07: Cart schema validation ───────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-C07: Cart response must contain id, userId, date, products fields")
    public void testCartSchemaFields() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("id", 2)
        .when()
            .get(CART_BY_ID)
        .then()
            .statusCode(200)
            .extract().response();

        Assert.assertNotNull(response.jsonPath().get("id"),       "id field required");
        Assert.assertNotNull(response.jsonPath().get("userId"),   "userId field required");
        Assert.assertNotNull(response.jsonPath().get("date"),     "date field required");
        Assert.assertNotNull(response.jsonPath().get("products"), "products field required");

        List<Map<String, Object>> products = response.jsonPath().getList("products");
        Assert.assertFalse(products.isEmpty(), "Products list should not be empty");
        Assert.assertNotNull(products.get(0).get("productId"), "productId required in product");
        Assert.assertNotNull(products.get(0).get("quantity"),  "quantity required in product");
    }

    // ── TC-API-C08: Date range filter ────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-C08: GET /carts?startdate&enddate should filter by date range")
    public void testGetCartsWithDateRange() {
        given()
            .spec(requestSpec)
            .queryParam("startdate", "2019-01-01")
            .queryParam("enddate", "2020-12-31")
        .when()
            .get(CARTS_ENDPOINT)
        .then()
            .statusCode(200)
            .body("$", not(empty()));
    }
}

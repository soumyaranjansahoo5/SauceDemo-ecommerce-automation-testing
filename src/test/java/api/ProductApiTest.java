package api;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * ProductApiTest — REST Assured tests for /products endpoints.
 * Uses FakeStoreAPI (fakestoreapi.com) as a realistic public REST backend.
 */
public class ProductApiTest extends ApiBaseTest {

    private static final String PRODUCTS_ENDPOINT  = "/products";
    private static final String PRODUCT_BY_ID      = "/products/{id}";
    private static final String PRODUCTS_LIMIT      = "/products?limit={limit}";
    private static final String PRODUCTS_SORT       = "/products?sort={order}";
    private static final String CATEGORIES_ENDPOINT = "/products/categories";

    // ── TC-API-P01: Get all products ─────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-P01: GET /products should return 200 and a non-empty list")
    public void testGetAllProducts() {
        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .contentType("application/json")
            .body("$", not(empty()))
            .body("size()", greaterThan(0));
    }

    // ── TC-API-P02: Get product by ID ────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-P02: GET /products/{id} should return correct product fields")
    public void testGetProductById() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(PRODUCT_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", notNullValue())
            .body("price", greaterThan(0.0f))
            .body("category", notNullValue())
            .body("image", notNullValue())
            .body("rating", notNullValue())
            .body("rating.rate", greaterThan(0.0f))
            .body("rating.count", greaterThan(0));
    }

    // ── TC-API-P03: Validate response schema fields ───────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-P03: Each product should contain all required schema fields")
    public void testProductSchemaFields() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_ENDPOINT)
        .then()
            .statusCode(200)
            .extract().response();

        List<String> titles = response.jsonPath().getList("title");
        List<Float> prices  = response.jsonPath().getList("price");
        List<String> cats   = response.jsonPath().getList("category");
        List<Integer> ids   = response.jsonPath().getList("id");

        Assert.assertFalse(titles.isEmpty(), "Titles list should not be empty");
        Assert.assertFalse(prices.isEmpty(), "Prices list should not be empty");
        Assert.assertFalse(cats.isEmpty(),   "Categories list should not be empty");
        Assert.assertEquals(ids.size(), titles.size(), "IDs and titles count should match");

        // Verify no null titles or zero prices
        titles.forEach(t -> Assert.assertNotNull(t, "Product title should not be null"));
        prices.forEach(p -> Assert.assertTrue(p > 0, "Product price should be > 0, got: " + p));
    }

    // ── TC-API-P04: Limit query parameter ────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-P04: ?limit=5 should return exactly 5 products")
    public void testGetProductsWithLimit() {
        given()
            .spec(requestSpec)
            .pathParam("limit", 5)
        .when()
            .get(PRODUCTS_LIMIT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", equalTo(5));
    }

    // ── TC-API-P05: Sort ascending ────────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-P05: ?sort=asc should return products with increasing IDs")
    public void testGetProductsSortedAsc() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("order", "asc")
        .when()
            .get(PRODUCTS_SORT)
        .then()
            .statusCode(200)
            .extract().response();

        List<Integer> ids = response.jsonPath().getList("id");
        for (int i = 0; i < ids.size() - 1; i++) {
            Assert.assertTrue(ids.get(i) <= ids.get(i + 1),
                    "IDs should be ascending. Found " + ids.get(i) + " before " + ids.get(i + 1));
        }
    }

    // ── TC-API-P06: Sort descending ───────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-P06: ?sort=desc should return products with decreasing IDs")
    public void testGetProductsSortedDesc() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("order", "desc")
        .when()
            .get(PRODUCTS_SORT)
        .then()
            .statusCode(200)
            .extract().response();

        List<Integer> ids = response.jsonPath().getList("id");
        for (int i = 0; i < ids.size() - 1; i++) {
            Assert.assertTrue(ids.get(i) >= ids.get(i + 1),
                    "IDs should be descending");
        }
    }

    // ── TC-API-P07: Invalid product ID ───────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-P07: GET /products/9999 with non-existent ID should return null body")
    public void testGetProductInvalidId() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("id", 9999)
        .when()
            .get(PRODUCT_BY_ID)
        .then()
            .statusCode(200)   // FakeStoreAPI returns 200 with null for unknown IDs
            .extract().response();

        // FakeStoreAPI returns null for unknown product — verify body is null/empty
        String body = response.getBody().asString();
        Assert.assertTrue(body.equals("null") || body.isEmpty(),
                "Non-existent product ID should return null body, got: " + body);
    }

    // ── TC-API-P08: Get all categories ───────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-P08: GET /products/categories should return a list of category strings")
    public void testGetAllCategories() {
        given()
            .spec(requestSpec)
        .when()
            .get(CATEGORIES_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$", not(empty()))
            .body("size()", greaterThan(0));
    }

    // ── TC-API-P09: Response time check ──────────────────────────────────────

    @Test(groups = {"api", "performance"},
          description = "TC-API-P09: GET /products should respond within 5 seconds")
    public void testProductApiResponseTime() {
        long startTime = System.currentTimeMillis();

        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_ENDPOINT)
        .then()
            .statusCode(200);

        long elapsed = System.currentTimeMillis() - startTime;
        Assert.assertTrue(elapsed < 5000,
                "Response time should be < 5s but was: " + elapsed + "ms");
    }
}

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
 * OrderApiTest — REST Assured tests for /users endpoints (simulating order/user flows).
 * FakeStoreAPI uses /users to represent registered customers.
 */
public class OrderApiTest extends ApiBaseTest {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID     = "/users/{id}";
    private static final String AUTH_ENDPOINT  = "/auth/login";

    // ── TC-API-O01: Get all users ────────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-O01: GET /users should return 200 with user list")
    public void testGetAllUsers() {
        given()
            .spec(requestSpec)
        .when()
            .get(USERS_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$", not(empty()))
            .body("size()", greaterThan(0));
    }

    // ── TC-API-O02: Get user by ID ────────────────────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-O02: GET /users/{id} should return correct user fields")
    public void testGetUserById() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(USER_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("email", notNullValue())
            .body("username", notNullValue())
            .body("address", notNullValue())
            .body("address.city", notNullValue())
            .body("address.zipcode", notNullValue())
            .body("name.firstname", notNullValue())
            .body("name.lastname", notNullValue())
            .body("phone", notNullValue());
    }

    // ── TC-API-O03: Create new user (POST) ────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O03: POST /users should create user and return new ID")
    public void testCreateUser() {
        Map<String, Object> name = new HashMap<>();
        name.put("firstname", "Soumyaranjan");
        name.put("lastname", "Sahoo");

        Map<String, Object> geolocation = new HashMap<>();
        geolocation.put("lat", "20.2961");
        geolocation.put("long", "85.8245");

        Map<String, Object> address = new HashMap<>();
        address.put("city", "Bhubaneswar");
        address.put("street", "MG Road");
        address.put("number", 10);
        address.put("zipcode", "751001");
        address.put("geolocation", geolocation);

        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", "soumya.test@ecommerce.com");
        userBody.put("username", "soumya_auto");
        userBody.put("password", "test@1234");
        userBody.put("name", name);
        userBody.put("address", address);
        userBody.put("phone", "9668177321");

        Response response = given()
            .spec(requestSpec)
            .body(userBody)
        .when()
            .post(USERS_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", notNullValue())
            .extract().response();

        int newUserId = response.jsonPath().getInt("id");
        Assert.assertTrue(newUserId > 0, "New user should have a valid ID, got: " + newUserId);
    }

    // ── TC-API-O04: Update user (PUT) ─────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O04: PUT /users/{id} should update user details")
    public void testUpdateUser() {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", "updated@ecommerce.com");
        userBody.put("username", "updated_user");
        userBody.put("password", "newPass@123");

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
            .body(userBody)
        .when()
            .put(USER_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1));
    }

    // ── TC-API-O05: Delete user ───────────────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O05: DELETE /users/{id} should return 200 with deleted user")
    public void testDeleteUser() {
        given()
            .spec(requestSpec)
            .pathParam("id", 10)
        .when()
            .delete(USER_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(10));
    }

    // ── TC-API-O06: Auth login — valid credentials ────────────────────────────

    @Test(groups = {"api", "smoke"},
          description = "TC-API-O06: POST /auth/login with valid creds should return a token")
    public void testAuthLoginValid() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mor_2314");
        credentials.put("password", "83r5^_");

        Response response = given()
            .spec(requestSpec)
            .body(credentials)
        .when()
            .post(AUTH_ENDPOINT)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("token", notNullValue())
            .extract().response();

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Auth token should be returned");
        Assert.assertFalse(token.isEmpty(), "Auth token should not be empty");
    }

    // ── TC-API-O07: Auth login — invalid credentials ─────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O07: POST /auth/login with wrong creds should return 401")
    public void testAuthLoginInvalid() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "wrong_user");
        credentials.put("password", "wrong_pass");

        given()
            .spec(requestSpec)
            .body(credentials)
        .when()
            .post(AUTH_ENDPOINT)
        .then()
            .statusCode(401);
    }

    // ── TC-API-O08: Users limit filter ───────────────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O08: GET /users?limit=3 should return exactly 3 users")
    public void testGetUsersWithLimit() {
        Response response = given()
            .spec(requestSpec)
            .queryParam("limit", 3)
        .when()
            .get(USERS_ENDPOINT)
        .then()
            .statusCode(200)
            .body("size()", equalTo(3))
            .extract().response();

        List<Integer> ids = response.jsonPath().getList("id");
        Assert.assertEquals(ids.size(), 3, "Should return exactly 3 users");
    }

    // ── TC-API-O09: Response headers validation ───────────────────────────────

    @Test(groups = {"api", "regression"},
          description = "TC-API-O09: API responses should return correct Content-Type header")
    public void testResponseHeaders() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(USER_BY_ID)
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"));
    }
}

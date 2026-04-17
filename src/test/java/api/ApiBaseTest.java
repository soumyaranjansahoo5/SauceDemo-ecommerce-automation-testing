package api;

import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static org.hamcrest.Matchers.lessThan;

/**
 * ApiBaseTest — all API test classes extend this.
 * Configures base URI, default headers, and logging for REST Assured.
 */
public class ApiBaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected static final ConfigReader config = ConfigReader.getInstance();

    @BeforeClass
    public void setupApiSpec() {
        RestAssured.baseURI = config.getApiBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}

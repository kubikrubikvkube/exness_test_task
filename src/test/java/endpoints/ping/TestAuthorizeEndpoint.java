package endpoints.ping;

import endpoints.Endpoints;
import lombok.extern.java.Log;
import lombok.val;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Log
public class TestAuthorizeEndpoint {
    private final String VALID_USERNAME = "supertest";
    private final String VALID_PASSWORD = "superpassword";

    @Test
    void validUserNameAndPasswordShouldLeadToGettingAuthorizationToken() {
        val response = given()
                .contentType("multipart/form-data")
                .multiPart("username", VALID_USERNAME)
                .multiPart("password", VALID_PASSWORD)
                .when()
                .log().all()
                .post(Endpoints.AUTHORIZE.url)
                .then()
                .log().all()
                .extract();

        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.contentType(), "application/json");
        assertNotNull(response.body().jsonPath());
        val body = response.body().jsonPath();
        val authorizationToken = body.getString("token");
        assertNotNull(authorizationToken);
        log.info("Got authorization token: " + authorizationToken);
    }

    @Test
    void invalidUserNameAndPasswordShouldLeadToError() {
        val response = given()
                .contentType("multipart/form-data")
                .multiPart("username", "invalidusername")
                .multiPart("password", "invalidpassword")
                .when()
                .log().all()
                .post(Endpoints.AUTHORIZE.url)
                .then()
                .log().all()
                .extract();

        assertEquals(response.statusCode(), HTTP_FORBIDDEN);
        assertEquals(response.contentType(), "text/html; charset=UTF-8");
        assertNotNull(response.body().htmlPath());
        val body = response.body().htmlPath();
        assertEquals(body.get("html.head.title"), "Error: 403 Forbidden");
    }
}

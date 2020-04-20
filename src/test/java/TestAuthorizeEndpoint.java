import endpoints.Endpoints;
import lombok.extern.java.Log;
import lombok.val;
import org.testng.annotations.Test;
import utils.AuthorizationHelper;

import java.util.concurrent.TimeUnit;

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
    public void testTokenInvalidation() throws InterruptedException {
        val token = AuthorizationHelper.getValidToken();
        val response = given()
                .log().all()
                .header("Authorization", "Bearer " + token)
                .post(Endpoints.SAVE_DATA.url)
                .then()
                .log().all()
                .assertThat()
                .statusCode(400)
                .extract();

        String error = response.htmlPath().getString("html.head.title");
        assertEquals(error, "Error: 400 Bad Request");

        log.info("We've sent bad request, but token is accepted and valid.");

        Thread.sleep(TimeUnit.SECONDS.toMillis(65));
        given()
                .log().all()
                .header("Authorization", "Bearer " + token)
                .post(Endpoints.SAVE_DATA.url)
                .then()
                .log().all()
                .assertThat()
                .statusCode(403)
                .extract();

        log.info("60 seconds passed and this token usage leads to '403 forbidden'");
    }

    @Test
    void invalidUserNameAndPasswordShouldLeadToError() {
        val response = given()
                .contentType("multipart/form-data")
                .multiPart("username", "NOT_" + VALID_USERNAME)
                .multiPart("password", "NOT_" + VALID_PASSWORD)
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

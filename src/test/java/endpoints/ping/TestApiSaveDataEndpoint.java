package endpoints.ping;

import endpoints.Endpoints;
import endpoints.utils.AuthorizationHelper;
import io.restassured.http.ContentType;
import lombok.extern.java.Log;
import lombok.val;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertEquals;

@Log
public class TestApiSaveDataEndpoint {
    private String token;

    @BeforeMethod
    public void setUp() {
        token = AuthorizationHelper.getValidToken();
    }

    @Test
    public void shouldGet403IfNoAuthorizationTokenProvided() {
        val response = when()
                .post(Endpoints.SAVE_DATA.url)
                .then()
                .log().all()
                .assertThat()
                .statusCode(403)
                .extract();
        String error = response.htmlPath().getString("html.head.title");
        assertEquals(error, "Error: 403 Forbidden");
    }

    @Test
    public void shouldBeAbleToAuthorize() {
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
        log.info("We've sent bad request, but authorization header is accepted");
    }

    @Test
    void shouldBeAbleToSaveJsonPayload() {
        String jsonBody = String.format("{\"payload\": \"%s\"}", UUID.randomUUID().toString());
        val response = given()
                .log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .post(Endpoints.SAVE_DATA.url)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract();

        assertEquals(response.contentType(), "application/json");
        val responseBody = response.jsonPath();
        assertEquals(responseBody.getString("status"), "OK");
        assertThat(responseBody.getInt("id"), greaterThan(0));
        log.info("Saved entry id is " + responseBody.getInt("id"));
    }

    @Test
    void shouldBeAbleToSaveFormEncodedPayload() {
        String body = String.format("payload=%s", UUID.randomUUID().toString());

        val response = given()
                .log().all()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .post(Endpoints.SAVE_DATA.url)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract();

        assertEquals(response.contentType(), "application/json");
        val responseBody = response.jsonPath();
        assertEquals(responseBody.getString("status"), "OK");
        assertThat(responseBody.getInt("id"), greaterThan(0));
        log.info("Saved entry id is " + responseBody.getInt("id"));
    }

}

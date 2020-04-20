package endpoints.utils;

import endpoints.Endpoints;

import static io.restassured.RestAssured.given;

public class AuthorizationHelper {
    private final static String VALID_USERNAME = "supertest";
    private final static String VALID_PASSWORD = "superpassword";

    public static String getValidToken() {
        return given()
                .contentType("multipart/form-data")
                .multiPart("username", VALID_USERNAME)
                .multiPart("password", VALID_PASSWORD)
                .when()
                .log().all()
                .post(Endpoints.AUTHORIZE.url)
                .then()
                .log().all()
                .extract()
                .body()
                .jsonPath()
                .getString("token");
    }
}

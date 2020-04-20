import endpoints.Endpoints;
import lombok.val;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.testng.Assert.assertEquals;

public class TestPingEndpoint {


    @Test
    public void testGetRequestLeadToCorrectAnswer() {
        val response = when()
                .get(Endpoints.PING.url)
                .then()
                .log().all()
                .extract();

        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.contentType(), "text/html; charset=UTF-8");
        assertEquals(response.body().asString(), "OK");
    }
}

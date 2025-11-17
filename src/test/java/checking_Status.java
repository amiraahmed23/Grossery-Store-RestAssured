import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

public class checking_Status {
    @Test
    public void checking_StatusCode(){
        Response response = RestAssured
                .given()
                .when()
                .get("https://simple-grocery-store-api.click/status");
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("status", Matchers .equalTo("UP"));
        String statusValue = response.jsonPath().getString("status");
        Assert.assertTrue(statusValue instanceof String);
        Allure.addAttachment("Status Response", "application/json", response.asString());
    }
}

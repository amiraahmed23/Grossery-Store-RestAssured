import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;

public class Register {
    public String Base_URl = "https://simple-grocery-store-api.click";
    long timeStamp = System.currentTimeMillis();
    String clientName = "Client" + timeStamp;
    String clientEmail = "client" + timeStamp + "@gmail.com";

    @Test
    public void valid_register() {
        Response resposnse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"clientName\": \"" + clientName + "\",\n" +
                        "  \"clientEmail\": \"" + clientEmail + "\"\n" +
                        "}")
                .when()
                .post(Base_URl + "/api-clients");
        resposnse.prettyPrint();
        resposnse.then().statusCode(201);
        resposnse.then().body("accessToken", Matchers.notNullValue());
        resposnse.then().body("accessToken", Matchers.instanceOf(String.class));

        String accessToken = resposnse.jsonPath().getString("accessToken");
        System.setProperty("accessToken", accessToken);
        System.setProperty("clientName", clientName);
        System.out.println("Access Token: " + accessToken);

        Allure.addAttachment("Valid Register Response", "application/json", resposnse.asInputStream(), "json");

    }

    @Test
    public void invalid_register_with_existed_account() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"clientName\": \"Amira Ahmed\",\n" +
                        "  \"clientEmail\": \"amira23@gmail.com\"\n" +
                        "}")
                .when()
                .post(Base_URl + "/api-clients");

        response.prettyPrint();
        response.then().statusCode(409);
        response.then().body("error", Matchers.equalTo("API client already registered. Try a different email."));
        Allure.addAttachment( "Invalid Register with Existed Account Response", "application/json", response.asInputStream(), "json");
    }

    @Test
    public void invalid_register_with_wrong_data() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"clientName\": \"\",\n" +
                        "  \"clientEmail\": \"\"\n" +
                        "}")
                .when()
                .post(Base_URl + "/api-clients");

        response.prettyPrint();

        response.then().statusCode(400);
        response.then().body("error", Matchers.equalTo("Invalid or missing client name."));

        response.then().contentType(Matchers.equalTo("application/json"));
        Allure.addAttachment("Invalid Register Response", "application/json", response.asInputStream(), "json");
    }
}

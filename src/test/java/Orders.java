import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class Orders {
    public String Base_URl = "https://simple-grocery-store-api.click";


    @Test(dependsOnMethods = {"carts.addItemToCart"}, priority = 1)
    public void createNewOrder() {

        String accessToken = System.getProperty("accessToken");
        String cartId = System.getProperty("cartId");
        String clientName = System.getProperty("clientName");

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body("{\n" +
                        "  \"cartId\": \"" + cartId + "\",\n" +
                        "  \"customerName\": \"" + clientName + "\"\n" +
                        "}")
                .when()
                .post(Base_URl+"/orders");
        response.prettyPrint();

        response.then().statusCode(201);
        response.then().body("created", Matchers.equalTo(true));
        response.then().body("orderId", Matchers.notNullValue());
        response.then().body("orderId", Matchers.instanceOf(String.class));

        String orderId = response.jsonPath().getString("orderId");
        System.setProperty("orderId", orderId);
        System.out.println("Created Order ID: " + orderId);

        Allure.addAttachment("Created Order ID", "Application/json", response.asString());
}
    @Test(dependsOnMethods = {"createNewOrder"})
    public void verify_order_exists() {
        String accessToken = System.getProperty("accessToken");
        String orderId = System.getProperty("orderId");

        Response checkResponse = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(Base_URl + "/orders/" + orderId);

        checkResponse.prettyPrint();
        Allure.addAttachment( "Verify Order Exists Response", "Application/json", checkResponse.asString());
    }

@Test
    public void bad_request_createNewOrder() {

    String accessToken = System.getProperty("accessToken");
    String cartId = System.getProperty("cartId");

    Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
            .body("{\n" +
                    "  \"cartId\": \"" + cartId + "\"\n" +
                    "}")
            .when()
            .post(Base_URl + "/orders");
    response.prettyPrint();

    response.then().statusCode(400);
    response.then().body("error", Matchers.equalTo("Invalid or missing customer name."));
    Allure.addAttachment( "Bad Request Create New Order Response", "Application/json", response.asString());
}
@Test
    public void unauthorized_createNewOrder() {

    String cartId = System.getProperty("cartId");
    String clientName = System.getProperty("clientName");

    Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .body("{\n" +
                    "  \"cartId\": \"" + cartId + "\",\n" +
                    "  \"customerName\": \"" + clientName + "\"\n" +
                    "}")
            .when()
            .post(Base_URl + "/orders");
    response.prettyPrint();

    response.then().statusCode(401);
    response.then().body("error", Matchers.equalTo("Missing Authorization header."));
    Allure.addAttachment( "Unauthorized Create New Order Response", "Application/json", response.asString());

}
@Test(priority = 2, dependsOnMethods = {"createNewOrder"})
public void get_single_order() {
    String accessToken = System.getProperty("accessToken");
    String orderId = System.getProperty("orderId");

    System.out.println("Access Token: " + accessToken);
    System.out.println("Order ID: " + orderId);

    Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get(Base_URl + "/orders/" + orderId);

    response.prettyPrint();

    response.then().statusCode(200);
    response.then().body("id", Matchers.equalTo(orderId));
    Allure.addAttachment( "Get Single Order Response", "Application/json", response.asString());
}
@Test
    public void unauthorized_get_single_order() {
    String orderId = System.getProperty("orderId");

    Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .when()
            .get(Base_URl + "/orders/" + orderId);

    response.prettyPrint();

    response.then().statusCode(401);
    response.then().body("error", Matchers.equalTo("Missing Authorization header."));
    Allure.addAttachment( "Unauthorized Get Single Order Response", "Application/json", response.asString());
}
@Test
    public void get_invalid_single_order() {
    String accessToken = System.getProperty("accessToken");
    String invalidOrderId = "invalid-order-id";

    Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get(Base_URl + "/orders/" + invalidOrderId);

    response.prettyPrint();

    response.then().statusCode(404);
    response.then().body("error", Matchers.equalTo("No order with id invalid-order-id."));
    Allure.addAttachment( "Get Invalid Single Order Response", "Application/json", response.asString());
}
    @Test(priority = 3, dependsOnMethods = {"get_single_order"})
    public void updateOrder() throws InterruptedException {
        String accessToken = System.getProperty("accessToken");
        String orderId = System.getProperty("orderId");


        System.out.println("Access Token: " + accessToken);
        System.out.println("Order ID: " + orderId);

        String requestBody = "{\n" +
                "  \"customerName\": \"Updated Client Name\",\n" +
                "  \"comment\": \"Order updated successfully\"\n" +
                "}";

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .patch(Base_URl + "/orders/" + orderId);

        response.prettyPrint();

        response.then().statusCode(204);
        Allure.addAttachment( "Update Order Response", "Application/json", response.asString());
    }
    @Test (priority = 4)
    public void bad_request_update(){
        String accessToken = System.getProperty("accessToken");
        String orderId = System.getProperty("orderId");

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body("{ \"customerName\": }")
                .when()
                .patch(Base_URl + "/orders/" + orderId);

        response.prettyPrint();

        response.then().statusCode(400);
        response.then().body("error", Matchers.containsString("The request body could not be parsed."));
        Allure.addAttachment( "Bad Request Update Order Response", "Application/json", response.asString());
    }

    @Test(priority = 5)
    public void updateOrder_NotFound() {
        String accessToken = System.getProperty("accessToken");


        String invalidOrderId = "12345";

        String requestBody = "{\n" +
                "  \"customerName\": \"Updated Client Name\",\n" +
                "  \"comment\": \"Trying to update invalid order\"\n" +
                "}";

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .patch(Base_URl + "/orders/" + invalidOrderId);

        response.prettyPrint();

        response.then().statusCode(404);
        response.then().body("error", Matchers.containsString("No order with id"));

        Allure.addAttachment( "Update Order Not Found Response", "Application/json", response.asString());
    }


    @Test(priority = 4, dependsOnMethods = {"updateOrder"})
    public void deleteOrderSuccessfully() {
        String accessToken = System.getProperty("accessToken");
        String orderId = System.getProperty("orderId");

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(Base_URl + "/orders/" + orderId);

        response.prettyPrint();
        response.then().statusCode(204);
        Allure.addAttachment( "Delete Order Successfully Response", "Application/json", response.asString());
    }

    @Test
    public void deleteOrderWithInvalidOrderId() {
        String accessToken = System.getProperty("accessToken");

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(Base_URl + "/orders/invalid123");

        response.prettyPrint();
        response.then().statusCode(404);
        response.then().body("error", Matchers.containsString("No order with id invalid123."));
        Allure.addAttachment( "Delete Order With Invalid Order ID Response", "Application/json", response.asString());
    }

    @Test
    public void deleteOrderWithoutAuth() {
        String orderId = System.getProperty("orderId");

        Response response = RestAssured
                .given()
                .when()
                .delete(Base_URl + "/orders/" + orderId);

        response.prettyPrint();
        response.then().statusCode(401);
        response.then().body("error", Matchers.equalTo("Missing Authorization header."));

        Allure.addAttachment( "Delete Order Without Auth Response", "Application/json", response.asString());
    }

    @Test
    public void deleteOrderNotFound() {
        String accessToken = System.getProperty("accessToken");

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(Base_URl + "/orders/999999999");

        response.prettyPrint();
        response.then().statusCode(404);
        response.then().body("error", Matchers.containsString("No order"));

        Allure.addAttachment( "Delete Order Not Found Response", "Application/json", response.asString());
    }
}

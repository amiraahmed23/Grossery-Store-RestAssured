import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.when;

public class carts {
    public String Base_URl = "https://simple-grocery-store-api.click";

    @Test (priority = 1)
    public void createNewCart() {
        Response Response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .when()
                .post("/carts").then().extract().response();
        Response.prettyPrint();
        Response.then().statusCode(201);
        Response.then().body("created", Matchers.equalTo(true));
        Response.then().body("cartId", Matchers.notNullValue());
        String cartId = Response.path("cartId");
        System.setProperty("cartId", cartId);
        System.out.println("Cart ID: " + cartId);
        Allure.addAttachment( "Create New Cart Response", "application/json" , Response.asString());
    }

    @Test(dependsOnMethods = {"createNewCart"}, priority = 2)
    public void getCartById() {
        String cartId = System.getProperty("cartId");
        Response Response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json").pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}").then().extract().response();
        Response.prettyPrint();
        Response.then().statusCode(200);

        Allure.addAttachment( "Get Cart By ID Response", "application/json" , Response.asString());
    }
    @Test( groups = "carts", dependsOnMethods = {"createNewCart"}, priority = 3)
    public void addItemToCart() {
        String cartId = System.getProperty("cartId");

        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .body("{ \"productId\": 8554, \"quantity\": 1 }")
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(201);
        response.then().body("created", Matchers.equalTo(true));
        response.then().body("itemId", Matchers.instanceOf(Integer.class));
        int itemId = response.path("itemId");
        System.setProperty("itemId", String.valueOf(itemId));

        Allure.addAttachment( "Add Item To Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 4)
    public void addTheSameProductToCart() {
        String cartId = System.getProperty("cartId");
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .body("{ \"productId\": 8554, \"quantity\": 1 }")
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(400);

        Allure.addAttachment( "Add The Same Product To Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"} , priority = 5)
    public void modifyItemInTheCart() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body("{ \"quantity\": 1 }")
                .when()
                .patch("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(204);
        Allure.addAttachment( "Modify Item In The Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 6)
    public void modifyItemInTheCartWithMissingItemId() {
        String cartId = System.getProperty("cartId");
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .body("{ \"quantity\": 1 }")
                .when()
                .patch("/carts/{cartId}/items")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(404);
        response.then().body("error", Matchers.equalTo("The resource could not be found. Check your endpoint and request method."));
        Allure.addAttachment(  "Modify Item In The Cart With Missing Item ID Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 7)
    public void modifyItemInTheCartWithMissingQuantity() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body("{ \"quantity\": }")
                .when()
                .patch("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(400);
        Allure.addAttachment( "Modify Item In The Cart With Missing Quantity Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 8)
    public void replaceItemInTheCart() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body("{ \"productId\": 8554}")
                .when()
                .put("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(204);

        Allure.addAttachment( "Replace Item In The Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 9)
    public void replaceItemInTheCartWithMissingCartId() {
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("itemId", itemId)
                .body("{ \"productId\": 8554}")
                .when()
                .put("/carts/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(404);
        Allure.addAttachment( "Replace Item In The Cart With Missing Cart ID Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 10)
    public void replaceItemInTheCartWithMissingProductId() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId).
                body("{ \"productId\": }")
                .when()
                .put("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(400);
        Allure.addAttachment( "Replace Item In The Cart With Missing Product ID Response", "application/json" , response.asString());
    }
    @Test (priority = 11)
    public void getCartWithInvalidId() {
        Response Response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", 123456789)
                .when()
                .get("/carts/{cartId}").then().extract().response();
        Response.prettyPrint();
        Response.then().statusCode(404);
        Allure.addAttachment( "Get Cart With Invalid ID Response", "application/json" , Response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 12)
    public void  getItemsInTheCart() {
        String cartId = System.getProperty("cartId");
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("[0].productId", Matchers.instanceOf(Integer.class));
        response.then().body("[0].id", Matchers.instanceOf(Integer.class));
        response.then().body("[0].quantity", Matchers.instanceOf(Integer.class));
        Allure.addAttachment( "Get Items In The Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 13)
    public void  deleteItemFromCart() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(204);
        Allure.addAttachment( "Delete Item From Cart Response", "application/json" , response.asString());
    }
    @Test(dependsOnMethods = {"addItemToCart"}, priority = 14)
    public void  deleteNotExistItemFromCart() {
        String cartId = System.getProperty("cartId");
        int itemId = Integer.parseInt(System.getProperty("itemId"));
        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .pathParam("itemId", 123456789)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then()
                .extract()
                .response();
        response.prettyPrint();
        response.then().statusCode(404);
         Allure.addAttachment( "Delete Not Exist Item From Cart Response", "application/json" , response.asString());
    }

    @Test (dependsOnMethods = {"deleteItemFromCart"})
    public void addNewItemAfterDelete() {
        String cartId = System.getProperty("cartId");

        Response response = RestAssured
                .given()
                .baseUri(Base_URl)
                .header("Content-Type", "application/json")
                .pathParam("cartId", cartId)
                .body("{ \"productId\": 8554, \"quantity\": 2 }")
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .extract()
                .response();

        response.prettyPrint();
        response.then().statusCode(201);
        response.then().body("created", Matchers.equalTo(true));

        int newItemId = response.path("itemId");
        System.setProperty("itemId", String.valueOf(newItemId));

        System.out.println(" Added new item after delete. Item ID: " + newItemId);

        Allure.addAttachment( "Add New Item After Delete Response", "application/json" , response.asString());
    }


}


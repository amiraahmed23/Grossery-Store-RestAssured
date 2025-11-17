import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class Products {
    public String Base_URl = "https://simple-grocery-store-api.click";
    static int productId;
    @Test
    public void get_all_products(){
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products");
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("id[2]", Matchers.instanceOf(Integer.class));
        response.then().body("name[2]", Matchers.instanceOf(String.class));
        Allure.addAttachment("All Products", "application/json", response.asString(), "json");
    }
    @Test
    public void invalid_get_products(){
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products?available=9999");
        response.prettyPrint();
        response.then().statusCode(400);
        response.then().body("error", Matchers.containsString("Invalid value for query parameter 'available'. Must be one of: true, false"));

        Allure.addAttachment( "Invalid Products Response", "application/json", response.asString(), "json");
    }
    @Test( groups = "products", priority = 1)
    public void get_instock_products(){
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products?available=true");
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("inStock", Matchers.everyItem(Matchers.equalTo(true)));

        List<Integer> ids = response.jsonPath().getList("id");
        int randomIndex = new Random().nextInt(ids.size());
        productId = ids.get(randomIndex);
        System.setProperty("productId", String.valueOf(productId));

        System.out.println("Saved product ID: " + productId);
        Allure.addAttachment( "In-stock Products", "application/json", response.asString(), "json");
    }
    @Test
    public void get_outofstock_products(){
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products?available=false");
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("inStock", Matchers.everyItem(Matchers.equalTo(false)));

        Allure.addAttachment( "Out-of-stock Products", "application/json", response.asString(), "json");
    }
    @Test(priority = 2)
    public void get_specific_product(){
        String idString = System.getProperty("productId");
        int productId = Integer.parseInt(idString);
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products/" + productId);
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().body("id", Matchers.equalTo(productId));
        Allure.addAttachment( "Specific Product", "application/json", response.asString(), "json");
    }
    @Test
    public void invalid_get_specific_product(){
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base_URl + "/products/9999999");
        response.prettyPrint();
        response.then().statusCode(404);
        response.then().body("error", Matchers.containsString("No product with id 9999999."));

        Allure.addAttachment( "Invalid Specific Product Response", "application/json", response.asString(), "json");
    }

}

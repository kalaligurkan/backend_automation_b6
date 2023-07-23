package api;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

public class APIAutomationSample {

    public static void main(String[] args) {

        /**
         * Response is an interface coming from the RestAssured library
         * The Response variable "response" stores all the components of API calls
         * including the request, and the response
         * API calls in RestAssured is written with BDD flow
         */

        Response response;
        Faker faker = new Faker();

        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();


        // Creating the post request
        response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 2d21f330490b045a2de36199d329c48bb77f58905d0c1ce4cac536f7bfdae50c")
                .body("{\n" +
                        "    \"name\": \"" + name + "\",\n" +
                        "    \"gender\": \"male\",\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"status\": \"active\"\n" +
                        "}")
                .when().post("https://gorest.co.in/public/v2/users")
                .then().log().all().extract().response();

        int userId = response.jsonPath().getInt("id");

        System.out.println("user id is = " + userId);

        // Creating the get request to fetch specific user
        response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 2d21f330490b045a2de36199d329c48bb77f58905d0c1ce4cac536f7bfdae50c")
                .when().get("https://gorest.co.in/public/v2/users/" + userId)
                .then().log().all().extract().response();

        String responseName = response.jsonPath().getString("name");

        Assert.assertEquals(responseName, name);

        String responseEmail = response.jsonPath().getString("email");
        Assert.assertEquals(responseEmail, email);

        // Get all users
        response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 2d21f330490b045a2de36199d329c48bb77f58905d0c1ce4cac536f7bfdae50c")
                .when().get("https://gorest.co.in/public/v2/users/")
                .then().log().all().extract().response();


        //Put request
        response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 2d21f330490b045a2de36199d329c48bb77f58905d0c1ce4cac536f7bfdae50c")
                .body("{\n" +
                        "    \"name\": \"" + name + "\",\n" +
                        "    \"gender\": \"male\",\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"status\": \"active\"\n" +
                        "}")
                .when().put("https://gorest.co.in/public/v2/users/" + userId)
                .then().log().all().extract().response();

        //Delete request
        response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 2d21f330490b045a2de36199d329c48bb77f58905d0c1ce4cac536f7bfdae50c")
                .when().delete("https://gorest.co.in/public/v2/users/" + userId)
                .then().log().all().extract().response();

    }
}

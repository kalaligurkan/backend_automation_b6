package api.scripts.tg_application;

import api.pojo_classes.go_rest.CreateUserWithLombok;
import api.pojo_classes.go_rest.UpdateUserWithLombok;
import api.pojo_classes.tg_application.TGApplicationCreateStudent;
import api.pojo_classes.tg_application.TGApplicationPatchUpdateStudent;
import api.pojo_classes.tg_application.TGApplicationUpdateStudent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static org.hamcrest.Matchers.*;

public class TGApplicationProject {
    Response response;
    RequestSpecification baseSpec;
    Faker faker = new Faker();

    @BeforeMethod
    public void setAPI() {

        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGApplicationURI"))
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void TGApplicationCRUD() throws JsonProcessingException {

        // Retrieve a list of users
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(6000L))
                .body("", hasSize(greaterThanOrEqualTo(2)))
                .body("[1].firstName", equalTo("John"))
                .body("[1].lastName", equalTo("Doe"))
                .extract().response();

        // Create a new user
        TGApplicationCreateStudent createStudent = TGApplicationCreateStudent.builder()
                .firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).dob("2000-01-01")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all().assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(createStudent.getFirstName()))
                .body("lastName", equalTo(createStudent.getLastName()))
                .body("email", equalTo(createStudent.getEmail()))
                .body("dob", equalTo(createStudent.getDob()))
                .extract().response();

        // Get student
        int student_id = response.jsonPath().getInt("id");

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(createStudent.getFirstName()))
                .body("lastName", equalTo(createStudent.getLastName()))
                .body("email", equalTo(createStudent.getEmail()))
                .body("dob", equalTo(createStudent.getDob()))
                .extract().response();

        // Put request
        TGApplicationUpdateStudent updateStudent = TGApplicationUpdateStudent.builder()
                .firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).dob("2002-02-02")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updateStudent)
                .when().put("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(updateStudent.getFirstName()))
                .body("lastName", equalTo(updateStudent.getLastName()))
                .body("email", equalTo(updateStudent.getEmail()))
                .body("dob", equalTo(updateStudent.getDob()))
                .extract().response();

        // Patch request

        TGApplicationPatchUpdateStudent updatePatchStudent = TGApplicationPatchUpdateStudent.builder()
                .email(faker.internet().emailAddress()).dob("2003-03-03")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updatePatchStudent)
                .when().patch("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .body("email", equalTo(updatePatchStudent.getEmail()))
                .body("dob", equalTo(updatePatchStudent.getDob()))
                .extract().response();


        // Get request again
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(6000L))
                .body("", hasSize(greaterThanOrEqualTo(3)))
                .extract().response();

        // Retrieve specific user again

       // int student_id = response.jsonPath().getInt("id");

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(updateStudent.getFirstName()))
                .body("lastName", equalTo(updateStudent.getLastName()))
                .body("email", equalTo(updatePatchStudent.getEmail()))
                .body("dob", equalTo(updatePatchStudent.getDob()))
                .extract().response();

        // Delete student

        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .extract().response();



    }
}

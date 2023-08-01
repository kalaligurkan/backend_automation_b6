package api.scripts.tg_application;

import api.pojo_classes.tg_application.TGApplicationUpdateStudent;
import api.pojo_classes.tg_school.CreateStudent;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class APIProject03 {

    Response response;
    RequestSpecification baseSpec;
    Faker faker = new Faker();


    @BeforeMethod
    public void setAPI(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGApplicationURI"))
                .setContentType(ContentType.JSON)
                .build();

        DBUtil.createDBConnection();
    }

    @Test
    public void TGApplicationCRUD(){
        CreateStudent createStudent = CreateStudent.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("2000-11-11").build();


        // Create user
        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        int id = response.jsonPath().getInt("id");

        String query = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList = DBUtil.getQueryResultList(query);
        List<Object> dbResult = queryResultList.get(0);

        BigDecimal dbId = (BigDecimal) dbResult.get(0);
        int dbIdInt = dbId.intValue();

        List<Object> formattedDBResult = new ArrayList<>(dbResult);
        formattedDBResult.set(0, dbIdInt);

        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createStudent.getDob(),
                createStudent.getEmail(), createStudent.getFirstName(), createStudent.getLastName()));


        // Retrieve a specific user
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .extract().response();

        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createStudent.getDob(),
                createStudent.getEmail(), createStudent.getFirstName(), createStudent.getLastName()));


        TGApplicationUpdateStudent updateStudent = TGApplicationUpdateStudent.builder()
                .firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).dob("2002-12-12")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updateStudent)
                .when().put("/students/" + id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .extract().response();

        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createStudent.getDob(),
                createStudent.getEmail(), createStudent.getFirstName(), createStudent.getLastName()));

        // Retrieve the user again to confirm
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .extract().response();

        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createStudent.getDob(),
                createStudent.getEmail(), createStudent.getFirstName(), createStudent.getLastName()));

        // Delete the user
        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        queryResultList = DBUtil.getQueryResultList(query);

        Assert.assertTrue(queryResultList.isEmpty(), "The student with id: " + id + " is not deleted from the database.");




    }
}
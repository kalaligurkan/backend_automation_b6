package api.scripts.tg_school;

import api.pojo_classes.tg_school.CreateStudent;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TGApplicationAPI {

    Response response;
    private RequestSpecification baseSpec;
    Faker faker = new Faker();



    @BeforeMethod
    public void SetTest(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGApplicationURI"))
                .setContentType(ContentType.JSON)
                .build();

        DBUtil.createDBConnection();
    }

    @Test
    public void TGAPIProject(){
        CreateStudent createStudent = CreateStudent.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("2020-09-20").build();

        // Create a student and validate it is injected in our database
        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        int id = response.jsonPath().getInt("id");


        //Validate the student is created in the database

        String query = "SELECT * FROM STUDENT WHERE id = " + id;

        // First we get the all the rows
        List<List<Object>> queryResultList = DBUtil.getQueryResultList(query);
        // List<List<Object>> arr = { List <Object> {2, "2000-01-01", "john.doe@techglobal.com", "John", "Doe"} }


        //Then since our query will return a single row, we fetch the first element from the list of list
        // which represents our query result
        List<Object> dbResult = queryResultList.get(0);

        /**
         * So making sure the value of our id is int, and we find out that it is actually BigDecimal
         * so even though the values are correctly matching, the test will fail because of different data types.
         * So we print out Data type of the id value that is coming from the database to make sure what is our ID data type
         */

        System.out.println(dbResult.get(0).getClass().getSimpleName() + " data type of the ID");


        //To fix the error above we are casting our ID that is coming from the database as a BigDecimal to int
        BigDecimal dbId = (BigDecimal) dbResult.get(0);
        int dbIdInt = dbId.intValue();

        /**
         * So we create an another ArrayList, and we put our existing List of dbResult in it
         * then, formattedDBResult.set(0, dbIdInt); with set method, we are replacing that
         * BigDecimal id with our casted int id so it won't fail because of the Data type
         */
        List<Object> formattedDBResult = new ArrayList<>(dbResult);
        //this is where we replace BigDecimal id with our casted int id
        formattedDBResult.set(0, dbIdInt);

        for (Object o : formattedDBResult) {
            System.out.println(o);

        }
        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createStudent.getDob(), createStudent.getEmail(),
                createStudent.getFirstName(), createStudent.getLastName()));


        // Creating a delete request to delete what we created, and we will validate it is also removed from the database

        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        queryResultList = DBUtil.getQueryResultList(query);

        Assert.assertTrue(queryResultList.isEmpty(), " The student with id: " + id + " is not deleted from the database.");
    }
}

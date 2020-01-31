package com.automation.tests.day6;

import com.automation.pojos.Spartan;
import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpartanTests {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("spartan.uri");
    }

    /**
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user verifies that status code is 200
     * and user verifies that content type is JSON
     */

    @Test
    @DisplayName("Verify that /spartans end-point returns 200 and content type as JSON")
    public void test1() {
        //web service may return different content type
        //and to request JSON, you can just put in the given part  ContentType.JSON
        //If you want to ask for XML, you can put ContentType.XML
        //but, if web service configured only for JSON
        //it will not give you anything else
        //GET, PUT, POST, DELETE, etc. - HTTP verbs or methods
        //GET - to get the data from web service
        //PUT - update existing record
        //DELETE - delete something, like delete spartan
        //PATCH - partial update of existing record
        given().
                accept(ContentType.JSON).
                when().
                get("/spartans").prettyPeek().
                then().assertThat().
                statusCode(200).
                contentType(ContentType.JSON);

    }


    /**
     * TASK
     * given accept content type as XML
     * when user sends GET request to /spartans
     * then user verifies that status code is 200
     * and user verifies that content type is XML
     */
    @Test
    @DisplayName("Verify that /spartans end-point returns 200 and content type as XML")
    public void test2() {
        //asking
        //accept(ContentType.XML). <- you are asking for payload format as XML from web service
        //receiving
        //contentType(ContentType.XML) <- you are verifying that payload's format is XML
        given().
                accept(ContentType.XML).
                when().
                get("/spartans").prettyPeek().
                then().assertThat().
                statusCode(200).
                contentType(ContentType.XML);

    }

    /**
     * TASK
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user saves payload into collection
     *
     * ###################
     * We can convert payload (JSON body for example) into collection.
     * if it's a single variable: "name" : "James", we acn store in String or List<String>
     * If, there multiple names in the payload, we cannot use single String as a storage
     * instead, use List<String>
     * If payload returns object:
     *  {
     *  "name": "James"
     *  "age" 25
     *  }
     *  Then, we can store this object (on our, java side, as POJO or Map<String, ?>)
     *  If it's a POJO, we need to create corresponding POJO class, in order to map properties
     *  from json and java object:
     *          Java class         JSON file
     *      private String name |  "name"
     *      private int age     |  "age"
     *
     *      If you want to use different variable name in Java class, use @SerializedName annotation
     *
     *          Java class            JSON file
     *      @SerializedName("name")
     *      private String firstName |  "name"
     *      private int age          |  "age"
     *
     *      otherwise,  Gson, jackson, or any other Json parser, will not be able to map properties correctly.
     *      Serialization - from POJO (java object) to stream of bytes, let's say to JSON
     *      Deserialization - from stream of bytes, let's say JSON into POJO (java object)
     *
     *      If payload returns array ob objects:
     *
     *
     *          [
     *              {
     *                  "id": 202,
     *                  "name": "Helen Highwater",
     *                  "gender": "Female",
     *                  "phone": 60242012223
     *              },
     *              {
     *                  "id": 203,
     *                  "name": "Ellie Noise",
     *                  "gender": "Female",
     *                  "phone": 37812781233
     *              },
     *          ]
     *
     *          Then we can store this payload as List<Map<?, ?>>>
     *          or like list of POJO's List<Spartan>
     *
     */
    @Test
    @DisplayName("Save payload into java collection")
    public void test3() {
        Response response = given().
                contentType(ContentType.JSON).
                when().
                get("/spartans");

        List<Map<String, ?>> collection = response.jsonPath().get();

        for (Map<String, ?> map : collection) {
            System.out.println(map);
        }
    }

    /**
     * TASK
     * given accept content type as JSON
     * when user sends GET request to /spartans
     * then user saves payload into collection of Spartan
     */

    @Test
    @DisplayName("Save payload into java collection of Spartan")
    public void test4() {
        Response response = given().
                contentType(ContentType.JSON).
                when().
                get("/spartans").prettyPeek();
        //whenever you see: Class object = response.jsonPath.getObject() | deserialization
        List<Spartan> collection = response.jsonPath().getList("", Spartan.class);

        for (Spartan spartan : collection) {
            System.out.println(spartan);
        }
    }

    /**
     * TASK
     * given accept content type as JSON
     * when user sends POST request to /spartans
     * and user should be able to create new spartan
     * |gender|name           |phone     |
     * | male |Mister Twister |5712134235|
     * then user verifies that status code is 201
     * <p>
     * 201 - means created. Whenever you POST something, you should get back 201 status code
     * in case of created record
     */

    @Test
    @DisplayName("Create new spartan and verify that status code is 201")
    public void test5() {
        //builder pattern, one of the design patterns in OOP
        //instead of having too many different constructors
        //we can use builder pattern and chain with{preopertyName} methods to specify properties of an object
        Spartan spartan1 = new Spartan().
                withGender("Male").
                withName("Some User").
                withPhone(5712134235L);

        Spartan spartan = new Spartan();
        spartan.setGender("Male");//Male or Female
        spartan.setName("Mister Twister");
        spartan.setPhone(5712134235L); //at least 10 digits

        Response response = given().
                contentType(ContentType.JSON).
                body(spartan).
                when().
                post("/spartans");
        assertEquals(201, response.getStatusCode(), "Status code is wrong!");
        assertEquals("application/json", response.getContentType(), "Content type is invalid!");
        assertEquals(response.jsonPath().getString("success"), "A Spartan is Born!");

        response.prettyPrint();

        Spartan spartan_from_response = response.jsonPath().getObject("data", Spartan.class);

        System.out.println("Spartan id: " + spartan_from_response.getSpartanId());

//        //delete spartan that you've just created
//        when().delete("/spartans/{id}", spartan_from_response.getSpartanId()).
//                prettyPeek().
//                then().assertThat().statusCode(204);

    }

    @Test
    @DisplayName("Delete user")
    public void test6() {
        int idOfTheUserThatYouWantToDelete = 125;

        Response response = when().delete("/spartans/{id}", idOfTheUserThatYouWantToDelete);

        response.prettyPeek();
    }



    @Test
    @DisplayName("Update spartan")
    public void test9() {
        Spartan spartan = new Spartan().
                withGender("Male").
                withName("Guru of Java").
                withPhone(9999999999L);

        Response response = given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                body(spartan).
                pathParam("id", 380).
                put("/spartans/{id}").prettyPeek();
        //put update existing record
        // also when you make PUT request, you need to specify entire body
        //post - create new one
        //we never POST/PUT id, it must be auto generated
        //if it's not like this - it's a bug

        // contentType(ContentType.JSON) in the given()
        // you tell to the web service, what data you are sending
    }

    @Test
    @DisplayName("Update only name with PATCH")
    public void test10(){
        Map<String, Long> update = new HashMap<>();
        update.put("phone", 10000000000L);

        Response response = given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                body(update).
                pathParam("id", 381).
                patch("/spartans/{id}");

        response.prettyPrint();
        //POST - add new spartan
        //PUT - update existing one, but you have to specify all properties
        //PATCH - update existing one, but ypu may specify one or more properties to update


    }

}
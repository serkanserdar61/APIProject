package com.automation.tests.day4;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ORDSTestsDay4 {
    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    /**
     * Warmup!
     * Given accept type is JSON
     * When users sends a GET request to "/employees"
     * Then status code is 200
     * And Content type is application/json
     * And response time is less than 3 seconds
     */
    @Test
    @DisplayName("Verify that response time is less than 3 seconds")
    public void test1() {
        given().
                accept(ContentType.JSON).
                when().
                get("/employees").
                then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                time(lessThan(3L), TimeUnit.SECONDS).
                log().all(true);//payload=body, and our body has JSON format

        //.all(true) < it's like prettyPrint(), prettyPick(), but they just print body
        //log().all(true) <-- print into console all info about response: header, body, status code, schema(http), schema version (1.1)
    }

    /*{{baseUrl}}/countries?q={"country_id":"US"}
    Given accept type is JSON
    And parameters: q = {"country_id":"US"}
    When users sends a GET request to "/countries"
    Then status code is 200
    And Content type is application/json
    And country_name from payload is "United States of America"
 */
    @Test
    @DisplayName("Verify that country_name from payload is \"United States of America\"")
    public void test2() {
        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"country_id\":\"US\"}").
                when().
                get("/countries").
                then().
                assertThat().
                contentType(ContentType.JSON).
                statusCode(200).
                body("items[0].country_name", is("United States of America")).
                log().all(true);
    }

    @Test
    @DisplayName("Get all links and print them")
    public void test3() {
        Response response = given().
                accept(ContentType.JSON).
//                                queryParam("q", "{\"country_id\":\"US\"}").
        when().
                        get("/countries");

        JsonPath jsonPath = response.jsonPath();
        //if I don't put index, I will get collection of properties (only if they exists)
        List<?> links = jsonPath.getList("items.links.href");

        for(Object link: links){
            System.out.println(link);
        }

    }

    @Test
    @DisplayName("Verify that payload contains only 25 countries")
    public void test4(){
        List<?> countries = given().
                accept(ContentType.JSON).
                when().
                get("/countries").prettyPeek().
                thenReturn().jsonPath().getList("items");

        assertEquals(25, countries.size());
    }

    /**
     * given path parameter is "/countries" and region id is 2
     * when user makes get request
     * then assert that status code is 200
     * and user verifies that body returns following country names
     *  |Argentina                |
     *  |Brazil                   |
     *  |Canada                   |
     *  |Mexico                   |
     *  |United States of America |
     *
     */
//to switch to java 9, add/replace it in pom.xml:

//    <build>
//        <plugins>
//            <plugin>
//                <groupId>org.apache.maven.plugins</groupId>
//                <artifactId>maven-compiler-plugin</artifactId>
//                <configuration>
//                    <source>9</source>
//                    <target>9</target>
//                </configuration>
//            </plugin>
//        </plugins>
//    </build>

    @Test
    @DisplayName("Verify that payload contains following countries")
    public void test5() {
        //to use List.of() set java 9 at least
        List<String> expected = List.of("Argentina", "Brazil", "Canada", "Mexico", "United States of America");
                             //Arrays.asList de kullanilabilir
        Response response = given().
                accept(ContentType.JSON).
                queryParam("q", "{\"region_id\":\"2\"}").
                when().
                get("/countries").prettyPeek();
        List<String> actual = response.jsonPath().getList("items.country_name");

        assertEquals(expected, actual);

        ///with assertThat()

        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"region_id\":\"2\"}").
                when().
                get("/countries").
                then().assertThat().body("items.country_name" , contains("Argentina", "Brazil", "Canada", "Mexico", "United States of America"));
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * Then user verifies that every employee has positive salary
     *
     */
    @Test
    @DisplayName("Verify that every employee has positive salary")
    public void test6(){
        given().
                accept(ContentType.JSON).
                when().
                get("/employees").
                then().
                assertThat().
                statusCode(200).
                body("items.salary", everyItem(greaterThan(0)));

        //whenever you specify path as items.salary, you will get collection of salaries
        //then to check every single value
        //we can use everyItem(is()), everyItem(greaterThan())
        /**
         * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
         * examined {@link Iterable} yields items that are all matched by the specified
         * <code>itemMatcher</code>.
         * For example:
         * <pre>assertThat(Arrays.asList("bar", "baz"), everyItem(startsWith("ba")))</pre>*/
    }

    /**
     * given path parameter is "/employees/{id}"
     * and path parameter is 101
     * when user makes get request
     * then assert that status code is 200
     * and verifies that phone number is 515-123-4568
     *
     */

    @Test
    @DisplayName("Verify that employee 101 has following phone number: 515-123-4568")
    public void test7(){
        Response response = given().
                accept(ContentType.JSON).
                when().
                get("/employees/{id}", 101);
        assertEquals(200, response.getStatusCode());

        String expected = "515-123-4568";
        String actual = response.jsonPath().get("phone_number");

        expected = expected.replace("-", ".");
        assertEquals(expected, actual);
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * and verify that body returns following salary information after sorting from higher to lower
     *  24000, 17000, 17000, 12008, 11000,
     *  9000, 9000, 8200, 8200, 8000,
     *  7900, 7800, 7700, 6900, 6500,
     *  6000, 5800, 4800, 4800, 4200,
     *  3100, 2900, 2800, 2600, 2500
     *
     */

    @Test
    @DisplayName("verify that body returns following salary information after sorting from higher to lower(after sorting it in descending order)")
    public void test8(){
        List<Integer> expectedSalaries = List.of(24000, 17000, 17000, 12008, 11000,
                9000, 9000, 8200, 8200, 8000,
                7900, 7800, 7700, 6900, 6500,
                6000, 5800, 4800, 4800, 4200,
                3100, 2900, 2800, 2600, 2500);//Arrays.asList de kullanilabilir
        Response response = given().
                accept(ContentType.JSON).
                when().
                get("/employees");
        assertEquals(200, response.statusCode());

        List<Integer> actualSalaries = response.jsonPath().getList("items.salary");

        Collections.sort(actualSalaries, Collections.reverseOrder());

        System.out.println(actualSalaries);

        assertEquals(expectedSalaries, actualSalaries, "Salaries are not matching");
    }


    /** ####TASK#####
     *  Given accept type as JSON
     *  And path parameter is id with value 2900
     *  When user sends get request to /locations/{id}
     *  Then user verifies that status code is 200
     *  And user verifies following json path contains following entries:
     *      |street_address         |city  |postal_code|country_id|state_province|
     *      |20 Rue des Corps-Saints|Geneva|1730       |CH        |Geneve        |
     *
     */

    /**
     *     "location_id": 2900,
     *     "street_address": "20 Rue des Corps-Saints",
     *     "postal_code": "1730",
     *     "city": "Geneva",
     *     "state_province": "Geneve",
     *     "country_id": "CH",
     */
    @Test
    @DisplayName("Verify that JSON body has following entries")
    public void test9(){
        given().
                accept(ContentType.JSON).
                pathParam("id", 2900).
                when().
                get("/locations/{id}").
                then().
                assertThat().
                statusCode(200).
                body("", hasEntry("street_address", "20 Rue des Corps-Saints")).
                body("", hasEntry("city", "Geneva")).
                body("", hasEntry("postal_code", "1730")).
                body("", hasEntry("country_id", "CH")).
                body("", hasEntry("state_province", "Geneve")).
                log().all(true);

    }

    @Test
    @DisplayName("Verify that JSON body has following entries")
    public void test9_2(){
        given().
                accept(ContentType.JSON).
                pathParam("id", 2900).
                when().
                get("/locations/{id}").
                then().
                assertThat().
                statusCode(200).
                body("street_address", is("20 Rue des Corps-Saints")).
                body("city", is("Geneva")).
                body("postal_code", is("1730")).
                body("country_id", is("CH")).
                body("state_province", is("Geneve")).
                log().all(true);
    }

}
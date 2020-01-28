package com.automation.tests.day4;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class practice {
    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("meta.weather.uri");
    }

    @Test
    @DisplayName("verify.......")
    public void test() {
        given().
                accept(ContentType.JSON).
                queryParam("query", "new").
                when().
                get("/search").
                then().
                assertThat().
                statusCode(200).
                body("", hasSize(5)).
                log().all(true);


    }

    @Test
    @DisplayName("verify....")
    public void test1() {
        given().
                accept(ContentType.JSON).
                queryParam("query", "new").
                when().
                get("/search").
                then().
                assertThat().
                statusCode(200).
                body("title[0]", is("New York")).
                body("location_type[0]", is("City")).
                body("woeid[0]", is(2459115)).
                body("latt_long[0] ", is("40.71455,-74.007118")).
                log().all(true);

    }

    @Test
    @DisplayName("verify....")
    public void test2() {
        Map<String,String> expected = new HashMap<>();
        expected.put("title", "New York");
        expected.put("location_type", "City");
        expected.put("woeid", "2459115");
        expected.put("latt_long", "40.71455,-74.007118");

        Response response = given().
                accept(ContentType.JSON).
                queryParam("query", "New").
                when().
                get("/search");
        JsonPath jsonPath = response.jsonPath();
        List<Map<String, ?>> values = jsonPath.get();
        for(Map<String, ?> value: values){
            System.out.println(value);
        }

    }
    @Test
    @DisplayName("verify....")
    public void test3() {
        given().
                accept(ContentType.JSON).
                queryParam("query","Las").
                when().
                get("/search").
                then().
                assertThat().statusCode(200).
                body("title",contains("Glasgow", "Dallas", "Las Vegas"));

    }
    @Test
    @DisplayName("verify....")
    public void test4() {
        given().
                accept(ContentType.JSON).
                queryParam("query","Las").
        when().
                get("/search").
        then().
                assertThat().statusCode(200).
                body("title[0]",is("Glasgow")).
                body("title[1]",is("Dallas")).
                body("title[2]",is("Las Vegas"));

    }

    @Test
    @DisplayName("verify....")
    public void test5() {
        given().
                accept(ContentType.JSON).
                queryParam("query","Las").
                when().
                get("/search").
                then().
                assertThat().statusCode(200).
                body("location_type[0]",is("City")).
                body("location_type[1]",is("City")).
                body("location_type[2]",is("City"));

    }
    @Test
    @DisplayName("Verify.....")
    public void test7() {
        given().
                accept(ContentType.JSON).
                pathParam("woeid",44418).
                when().
                get("/location{woeid}").
                then().
                assertThat().statusCode(200).
               body("title",hasItems("BBC", "Forecast.io", "HAMweather","Met Office","OpenWeatherMap","Weather Underground","World Weather Online"));

    }
}













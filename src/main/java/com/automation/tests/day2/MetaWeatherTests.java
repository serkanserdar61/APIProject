package com.automation.tests.day2;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetaWeatherTests {

    /**
     * /api/location/search/?query=san
     * /api/location/search/?query=london
     * /api/location/search/?lattlong=36.96,-122.02
     * /api/location/search/?lattlong=50.068,-5.316
     * /api/location/44418/ }/
     * "title": "San Francisco",
     * "location_type": "City",
     * "woeid": 2487956,
     * "latt_long": "37.777119, -122.41964"
     * },
     */

    private String baseURI = "https://www.metaweather.com/api/";

    @Test
    public void test1() {
        Response response = given()
                .baseUri(baseURI + "location/search/")
                .queryParam("query", "New")
                .get();
        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPeek());

    }

    // /users/100/ - 100 it's a path parameter
    // /users/255/ - 255 it's a path parameter
    // /users/255?name=James | name - query parameter key=value , key it's a query parameter
    //        "woeid": 2514815,this woeid stands for where on earth ID,based on this value we can get weather info in specific place
    @Test
    public void test2() {
        Response response = given()

                .pathParam("woeid", "2514815")
                .get(baseURI + "location/{woeid}");
        System.out.println(response.prettyPrint());

    }
}
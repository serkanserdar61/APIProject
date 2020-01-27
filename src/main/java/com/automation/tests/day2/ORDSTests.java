package com.automation.tests.day2;


import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ORDSTests {
    //address of ORDS web service, that is running no AWS ec2.
    //data is coming from SQL Oracle data base to this web service
    //during back-end testing with SQL developer and JDBC API
    //we were accessing data base directly
    //now, we gonna access web service

    //according to OOP conventions, all instance variable should be private
    //but, if we will make it public, it will not make ant difference for us
    //it's just good practice, so later we will not hesitate which keyword to use when it's gonna important

    //ec2-34-201-69-55.compute-1.amazonaws.com - my host, you have something else
    //   /ords/hr or //ords/hr/employees - same for all
    private String baseURI = "http://ec2-34-201-69-55.compute-1.amazonaws.com:1000/ords/hr";

    //we start from given()
    //then we can specify type of request like: get(), put(), delete(), post()
    //and as parameter, we enter resource location (URI)
    //then we are getting response back. that response we can put into Response object
    //from response object, we can retrieve: body, header, status code
    //it works without RestAssured.given() because of static import
    //verify that status code is 200
    @Test
    public void test1() {
        Response response = given().get(baseURI + "/employees");

       //System.out.println(response.getBody().asString());

        assertEquals(200, response.getStatusCode());


        System.out.println(response.prettyPrint());
    }

    //#TASK: get employee with id 100 and verify that response returns status code 200
    //also, print body
    @Test
    public void test2() {
        //header stands for meta data
        //usually it's used to include cookies
        //in this example, we are specifying what kind of response type we need
        //because web service can return let's say json or xml
        //when we put header info "Accept", "application/json", we are saying that we need only json as response
        Response response = given().
                header("accept", "application/json").
                get(baseURI + "/employees/100");
        System.out.println(response.path("first_name").toString());
        System.out.println(response.path("last_name").toString());
        System.out.println(response.path("email").toString());
        System.out.println(response.path("hire_date").toString());
       assertEquals("Holy",response.path("first_name").toString());
        assertTrue(response.asString().contains("Holy"));
        int actualStatusCode = response.getStatusCode();
        System.out.println(response.prettyPrint());
        assertEquals(200, actualStatusCode);

        //get information about response content type, you can retrieve from response object
        System.out.println("What kind of content server sends to you, in this response: "+response.getHeader("Content-Type"));
    }

    //    #Task: perform GET request to /regions, print body and all headers.
    @Test
    public void test3(){
        Response response = given().get(baseURI+"/regions");
        assertEquals(200, response.getStatusCode());
        //to get specific header
        Header header = response.getHeaders().get("Content-Type");

        //print all headers one by one
        for(Header h: response.getHeaders()){
            System.out.println(h);
        }
        System.out.println("##########################");
        System.out.println(response.prettyPrint());
    }
}
package com.musicfever.music_fever.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthE2ETest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private Response login(boolean usernameMode){
        Map<String, String> body = Map.of(
                usernameMode ? "username" : "email",
                usernameMode ? "armiiin" : "armin@gmail.com",
                "password", "eras1325"
        );

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1/auth/login");
    }

    @Test
    public void loginUser_withEmail_returns200() {
        Response response = this.login(false);
        response.then().statusCode(200);
    }

    @Test
    public void loginUser_withUsername_returns200() {
        Response response = this.login(true);
        response.then().statusCode(200);
    }

    @Test 
    void loginUser_returns409(){
        Map<String, String> body = Map.of(
                "username", "doesNotExist",
                "password", "eras1325"
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1/auth/login");
        response.then().statusCode(409);
    }

    @Test
    public void registerUser_returns200() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "username": "test",
                    "email": "test@gmail.com",
                    "password": "test1234"
                }
                """)
                .when()
                .post("/api/v1/auth/register");

        response.then().statusCode(200);
    }

    @Test 
    public void registerUser_returns409(){
        Response response = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "username": "armiiin",
                    "email": "armiiin@gmail.com",
                    "password": "test1234"
                }
                """)
                .when()
                .post("/api/v1/auth/register");

        response.then().statusCode(409);
    }

    @Test
    public void getActiveUser_returns200_withCorrectData(){
        Response response = this.login(true);
        Map<String, String> cookies = response.getCookies();
        Response getResponse = given()
                .cookies(cookies)
                .get("/api/v1/auth/me");
        getResponse.then().statusCode(200)
                .body("username", is("armiiin"));
    }

    @Test
    public void logoutUser_returns204(){
        Response response = this.login(true);
        Map<String, String> cookies = response.getCookies();
        Response logoutResponse = given()
                .cookies(cookies)
                .post("/api/v1/auth/logout");
        logoutResponse.then().statusCode(204); //No Content
    }
}

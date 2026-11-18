package com.musicfever.music_fever.e2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserE2ETest {

    @LocalServerPort
    int port;

    private String username;
    private String email;
    private Map<String, String> cookies;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        username = "test" + UUID.randomUUID().toString().substring(0, 8);
        email = username + "@example.com";
        String password = "test1234";

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", email, "password", password))
            .when()
            .post("/api/v1/auth/register")
            .then()
            .statusCode(200);

        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "password", password))
            .when()
            .post("/api/v1/auth/login");

        loginResponse.then().statusCode(200);
        cookies = loginResponse.getCookies();
    }

    @Test
    public void getActiveUser_returns200_withExpectedUsername() {
        Response response = given()
            .cookies(cookies)
            .get("/api/v1/me");

        response.then().statusCode(200)
            .body("username", is(username));
    }

    @Test
    public void getUserProfile_returns200_withExpectedData() {
        Response response = given()
            .cookies(cookies)
            .get("/api/v1/me/profile");

        response.then().statusCode(200)
            .body("username", is(username))
            .body("email", is(email));
    }

    @Test
    public void updateProfile_returns200_andPersistsChanges() {
        String updatedEmail = username + ".updated@example.com";
        String pictureUrl = "https://example.com/profile.png";

        Response response = given()
            .cookies(cookies)
            .contentType(ContentType.JSON)
            .body(Map.of("email", updatedEmail, "profilePicture", pictureUrl))
            .when()
            .patch("/api/v1/me");

        response.then().statusCode(200)
            .body("username", is(username))
            .body("email", is(updatedEmail))
            .body("profilePicture.url", is(pictureUrl));

        Response profileResponse = given()
            .cookies(cookies)
            .get("/api/v1/me/profile");

        profileResponse.then().statusCode(200)
            .body("email", is(updatedEmail))
            .body("profilePicture.url", is(pictureUrl));
    }
}
package com.musicfever.music_fever.e2e;

import static io.restassured.RestAssured.*;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.config.PostgresTestBase;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TrackE2ETest extends PostgresTestBase{
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test 
    public void getTracklist_returns200_withExpectedTracks() {
        Response response = when().get("/api/v1/tracks/");

        response.then().body("name", hasSize(3));
        response.then().statusCode(200)
            .body("name", hasItem("Patient Zero"))
            .body("name", hasItem("NOBODY'S GIRL"))
            .body("name", hasItem("petal"));
    }
}
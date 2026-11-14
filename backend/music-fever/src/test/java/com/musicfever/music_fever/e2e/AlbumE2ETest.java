package com.musicfever.music_fever.e2e;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.TrackRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AlbumE2ETest extends PostgresTestBase {

    @LocalServerPort
    int port;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    private Album albumTest;
    private Album otherAlbum;
    private List<Track> tracksTest = List.of();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        albumTest = albumRepository.save(createAlbum("e2e-album-1", "Test Album"));
        otherAlbum = albumRepository.save(createAlbum("e2e-album-2", "Other Album"));

        tracksTest = trackRepository.saveAll(List.of(
            createTrack("e2e-track-3", "Final Track", albumTest, 2, 1),
            createTrack("e2e-track-2", "Second Track", albumTest, 1, 2),
            createTrack("e2e-track-1", "Opening Track", albumTest, 1, 1),
            createTrack("e2e-track-4", "Unrelated Track", otherAlbum, 1, 1)
        ));
    }

    @AfterEach
    void cleanUp() {
        trackRepository.deleteAllById(tracksTest.stream().map(Track::getId).toList());

        if (albumTest != null) {
            albumRepository.deleteById(albumTest.getId());
        }
        if (otherAlbum != null) {
            albumRepository.deleteById(otherAlbum.getId());
        }
    }

    @Test
    public void getAlbum_returns200_withExpectedAlbum() {
        Response response = when().get("/api/v1/albums/{id}", albumTest.getId());

        response.then().statusCode(200)
            .body("name", is("Test Album"));
    }

    @Test
    public void getTracklist_returns200_withOrderedAndPaginatedTracks() {
        Response firstPage = given()
            .queryParam("page", 0)
            .queryParam("size", 2)
            .when()
            .get("/api/v1/albums/{id}/tracks", albumTest.getId());

        firstPage.then().statusCode(200)
            .body("content.name", contains("Opening Track", "Second Track"));

        Response secondPage = given()
            .queryParam("page", 1)
            .queryParam("size", 2)
            .when()
            .get("/api/v1/albums/{id}/tracks", albumTest.getId());

        secondPage.then().statusCode(200)
            .body("content.name", contains("Final Track"));
    }

    @Test
    public void getAlbum_returns409_whenAlbumDoesNotExist() {
        Response response = when().get("/api/v1/albums/{id}", Long.MAX_VALUE);

        response.then().statusCode(409);
    }

    @Test
    public void getTracklist_returns409_whenAlbumDoesNotExist() {
        Response response = when().get("/api/v1/albums/{id}/tracks", Long.MAX_VALUE);

        response.then().statusCode(409);
    }

    private Album createAlbum(String spotifyId, String name) {
        Album album = new Album();
        album.setSpotifyId(spotifyId);
        album.setName(name);
        album.setLoaded(true);
        return album;
    }

    private Track createTrack(String spotifyId, String name, Album album,
                              int discNumber, int number) {
        Track track = new Track();
        track.setSpotifyId(spotifyId);
        track.setName(name);
        track.setAlbum(album);
        track.setDiscNumber(discNumber);
        track.setNumber(number);
        return track;
    }
}

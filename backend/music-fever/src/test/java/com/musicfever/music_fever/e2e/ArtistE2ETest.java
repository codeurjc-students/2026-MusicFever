package com.musicfever.music_fever.e2e;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ArtistE2ETest extends PostgresTestBase {

    private static final String ARTIST_PATH = "/api/v1/artists/{id}";

    @LocalServerPort
    int port;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    private Long artistId;
    private List<Long> artistIds = List.of();
    private List<Long> albumIds = List.of();
    private List<Long> trackIds = List.of();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        transactionTemplate.executeWithoutResult(status -> {
            Artist artist = createArtist("e2e-artist-1", "Test Artist");
            Artist otherArtist = createArtist("e2e-artist-2", "Other Artist");

            Album album = createAlbum("e2e-artist-album-1", "Test Album", artist);
            Album otherAlbum = createAlbum("e2e-artist-album-2", "Other Album", otherArtist);

            Track popularTrack = createTrack("e2e-artist-track-1", "Popular Track", album, 1);
            Track albumTrack = createTrack("e2e-artist-track-2", "Album Track", album, 2);
            Track otherTrack = createTrack("e2e-artist-track-3", "Other Track", otherAlbum, 1);

            artist.getTopTracks().add(popularTrack);
            otherArtist.getTopTracks().add(otherTrack);

            artistId = artist.getId();
            artistIds = List.of(artistId, otherArtist.getId());
            albumIds = List.of(album.getId(), otherAlbum.getId());
            trackIds = List.of(popularTrack.getId(), albumTrack.getId(), otherTrack.getId());
        });
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            for (Artist artist : artistRepository.findAllById(artistIds)) {
                artist.getTopTracks().clear();
            }
            artistRepository.flush();

            trackRepository.deleteAllById(trackIds);
            trackRepository.flush();

            albumRepository.deleteAllById(albumIds);
            albumRepository.flush();

            artistRepository.deleteAllById(artistIds);
        });
    }

    @Test
    public void getArtist_returns200_withExpectedArtist() {
        Response response = when().get(ARTIST_PATH, artistId);

        response.then().statusCode(200)
            .body("name", is("Test Artist"));
    }

    @Test
    public void getArtistAlbums_returns200_withOnlyTheArtistsAlbums() {
        Response response = given()
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get(ARTIST_PATH + "/albums", artistId);

        response.then().statusCode(200)
            .body("content.name", contains("Test Album"));
    }

    @Test
    public void getPopularTracks_returns200_withOnlyTheArtistsPopularTracks() {
        Response response = given()
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get(ARTIST_PATH + "/popular-tracks", artistId);

        response.then().statusCode(200)
            .body("content.name", contains("Popular Track"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/albums", "/popular-tracks"})
    public void getArtistEndpoint_returns409_whenArtistDoesNotExist(String suffix) {
        Response response = when().get(ARTIST_PATH + suffix, Long.MAX_VALUE);

        response.then().statusCode(409);
    }

    private Artist createArtist(String spotifyId, String name) {
        Artist artist = new Artist();
        artist.setSpotifyId(spotifyId);
        artist.setName(name);
        artist.setLoaded(true);
        return artistRepository.save(artist);
    }

    private Album createAlbum(String spotifyId, String name, Artist artist) {
        Album album = new Album();
        album.setSpotifyId(spotifyId);
        album.setName(name);
        album.setLoaded(true);
        album.getArtists().add(artist);
        return albumRepository.save(album);
    }

    private Track createTrack(String spotifyId, String name, Album album, int number) {
        Track track = new Track();
        track.setSpotifyId(spotifyId);
        track.setName(name);
        track.setAlbum(album);
        track.setDiscNumber(1);
        track.setNumber(number);
        return trackRepository.save(track);
    }
}

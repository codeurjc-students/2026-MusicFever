package com.musicfever.music_fever.e2e;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.api.ApiService;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@MockitoBean(types = ApiService.class)
public class SearchE2ETest extends PostgresTestBase {

    private static final String SEARCH_PATH = "/api/v1/search";
    private static final String LOCAL_SEARCH = "e2eSearchFixture";
    private static final String ARTIST_NAME = LOCAL_SEARCH + " Artist";
    private static final String ALBUM_NAME = LOCAL_SEARCH + " Album";
    private static final String TRACK_NAME = LOCAL_SEARCH + " Track";

    @LocalServerPort
    int port;

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final ApiService apiService;

    private Artist artistTest;
    private Album albumTest;
    private Track trackTest;

    public SearchE2ETest(
            ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            TrackRepository trackRepository,
            ApiService apiService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.apiService = apiService;
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        Artist artist = new Artist();
        artist.setSpotifyId("e2e-search-artist");
        artist.setName(ARTIST_NAME);
        artist.setLoaded(true);
        artistTest = artistRepository.save(artist);

        Album album = new Album();
        album.setSpotifyId("e2e-search-album");
        album.setName(ALBUM_NAME);
        album.setLoaded(true);
        albumTest = albumRepository.save(album);

        Track track = new Track();
        track.setSpotifyId("e2e-search-track");
        track.setName(TRACK_NAME);
        track.setAlbum(albumTest);
        trackTest = trackRepository.save(track);
    }

    @AfterEach
    void cleanUp() {
        if (trackTest != null) {
            trackRepository.deleteById(trackTest.getId());
        }
        if (albumTest != null) {
            albumRepository.deleteById(albumTest.getId());
        }
        if (artistTest != null) {
            artistRepository.deleteById(artistTest.getId());
        }
    }

    @Test
    public void getSearch_returns200_withLocalResults() {
        Response response = given()
            .queryParam("search", LOCAL_SEARCH)
            .when()
            .get(SEARCH_PATH);

        response.then().statusCode(200)
            .body("size", is(3))
            .body("artists.name", contains(ARTIST_NAME))
            .body("albums.name", contains(ALBUM_NAME))
            .body("tracks.name", contains(TRACK_NAME));

        verifyNoInteractions(apiService);
    }

    @Test
    public void getSearch_returns200_withSpotifyResults_whenNoLocalResultsExist() {
        String search = "e2eSearchNoLocalMatch";
        when(apiService.getSearch(search)).thenReturn(createSpotifyResponse());

        Response response = given()
            .queryParam("search", search)
            .when()
            .get(SEARCH_PATH);

        response.then().statusCode(200)
            .body("size", is(3))
            .body("artists.name", contains(ARTIST_NAME))
            .body("albums.name", contains(ALBUM_NAME))
            .body("tracks.name", contains(TRACK_NAME));

        verify(apiService).getSearch(search);
    }

    @Test
    public void getExternalSearch_returns200_evenWhenLocalResultsExist() {
        when(apiService.getSearch(LOCAL_SEARCH)).thenReturn(createSpotifyResponse());

        Response response = given()
            .queryParam("search", LOCAL_SEARCH)
            .when()
            .get(SEARCH_PATH + "/external-search");

        response.then().statusCode(200)
            .body("size", is(3))
            .body("artists.name", contains(ARTIST_NAME))
            .body("albums.name", contains(ALBUM_NAME))
            .body("tracks.name", contains(TRACK_NAME));

        verify(apiService).getSearch(LOCAL_SEARCH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/external-search"})
    public void getSearchEndpoint_returns400_whenSearchParameterIsMissing(String suffix) {
        Response response = when().get(SEARCH_PATH + suffix);

        response.then().statusCode(400);
        verifyNoInteractions(apiService);
    }

    private SearchResponse createSpotifyResponse() {
        SpotifyArtistDTO artist = new SpotifyArtistDTO();
        artist.setId(artistTest.getSpotifyId());

        SpotifyAlbumDTO album = new SpotifyAlbumDTO();
        album.setId(albumTest.getSpotifyId());

        SpotifyTrackDTO track = new SpotifyTrackDTO();
        track.setId(trackTest.getSpotifyId());

        SearchResponse response = new SearchResponse();
        response.setArtists(createPage(List.of(artist)));
        response.setAlbums(createPage(List.of(album)));
        response.setTracks(createPage(List.of(track)));
        return response;
    }

    private <T> ItemsPage<T> createPage(List<T> items) {
        ItemsPage<T> page = new ItemsPage<>();
        page.setItems(items);
        return page;
    }
}
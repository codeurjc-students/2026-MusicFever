package com.musicfever.music_fever.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;
import com.musicfever.music_fever.service.api.SpotifyService;
import com.musicfever.music_fever.service.api.TokenService;

@ActiveProfiles("test")
@SpringBootTest(
    classes = {SpotifyService.class, SpotifyIntegrationTest.Config.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SpotifyIntegrationTest {

    private static final String BASE_URL = "https://api.spotify.com/v1";
    private static final String ARTIST_ID = "artist-1";
    private static final String ALBUM_ID = "album-1";
    private static final String TRACK_ID = "track-1";

    private final SpotifyService service;
    private final MockRestServiceServer server;

    public SpotifyIntegrationTest(SpotifyService service, MockRestServiceServer server) {
        this.service = service;
        this.server = server;
    }

    @BeforeEach
    void setup() {
        // Given
        server.reset();
    }

    @AfterEach
    void verifyRequests() {
        server.verify();
    }

    @Test
    void shouldReturnArtistsAlbumsAndTracks_whenSearching() {
        // Given
        expectGet(BASE_URL + "/search?q=Test&type=artist,album,track&limit=3", """
            {
              "artists": {"items": [{"id": "artist-1", "name": "Test Artist"}]},
              "albums": {"items": [{"id": "album-1", "name": "Test Album"}]},
              "tracks": {"items": [{"id": "track-1", "name": "Test Track"}]}
            }
            """);

        // When
        SearchResponse result = this.service.getSearch("Test");

        // Then
        assertEquals(ARTIST_ID, result.getArtists().getItems().get(0).getId());
        assertEquals(ALBUM_ID, result.getAlbums().getItems().get(0).getId());
        assertEquals(TRACK_ID, result.getTracks().getItems().get(0).getId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReturnArtist_whenLookingUpByIdOrHref(boolean byHref) {
        // Given
        String href = BASE_URL + "/artists/" + ARTIST_ID;
        expectGet(href, """
            {"id": "artist-1", "name": "Test Artist"}
            """);

        // When
        SpotifyArtistDTO result = byHref
            ? this.service.getArtistByHref(href)
            : this.service.getArtistById(ARTIST_ID);

        // Then
        assertEquals(ARTIST_ID, result.getId());
        assertEquals("Test Artist", result.getName());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReturnAlbum_whenLookingUpByIdOrHref(boolean byHref) {
        // Given
        String href = BASE_URL + "/albums/" + ALBUM_ID;
        expectGet(href, """
            {
              "id": "album-1",
              "name": "Test Album",
              "album_type": "album",
              "total_tracks": 2,
              "release_date": "2025-06",
              "artists": [{"id": "artist-1", "name": "Test Artist"}]
            }
            """);

        // When
        SpotifyAlbumDTO result = byHref
            ? this.service.getAlbumByHref(href)
            : this.service.getAlbumById(ALBUM_ID);

        // Then
        assertEquals(ALBUM_ID, result.getId());
        assertEquals("album", result.getAlbumType());
        assertEquals(2, result.getTotalTracks());
        assertEquals(java.time.LocalDate.of(2025, 6, 1), result.getReleaseDate());
        assertEquals(ARTIST_ID, result.getArtists().get(0).getId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReturnTrack_whenLookingUpByIdOrHref(boolean byHref) {
        // Given
        String href = BASE_URL + "/tracks/" + TRACK_ID;
        expectGet(href, """
            {
              "id": "track-1",
              "name": "Test Track",
              "duration_ms": 180000,
              "track_number": 2,
              "album": {"id": "album-1", "name": "Test Album"}
            }
            """);

        // When
        SpotifyTrackDTO result = byHref
            ? this.service.getTrackByHref(href)
            : this.service.getTrackById(TRACK_ID);

        // Then
        assertEquals(TRACK_ID, result.getId());
        assertEquals(180_000, result.getDuration());
        assertEquals(2, result.getNumber());
        assertEquals(ALBUM_ID, result.getAlbum().getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"album", "single"})
    void shouldReturnArtistReleases_whenRequestingAlbumsOrSingles(String group) {
        // Given
        String url = BASE_URL + "/artists/" + ARTIST_ID
            + "/albums?id=" + ARTIST_ID + "&include_groups=" + group;
        expectGet(url, """
            {
              "items": [{"id": "album-1", "album_type": "%s"}],
              "next": null
            }
            """.formatted(group));

        // When
        ItemsPage<SpotifyAlbumDTO> result = group.equals("album")
            ? this.service.getArtistsAlbums(ARTIST_ID)
            : this.service.getArtistsSingles(ARTIST_ID);

        // Then
        assertEquals(1, result.getItems().size());
        assertEquals(ALBUM_ID, result.getItems().get(0).getId());
        assertEquals(group, result.getItems().get(0).getAlbumType());
    }

    @Test
    void shouldReturnTopTracks_whenRequestingAnArtist() {
        // Given
        String url = BASE_URL + "/artists/" + ARTIST_ID
            + "/top-tracks?id=" + ARTIST_ID;
        expectGet(url, """
            {"tracks": [{"id": "track-1", "name": "Test Track"}]}
            """);

        // When
        TracksResponse result = this.service.getArtistsTopTracks(ARTIST_ID);

        // Then
        assertEquals(1, result.getTracks().size());
        assertEquals(TRACK_ID, result.getTracks().get(0).getId());
    }

    @Test
    void shouldReturnTrackPage_whenRequestingAlbumTracks() {
        // Given
        String url = BASE_URL + "/albums/" + ALBUM_ID
            + "/tracks?id=" + ALBUM_ID + "&limit=40";
        expectGet(url, """
            {
              "limit": 40,
              "items": [{"id": "track-1", "track_number": 2}]
            }
            """);

        // When
        ItemsPage<SpotifyTrackDTO> result = this.service.getAlbumsTracks(ALBUM_ID);

        // Then
        assertEquals(40, result.getLimit());
        assertEquals(1, result.getItems().size());
        assertEquals(TRACK_ID, result.getItems().get(0).getId());
        assertEquals(2, result.getItems().get(0).getNumber());
    }

    @Test
    void shouldReturnNextPage_whenGivenThePaginationUrl() {
        // Given
        String next = BASE_URL + "/artists/" + ARTIST_ID
            + "/albums?offset=20&limit=20";
        expectGet(next, """
            {
              "limit": 20,
              "items": [{"id": "album-2", "name": "Second Album"}]
            }
            """);

        // When
        ItemsPage<SpotifyAlbumDTO> result =
            this.service.getNextPage(next, SpotifyAlbumDTO.class);

        // Then
        assertEquals(20, result.getLimit());
        assertEquals(1, result.getItems().size());
        assertEquals("album-2", result.getItems().get(0).getId());
    }

    private void expectGet(String url, String body) {
        server.expect(requestTo(url))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
            .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
    }

    @TestConfiguration
    static class Config {

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        MockRestServiceServer mockRestServiceServer(RestTemplate restTemplate) {
            return MockRestServiceServer.bindTo(restTemplate).build();
        }

        @Bean
        TokenService tokenService() {
            TokenService tokenService = mock(TokenService.class);
            when(tokenService.getTokenType()).thenReturn("Bearer");
            when(tokenService.getAccessToken()).thenReturn("test-token");
            return tokenService;
        }
    }
}

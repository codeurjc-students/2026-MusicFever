package com.musicfever.music_fever.unit.api;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;
import com.musicfever.music_fever.service.api.SpotifyService;
import com.musicfever.music_fever.service.api.TokenService;

@ActiveProfiles("unit")
public class SpotifyServiceTest {

    // Mocks and tools
    SpotifyService service;
    RestTemplate restTemplate;
    TokenService tokenService;

    // Test Objects
    private static final String BASE_URL = "https://api.spotify.com/v1";
    private static final String ARTIST_ID = "test-artist";
    private static final String ALBUM_ID = "test-album";
    private static final String TRACK_ID = "test-track";

    HttpEntity<Void> requestEntity;
    SpotifyArtistDTO artistTest;
    SpotifyAlbumDTO albumTest;
    SpotifyTrackDTO trackTest;

    @BeforeEach
    void setup() {
        // Given
        restTemplate = mock(RestTemplate.class);
        tokenService = mock(TokenService.class);

        when(tokenService.getTokenType()).thenReturn("Bearer");
        when(tokenService.getAccessToken()).thenReturn("test-token");

        service = new SpotifyService(restTemplate, tokenService);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        requestEntity = new HttpEntity<>(headers);

        artistTest = new SpotifyArtistDTO();
        artistTest.setId(ARTIST_ID);

        albumTest = new SpotifyAlbumDTO();
        albumTest.setId(ALBUM_ID);

        trackTest = new SpotifyTrackDTO();
        trackTest.setId(TRACK_ID);
    }

    @Test
    @DisplayName("Search Spotify for artists, albums and tracks")
    void getSearch_givenASearchString() {
        // Given
        String search = "Test Artist";
        String url = BASE_URL
            + "/search?q=Test%20Artist&type=artist,album,track&limit=3";
        SearchResponse searchTest = mock(SearchResponse.class);

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, SearchResponse.class))
            .thenReturn(ResponseEntity.ok(searchTest));

        SearchResponse result = this.service.getSearch(search);

        // Then
        assertSame(searchTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, SearchResponse.class);
    }

    @ParameterizedTest(name = "Look up by href: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Get a Spotify artist by ID or href")
    void getArtist_givenAnIdOrHref(boolean byHref) {
        // Given
        String url = byHref
            ? BASE_URL + "/artists/artist-from-href"
            : BASE_URL + "/artists/" + ARTIST_ID;

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, SpotifyArtistDTO.class))
            .thenReturn(ResponseEntity.ok(artistTest));

        SpotifyArtistDTO result = byHref
            ? this.service.getArtistByHref(url)
            : this.service.getArtistById(ARTIST_ID);

        // Then
        assertSame(artistTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, SpotifyArtistDTO.class);
    }

    @ParameterizedTest(name = "Look up by href: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Get a Spotify album by ID or href")
    void getAlbum_givenAnIdOrHref(boolean byHref) {
        // Given
        String url = byHref
            ? BASE_URL + "/albums/album-from-href"
            : BASE_URL + "/albums/" + ALBUM_ID;

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, SpotifyAlbumDTO.class))
            .thenReturn(ResponseEntity.ok(albumTest));

        SpotifyAlbumDTO result = byHref
            ? this.service.getAlbumByHref(url)
            : this.service.getAlbumById(ALBUM_ID);

        // Then
        assertSame(albumTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, SpotifyAlbumDTO.class);
    }

    @ParameterizedTest(name = "Look up by href: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Get a Spotify track by ID or href")
    void getTrack_givenAnIdOrHref(boolean byHref) {
        // Given
        String url = byHref
            ? BASE_URL + "/tracks/track-from-href"
            : BASE_URL + "/tracks/" + TRACK_ID;

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, SpotifyTrackDTO.class))
            .thenReturn(ResponseEntity.ok(trackTest));

        SpotifyTrackDTO result = byHref
            ? this.service.getTrackByHref(url)
            : this.service.getTrackById(TRACK_ID);

        // Then
        assertSame(trackTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, SpotifyTrackDTO.class);
    }

    @ParameterizedTest(name = "Include group: {0}")
    @ValueSource(strings = {"album", "single"})
    @DisplayName("Get an artist's albums or singles using the requested group")
    void getArtistReleases_givenAnArtistIdAndGroup(String group) {
        // Given
        String url = BASE_URL + "/artists/" + ARTIST_ID
            + "/albums?id=" + ARTIST_ID + "&include_groups=" + group;
        ItemsPage<SpotifyAlbumDTO> pageTest = new ItemsPage<>();
        ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>> responseType =
            new ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>>() {};

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType))
            .thenReturn(ResponseEntity.ok(pageTest));

        ItemsPage<SpotifyAlbumDTO> result = group.equals("album")
            ? this.service.getArtistsAlbums(ARTIST_ID)
            : this.service.getArtistsSingles(ARTIST_ID);

        // Then
        assertSame(pageTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, responseType);
    }

    @Test
    @DisplayName("Get an artist's top tracks")
    void getArtistsTopTracks_givenAnArtistId() {
        // Given
        String url = BASE_URL + "/artists/" + ARTIST_ID + "/top-tracks?id=" + ARTIST_ID;
        TracksResponse tracksTest = new TracksResponse();

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, TracksResponse.class))
            .thenReturn(ResponseEntity.ok(tracksTest));

        TracksResponse result = this.service.getArtistsTopTracks(ARTIST_ID);

        // Then
        assertSame(tracksTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, TracksResponse.class);
    }

    @Test
    @DisplayName("Get an album's tracks with a page limit of 40")
    void getAlbumsTracks_givenAnAlbumId() {
        // Given
        String url = BASE_URL + "/albums/" + ALBUM_ID + "/tracks?id=" + ALBUM_ID + "&limit=40";
        ItemsPage<SpotifyTrackDTO> pageTest = new ItemsPage<>();
        ParameterizedTypeReference<ItemsPage<SpotifyTrackDTO>> responseType =
            new ParameterizedTypeReference<ItemsPage<SpotifyTrackDTO>>() {};

        // When
        when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType))
            .thenReturn(ResponseEntity.ok(pageTest));

        ItemsPage<SpotifyTrackDTO> result = this.service.getAlbumsTracks(ALBUM_ID);

        // Then
        assertSame(pageTest, result);
        verify(restTemplate).exchange(url, HttpMethod.GET, requestEntity, responseType);
    }

    @Test
    @DisplayName("Get the next page using the supplied URL and DTO type")
    void getNextPage_givenANextPageUrlAndDtoClass() {
        // Given
        String next = BASE_URL + "/artists/" + ARTIST_ID + "/albums?offset=20&limit=20";
        ItemsPage<SpotifyAlbumDTO> pageTest = new ItemsPage<>();
        ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>> responseType =
            new ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>>() {};

        // When
        when(restTemplate.exchange(next, HttpMethod.GET, requestEntity, responseType))
            .thenReturn(ResponseEntity.ok(pageTest));

        ItemsPage<SpotifyAlbumDTO> result = this.service.getNextPage(next, SpotifyAlbumDTO.class);

        // Then
        assertSame(pageTest, result);
        verify(restTemplate).exchange(next, HttpMethod.GET, requestEntity, responseType);
    }
}

package com.musicfever.music_fever.integration.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.service.api.iTunesService;

@ActiveProfiles("test")
@SpringBootTest(
    classes = {iTunesService.class, iTunesIntegrationTest.Config.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class iTunesIntegrationTest {

    private static final String ALBUM_NAME = "Test Album";
    private static final String ARTIST_NAME = "Test Artist";
    private static final String SEARCH_URL =
        "https://itunes.apple.com/search?term=Test%20Artist%20Test%20Album"
        + "&media=music&entity=album&attribute=albumTerm&limit=1";

    private final iTunesService service;
    private final MockRestServiceServer server;

    public iTunesIntegrationTest(iTunesService service, MockRestServiceServer server) {
        this.service = service;
        this.server = server;
    }

    @BeforeEach
    void setup() {
        // Given
        server.reset();
    }

    @Test
    void shouldReturnAlbum_whenBothNamesMatchIgnoringCase() {
        // Given
        String body = """
            {
              "results": [
                {
                  "collectionId": 12345,
                  "collectionName": "TEST ALBUM",
                  "artistName": "test artist",
                  "primaryGenreName": "Pop",
                  "collectionExplicitness": "explicit"
                }
              ]
            }
            """;
        expectSearch(withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        iTunesAlbumDTO result = this.service.getAlbum(ALBUM_NAME, ARTIST_NAME);

        // Then
        assertNotNull(result);
        assertEquals("12345", result.getCollectionId());
        assertEquals("test artist", result.getArtistName());
        assertEquals("Pop", result.getPrimaryGenreName());
        assertTrue(result.isExplicit());
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{}",
        "{\"results\":{}}",
        "{\"results\":[]}",
        "{\"results\":[{\"collectionName\":\"Another Album\",\"artistName\":\"Test Artist\"}]}",
        "{\"results\":[{\"collectionName\":\"Test Album\",\"artistName\":\"Another Artist\"}]}"
    })
    void shouldReturnNull_whenNoAlbumMatches(String body) {
        // Given
        expectSearch(withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        iTunesAlbumDTO result = this.service.getAlbum(ALBUM_NAME, ARTIST_NAME);

        // Then
        assertNull(result);
        server.verify();
    }

    @Test
    void shouldReturnNull_whenResponseHasNoBody() {
        // Given
        expectSearch(withNoContent());

        // When
        iTunesAlbumDTO result = this.service.getAlbum(ALBUM_NAME, ARTIST_NAME);

        // Then
        assertNull(result);
        server.verify();
    }

    @Test
    void shouldWrapParsingException_whenResponseContainsMalformedJson() {
        // Given
        expectSearch(withSuccess("{\"results\":", MediaType.APPLICATION_JSON));

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> this.service.getAlbum(ALBUM_NAME, ARTIST_NAME));

        assertEquals("Error parsing iTunes response", exception.getMessage());
        assertInstanceOf(JsonProcessingException.class, exception.getCause());
        server.verify();
    }

    private void expectSearch(ResponseCreator response) {
        server.expect(requestTo(SEARCH_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(response);
    }

    @TestConfiguration
    static class Config {
        private final RestClient.Builder builder = RestClient.builder();
        private final MockRestServiceServer server =
            MockRestServiceServer.bindTo(builder).build();

        @Bean
        RestClient.Builder restClientBuilder() {
            return builder;
        }

        @Bean
        MockRestServiceServer mockRestServiceServer() {
            return server;
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
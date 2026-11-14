package com.musicfever.music_fever.unit.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.service.api.iTunesService;

@ActiveProfiles("unit")
public class iTunesServiceTest {

    // Mocks and tools
    iTunesService service;
    RestClient.Builder builder;
    RestClient restClient;
    RestClient.RequestHeadersUriSpec<?> requestUriSpec;
    RestClient.RequestHeadersSpec<?> requestSpec;
    RestClient.ResponseSpec responseSpec;

    // Test Objects
    private static final String ALBUM_NAME = "Test Album";
    private static final String ARTIST_NAME = "Test Artist";

    @BeforeEach
    void setup() {
        // Given
        builder = mock(RestClient.Builder.class);
        restClient = mock(RestClient.class);
        requestUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(builder.baseUrl("https://itunes.apple.com")).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);
        doReturn(requestUriSpec).when(restClient).get();
        doReturn(requestSpec).when(requestUriSpec)
            .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any());
        when(requestSpec.retrieve()).thenReturn(responseSpec);

        service = new iTunesService(builder, new ObjectMapper());
    }

    @Test
    @DisplayName("Return the album matching both names regardless of case")
    void getAlbum_givenAMatchingAlbum() {
        // Given
        String body = """
            {
              "results": [
                {
                  "collectionName": "Test Album",
                  "artistName": "Another Artist"
                },
                {
                  "collectionId": "12345",
                  "collectionName": "TEST ALBUM",
                  "artistName": "test artist",
                  "primaryGenreName": "Pop",
                  "collectionExplicitness": "explicit"
                }
              ]
            }
            """;

        // When
        when(responseSpec.body(String.class)).thenReturn(body);
        iTunesAlbumDTO result = this.service.getAlbum(ALBUM_NAME, ARTIST_NAME);

        // Then
        assertNotNull(result);
        assertEquals("12345", result.getCollectionId());
        assertEquals("test artist", result.getArtistName());
        assertEquals("Pop", result.getPrimaryGenreName());
        assertTrue(result.isExplicit());
        verify(responseSpec).body(String.class);
    }

    static Stream<Arguments> setSearchResponses() {
        return Stream.of(
            Arguments.of("no response body", (String) null),
            Arguments.of("empty results", """
                {"results": []}
                """),
            Arguments.of("missing results", """
                {}
                """),
            Arguments.of("results is not an array", """
                {"results": {}}
                """),
            Arguments.of("different album name", """
                {"results": [{"collectionName": "Another Album", "artistName": "Test Artist"}]}
                """),
            Arguments.of("different artist name", """
                {"results": [{"collectionName": "Test Album", "artistName": "Another Artist"}]}
                """)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("setSearchResponses")
    @DisplayName("Return null when there is no matching album")
    void getAlbum_givenNoMatchingAlbum(String scenario, String body) {
        // When
        when(responseSpec.body(String.class)).thenReturn(body);
        iTunesAlbumDTO result = this.service.getAlbum(ALBUM_NAME, ARTIST_NAME);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Wrap the parsing exception when the response contains malformed JSON")
    void getAlbum_givenMalformedJson() {
        // When
        when(responseSpec.body(String.class)).thenReturn("{\"results\":");

        // Then
        RuntimeException result = assertThrows(RuntimeException.class,
            () -> this.service.getAlbum(ALBUM_NAME, ARTIST_NAME));

        assertEquals("Error parsing iTunes response", result.getMessage());
        assertInstanceOf(JsonProcessingException.class, result.getCause());
    }
}
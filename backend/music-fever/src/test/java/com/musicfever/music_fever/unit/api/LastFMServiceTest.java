package com.musicfever.music_fever.unit.api;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.service.api.LastFMService;

@ActiveProfiles("unit")
public class LastFMServiceTest {

    // Mocks and tools
    LastFMService service;
    RestClient.Builder builder;
    RestClient restClient;
    RestClient.RequestHeadersUriSpec<?> requestUriSpec;
    RestClient.RequestHeadersSpec<?> requestSpec;
    RestClient.ResponseSpec responseSpec;

    // Test Objects
    private static final String ARTIST_NAME = "Test Artist";
    LastFMArtistDTO artistTest;

    @BeforeEach
    void setup() {
        // Given
        builder = mock(RestClient.Builder.class);
        restClient = mock(RestClient.class);
        requestUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(builder.baseUrl("https://ws.audioscrobbler.com/2.0/")).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        doReturn(requestUriSpec).when(restClient).get();
        doReturn(requestSpec).when(requestUriSpec)
            .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any());
        when(requestSpec.retrieve()).thenReturn(responseSpec);

        service = new LastFMService(builder);
        ReflectionTestUtils.setField(service, "apiKey", "test-api-key");

        artistTest = new LastFMArtistDTO();
        artistTest.setMbid("test-artist-id");
        artistTest.setListeners(1200L);
    }

    @Test
    @DisplayName("Return the artist DTO provided by the HTTP client")
    void getArtist_givenAnArtistName() {
        // When
        when(responseSpec.body(LastFMArtistDTO.class)).thenReturn(artistTest);
        LastFMArtistDTO result = this.service.getArtist(ARTIST_NAME);

        // Then
        assertSame(artistTest, result);
        verify(responseSpec).body(LastFMArtistDTO.class);
    }

    @Test
    @DisplayName("Return null when the HTTP client returns no body")
    void getArtist_givenNoResponseBody() {
        // When
        when(responseSpec.body(LastFMArtistDTO.class)).thenReturn(null);
        LastFMArtistDTO result = this.service.getArtist(ARTIST_NAME);

        // Then
        assertNull(result);
        verify(responseSpec).body(LastFMArtistDTO.class);
    }

    @Test
    @DisplayName("Propagate the exception when the HTTP client fails")
    void getArtist_givenAClientError() {
        // Given
        ResourceAccessException clientError =
            new ResourceAccessException("Last.fm is unavailable");

        // When
        when(requestSpec.retrieve()).thenThrow(clientError);

        // Then
        ResourceAccessException result = assertThrows(ResourceAccessException.class,
            () -> this.service.getArtist(ARTIST_NAME));

        assertSame(clientError, result);
    }
}
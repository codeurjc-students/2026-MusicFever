package com.musicfever.music_fever.integration.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.service.api.LastFMService;

@ActiveProfiles("test")
@SpringBootTest(
    classes = {LastFMService.class, LastFMIntegrationTest.Config.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(properties = "LASTFM_API_KEY=test-api-key")
public class LastFMIntegrationTest {

    private static final String ARTIST_NAME = "Test Artist";
    private static final String ARTIST_URL =
        "https://ws.audioscrobbler.com/2.0/?method=artist.getInfo"
        + "&artist=Test%20Artist&api_key=test-api-key&format=json";

    private final LastFMService service;
    private final MockRestServiceServer server;

    public LastFMIntegrationTest(LastFMService service, MockRestServiceServer server) {
        this.service = service;
        this.server = server;
    }

    @BeforeEach
    void setup() {
        // Given
        server.reset();
    }

    @Test
    void shouldReturnArtist_whenResponseMatchesDto() {
        // Given
        String body = """
            {
              "mbid": "test-artist-id",
              "listeners": 1200
            }
            """;
        expectArtistRequest(withSuccess(body, MediaType.APPLICATION_JSON));

        // When
        LastFMArtistDTO result = this.service.getArtist(ARTIST_NAME);

        // Then
        assertNotNull(result);
        assertEquals("test-artist-id", result.getMbid());
        assertEquals(1200L, result.getListeners());
        server.verify();
    }

    @Test
    void shouldReturnNull_whenResponseHasNoBody() {
        // Given
        expectArtistRequest(withNoContent());

        // When
        LastFMArtistDTO result = this.service.getArtist(ARTIST_NAME);

        // Then
        assertNull(result);
        server.verify();
    }

    @Test
    void shouldPropagateException_whenLastFMReturnsServerError() {
        // Given
        expectArtistRequest(withServerError());

        // When / Then
        assertThrows(HttpServerErrorException.class,
            () -> this.service.getArtist(ARTIST_NAME));
        server.verify();
    }

    private void expectArtistRequest(ResponseCreator response) {
        server.expect(requestTo(ARTIST_URL))
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
    }
}

package com.musicfever.music_fever.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.musicfever.music_fever.service.api.TokenService;

@ActiveProfiles("test")
@SpringBootTest(
    classes = {TokenService.class, TokenIntegrationTest.Config.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spotify.client-id=test-client",
        "spotify.client-secret=test-secret"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TokenIntegrationTest {

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String REDIRECT_URI = "http://127.0.0.1:8080/spotify/callback";

    private final TokenService service;
    private final MockRestServiceServer server;

    public TokenIntegrationTest(TokenService service, MockRestServiceServer server) {
        this.service = service;
        this.server = server;
    }

    @BeforeEach
    void setup() {
        // Given
        server.reset();
        ReflectionTestUtils.setField(service, "accessToken", null);
        ReflectionTestUtils.setField(service, "userAccessToken", null);
    }

    @AfterEach
    void verifyRequests() {
        server.verify();
    }

    @Test
    void shouldRequestAndReuseAppToken_whenNoTokenIsCached() {
        // Given
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        expectTokenRequest(form, """
            {
              "access_token": "app-token",
              "token_type": "Bearer",
              "expires_in": 3600
            }
            """);

        // When
        String accessToken = this.service.getAccessToken();
        String tokenType = this.service.getTokenType();
        String cachedToken = this.service.getAccessToken();

        // Then
        assertEquals("app-token", accessToken);
        assertEquals("Bearer", tokenType);
        assertEquals(accessToken, cachedToken);
    }

    @Test
    void shouldExchangeCodeAndRefreshUserToken_whenItExpires() {
        // Given
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("grant_type", "authorization_code");
        loginForm.add("code", "test-code");
        loginForm.add("redirect_uri", REDIRECT_URI);

        expectTokenRequest(loginForm, """
            {
              "access_token": "user-token",
              "refresh_token": "refresh-token",
              "token_type": "Bearer",
              "expires_in": 3600
            }
            """);

        MultiValueMap<String, String> refreshForm = new LinkedMultiValueMap<>();
        refreshForm.add("grant_type", "refresh_token");
        refreshForm.add("refresh_token", "refresh-token");

        expectTokenRequest(refreshForm, """
            {
              "access_token": "renewed-user-token",
              "token_type": "Bearer",
              "expires_in": 3600
            }
            """);

        // When
        this.service.exchangeCodeForUserTokens("test-code");
        String initialToken = this.service.getUserAccessToken();

        ReflectionTestUtils.setField(service, "userExpiresAt", 0L);
        String renewedToken = this.service.getUserAccessToken();
        String tokenType = this.service.getUserTokenType();

        // Then
        assertEquals("user-token", initialToken);
        assertEquals("renewed-user-token", renewedToken);
        assertEquals("Bearer", tokenType);
    }

    private void expectTokenRequest(MultiValueMap<String, String> form, String response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("test-client", "test-secret");

        server.expect(requestTo(TOKEN_URL))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION)))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(content().formData(form))
            .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
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
    }
}
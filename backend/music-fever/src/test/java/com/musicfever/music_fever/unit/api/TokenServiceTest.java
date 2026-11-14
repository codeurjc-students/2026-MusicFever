package com.musicfever.music_fever.unit.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.musicfever.music_fever.dto.api.spotify.TokenResponse;
import com.musicfever.music_fever.service.api.TokenService;

import static org.mockito.Mockito.mock;

@ActiveProfiles("unit")
public class TokenServiceTest {

    // Mocks and tools
    TokenService service;
    RestTemplate restTemplate;

    // Test Objects
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = "test+client";
    private static final String CLIENT_SECRET = "test-secret";
    private static final String REDIRECT_URI = "http://127.0.0.1:8080/spotify/callback";
    private static final String AUTHORIZATION_CODE = "test-code";
    private static final String REFRESH_TOKEN = "test-refresh-token";

    HttpHeaders headers;
    TokenResponse appTokenTest;
    TokenResponse userTokenTest;

    @BeforeEach
    void setup() {
        // Given
        restTemplate = mock(RestTemplate.class);
        service = new TokenService(restTemplate);
        ReflectionTestUtils.setField(service, "clientId", CLIENT_ID);
        ReflectionTestUtils.setField(service, "clientSecret", CLIENT_SECRET);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        appTokenTest = createToken("app-token", "Bearer", null);
        userTokenTest = createToken("user-token", "Bearer", REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Request an app token and reuse it while it is valid")
    void getAccessToken_givenNoAppToken() {
        // Given
        HttpEntity<MultiValueMap<String, String>> request = appTokenRequest();

        // When
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(appTokenTest));

        String accessToken = this.service.getAccessToken();
        String tokenType = this.service.getTokenType();
        String cachedToken = this.service.getAccessToken();

        // Then
        assertEquals("app-token", accessToken);
        assertEquals("Bearer", tokenType);
        assertEquals(accessToken, cachedToken);
        verify(restTemplate).exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @ParameterizedTest(name = "Read token type first: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Refresh an expired app token from either getter")
    void getAppToken_givenAnExpiredToken(boolean readTypeFirst) {
        // Given
        HttpEntity<MultiValueMap<String, String>> request = appTokenRequest();
        TokenResponse renewedToken = createToken("new-app-token", "NewType", null);

        // When
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(appTokenTest))
            .thenReturn(ResponseEntity.ok(renewedToken));

        this.service.getAccessToken();
        ReflectionTestUtils.setField(service, "expiresAt", 0L);

        String firstResult = readTypeFirst
            ? this.service.getTokenType()
            : this.service.getAccessToken();

        // Then
        assertEquals(readTypeFirst ? "NewType" : "new-app-token", firstResult);
        assertEquals("new-app-token", this.service.getAccessToken());
        assertEquals("NewType", this.service.getTokenType());
        verify(restTemplate, times(2))
            .exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    @DisplayName("Build the Spotify authorization URL with encoded parameters")
    void buildUserAuthorizationUrl_givenAClientId() {
        // Given
        String expected = "https://accounts.spotify.com/authorize?"
            + "response_type=code"
            + "&client_id=test%2Bclient"
            + "&scope=streaming+user-read-playback-state+user-modify-playback-state"
            + "&redirect_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fspotify%2Fcallback";

        // When
        String result = this.service.buildUserAuthorizationUrl();

        // Then
        assertEquals(expected, result);
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Exchange an authorization code and reuse the valid user token")
    void exchangeCodeForUserTokens_givenAnAuthorizationCode() {
        // Given
        HttpEntity<MultiValueMap<String, String>> request = authorizationCodeRequest();

        // When
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(userTokenTest));

        this.service.exchangeCodeForUserTokens(AUTHORIZATION_CODE);

        // Then
        assertEquals("user-token", this.service.getUserAccessToken());
        assertEquals("Bearer", this.service.getUserTokenType());
        verify(restTemplate).exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    @DisplayName("Reject user token requests before Spotify login")
    void getUserTokens_givenNoLogin() {
        // When / Then
        assertThrows(IllegalStateException.class, service::getUserAccessToken);
        assertThrows(IllegalStateException.class, service::getUserTokenType);
        verifyNoInteractions(restTemplate);
    }

    @ParameterizedTest(name = "Read token type first: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Refresh an expired user token from either getter")
    void getUserToken_givenAnExpiredToken(boolean readTypeFirst) {
        // Given
        HttpEntity<MultiValueMap<String, String>> loginRequest = authorizationCodeRequest();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", REFRESH_TOKEN);
        HttpEntity<MultiValueMap<String, String>> refreshRequest = new HttpEntity<>(body, headers);

        TokenResponse renewedToken = createToken("new-user-token", "NewType", null);

        // When
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, loginRequest, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(userTokenTest));
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, refreshRequest, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(renewedToken));

        this.service.exchangeCodeForUserTokens(AUTHORIZATION_CODE);
        ReflectionTestUtils.setField(service, "userExpiresAt", 0L);

        String firstResult = readTypeFirst
            ? this.service.getUserTokenType()
            : this.service.getUserAccessToken();

        // Then
        assertEquals(readTypeFirst ? "NewType" : "new-user-token", firstResult);
        assertEquals("new-user-token", this.service.getUserAccessToken());
        assertEquals("NewType", this.service.getUserTokenType());
        verify(restTemplate).exchange(TOKEN_URL, HttpMethod.POST, loginRequest, TokenResponse.class);
        verify(restTemplate).exchange(TOKEN_URL, HttpMethod.POST, refreshRequest, TokenResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    @DisplayName("Reject renewal when the user has no refresh token")
    void getUserAccessToken_givenNoRefreshToken() {
        // Given
        HttpEntity<MultiValueMap<String, String>> request = authorizationCodeRequest();
        userTokenTest.setRefreshToken(null);

        // When
        when(restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class))
            .thenReturn(ResponseEntity.ok(userTokenTest));

        this.service.exchangeCodeForUserTokens(AUTHORIZATION_CODE);
        ReflectionTestUtils.setField(service, "userExpiresAt", 0L);

        // Then
        IllegalStateException result =
            assertThrows(IllegalStateException.class, service::getUserAccessToken);

        assertEquals("No existe refresh token de usuario", result.getMessage());
        verify(restTemplate).exchange(TOKEN_URL, HttpMethod.POST, request, TokenResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }

    private TokenResponse createToken(String accessToken, String tokenType, String refreshToken) {
        TokenResponse token = new TokenResponse();
        token.setAccessToken(accessToken);
        token.setTokenType(tokenType);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn(3600L);
        return token;
    }

    private HttpEntity<MultiValueMap<String, String>> appTokenRequest() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> authorizationCodeRequest() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", AUTHORIZATION_CODE);
        body.add("redirect_uri", REDIRECT_URI);
        return new HttpEntity<>(body, headers);
    }
}
package com.musicfever.music_fever.service.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;

@Service 
public class SpotifyService {
    private final RestTemplate restTemplate;
    private final TokenService tokenService;

    // URL
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search";
    private static final String ARTIST_URL = "https://api.spotify.com/v1/artists/"; // + id
    private static final String ALBUM_URL = "https://api.spotify.com/v1/albums/"; // + id
    private static final String TRACK_URL = "https://api.spotify.com/v1/tracks/"; // + id

    // CONSTANTS
    private static final int SEARCH_LIMIT = 3;
    private static final int TRACKS_LIMIT = 40;

    public SpotifyService(RestTemplate restTemplate, TokenService tokenService){
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }

    public SearchResponse getSearch(String search){
        // parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(SEARCH_URL)
            .queryParam("q", search)
            .queryParam("type", "artist,album,track")
            .queryParam("limit", SEARCH_LIMIT);

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, SearchResponse.class).getBody();
    }

    public SpotifyArtistDTO getArtistById(String id) {
        // parameters
        String uri = ARTIST_URL + id;
        return getArtistByHref(uri);
    }

    public SpotifyArtistDTO getArtistByHref(String href){
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(href, HttpMethod.GET, entity, SpotifyArtistDTO.class).getBody();
    }

    public SpotifyAlbumDTO getAlbumByHref(String href) {
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(href, HttpMethod.GET, entity, SpotifyAlbumDTO.class).getBody();
    }

    public SpotifyAlbumDTO getAlbumById(String id){
        String uri = ALBUM_URL + id;
        return getAlbumByHref(uri);
    }

    public SpotifyTrackDTO getTrackByHref(String href) {
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(href, HttpMethod.GET, entity, SpotifyTrackDTO.class).getBody();
    }

    public SpotifyTrackDTO getTrackById(String id){
        String completeUrl = TRACK_URL + id;
        return getTrackByHref(completeUrl);
    }

    public ItemsPage<SpotifyAlbumDTO> getArtistsAlbums(String id){
        String uri = ARTIST_URL + id + "/albums";

        // parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            .queryParam("id", id)
            .queryParam("include_groups", "album");

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>> responseType = new ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>>() {};
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, responseType).getBody();
    }

    public ItemsPage<SpotifyAlbumDTO> getArtistsSingles(String id) {
        String uri = ARTIST_URL + id + "/albums";

        // parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            .queryParam("id", id)
            .queryParam("include_groups", "single");

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>> responseType = new ParameterizedTypeReference<ItemsPage<SpotifyAlbumDTO>>() {};
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, responseType).getBody();
    }

    public TracksResponse getArtistsTopTracks(String id) {
        String uri = ARTIST_URL + id + "/top-tracks";

        // parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            .queryParam("id", id);

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, TracksResponse.class).getBody();
    }

    public ItemsPage<SpotifyTrackDTO> getAlbumsTracks(String id) {
        String uri = ALBUM_URL + id + "/tracks";

        // parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            .queryParam("id", id)
            .queryParam("limit", TRACKS_LIMIT);

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<ItemsPage<SpotifyTrackDTO>> responseType = new ParameterizedTypeReference<ItemsPage<SpotifyTrackDTO>>() {};
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, responseType).getBody();
    }

    public <T> ItemsPage<T> getNextPage(String next, Class<T> dtoClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenService.getTokenType() + " " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<ItemsPage<T>> responseType =
                ParameterizedTypeReference.forType(
                        ResolvableType.forClassWithGenerics(ItemsPage.class, dtoClass).getType()
                );

        return restTemplate.exchange(
                next,
                HttpMethod.GET,
                entity,
                responseType
        ).getBody();
    }
}

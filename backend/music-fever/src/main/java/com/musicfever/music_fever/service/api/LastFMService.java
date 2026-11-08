package com.musicfever.music_fever.service.api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;

import org.springframework.beans.factory.annotation.Value;

@Service
public class LastFMService {
    private final RestClient restClient;

    @Value("${LASTFM_API_KEY}")
    private String apiKey;

    public LastFMService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://ws.audioscrobbler.com/2.0/")
                .build();
    }

    public LastFMArtistDTO getArtist(String name) {
        LastFMArtistDTO response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "artist.getInfo")
                        .queryParam("artist", name)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .body(LastFMArtistDTO.class);

        return response;
    }
}
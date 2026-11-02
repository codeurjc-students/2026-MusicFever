package com.musicfever.music_fever.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class iTunesService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final int SEARCH_LIMIT = 1;

    public iTunesService(RestClient.Builder builder, ObjectMapper objectMapper) {
        this.restClient = builder.baseUrl("https://itunes.apple.com").build();
        this.objectMapper = objectMapper;
    }

    public iTunesAlbumDTO getAlbum(String albumName, String artistName) {
        JsonNode response;

        try {
            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("term", artistName + " " + albumName)
                            .queryParam("media", "music")
                            .queryParam("entity", "album")
                            .queryParam("attribute", "albumTerm")
                            .queryParam("limit", SEARCH_LIMIT)
                            .build())
                    .retrieve()
                    .body(String.class);

            if (body == null) {
                return null;
            }

            response = objectMapper.readTree(body);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing iTunes response", e);
        }

        if (response == null || !response.path("results").isArray()) {
            return null;
        }

        for (JsonNode album : response.path("results")) {
            if (album.path("collectionName").asText()
                    .equalsIgnoreCase(albumName)
                    && album.path("artistName").asText()
                    .equalsIgnoreCase(artistName)) {

                return objectMapper.convertValue(album, iTunesAlbumDTO.class);
            }
        }

        return null;
    }
}
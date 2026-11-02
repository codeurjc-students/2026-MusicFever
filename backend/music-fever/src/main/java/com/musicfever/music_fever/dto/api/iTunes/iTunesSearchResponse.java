package com.musicfever.music_fever.dto.api.iTunes;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record iTunesSearchResponse(
    int resultCount,
    List<iTunesAlbumDTO> results
) {}
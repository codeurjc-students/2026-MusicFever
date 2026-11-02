package com.musicfever.music_fever.dto.api.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackDTO {
    private String id;
    private String href;
    private String name;
    private boolean explicit;

    @JsonProperty("duration_ms")
    private int duration;

    @JsonProperty("track_number")
    private int number;
}

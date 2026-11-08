package com.musicfever.music_fever.dto.api.lastFM;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastFMArtistDTO {
    private String mbid;
    private long listeners;
}

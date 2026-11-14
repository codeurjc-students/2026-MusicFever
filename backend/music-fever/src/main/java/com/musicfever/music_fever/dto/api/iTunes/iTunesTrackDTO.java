package com.musicfever.music_fever.dto.api.iTunes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonIgnoreProperties(ignoreUnknown = true)
public class iTunesTrackDTO {
    private String trackId;
    private String trackName;
    private String primaryGenreName;
    private int discNumber;
    private int discCount;
}

package com.musicfever.music_fever.dto.api;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class iTunesTrackDTO {
    private String trackId;
    private String trackName;
    private String primaryGenreName;
    private int discNumber;
    private int discCount;
}

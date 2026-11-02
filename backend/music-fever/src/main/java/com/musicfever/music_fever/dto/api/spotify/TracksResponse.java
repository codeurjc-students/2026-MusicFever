package com.musicfever.music_fever.dto.api.spotify;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter 
public class TracksResponse {
    private List<SpotifyTrackDTO> tracks;

    public TracksResponse(){}
}

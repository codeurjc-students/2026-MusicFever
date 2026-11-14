package com.musicfever.music_fever.dto.api.spotify;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class SearchResponse {
    private ItemsPage<SpotifyArtistDTO> artists;
    private ItemsPage<SpotifyAlbumDTO> albums;
    private ItemsPage<SpotifyTrackDTO> tracks;
}

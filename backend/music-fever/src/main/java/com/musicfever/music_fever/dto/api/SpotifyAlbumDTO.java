package com.musicfever.music_fever.dto.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicfever.music_fever.model.Image;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class SpotifyAlbumDTO {
    private String id;
    private String href;
    private String name;

    @JsonProperty("album_type")
    private String albumType;

    @JsonProperty("total_tracks")
    private int totalTracks;

    private Image smallImage;
    private Image bigImage;

    @JsonProperty("images")
    private void stablishImage(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            this.smallImage = images.getLast();
            this.bigImage = images.getFirst();
        }
    }
}

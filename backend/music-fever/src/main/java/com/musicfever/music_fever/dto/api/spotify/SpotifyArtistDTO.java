package com.musicfever.music_fever.dto.api.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicfever.music_fever.model.Image;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistDTO {
    private String id;
    private String href;
    private String name;
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

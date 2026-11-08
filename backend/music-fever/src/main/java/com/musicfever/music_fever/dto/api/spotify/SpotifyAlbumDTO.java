package com.musicfever.music_fever.dto.api.spotify;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicfever.music_fever.model.Image;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyAlbumDTO {
    private String id;
    private String href;
    private String name;

    @JsonProperty("album_type")
    private String albumType;

    @JsonProperty("total_tracks")
    private int totalTracks;

    private LocalDate releaseDate;

    private List<SpotifyArtistDTO> artists;

    private Image smallImage;
    private Image bigImage;

    @JsonProperty("release_date")
    private void setReleaseDate(String date) {
        if (date == null) return;

        this.releaseDate = switch (date.length()) {
            case 4 -> LocalDate.parse(date + "-01-01");
            case 7 -> LocalDate.parse(date + "-01");
            default -> LocalDate.parse(date);
        };
    }

    @JsonProperty("images")
    private void stablishImage(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            this.smallImage = images.getLast();
            this.bigImage = images.getFirst();
        }
    }
}

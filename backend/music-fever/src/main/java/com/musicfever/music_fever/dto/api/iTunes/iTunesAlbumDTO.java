package com.musicfever.music_fever.dto.api.iTunes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@JsonIgnoreProperties(ignoreUnknown = true)
public class iTunesAlbumDTO {
    private String collectionId;
    private String artistName;
    private String primaryGenreName;
    private LocalDate releaseDateFormat;
    private boolean explicit;

    @JsonProperty("releaseDate")
    private void dateConversion(Instant releaseDate) {
        this.releaseDateFormat = releaseDate.atZone(ZoneOffset.UTC).toLocalDate();
    }

    @JsonProperty("collectionExplicitness")
    private void explicitConversion(String explicitness){
        if (explicitness.equals("explicit")){
            this.explicit = true;
        } else {
            this.explicit = false;
        }
    }
}

package com.musicfever.music_fever.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter 
@Getter 
public class Image {
    private int width;
    private int height;

    @Column(columnDefinition = "text")
    private String url;
}

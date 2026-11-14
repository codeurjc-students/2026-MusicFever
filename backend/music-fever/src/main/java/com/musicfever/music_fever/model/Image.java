package com.musicfever.music_fever.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter 
@Getter 
public class Image {
    private int width;
    private int height;
    private String url;
}

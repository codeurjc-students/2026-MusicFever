package com.musicfever.music_fever.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
public class Image {
    private int width;
    private int height;
    private String url;
}

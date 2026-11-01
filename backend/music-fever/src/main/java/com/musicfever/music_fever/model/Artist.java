package com.musicfever.music_fever.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
public class Artist {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String spotifyId;
    private String spotifyHref;
    private String name;
    private Image smallImage;
    private Image bigImage;

    public Artist(){}
}

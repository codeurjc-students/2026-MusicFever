package com.musicfever.music_fever.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String iTunesId;
    private String name;
    private String genre;
    private LocalDate releaseDate;
    private boolean explicit;
    private String spotifyId;
    private String spotifyHref;
    private String albumType;
    private int totalTracks;
    private Image smallImage;
    private Image bigImage;

    public Album(){}
}

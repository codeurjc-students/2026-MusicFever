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
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String spotifyId;
    private String iTunesId;
    private String spotifyHref;
    private String name;
    private String genre;
    private int number;
    private int duration;
    private boolean explicit;
    private int discNumber;
    private int totalDiscCount;

    public Track(){}
    
}

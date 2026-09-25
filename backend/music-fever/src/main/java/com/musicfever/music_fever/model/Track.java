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

    private String name;
    private String artistName;
    private int number;
    private boolean explicit;

    public Track(String artistName, String name, int number, boolean explicit) {
        this.artistName = artistName;
        this.name = name;
        this.number = number;
        this.explicit = explicit;
    }    
}

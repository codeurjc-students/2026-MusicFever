package com.musicfever.music_fever.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

    private String lastFMId;
    private long listeners;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "url", column = @Column(name = "small_image_url")),
        @AttributeOverride(name = "width", column = @Column(name = "small_image_width")),
        @AttributeOverride(name = "height", column = @Column(name = "small_image_height"))
    })
    private Image smallImage;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "url", column = @Column(name = "big_image_url")),
        @AttributeOverride(name = "width", column = @Column(name = "big_image_width")),
        @AttributeOverride(name = "height", column = @Column(name = "big_image_height"))
    })
    private Image bigImage;

    private boolean loaded = false; // if all albums have been loaded

    @ManyToMany(mappedBy = "artists")
    private List<Album> albums = new ArrayList<>();

    @ManyToMany
    private List<Track> topTracks = new ArrayList<>();

    private Set<String> genres = new HashSet<>();

    public Artist(){}
}

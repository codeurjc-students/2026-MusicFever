package com.musicfever.music_fever.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

    private boolean loaded = false; // if all tracks have been loaded

    @OneToMany(mappedBy = "album")
    private List<Track> tracks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "album_artist",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists = new ArrayList<>();

    public Album(){}
}

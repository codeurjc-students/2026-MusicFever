package com.musicfever.music_fever.service;

import org.springframework.stereotype.Component;

import com.musicfever.music_fever.model.Track;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    private final TrackService trackService;

    // Spring inyecta la dependencia aquí automáticamente
    public DatabaseInitializer(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostConstruct 
    public void initDatabase(){
        this.trackService.saveTrack(new Track("Taylor Swift", "Pacient Zero", 1, false));
        this.trackService.saveTrack(new Track("Tate McRae", "NOBODY'S GIRL", 3, false));
        this.trackService.saveTrack(new Track("Ariana Grande", "petal", 3, false));
    }
}

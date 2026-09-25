package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.service.TrackService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@RequestMapping("api/v1/tracks")
public class TrackController {
    private final TrackService service;

    public TrackController(TrackService service){
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Track>> getTracks() {
        List<Track> listTrack = service.findAll();
        if (listTrack == null){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(listTrack);
        }
    }
}

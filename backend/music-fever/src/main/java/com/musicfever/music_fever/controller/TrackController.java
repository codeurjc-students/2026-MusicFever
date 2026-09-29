package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.service.TrackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@Tag(name = "Tracks", description = "Endpoints for managing track resources")
@RequestMapping("api/v1/tracks")
public class TrackController {
    private final TrackService service;

    public TrackController(TrackService service){
        this.service = service;
    }

    @Operation(summary = "Get all the existing tracks.", 
        description = "Returns all tracks on the database"
    )
    @ApiResponse(responseCode = "200", description = "All tracks fetched successfully.")
    @GetMapping("/")
    public ResponseEntity<List<Track>> getTracks() {
        List<Track> listTrack = service.findAll();
        return ResponseEntity.ok(listTrack);
    }
}

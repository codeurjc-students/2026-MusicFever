package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicfever.music_fever.dto.artist.ArtistDisplayDTO;
import com.musicfever.music_fever.service.ArtistService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@Tag(name = "Tracks", description = "Endpoints for managing artists")
@RequestMapping("api/v1/artists")
public class ArtistController {
    private final ArtistService service;

    public ArtistController(ArtistService service){
        this.service = service;
    }

    @GetMapping("/{id}")
    public ArtistDisplayDTO getSearch(@PathVariable Long id) {
        return this.service.getArtistDTOById(id);
    }
}
package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.artist.ArtistDisplayDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.service.ArtistService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@Tag(name = "Artists", description = "Endpoints for managing artists")
@RequestMapping("/api/v1/artists/{id}")
public class ArtistController {
    private final ArtistService service;

    public ArtistController(ArtistService service){
        this.service = service;
    }

    @GetMapping
    public ArtistDisplayDTO getSearch(@PathVariable Long id) {
        return this.service.getArtistDTOById(id);
    }

    @GetMapping("/albums")
    public Page<AlbumDTO> getArtistAlbums(@PathVariable Long id, Pageable pageable) {
        return this.service.getArtistAlbums(id, pageable);
    }

    @GetMapping("/popular-tracks")
    public Page<TrackDTO> getPopularTracks(@PathVariable Long id, Pageable pageable) {
        return this.service.getArtistPopularTracks(id, pageable);
    }
}
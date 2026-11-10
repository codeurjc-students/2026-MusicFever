package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.musicfever.music_fever.dto.album.AlbumDisplayDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.service.AlbumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController 
@Tag(name = "Albums", description = "Endpoints for managing albums")
@RequestMapping("/api/v1/albums/{id}")
public class AlbumController {
    private final AlbumService service;

    public AlbumController(AlbumService albumService){
        this.service = albumService;
    }

    @GetMapping("")
    public AlbumDisplayDTO getAlbum(@PathVariable Long id) {
        return this.service.getAlbumDisplayDTO(id);
    }

    @GetMapping("/tracks")
    public Page<TrackDTO> getTracklist(@PathVariable Long id, Pageable pageable) {
        return this.service.getAlbumTracklist(id, pageable);
    }
}

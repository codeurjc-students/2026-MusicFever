package com.musicfever.music_fever.dto.album;

import java.time.LocalDate;
import java.util.List;

import com.musicfever.music_fever.dto.artist.ArtistDTO;
import com.musicfever.music_fever.model.Image;

public record AlbumDisplayDTO(Long id, String name, List<ArtistDTO> artists, 
    LocalDate releaseDate, Integer totalTracks, String genre, Image bigImage) {}

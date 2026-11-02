package com.musicfever.music_fever.dto.track;

import java.util.List;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.artist.ArtistDTO;

public record TrackDTO(String name, Integer number, AlbumDTO album, 
    List<ArtistDTO> artists, Integer duration, Boolean explicit
) {}

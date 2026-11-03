package com.musicfever.music_fever.dto.track;

import com.musicfever.music_fever.dto.album.AlbumDTO;

public record TrackDTO(Long id, String name, Integer number, AlbumDTO album, 
    Integer duration, Boolean explicit
) {}

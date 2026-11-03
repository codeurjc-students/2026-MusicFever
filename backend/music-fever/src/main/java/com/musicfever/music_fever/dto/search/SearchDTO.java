package com.musicfever.music_fever.dto.search;

import java.util.List;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.artist.ArtistDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;

public record SearchDTO(
    int size,
    List<ArtistDTO> artists,
    List<AlbumDTO> albums,
    List<TrackDTO> tracks
) {}

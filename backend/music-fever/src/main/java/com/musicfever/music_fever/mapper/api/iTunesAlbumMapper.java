package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.model.Album;

public class iTunesAlbumMapper {
    public static void updateEntity(iTunesAlbumDTO dto, Album album){
        album.setITunesId(dto.getCollectionId());
        album.setGenre(dto.getPrimaryGenreName());
        album.setReleaseDate(dto.getReleaseDateFormat());
        album.setExplicit(dto.isExplicit());
    }
}
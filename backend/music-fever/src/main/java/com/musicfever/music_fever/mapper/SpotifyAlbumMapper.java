package com.musicfever.music_fever.mapper;

import com.musicfever.music_fever.dto.api.SpotifyAlbumDTO;
import com.musicfever.music_fever.model.Album;

public class SpotifyAlbumMapper {
    public static void updateEntity(SpotifyAlbumDTO dto, Album album){
        album.setSpotifyId(dto.getId());
        album.setSpotifyHref(dto.getHref());
        album.setAlbumType(dto.getAlbumType());
        album.setTotalTracks(dto.getTotalTracks());
        album.setSmallImage(dto.getSmallImage());
        album.setBigImage(dto.getBigImage());
    }
}
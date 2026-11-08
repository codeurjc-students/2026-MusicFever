package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.model.Album;

public class SpotifyAlbumMapper {
    public static void updateEntity(SpotifyAlbumDTO dto, Album album){
        album.setSpotifyId(dto.getId());
        album.setSpotifyHref(dto.getHref());
        album.setName(dto.getName());
        album.setReleaseDate(dto.getReleaseDate());
        album.setAlbumType(dto.getAlbumType());
        album.setTotalTracks(dto.getTotalTracks());
        album.setSmallImage(dto.getSmallImage());
        album.setBigImage(dto.getBigImage());
    }
}
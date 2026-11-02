package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.model.Artist;

public class SpotifyArtistMapper {
    public static void updateEntity(SpotifyArtistDTO dto, Artist artist){
        artist.setBigImage(dto.getBigImage());
        artist.setName(dto.getName());
        artist.setSmallImage(dto.getSmallImage());
        artist.setSpotifyHref(dto.getHref());
        artist.setSpotifyId(dto.getId());
    }
}

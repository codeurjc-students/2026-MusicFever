package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.model.Track;

public class SpotifyTrackMapper {
    public static void updateEntity(SpotifyTrackDTO dto, Track track){
        track.setSpotifyId(dto.getId());
        track.setSpotifyHref(dto.getHref());
        track.setName(dto.getName());
        track.setDuration(dto.getDuration());
        track.setNumber(dto.getNumber());;
    }
}
package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.iTunes.iTunesTrackDTO;
import com.musicfever.music_fever.model.Track;

public class iTunesTrackMapper {
    public static void updateEntity(iTunesTrackDTO dto, Track track){
        track.setITunesId(dto.getTrackId());
        track.setGenre(dto.getPrimaryGenreName());
        track.setDiscNumber(dto.getDiscNumber());
        track.setTotalDiscCount(dto.getDiscCount());
    }
}
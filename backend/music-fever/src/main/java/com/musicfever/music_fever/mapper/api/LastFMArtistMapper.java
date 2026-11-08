package com.musicfever.music_fever.mapper.api;

import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.model.Artist;

public class LastFMArtistMapper {
    public static void updateEntity(LastFMArtistDTO dto, Artist artist) {
        artist.setLastFMId(dto.getMbid());
        artist.setListeners(dto.getListeners());
    }
}
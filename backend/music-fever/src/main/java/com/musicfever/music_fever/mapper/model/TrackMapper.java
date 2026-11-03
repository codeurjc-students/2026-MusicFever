package com.musicfever.music_fever.mapper.model;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.model.Track;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    TrackDTO toDTO(Track track);
    
    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "ITunesId", ignore = true)
    @Mapping(target = "discNumber", ignore = true)
    @Mapping(target = "totalDiscCount", ignore = true)
    List<TrackDTO> toDTOs(Collection<Track> tracks);

    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "ITunesId", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "discNumber", ignore = true)
    @Mapping(target = "totalDiscCount", ignore = true)
    @Mapping(target = "album", ignore = true)
    Track toDomain(TrackDTO trackDTO);
}
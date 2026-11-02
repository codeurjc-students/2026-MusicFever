package com.musicfever.music_fever.mapper.model;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.musicfever.music_fever.dto.artist.ArtistDTO;
import com.musicfever.music_fever.model.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper{
    ArtistDTO toDTO(Artist artist);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "bigImage", ignore = true)
    List<ArtistDTO> toDTOs(Collection<Artist> artists);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "bigImage", ignore = true)
    Artist toDomain(ArtistDTO artistDTO);
}
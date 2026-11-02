package com.musicfever.music_fever.mapper.model;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.model.Album;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    AlbumDTO toDTO(Album album);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "ITunesId", ignore = true)
    @Mapping(target = "albumType", ignore = true)
    @Mapping(target = "bigImage", ignore = true)
    @Mapping(target = "loaded", ignore = true)
    List<AlbumDTO> toDTOs(Collection<Album> albums);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spotifyId", ignore = true)
    @Mapping(target = "spotifyHref", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    @Mapping(target = "ITunesId", ignore = true)
    @Mapping(target = "albumType", ignore = true)
    @Mapping(target = "bigImage", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "loaded", ignore = true)
    Album toDomain(AlbumDTO albumDTO);
}

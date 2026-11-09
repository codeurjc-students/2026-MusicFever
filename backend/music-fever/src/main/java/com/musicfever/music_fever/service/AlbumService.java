package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.mapper.model.AlbumMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.repository.AlbumRepository;

@Service 
public class AlbumService {
    private final AlbumRepository repository;
    private final MusicService musicService;
    private final AlbumMapper mapper;

    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper, MusicService musicService){
        this.repository = albumRepository;
        this.musicService = musicService;
        this.mapper = albumMapper;
    }

    public List<Album> searchName(String searchString) {
        return this.repository.findByNameContainingIgnoreCase(searchString);
    }

    public List<Album> loadAlbums(List<SpotifyAlbumDTO> albums) {
        List<Album> newAlbums = new ArrayList<>();
        for(SpotifyAlbumDTO dto: albums){
            newAlbums.add(this.loadAlbum(dto));
        }
        return newAlbums;
    }

    public Album loadAlbum(SpotifyAlbumDTO basicAlbum) {
        Optional<Album> op = this.repository.findBySpotifyId(basicAlbum.getId());
        if (op.isEmpty()){
            return this.musicService.loadAlbum(basicAlbum);
        } else {
            return op.get();
        }
    }

    public Album findAlbumById(Long id) {
        return this.repository.findById(id).get();
    }

    public List<Album> loadAlbumsTracks(List<Album> albums){
        List<Album> loadedAlbums = new ArrayList<>();
        for(Album album: albums){
            loadedAlbums.add(this.loadAlbumTracks(album));
        }
        return loadedAlbums;
    }

    public Album loadAlbumTracks(Album album) {
        if (!album.isLoaded()){
            return this.musicService.loadAlbumTracks(album);
        }
        return this.repository.findById(album.getId()).get();
    }

    public Page<AlbumDTO> getArtistAlbums(Artist artist, Pageable pageable) {
        Page<Album> page = this.repository.findByArtistsContaining(artist, pageable);
        return page.map(mapper::toDTO);
    }
}

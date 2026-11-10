package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.album.AlbumDisplayDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.exception.AlbumNotFoundException;
import com.musicfever.music_fever.mapper.model.AlbumMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.repository.AlbumRepository;

@Service 
public class AlbumService {
    private final AlbumRepository repository;
    private final MusicService musicService;
    private final AlbumMapper mapper;
    private final TrackService trackService;

    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper, MusicService musicService, TrackService trackService){
        this.repository = albumRepository;
        this.musicService = musicService;
        this.mapper = albumMapper;
        this.trackService = trackService;
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

    public List<Album> loadTracklists(List<Album> albums){
        List<Album> loadedAlbums = new ArrayList<>();
        for(Album album: albums){
            loadedAlbums.add(this.loadTracklist(album));
        }
        return loadedAlbums;
    }

    public Album loadTracklist(Album album) {
        if (!album.isLoaded()){
            return this.musicService.loadTracklist(album);
        }
        return this.repository.findById(album.getId()).get();
    }

    public Page<AlbumDTO> getArtistAlbums(Artist artist, Pageable pageable) {
        Page<Album> page = this.repository.findByArtistsContaining(artist, pageable);
        return page.map(mapper::toDTO);
    }

    public AlbumDisplayDTO getAlbumDisplayDTO(Long id) {
        Optional<Album> op = this.repository.findById(id);
        if (op.isEmpty()){
            throw new AlbumNotFoundException("Album not found");
        }

        Album album = op.get();
        if (!album.isLoaded()){
            album = this.musicService.loadTracklist(album);
        }

        return this.mapper.toDisplayDTO(album);
    }

    public Page<TrackDTO> getAlbumTracklist(Long id, Pageable pageable) {
        Optional<Album> album = this.repository.findById(id);
        if (album.isEmpty()){
            throw new AlbumNotFoundException("Album not found");
        }
        return this.trackService.getAlbumTracklist(album.get(), pageable);
    }
}

package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.mapper.api.SpotifyTrackMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.api.ApiService;

@Service 
public class TrackService {
    private final TrackRepository repository;
    private final ApiService apiService;
    private final AlbumService albumService;

    public TrackService(TrackRepository repository, ApiService apiService, AlbumService albumService){
        this.repository = repository;
        this.apiService = apiService;
        this.albumService = albumService;
    }

    public void saveTrack(Track track){
        this.repository.save(track);
    }

    public List<Track> findAll(){
        return this.repository.findAll();
    }

    public List<Track> searchName(String searchString) {
        return this.repository.findByNameContainingIgnoreCase(searchString);
    }

    public List<Track> loadTracks(List<SpotifyTrackDTO> tracks) {
        List<Track> newTracks = new ArrayList<>();
        for (SpotifyTrackDTO dto: tracks){
            newTracks.add(this.loadTrack(dto));
        }
        return newTracks;
    }

    private Track loadTrack(SpotifyTrackDTO dto) {
        Optional<Track> op = this.repository.findBySpotifyId(dto.getId());

        if (op.isEmpty()){
            dto = this.apiService.getSpotifyTrack(dto.getId());
            Track newTrack = new Track();
            SpotifyTrackMapper.updateEntity(dto, newTrack);
            Album album = this.albumService.loadAlbum(dto.getAlbum());
            newTrack.setAlbum(album);
            newTrack = this.repository.save(newTrack);

            this.albumService.updateTrack(album, newTrack);

            return newTrack;
        } else {
            return op.get();
        }
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
            ItemsPage<SpotifyTrackDTO> tracksPage = this.apiService.getAlbumTracks(album.getSpotifyId());
            List<Track> tracks = this.loadTracks(tracksPage.getItems());
            return this.albumService.updateTracks(album, tracks);
        }
        return this.albumService.findAlbumById(album.getId());
    }

}

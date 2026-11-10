package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.mapper.model.TrackMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;

@Service 
public class TrackService {
    private final TrackRepository repository;
    private final MusicService musicService;
    private final TrackMapper mapper;

    public TrackService(TrackRepository repository, TrackMapper trackMapper,MusicService musicService){
        this.repository = repository;
        this.musicService = musicService;
        this.mapper = trackMapper;
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
            return this.musicService.loadTrack(dto);
        } else {
            return op.get();
        }
    }

    public Page<TrackDTO> getArtistPopularTracks(Artist artist, Pageable pageable) {
        Page<Track> page = this.repository.findTopTracksByArtist(artist.getId(), pageable);
        return page.map(mapper::toDTO);
    }

    public Page<TrackDTO> getAlbumTracklist(Album album, Pageable pageable) {
        Page<Track> page = this.repository.findByAlbumIdOrderByDiscNumberAscNumberAsc(album.getId(), pageable);
        return page.map(mapper::toDTO);
    }
}

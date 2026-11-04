package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;

@Service 
public class TrackService {
    private final TrackRepository repository;
    private final MusicService musicService;

    public TrackService(TrackRepository repository, MusicService musicService){
        this.repository = repository;
        this.musicService = musicService;
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
}

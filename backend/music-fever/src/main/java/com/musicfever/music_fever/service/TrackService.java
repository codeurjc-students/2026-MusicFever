package com.musicfever.music_fever.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;

@Service 
public class TrackService {
    private final TrackRepository repository;

    public TrackService(TrackRepository repository){
        this.repository = repository;
    }

    public void saveTrack(Track track){
        this.repository.save(track);
    }

    public List<Track> findAll(){
        return this.repository.findAll();
    }

}

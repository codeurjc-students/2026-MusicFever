package com.musicfever.music_fever.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicfever.music_fever.model.Track;

public interface TrackRepository extends JpaRepository<Track, Long>{
    
}

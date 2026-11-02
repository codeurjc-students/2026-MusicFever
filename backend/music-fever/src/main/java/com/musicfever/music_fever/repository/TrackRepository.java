package com.musicfever.music_fever.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicfever.music_fever.model.Track;

public interface TrackRepository extends JpaRepository<Track, Long>{
    public List<Track> findByNameContainingIgnoreCase(String search);
    public Optional<Track> findBySpotifyId(String id);
}

package com.musicfever.music_fever.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicfever.music_fever.model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long>{
    public List<Artist> findByNameContainingIgnoreCase(String search);
    public Optional<Artist> findBySpotifyId(String id);
}

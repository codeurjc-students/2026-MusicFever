package com.musicfever.music_fever.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicfever.music_fever.model.Album;

public interface AlbumRepository extends JpaRepository<Album, Long>{
    public List<Album> findByNameContainingIgnoreCase(String search);
    public Optional<Album> findBySpotifyId(String id);
}

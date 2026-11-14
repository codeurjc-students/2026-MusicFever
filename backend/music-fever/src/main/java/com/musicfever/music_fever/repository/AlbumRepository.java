package com.musicfever.music_fever.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;

public interface AlbumRepository extends JpaRepository<Album, Long>{
    public List<Album> findByNameContainingIgnoreCase(String search);
    public Optional<Album> findBySpotifyId(String id);
    public Optional<Album> findById(Long id);
    Page<Album> findByArtistsContaining(Artist artist, Pageable pageable);
}

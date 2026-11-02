package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.mapper.api.SpotifyArtistMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.service.api.ApiService;

@Service 
public class ArtistService {
    private final ArtistRepository repository;
    private final ApiService apiService;

    public ArtistService(ArtistRepository artistRepository, ApiService apiService){
        this.repository = artistRepository;
        this.apiService = apiService;
    }

    public List<Artist> searchName(String searchString) {
        return repository.findByNameContainingIgnoreCase(searchString);
    }

    public List<Artist> loadArtists(List<SpotifyArtistDTO> artists) {
        List<Artist> newArtists = new ArrayList<>();
        for(SpotifyArtistDTO dto: artists){
            newArtists.add(this.loadArtist(dto));
        }
        return newArtists;
    }

    public Artist loadArtist(SpotifyArtistDTO basicArtist){
        Optional<Artist> op = this.repository.findBySpotifyId(basicArtist.getId());

        if (op.isEmpty()){
            SpotifyArtistDTO spotifyArtist = this.apiService.getSpotifyArtist(basicArtist.getId());
            Artist newArtist = new Artist();
            SpotifyArtistMapper.updateEntity(spotifyArtist, newArtist);
            return this.repository.save(newArtist);
        } else {
            return op.get();
        }
    }

    public Artist updateAlbum(Artist artist, Album newAlbum) {
        artist.getAlbums().add(newAlbum);
        return this.repository.save(artist);
    }

    public Artist updateAlbums(Artist artist, List<Album> albums) {
        artist.setAlbums(albums);
        artist.setLoaded(true);
        return this.repository.save(artist);
    }

    public Artist getArtistById(Long id) {
        return this.repository.findById(id).get();
    }
}

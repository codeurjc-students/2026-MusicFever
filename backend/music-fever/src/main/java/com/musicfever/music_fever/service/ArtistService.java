package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.artist.ArtistDisplayDTO;
import com.musicfever.music_fever.exception.ArtistNotFoundException;
import com.musicfever.music_fever.mapper.model.ArtistMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.repository.ArtistRepository;

@Service 
public class ArtistService {
    private final ArtistRepository repository;
    private final MusicService musicService;
    private final ArtistMapper mapper;

    public ArtistService(ArtistRepository artistRepository, ArtistMapper mapper, MusicService musicService){
        this.repository = artistRepository;
        this.musicService = musicService;
        this.mapper = mapper;
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
            return this.musicService.loadArtist(basicArtist);
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

    public ArtistDisplayDTO getArtistDTOById(Long id) {
        Optional<Artist> op = this.repository.findById(id);
        if(op.isEmpty()){
            throw new ArtistNotFoundException("Artist does not exist");
        }

        Artist artist = op.get();
        if(!artist.isLoaded()){
            artist = this.musicService.loadArtistAlbums(artist);
            artist = this.musicService.loadArtistTopTracks(artist);
        }
        return mapper.toDisplayDTO(artist);
    }
}

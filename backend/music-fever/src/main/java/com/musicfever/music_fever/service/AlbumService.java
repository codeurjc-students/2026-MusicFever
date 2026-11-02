package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.mapper.api.SpotifyAlbumMapper;
import com.musicfever.music_fever.mapper.api.iTunesAlbumMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.service.api.ApiService;

@Service 
public class AlbumService {
    private final AlbumRepository repository;
    private final ApiService apiService;
    private final ArtistService artistService;

    public AlbumService(AlbumRepository albumRepository, ApiService apiService, ArtistService artistService){
        this.repository = albumRepository;
        this.apiService = apiService;
        this.artistService = artistService;
    }

    public List<Album> searchName(String searchString) {
        return this.repository.findByNameContainingIgnoreCase(searchString);
    }

    public List<Album> loadAlbums(List<SpotifyAlbumDTO> albums) {
        List<Album> newAlbums = new ArrayList<>();
        for(SpotifyAlbumDTO dto: albums){
            newAlbums.add(this.loadAlbum(dto));
        }
        return newAlbums;
    }

    public Album loadAlbum(SpotifyAlbumDTO basicAlbum) {
        Optional<Album> op = this.repository.findBySpotifyId(basicAlbum.getId());
        if (op.isEmpty()){
            SpotifyAlbumDTO spotifyAlbum = this.apiService.getSpotifyAlbum(basicAlbum.getId());
            List<Artist> albumArtists = this.artistService.loadArtists(spotifyAlbum.getArtists());
            iTunesAlbumDTO iTunesAlbum = this.apiService.getITunesAlbum(basicAlbum.getName(), albumArtists.get(0).getName());

            Album newAlbum = new Album();
            SpotifyAlbumMapper.updateEntity(spotifyAlbum, newAlbum);
            if (iTunesAlbum != null) {
                iTunesAlbumMapper.updateEntity(iTunesAlbum, newAlbum);
            }
            newAlbum.setArtists(albumArtists);

            newAlbum = this.repository.save(newAlbum);

            for (Artist artist : albumArtists) {
                this.artistService.updateAlbum(artist, newAlbum);
            }

            return newAlbum;

        } else {
            return op.get();
        }
    }

    public Album updateTracks(Album album, List<Track> tracks) {
        album.setTracks(tracks);
        album.setLoaded(true);
        return this.repository.save(album);
    }

    public Artist loadArtistAlbums(Artist artist){
        if (!artist.isLoaded()){
            ItemsPage<SpotifyAlbumDTO> albumPage = this.apiService.getArtistAlbums(artist.getSpotifyId());
            List<Album> albums = this.loadAlbums(albumPage.getItems());
            return this.artistService.updateAlbums(artist, albums);
        }
        return this.artistService.getArtistById(artist.getId());
    }

    public Album findAlbumById(Long id) {
        return this.repository.findById(id).get();
    }

    public void updateTrack(Album album, Track newTrack) {
        album.getTracks().add(newTrack);
        this.repository.save(album);
    }
}

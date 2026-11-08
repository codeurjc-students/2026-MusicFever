package com.musicfever.music_fever.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;
import com.musicfever.music_fever.mapper.api.LastFMArtistMapper;
import com.musicfever.music_fever.mapper.api.SpotifyAlbumMapper;
import com.musicfever.music_fever.mapper.api.SpotifyArtistMapper;
import com.musicfever.music_fever.mapper.api.SpotifyTrackMapper;
import com.musicfever.music_fever.mapper.api.iTunesAlbumMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.api.ApiService;

@Service 
public class MusicService {
    private final ApiService apiService;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;

    public MusicService(ApiService apiService, ArtistRepository artistRepository, AlbumRepository albumRepository,
            TrackRepository trackRepository) {
        this.apiService = apiService;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
    }

    // LOAD
    public List<Artist> loadArtists(List<SpotifyArtistDTO> artists) {
        List<Artist> newArtists = new ArrayList<>();
        for(SpotifyArtistDTO dto: artists){
            newArtists.add(this.loadArtist(dto));
        }
        return newArtists;
    }

    public Artist loadArtist(SpotifyArtistDTO basicArtist){
        SpotifyArtistDTO spotifyArtist = this.apiService.getSpotifyArtist(basicArtist.getId());
        LastFMArtistDTO lastFMArtist = this.apiService.getLastFMArtist(basicArtist.getName());
        Artist newArtist = new Artist();
        SpotifyArtistMapper.updateEntity(spotifyArtist, newArtist);
        LastFMArtistMapper.updateEntity(lastFMArtist, newArtist);
        return this.artistRepository.save(newArtist);
    }

    public Artist loadArtistAlbums(Artist artist) {
        List<Album> albums = new ArrayList<>();

        ItemsPage<SpotifyAlbumDTO> albumPage = this.apiService.getArtistAlbums(artist.getSpotifyId());

        while (albumPage != null) {
            albums.addAll(this.loadAlbums(albumPage.getItems()));

            if (albumPage.getNext() == null){
                albumPage = null;
            } else {
                albumPage = this.apiService.getNextPage(albumPage.getNext(), SpotifyAlbumDTO.class);
            }
        }
        return this.updateAlbums(artist, albums);
    }

    public List<Album> loadAlbums(List<SpotifyAlbumDTO> albums) {
        List<Album> newAlbums = new ArrayList<>();
        for(SpotifyAlbumDTO dto: albums){
            newAlbums.add(this.loadAlbum(dto));
        }
        return newAlbums;
    }

    public Album loadAlbum(SpotifyAlbumDTO basicAlbum) {
        SpotifyAlbumDTO spotifyAlbum = this.apiService.getSpotifyAlbum(basicAlbum.getId());
        List<Artist> albumArtists = this.loadArtists(spotifyAlbum.getArtists());
        iTunesAlbumDTO iTunesAlbum = this.apiService.getITunesAlbum(basicAlbum.getName(), albumArtists.get(0).getName());

        Album newAlbum = new Album();
        SpotifyAlbumMapper.updateEntity(spotifyAlbum, newAlbum);
        if (iTunesAlbum != null) {
            iTunesAlbumMapper.updateEntity(iTunesAlbum, newAlbum);
        }
        newAlbum.setArtists(albumArtists);

        newAlbum = this.albumRepository.save(newAlbum);

        for (Artist artist : albumArtists) {
            this.updateAlbum(artist, newAlbum);
        }

        return newAlbum;
    }

    public List<Track> loadTracks(List<SpotifyTrackDTO> tracks) {
        List<Track> newTracks = new ArrayList<>();
        for (SpotifyTrackDTO dto: tracks){
            newTracks.add(this.loadTrack(dto));
        }
        return newTracks;
    }

    public Track loadTrack(SpotifyTrackDTO dto) {
        dto = this.apiService.getSpotifyTrack(dto.getId());
        Track newTrack = new Track();
        SpotifyTrackMapper.updateEntity(dto, newTrack);
        Album album = this.loadAlbum(dto.getAlbum());
        newTrack.setAlbum(album);
        newTrack = this.trackRepository.save(newTrack);

        this.updateTrack(album, newTrack);

        return newTrack;
    }

    public List<Album> loadAlbumsTracks(List<Album> albums){
        List<Album> loadedAlbums = new ArrayList<>();
        for(Album album: albums){
            loadedAlbums.add(this.loadAlbumTracks(album));
        }
        return loadedAlbums;
    }

    public Album loadAlbumTracks(Album album) {
        ItemsPage<SpotifyTrackDTO> tracksPage = this.apiService.getAlbumTracks(album.getSpotifyId());
        List<Track> tracks = this.loadTracks(tracksPage.getItems());
        return this.updateTracks(album, tracks);
    }

    public Artist loadArtistTopTracks(Artist artist) {
        TracksResponse response = this.apiService.getArtistTopTracks(artist.getSpotifyId());
        List<Track> topTracks = this.loadTracks(response.getTracks());
        artist.setTopTracks(topTracks);
        return this.artistRepository.save(artist);
    }

    // UPDATE
    public Artist updateAlbum(Artist artist, Album newAlbum) {
        artist.getAlbums().add(newAlbum);
        artist.getGenres().add(newAlbum.getGenre());
        return this.artistRepository.save(artist);
    }

    public Artist updateAlbums(Artist artist, List<Album> albums) {
        artist.setAlbums(albums);
        Set<String> albumGenres = new HashSet<>();
        for (Album album: albums){
            if (album.getGenre() != null){
                albumGenres.add(album.getGenre());
            }
        }
        artist.setGenres(albumGenres);
        artist.setLoaded(true);
        return this.artistRepository.save(artist);
    }

    public Album updateTracks(Album album, List<Track> tracks) {
        album.setTracks(tracks);
        album.setLoaded(true);
        return this.albumRepository.save(album);
    }

    public void updateTrack(Album album, Track newTrack) {
        album.getTracks().add(newTrack);
        this.albumRepository.save(album);
    }
}

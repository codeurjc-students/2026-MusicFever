package com.musicfever.music_fever.service.api;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;

@Service 
public class ApiService {
    private final SpotifyService spotify;
    private final iTunesService iTunes;
    private final LastFMService lastFM;

    public ApiService(SpotifyService spotifyService, iTunesService iTunesService, LastFMService lastFMService){
        this.spotify = spotifyService;
        this.iTunes = iTunesService;
        this.lastFM = lastFMService;
    }

    // Looks in Spotify for the main entities of the search
    public SearchResponse getSearch(String searchString) {
        return this.spotify.getSearch(searchString);
    }

    public SpotifyArtistDTO getSpotifyArtist(String id) {
        return this.spotify.getArtistById(id);
    }

    public SpotifyAlbumDTO getSpotifyAlbum(String id) {
        return this.spotify.getAlbumById(id);
    }

    public iTunesAlbumDTO getITunesAlbum(String albumName, String artistName) {
        return this.iTunes.getAlbum(albumName, artistName);
    }

    public ItemsPage<SpotifyTrackDTO> getAlbumTracks(String id) {
        return this.spotify.getAlbumsTracks(id);
    }

    public ItemsPage<SpotifyAlbumDTO> getArtistAlbums(String id){
        return this.spotify.getArtistsAlbums(id);
    }

    public SpotifyTrackDTO getSpotifyTrack(String id) {
        return this.spotify.getTrackById(id);
    }

    public ItemsPage<SpotifyAlbumDTO> getNextPage(String next, Class<SpotifyAlbumDTO> class1) {
        return this.spotify.getNextPage(next, class1);
    }

    public TracksResponse getArtistTopTracks(String spotifyId) {
        return this.spotify.getArtistsTopTracks(spotifyId);
    }

    public LastFMArtistDTO getLastFMArtist(String name) {
    return this.lastFM.getArtist(name);
}
}

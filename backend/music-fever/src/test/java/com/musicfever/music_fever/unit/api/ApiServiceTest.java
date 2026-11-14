package com.musicfever.music_fever.unit.api;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;
import com.musicfever.music_fever.service.api.ApiService;
import com.musicfever.music_fever.service.api.LastFMService;
import com.musicfever.music_fever.service.api.SpotifyService;
import com.musicfever.music_fever.service.api.iTunesService;

@ActiveProfiles("unit")
public class ApiServiceTest {

    // Mocks and tools
    ApiService service;
    SpotifyService spotify;
    iTunesService iTunes;
    LastFMService lastFM;

    @BeforeEach
    void setup() {
        // Given
        spotify = mock(SpotifyService.class);
        iTunes = mock(iTunesService.class);
        lastFM = mock(LastFMService.class);

        service = new ApiService(spotify, iTunes, lastFM);
    }

    @Test
    @DisplayName("Return the Spotify search response for a search string")
    void getSearch_givenASearchString() {
        // Given
        String search = "Test Artist";
        SearchResponse searchTest = mock(SearchResponse.class);

        // When
        when(spotify.getSearch(search)).thenReturn(searchTest);

        SearchResponse result = this.service.getSearch(search);

        // Then
        assertSame(searchTest, result);
        verify(spotify).getSearch(search);
    }

    @Test
    @DisplayName("Get a Spotify artist by ID")
    void getSpotifyArtist_givenAnArtistId() {
        // Given
        String id = "Spotify Artist 1";
        SpotifyArtistDTO artistTest = new SpotifyArtistDTO();
        artistTest.setId(id);

        // When
        when(spotify.getArtistById(id)).thenReturn(artistTest);

        SpotifyArtistDTO result = this.service.getSpotifyArtist(id);

        // Then
        assertSame(artistTest, result);
        verify(spotify).getArtistById(id);
    }

    @Test
    @DisplayName("Get a Spotify album by ID")
    void getSpotifyAlbum_givenAnAlbumId() {
        // Given
        String id = "Spotify Album 1";
        SpotifyAlbumDTO albumTest = new SpotifyAlbumDTO();
        albumTest.setId(id);

        // When
        when(spotify.getAlbumById(id)).thenReturn(albumTest);

        SpotifyAlbumDTO result = this.service.getSpotifyAlbum(id);

        // Then
        assertSame(albumTest, result);
        verify(spotify).getAlbumById(id);
    }

    @Test
    @DisplayName("Get an iTunes album by album and artist name")
    void getITunesAlbum_givenAlbumAndArtistNames() {
        // Given
        String albumName = "Test Album";
        String artistName = "Test Artist";
        iTunesAlbumDTO albumTest = new iTunesAlbumDTO();

        // When
        when(iTunes.getAlbum(albumName, artistName)).thenReturn(albumTest);

        iTunesAlbumDTO result = this.service.getITunesAlbum(albumName, artistName);

        // Then
        assertSame(albumTest, result);
        verify(iTunes).getAlbum(albumName, artistName);
    }

    @Test
    @DisplayName("Get a page of Spotify tracks for an album")
    void getAlbumTracks_givenAnAlbumId() {
        // Given
        String id = "Spotify Album 1";
        ItemsPage<SpotifyTrackDTO> tracksPage = new ItemsPage<>();

        // When
        when(spotify.getAlbumsTracks(id)).thenReturn(tracksPage);

        ItemsPage<SpotifyTrackDTO> result = this.service.getAlbumTracks(id);

        // Then
        assertSame(tracksPage, result);
        verify(spotify).getAlbumsTracks(id);
    }

    @Test
    @DisplayName("Get a page of Spotify albums for an artist")
    void getArtistAlbums_givenAnArtistId() {
        // Given
        String id = "Spotify Artist 1";
        ItemsPage<SpotifyAlbumDTO> albumsPage = new ItemsPage<>();

        // When
        when(spotify.getArtistsAlbums(id)).thenReturn(albumsPage);

        ItemsPage<SpotifyAlbumDTO> result = this.service.getArtistAlbums(id);

        // Then
        assertSame(albumsPage, result);
        verify(spotify).getArtistsAlbums(id);
    }

    @Test
    @DisplayName("Get a Spotify track by ID")
    void getSpotifyTrack_givenATrackId() {
        // Given
        String id = "Spotify Track 1";
        SpotifyTrackDTO trackTest = new SpotifyTrackDTO();
        trackTest.setId(id);

        // When
        when(spotify.getTrackById(id)).thenReturn(trackTest);

        SpotifyTrackDTO result = this.service.getSpotifyTrack(id);

        // Then
        assertSame(trackTest, result);
        verify(spotify).getTrackById(id);
    }

    @Test
    @DisplayName("Get the next page of Spotify albums")
    void getNextPage_givenANextPageLink() {
        // Given
        String next = "next-album-page";
        ItemsPage<SpotifyAlbumDTO> albumsPage = new ItemsPage<>();

        // When
        when(spotify.getNextPage(next, SpotifyAlbumDTO.class)).thenReturn(albumsPage);

        ItemsPage<SpotifyAlbumDTO> result =
            this.service.getNextPage(next, SpotifyAlbumDTO.class);

        // Then
        assertSame(albumsPage, result);
        verify(spotify).getNextPage(next, SpotifyAlbumDTO.class);
    }

    @Test
    @DisplayName("Get an artist's top tracks from Spotify")
    void getArtistTopTracks_givenAnArtistId() {
        // Given
        String id = "Spotify Artist 1";
        TracksResponse tracksTest = new TracksResponse();

        // When
        when(spotify.getArtistsTopTracks(id)).thenReturn(tracksTest);

        TracksResponse result = this.service.getArtistTopTracks(id);

        // Then
        assertSame(tracksTest, result);
        verify(spotify).getArtistsTopTracks(id);
    }

    @Test
    @DisplayName("Get a Last.fm artist by name")
    void getLastFMArtist_givenAnArtistName() {
        // Given
        String name = "Test Artist";
        LastFMArtistDTO artistTest = new LastFMArtistDTO();

        // When
        when(lastFM.getArtist(name)).thenReturn(artistTest);

        LastFMArtistDTO result = this.service.getLastFMArtist(name);

        // Then
        assertSame(artistTest, result);
        verify(lastFM).getArtist(name);
    }
}

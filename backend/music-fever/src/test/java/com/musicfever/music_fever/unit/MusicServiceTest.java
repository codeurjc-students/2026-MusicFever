package com.musicfever.music_fever.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.dto.api.iTunes.iTunesAlbumDTO;
import com.musicfever.music_fever.dto.api.lastFM.LastFMArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.api.spotify.TracksResponse;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.MusicService;
import com.musicfever.music_fever.service.api.ApiService;

@ActiveProfiles("unit")
public class MusicServiceTest {

    // Mocks and tools
    MusicService service;
    ApiService apiService;
    ArtistRepository artistRepository;
    AlbumRepository albumRepository;
    TrackRepository trackRepository;

    // Test Objects
    Artist artistTest;
    Album albumTest;
    Track trackTest;
    SpotifyArtistDTO spotifyArtistTest;
    SpotifyAlbumDTO spotifyAlbumTest;
    SpotifyTrackDTO spotifyTrackTest;
    LastFMArtistDTO lastFMArtistTest;
    iTunesAlbumDTO iTunesAlbumTest;

    @BeforeEach
    void setup() {
        // Given
        apiService = mock(ApiService.class);
        artistRepository = mock(ArtistRepository.class);
        albumRepository = mock(AlbumRepository.class);
        trackRepository = mock(TrackRepository.class);

        service = new MusicService(
            apiService, artistRepository, albumRepository, trackRepository
        );

        artistTest = new Artist();
        artistTest.setId(1L);
        artistTest.setSpotifyId("Spotify Artist 1");
        artistTest.setName("Test Artist");

        albumTest = new Album();
        albumTest.setId(1L);
        albumTest.setSpotifyId("Spotify Album 1");
        albumTest.setName("Test Album");
        albumTest.setGenre("Pop");

        trackTest = new Track();
        trackTest.setId(1L);
        trackTest.setSpotifyId("Spotify Track 1");
        trackTest.setName("Test Track");
        trackTest.setAlbum(albumTest);

        spotifyArtistTest = new SpotifyArtistDTO();
        spotifyArtistTest.setId(artistTest.getSpotifyId());
        spotifyArtistTest.setName(artistTest.getName());

        spotifyAlbumTest = new SpotifyAlbumDTO();
        spotifyAlbumTest.setId(albumTest.getSpotifyId());
        spotifyAlbumTest.setName(albumTest.getName());
        spotifyAlbumTest.setArtists(List.of(spotifyArtistTest));

        spotifyTrackTest = new SpotifyTrackDTO();
        spotifyTrackTest.setId(trackTest.getSpotifyId());
        spotifyTrackTest.setName(trackTest.getName());
        spotifyTrackTest.setDuration(180_000);
        spotifyTrackTest.setNumber(2);
        spotifyTrackTest.setAlbum(spotifyAlbumTest);

        lastFMArtistTest = new LastFMArtistDTO();
        lastFMArtistTest.setMbid("LastFM Artist 1");
        lastFMArtistTest.setListeners(1200L);

        iTunesAlbumTest = new iTunesAlbumDTO();
        iTunesAlbumTest.setCollectionId("iTunes Album 1");
        iTunesAlbumTest.setPrimaryGenreName(albumTest.getGenre());
        iTunesAlbumTest.setExplicit(true);
    }

    @Test
    @DisplayName("Reuse stored artists and load missing artists in input order")
    void loadArtists_givenStoredAndMissingArtists() {
        // Given
        SpotifyArtistDTO missingArtist = new SpotifyArtistDTO();
        missingArtist.setId("Spotify Artist 2");
        missingArtist.setName("Another Test Artist");

        // When
        when(artistRepository.findBySpotifyId(spotifyArtistTest.getId()))
            .thenReturn(Optional.of(artistTest));
        when(artistRepository.findBySpotifyId(missingArtist.getId()))
            .thenReturn(Optional.empty());
        when(apiService.getSpotifyArtist(missingArtist.getId())).thenReturn(missingArtist);
        when(apiService.getLastFMArtist(missingArtist.getName())).thenReturn(lastFMArtistTest);
        when(artistRepository.save(any(Artist.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<Artist> result = this.service.loadArtists(List.of(spotifyArtistTest, missingArtist));

        // Then
        assertEquals(2, result.size());
        assertSame(artistTest, result.get(0));
        assertEquals(missingArtist.getId(), result.get(1).getSpotifyId());
        verify(apiService, never()).getSpotifyArtist(spotifyArtistTest.getId());
        verify(artistRepository).save(result.get(1));
    }

    @Test
    @DisplayName("Load an artist using Spotify and Last.fm data")
    void loadArtist_givenABasicArtist() {
        // When
        when(apiService.getSpotifyArtist(spotifyArtistTest.getId())).thenReturn(spotifyArtistTest);
        when(apiService.getLastFMArtist(spotifyArtistTest.getName())).thenReturn(lastFMArtistTest);
        when(artistRepository.save(any(Artist.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Artist result = this.service.loadArtist(spotifyArtistTest);

        // Then
        assertEquals(spotifyArtistTest.getId(), result.getSpotifyId());
        assertEquals(spotifyArtistTest.getName(), result.getName());
        assertEquals(lastFMArtistTest.getMbid(), result.getLastFMId());
        assertEquals(lastFMArtistTest.getListeners(), result.getListeners());
        verify(artistRepository).save(result);
    }

    @Test
    @DisplayName("Load all pages of an artist's albums")
    void loadArtistAlbums_givenTwoPages() {
        // Given
        Album secondAlbum = new Album();
        secondAlbum.setSpotifyId("Spotify Album 2");
        secondAlbum.setGenre("Rock");

        SpotifyAlbumDTO secondAlbumDTO = new SpotifyAlbumDTO();
        secondAlbumDTO.setId(secondAlbum.getSpotifyId());

        ItemsPage<SpotifyAlbumDTO> firstPage = new ItemsPage<>();
        firstPage.setItems(List.of(spotifyAlbumTest));
        firstPage.setNext("next-album-page");

        ItemsPage<SpotifyAlbumDTO> secondPage = new ItemsPage<>();
        secondPage.setItems(List.of(secondAlbumDTO));

        // When
        when(apiService.getArtistAlbums(artistTest.getSpotifyId())).thenReturn(firstPage);
        when(apiService.getNextPage(firstPage.getNext(), SpotifyAlbumDTO.class))
            .thenReturn(secondPage);
        when(albumRepository.findBySpotifyId(spotifyAlbumTest.getId()))
            .thenReturn(Optional.of(albumTest));
        when(albumRepository.findBySpotifyId(secondAlbumDTO.getId()))
            .thenReturn(Optional.of(secondAlbum));
        when(artistRepository.save(artistTest)).thenReturn(artistTest);

        Artist result = this.service.loadArtistAlbums(artistTest);

        // Then
        assertEquals(List.of(albumTest, secondAlbum), result.getAlbums());
        assertEquals(Set.of("Pop", "Rock"), result.getGenres());
        assertTrue(result.isLoaded());
        verify(apiService).getNextPage(firstPage.getNext(), SpotifyAlbumDTO.class);
        verify(artistRepository).save(artistTest);
    }

    @Test
    @DisplayName("Load albums in input order")
    void loadAlbums_givenSpotifyAlbums() {
        // Given
        Album secondAlbum = new Album();
        secondAlbum.setSpotifyId("Spotify Album 2");

        SpotifyAlbumDTO secondAlbumDTO = new SpotifyAlbumDTO();
        secondAlbumDTO.setId(secondAlbum.getSpotifyId());

        // When
        when(albumRepository.findBySpotifyId(spotifyAlbumTest.getId()))
            .thenReturn(Optional.of(albumTest));
        when(albumRepository.findBySpotifyId(secondAlbumDTO.getId()))
            .thenReturn(Optional.of(secondAlbum));

        List<Album> result = this.service.loadAlbums(List.of(spotifyAlbumTest, secondAlbumDTO));

        // Then
        assertEquals(List.of(albumTest, secondAlbum), result);
        verifyNoInteractions(apiService);
    }

    @Test
    @DisplayName("Return an album already stored in the repository")
    void loadAlbum_givenAStoredAlbum() {
        // When
        when(albumRepository.findBySpotifyId(spotifyAlbumTest.getId()))
            .thenReturn(Optional.of(albumTest));

        Album result = this.service.loadAlbum(spotifyAlbumTest);

        // Then
        assertSame(albumTest, result);
        verifyNoInteractions(apiService, artistRepository);
        verify(albumRepository, never()).save(any(Album.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Load a missing album with or without iTunes data")
    void loadAlbum_givenAMissingAlbum(boolean hasITunesData) {
        // When
        when(albumRepository.findBySpotifyId(spotifyAlbumTest.getId()))
            .thenReturn(Optional.empty());
        when(apiService.getSpotifyAlbum(spotifyAlbumTest.getId())).thenReturn(spotifyAlbumTest);
        when(artistRepository.findBySpotifyId(spotifyArtistTest.getId()))
            .thenReturn(Optional.of(artistTest));
        when(apiService.getITunesAlbum(spotifyAlbumTest.getName(), artistTest.getName()))
            .thenReturn(hasITunesData ? iTunesAlbumTest : null);
        when(albumRepository.save(any(Album.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Album result = this.service.loadAlbum(spotifyAlbumTest);

        // Then
        assertEquals(spotifyAlbumTest.getId(), result.getSpotifyId());
        assertEquals(spotifyAlbumTest.getName(), result.getName());
        assertEquals(List.of(artistTest), result.getArtists());
        assertEquals(List.of(result), artistTest.getAlbums());
        assertEquals(hasITunesData ? iTunesAlbumTest.getCollectionId() : null, result.getITunesId());
        assertEquals(hasITunesData ? iTunesAlbumTest.getPrimaryGenreName() : null, result.getGenre());
        assertEquals(hasITunesData, result.isExplicit());
        verify(albumRepository).save(result);
        verify(artistRepository).save(artistTest);
    }

    @Test
    @DisplayName("Load tracks in input order")
    void loadTracks_givenSpotifyTracks() {
        // Given
        Track secondTrack = new Track();
        secondTrack.setSpotifyId("Spotify Track 2");

        SpotifyTrackDTO secondTrackDTO = new SpotifyTrackDTO();
        secondTrackDTO.setId(secondTrack.getSpotifyId());

        // When
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.of(trackTest));
        when(trackRepository.findBySpotifyId(secondTrackDTO.getId()))
            .thenReturn(Optional.of(secondTrack));

        List<Track> result = this.service.loadTracks(List.of(spotifyTrackTest, secondTrackDTO));

        // Then
        assertEquals(List.of(trackTest, secondTrack), result);
        verifyNoInteractions(apiService);
    }

    @Test
    @DisplayName("Return a track already stored in the repository")
    void loadTrack_givenAStoredTrack() {
        // When
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.of(trackTest));

        Track result = this.service.loadTrack(spotifyTrackTest);

        // Then
        assertSame(trackTest, result);
        verifyNoInteractions(apiService, albumRepository);
        verify(trackRepository, never()).save(any(Track.class));
    }

    @Test
    @DisplayName("Load a missing track and add it to its album")
    void loadTrack_givenAMissingTrack() {
        // When
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.empty());
        when(apiService.getSpotifyTrack(spotifyTrackTest.getId())).thenReturn(spotifyTrackTest);
        when(albumRepository.findBySpotifyId(spotifyAlbumTest.getId()))
            .thenReturn(Optional.of(albumTest));
        when(trackRepository.save(any(Track.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Track result = this.service.loadTrack(spotifyTrackTest);

        // Then
        assertEquals(spotifyTrackTest.getId(), result.getSpotifyId());
        assertEquals(spotifyTrackTest.getName(), result.getName());
        assertEquals(spotifyTrackTest.getDuration(), result.getDuration());
        assertEquals(spotifyTrackTest.getNumber(), result.getNumber());
        assertSame(albumTest, result.getAlbum());
        assertEquals(List.of(result), albumTest.getTracks());
        verify(trackRepository).save(result);
        verify(albumRepository).save(albumTest);
    }

    @Test
    @DisplayName("Load tracklists for multiple albums in input order")
    void loadTrackslists_givenTwoAlbums() {
        // Given
        Album secondAlbum = new Album();
        secondAlbum.setSpotifyId("Spotify Album 2");

        ItemsPage<SpotifyTrackDTO> firstPage = new ItemsPage<>();
        firstPage.setItems(List.of(spotifyTrackTest));

        ItemsPage<SpotifyTrackDTO> secondPage = new ItemsPage<>();
        secondPage.setItems(List.of());

        // When
        when(apiService.getAlbumTracks(albumTest.getSpotifyId())).thenReturn(firstPage);
        when(apiService.getAlbumTracks(secondAlbum.getSpotifyId())).thenReturn(secondPage);
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.of(trackTest));
        when(albumRepository.save(any(Album.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<Album> result = this.service.loadTrackslists(List.of(albumTest, secondAlbum));

        // Then
        assertEquals(List.of(albumTest, secondAlbum), result);
        assertEquals(List.of(trackTest), albumTest.getTracks());
        assertTrue(secondAlbum.getTracks().isEmpty());
        assertTrue(albumTest.isLoaded());
        assertTrue(secondAlbum.isLoaded());
    }

    @Test
    @DisplayName("Load and save an album's tracklist")
    void loadTracklist_givenAnAlbum() {
        // Given
        ItemsPage<SpotifyTrackDTO> tracksPage = new ItemsPage<>();
        tracksPage.setItems(List.of(spotifyTrackTest));

        // When
        when(apiService.getAlbumTracks(albumTest.getSpotifyId())).thenReturn(tracksPage);
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.of(trackTest));
        when(albumRepository.save(albumTest)).thenReturn(albumTest);

        Album result = this.service.loadTracklist(albumTest);

        // Then
        assertEquals(List.of(trackTest), result.getTracks());
        assertTrue(result.isLoaded());
        verify(albumRepository).save(albumTest);
    }

    @Test
    @DisplayName("Load and save an artist's top tracks")
    void loadArtistTopTracks_givenAnArtist() {
        // Given
        TracksResponse tracksResponse = new TracksResponse();
        tracksResponse.setTracks(List.of(spotifyTrackTest));

        // When
        when(apiService.getArtistTopTracks(artistTest.getSpotifyId())).thenReturn(tracksResponse);
        when(trackRepository.findBySpotifyId(spotifyTrackTest.getId()))
            .thenReturn(Optional.of(trackTest));
        when(artistRepository.save(artistTest)).thenReturn(artistTest);

        Artist result = this.service.loadArtistTopTracks(artistTest);

        // Then
        assertEquals(List.of(trackTest), result.getTopTracks());
        verify(artistRepository).save(artistTest);
    }

    @Test
    @DisplayName("Add an album and its genre to an artist")
    void updateAlbum_givenANewAlbum() {
        // Given
        Album previousAlbum = new Album();
        previousAlbum.setGenre("Rock");
        artistTest.getAlbums().add(previousAlbum);
        artistTest.getGenres().add(previousAlbum.getGenre());

        // When
        when(artistRepository.save(artistTest)).thenReturn(artistTest);
        Artist result = this.service.updateAlbum(artistTest, albumTest);

        // Then
        assertEquals(List.of(previousAlbum, albumTest), result.getAlbums());
        assertEquals(Set.of("Rock", "Pop"), result.getGenres());
        verify(artistRepository).save(artistTest);
    }

    @Test
    @DisplayName("Replace an artist's albums and keep only distinct non-null genres")
    void updateAlbums_givenAlbumsWithRepeatedAndMissingGenres() {
        // Given
        artistTest.getAlbums().add(new Album());
        artistTest.getGenres().add("Rock");

        Album sameGenreAlbum = new Album();
        sameGenreAlbum.setGenre(albumTest.getGenre());

        Album noGenreAlbum = new Album();
        List<Album> albums = List.of(albumTest, sameGenreAlbum, noGenreAlbum);

        // When
        when(artistRepository.save(artistTest)).thenReturn(artistTest);
        Artist result = this.service.updateAlbums(artistTest, albums);

        // Then
        assertEquals(albums, result.getAlbums());
        assertEquals(Set.of("Pop"), result.getGenres());
        assertTrue(result.isLoaded());
        verify(artistRepository).save(artistTest);
    }

    @Test
    @DisplayName("Replace an album's tracklist and mark it as loaded")
    void updateTracks_givenANewTracklist() {
        // Given
        albumTest.getTracks().add(new Track());

        // When
        when(albumRepository.save(albumTest)).thenReturn(albumTest);
        Album result = this.service.updateTracks(albumTest, List.of(trackTest));

        // Then
        assertEquals(List.of(trackTest), result.getTracks());
        assertTrue(result.isLoaded());
        verify(albumRepository).save(albumTest);
    }

    @Test
    @DisplayName("Append a track without replacing the existing tracklist")
    void updateTrack_givenANewTrack() {
        // Given
        Track previousTrack = new Track();
        albumTest.getTracks().add(previousTrack);

        // When
        this.service.updateTrack(albumTest, trackTest);

        // Then
        assertEquals(List.of(previousTrack, trackTest), albumTest.getTracks());
        verify(albumRepository).save(albumTest);
    }
}
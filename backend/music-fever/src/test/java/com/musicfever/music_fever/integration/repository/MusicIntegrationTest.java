package com.musicfever.music_fever.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.musicfever.music_fever.config.PostgresTestBase;
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

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class MusicIntegrationTest extends PostgresTestBase {

    private static final String ARTIST_ID = "artist-1";
    private static final String ALBUM_ID = "album-1";
    private static final String TRACK_ID = "track-1";

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final MusicService service;

    @MockitoBean
    private ApiService apiService;

    @PersistenceContext
    private EntityManager entityManager;

    private SpotifyArtistDTO spotifyArtistTest;
    private SpotifyAlbumDTO spotifyAlbumTest;
    private SpotifyTrackDTO spotifyTrackTest;

    public MusicIntegrationTest(
            ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            TrackRepository trackRepository,
            MusicService service) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.service = service;
    }

    @BeforeEach
    void setup() {
        // Given
        spotifyArtistTest = new SpotifyArtistDTO();
        spotifyArtistTest.setId(ARTIST_ID);
        spotifyArtistTest.setName("Test Artist");

        spotifyAlbumTest = new SpotifyAlbumDTO();
        spotifyAlbumTest.setId(ALBUM_ID);
        spotifyAlbumTest.setName("Test Album");
        spotifyAlbumTest.setArtists(List.of(spotifyArtistTest));

        spotifyTrackTest = new SpotifyTrackDTO();
        spotifyTrackTest.setId(TRACK_ID);
        spotifyTrackTest.setName("Opening Track");
        spotifyTrackTest.setNumber(1);
        spotifyTrackTest.setDuration(180_000);
        spotifyTrackTest.setAlbum(spotifyAlbumTest);
    }

    @Test
    void shouldPersistArtistWithSpotifyAndLastFMData_whenArtistIsMissing() {
        // Given
        LastFMArtistDTO lastFMArtist = new LastFMArtistDTO();
        lastFMArtist.setMbid("lastfm-artist-1");
        lastFMArtist.setListeners(1200L);

        when(apiService.getSpotifyArtist(ARTIST_ID)).thenReturn(spotifyArtistTest);
        when(apiService.getLastFMArtist(spotifyArtistTest.getName())).thenReturn(lastFMArtist);

        // When
        List<Artist> result = this.service.loadArtists(List.of(spotifyArtistTest));
        flushAndClear();

        // Then
        Artist stored = artistRepository.findBySpotifyId(ARTIST_ID).orElseThrow();

        assertEquals(1, result.size());
        assertEquals(stored.getId(), result.get(0).getId());
        assertEquals("Test Artist", stored.getName());
        assertEquals("lastfm-artist-1", stored.getLastFMId());
        assertEquals(1200L, stored.getListeners());
    }

    @Test
    void shouldReuseStoredEntities_withoutCallingTheApi() {
        // Given
        Artist artist = saveArtist();
        Album album = saveAlbum();

        Track track = new Track();
        track.setSpotifyId(TRACK_ID);
        track.setName(spotifyTrackTest.getName());
        track.setAlbum(album);
        Track storedTrack = trackRepository.save(track);
        flushAndClear();

        // When
        List<Artist> artists = this.service.loadArtists(List.of(spotifyArtistTest));
        Album resultAlbum = this.service.loadAlbum(spotifyAlbumTest);
        Track resultTrack = this.service.loadTrack(spotifyTrackTest);

        // Then
        assertEquals(List.of(artist.getId()), artists.stream().map(Artist::getId).toList());
        assertEquals(album.getId(), resultAlbum.getId());
        assertEquals(storedTrack.getId(), resultTrack.getId());
        verifyNoInteractions(apiService);
    }

    @Test
    void shouldPersistAlbumAndAssociateItWithItsArtist() {
        // Given
        Artist artist = saveArtist();
        flushAndClear();

        iTunesAlbumDTO iTunesAlbum = new iTunesAlbumDTO();
        iTunesAlbum.setCollectionId("itunes-album-1");
        iTunesAlbum.setPrimaryGenreName("Pop");

        when(apiService.getSpotifyAlbum(ALBUM_ID)).thenReturn(spotifyAlbumTest);
        when(apiService.getITunesAlbum("Test Album", "Test Artist")).thenReturn(iTunesAlbum);

        // When
        Album result = this.service.loadAlbum(spotifyAlbumTest);
        flushAndClear();

        // Then
        Album storedAlbum = albumRepository.findBySpotifyId(ALBUM_ID).orElseThrow();
        Artist storedArtist = artistRepository.findById(artist.getId()).orElseThrow();

        assertEquals(storedAlbum.getId(), result.getId());
        assertEquals("Test Album", storedAlbum.getName());
        assertEquals("itunes-album-1", storedAlbum.getITunesId());
        assertEquals("Pop", storedAlbum.getGenre());
        assertEquals(
            List.of(artist.getId()),
            storedAlbum.getArtists().stream().map(Artist::getId).toList()
        );
        assertEquals(
            List.of(storedAlbum.getId()),
            storedArtist.getAlbums().stream().map(Album::getId).toList()
        );
        assertEquals(Set.of("Pop"), storedArtist.getGenres());
    }

    @Test
    void shouldPersistArtistAlbumsFromAllPages_andMarkArtistAsLoaded() {
        // Given
        Artist artist = saveArtist();
        Album firstAlbum = saveAlbum();
        firstAlbum.setGenre("Pop");

        Album secondAlbum = new Album();
        secondAlbum.setSpotifyId("album-2");
        secondAlbum.setName("Second Album");
        secondAlbum.setGenre("Rock");
        secondAlbum = albumRepository.save(secondAlbum);

        firstAlbum.getArtists().add(artist);
        secondAlbum.getArtists().add(artist);

        SpotifyAlbumDTO secondAlbumDTO = new SpotifyAlbumDTO();
        secondAlbumDTO.setId("album-2");

        String next = "next-album-page";
        ItemsPage<SpotifyAlbumDTO> firstPage = new ItemsPage<>();
        firstPage.setItems(List.of(spotifyAlbumTest));
        firstPage.setNext(next);

        ItemsPage<SpotifyAlbumDTO> secondPage = new ItemsPage<>();
        secondPage.setItems(List.of(secondAlbumDTO));

        when(apiService.getArtistAlbums(ARTIST_ID)).thenReturn(firstPage);
        when(apiService.getNextPage(next, SpotifyAlbumDTO.class)).thenReturn(secondPage);

        // When
        this.service.loadArtistAlbums(artist);
        flushAndClear();

        // Then
        Artist stored = artistRepository.findById(artist.getId()).orElseThrow();

        assertTrue(stored.isLoaded());
        assertEquals(Set.of("Pop", "Rock"), stored.getGenres());
        assertEquals(
            Set.of(firstAlbum.getId(), secondAlbum.getId()),
            Set.copyOf(stored.getAlbums().stream().map(Album::getId).toList())
        );
        verify(apiService).getNextPage(next, SpotifyAlbumDTO.class);
    }

    @Test
    void shouldPersistAlbumTracklist_andMarkAlbumAsLoaded() {
        // Given
        Album album = saveAlbum();
        ItemsPage<SpotifyTrackDTO> tracksPage = new ItemsPage<>();
        tracksPage.setItems(List.of(spotifyTrackTest));

        when(apiService.getAlbumTracks(ALBUM_ID)).thenReturn(tracksPage);
        when(apiService.getSpotifyTrack(TRACK_ID)).thenReturn(spotifyTrackTest);

        // When
        this.service.loadTracklist(album);
        flushAndClear();

        // Then
        Album storedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        Track storedTrack = trackRepository.findBySpotifyId(TRACK_ID).orElseThrow();

        assertTrue(storedAlbum.isLoaded());
        assertEquals("Opening Track", storedTrack.getName());
        assertEquals(180_000, storedTrack.getDuration());
        assertEquals(storedAlbum.getId(), storedTrack.getAlbum().getId());
        assertEquals(
            List.of(storedTrack.getId()),
            storedAlbum.getTracks().stream().map(Track::getId).toList()
        );
    }

    @Test
    void shouldPersistArtistTopTracks() {
        // Given
        Artist artist = saveArtist();
        saveAlbum();

        TracksResponse response = new TracksResponse();
        response.setTracks(List.of(spotifyTrackTest));

        when(apiService.getArtistTopTracks(ARTIST_ID)).thenReturn(response);
        when(apiService.getSpotifyTrack(TRACK_ID)).thenReturn(spotifyTrackTest);

        // When
        this.service.loadArtistTopTracks(artist);
        flushAndClear();

        // Then
        Artist storedArtist = artistRepository.findById(artist.getId()).orElseThrow();
        Track storedTrack = trackRepository.findBySpotifyId(TRACK_ID).orElseThrow();

        assertEquals(
            List.of(storedTrack.getId()),
            storedArtist.getTopTracks().stream().map(Track::getId).toList()
        );
    }

    private Artist saveArtist() {
        Artist artist = new Artist();
        artist.setSpotifyId(ARTIST_ID);
        artist.setName(spotifyArtistTest.getName());
        return artistRepository.save(artist);
    }

    private Album saveAlbum() {
        Album album = new Album();
        album.setSpotifyId(ALBUM_ID);
        album.setName(spotifyAlbumTest.getName());
        return albumRepository.save(album);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
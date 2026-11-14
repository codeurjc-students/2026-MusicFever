package com.musicfever.music_fever.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.album.AlbumDisplayDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.exception.AlbumNotFoundException;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.AlbumService;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class AlbumIntegrationTest extends PostgresTestBase {

    private final AlbumRepository repository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final AlbumService service;

    @PersistenceContext
    private EntityManager entityManager;

    private Artist artistTest;
    private Album[] albumsTest;

    public AlbumIntegrationTest(
            AlbumRepository repository,
            ArtistRepository artistRepository,
            TrackRepository trackRepository,
            AlbumService service) {
        this.repository = repository;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
        this.service = service;
    }

    @BeforeEach
    void setup() {
        // Given
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artistTest = artistRepository.save(artist);

        albumsTest = new Album[] {
            repository.save(createAlbum("album-1", "Midnight Sessions", artistTest)),
            repository.save(createAlbum("album-2", "Morning Sessions", artistTest)),
            repository.save(createAlbum("album-3", "Unrelated Album"))
        };

        Track track = new Track();
        track.setSpotifyId("track-1");
        track.setName("Opening Track");
        track.setDiscNumber(1);
        track.setNumber(1);
        track.setAlbum(albumsTest[0]);
        trackRepository.save(track);

        Track otherTrack = new Track();
        otherTrack.setSpotifyId("track-2");
        otherTrack.setName("Another Track");
        otherTrack.setDiscNumber(1);
        otherTrack.setNumber(1);
        otherTrack.setAlbum(albumsTest[2]);
        trackRepository.save(otherTrack);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldReturnAlbumsContainingNameIgnoringCase_orEmptyList() {
        // When
        List<Album> result = this.service.searchName("mIdNiGhT");
        List<Album> emptyResult = this.service.searchName("Unknown Album");

        // Then
        assertEquals(1, result.size());
        assertEquals(albumsTest[0].getId(), result.get(0).getId());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void shouldReturnAlbumById_orThrowWhenNotFound() {
        // When
        Album result = this.service.findAlbumById(albumsTest[0].getId());

        // Then
        assertEquals(albumsTest[0].getId(), result.getId());
        assertEquals(albumsTest[0].getName(), result.getName());
        assertThrows(AlbumNotFoundException.class,
            () -> this.service.findAlbumById(Long.MAX_VALUE));
    }

    @Test
    void shouldReturnStoredAlbumsInInputOrder_whenSearchingBySpotifyId() {
        // Given
        SpotifyAlbumDTO first = new SpotifyAlbumDTO();
        first.setId(albumsTest[1].getSpotifyId());

        SpotifyAlbumDTO second = new SpotifyAlbumDTO();
        second.setId(albumsTest[0].getSpotifyId());

        // When
        List<Album> result = this.service.searchAlbums(List.of(first, second));

        // Then
        assertEquals(
            List.of(albumsTest[1].getId(), albumsTest[0].getId()),
            result.stream().map(Album::getId).toList()
        );
    }

    @Test
    void shouldReturnOnlyAlbumsAssociatedWithTheArtist() {
        // When
        Page<AlbumDTO> result =
            this.service.getArtistAlbums(artistTest, PageRequest.of(0, 10));

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(
            List.of("Midnight Sessions", "Morning Sessions"),
            result.getContent().stream().map(AlbumDTO::name).sorted().toList()
        );
    }

    @Test
    void shouldReturnDisplayDtoForAnAlreadyLoadedAlbum() {
        // When
        AlbumDisplayDTO result =
            this.service.getAlbumDisplayDTO(albumsTest[0].getId());

        // Then
        assertEquals(albumsTest[0].getName(), result.name());
    }

    @Test
    void shouldReturnAlreadyLoadedAlbumsInInputOrder_whenLoadingTracklists() {
        // When
        List<Album> result =
            this.service.loadTracklists(List.of(albumsTest[1], albumsTest[0]));

        // Then
        assertEquals(
            List.of(albumsTest[1].getId(), albumsTest[0].getId()),
            result.stream().map(Album::getId).toList()
        );
        assertTrue(result.stream().allMatch(Album::isLoaded));
    }

    @Test
    void shouldReturnOnlyTracksFromTheRequestedAlbum() {
        // When
        Page<TrackDTO> result =
            this.service.getAlbumTracklist(albumsTest[0].getId(), PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Opening Track", result.getContent().get(0).name());
    }

    private Album createAlbum(String spotifyId, String name, Artist... artists) {
        Album album = new Album();
        album.setSpotifyId(spotifyId);
        album.setName(name);
        album.setLoaded(true);
        album.getArtists().addAll(List.of(artists));
        return album;
    }
}
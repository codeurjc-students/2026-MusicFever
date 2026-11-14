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
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.artist.ArtistDisplayDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.exception.ArtistNotFoundException;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.ArtistService;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class ArtistIntegrationTest extends PostgresTestBase {

    private final ArtistRepository repository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final ArtistService service;

    @PersistenceContext
    private EntityManager entityManager;

    private Artist[] artistsTest;

    public ArtistIntegrationTest(
            ArtistRepository repository,
            AlbumRepository albumRepository,
            TrackRepository trackRepository,
            ArtistService service) {
        this.repository = repository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.service = service;
    }

    @BeforeEach
    void setup() {
        // Given
        artistsTest = new Artist[] {
            repository.save(createArtist("artist-1", "Midnight Echo")),
            repository.save(createArtist("artist-2", "Morning Light"))
        };

        Album firstAlbum = albumRepository.save(
            createAlbum("album-1", "Night Sessions", artistsTest[0])
        );
        Album secondAlbum = albumRepository.save(
            createAlbum("album-2", "Morning Sessions", artistsTest[1])
        );

        Track popularTrack = trackRepository.save(
            createTrack("track-1", "Opening Track", firstAlbum, 1)
        );
        trackRepository.save(createTrack("track-2", "Deep Cut", firstAlbum, 2));
        Track otherArtistTrack = trackRepository.save(
            createTrack("track-3", "Morning Track", secondAlbum, 1)
        );

        artistsTest[0].getTopTracks().add(popularTrack);
        artistsTest[1].getTopTracks().add(otherArtistTrack);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldReturnArtistsContainingNameIgnoringCase_orEmptyList() {
        // When
        List<Artist> result = this.service.searchName("mIdNiGhT");
        List<Artist> emptyResult = this.service.searchName("Unknown Artist");

        // Then
        assertEquals(1, result.size());
        assertEquals(artistsTest[0].getId(), result.get(0).getId());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void shouldReturnStoredArtistsInInputOrder_whenSearchingBySpotifyId() {
        // Given
        SpotifyArtistDTO first = new SpotifyArtistDTO();
        first.setId(artistsTest[1].getSpotifyId());

        SpotifyArtistDTO second = new SpotifyArtistDTO();
        second.setId(artistsTest[0].getSpotifyId());

        // When
        List<Artist> result = this.service.searchArtists(List.of(first, second));

        // Then
        assertEquals(
            List.of(artistsTest[1].getId(), artistsTest[0].getId()),
            result.stream().map(Artist::getId).toList()
        );
    }

    @Test
    void shouldReturnArtistById_orThrowWhenNotFound() {
        // When
        Artist result = this.service.getArtistById(artistsTest[0].getId());

        // Then
        assertEquals(artistsTest[0].getId(), result.getId());
        assertEquals(artistsTest[0].getName(), result.getName());
        assertThrows(ArtistNotFoundException.class,
            () -> this.service.getArtistById(Long.MAX_VALUE));
    }

    @Test
    void shouldReturnDisplayDtoForAnAlreadyLoadedArtist() {
        // When
        ArtistDisplayDTO result =
            this.service.getArtistDTOById(artistsTest[0].getId());

        // Then
        assertEquals(artistsTest[0].getName(), result.name());
    }

    @Test
    void shouldReturnOnlyAlbumsAssociatedWithTheRequestedArtist() {
        // When
        Page<AlbumDTO> result =
            this.service.getArtistAlbums(artistsTest[0].getId(), PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Night Sessions", result.getContent().get(0).name());
    }

    @Test
    void shouldReturnOnlyTracksListedAsPopularForTheRequestedArtist() {
        // When
        Page<TrackDTO> result = this.service.getArtistPopularTracks(
            artistsTest[0].getId(), PageRequest.of(0, 10)
        );

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Opening Track", result.getContent().get(0).name());
    }

    private Artist createArtist(String spotifyId, String name) {
        Artist artist = new Artist();
        artist.setSpotifyId(spotifyId);
        artist.setName(name);
        artist.setLoaded(true);
        return artist;
    }

    private Album createAlbum(String spotifyId, String name, Artist artist) {
        Album album = new Album();
        album.setSpotifyId(spotifyId);
        album.setName(name);
        album.setLoaded(true);
        album.getArtists().add(artist);
        return album;
    }

    private Track createTrack(String spotifyId, String name, Album album, int number) {
        Track track = new Track();
        track.setSpotifyId(spotifyId);
        track.setName(name);
        track.setAlbum(album);
        track.setDiscNumber(1);
        track.setNumber(number);
        return track;
    }
}
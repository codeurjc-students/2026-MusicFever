package com.musicfever.music_fever.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.TrackService;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class TrackIntegrationTest extends PostgresTestBase {

    private final TrackRepository repository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackService service;

    private Album albumTest;
    private Artist artistTest;
    private Track[] tracksTest;

    public TrackIntegrationTest(TrackRepository repository, AlbumRepository albumRepository,
            ArtistRepository artistRepository, TrackService service) {
        this.repository = repository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.service = service;
    }

    @BeforeEach
    void setup() {
        // Given
        albumTest = new Album();
        albumTest.setName("Test Album");
        albumTest = albumRepository.save(albumTest);

        Album otherAlbum = new Album();
        otherAlbum.setName("Other Album");
        otherAlbum = albumRepository.save(otherAlbum);

        tracksTest = new Track[] {
            createTrack("track-3", "Final Track", albumTest, 2, 1),
            createTrack("track-2", "Second Track", albumTest, 1, 2),
            createTrack("track-1", "First Track", albumTest, 1, 1),
            createTrack("track-4", "Other Track", otherAlbum, 1, 1)
        };
        tracksTest = repository.saveAllAndFlush(List.of(tracksTest))
            .toArray(Track[]::new);

        artistTest = new Artist();
        artistTest.setName("Test Artist");
        artistTest.getTopTracks().add(tracksTest[1]);
        artistTest = artistRepository.saveAndFlush(artistTest);
    }

    @Test
    void shouldReturnTracksContainingNameIgnoringCase_orEmptyList() {
        // When
        List<Track> result = this.service.searchName("fIrSt");
        List<Track> emptyResult = this.service.searchName("Unknown Track");

        // Then
        assertEquals(1, result.size());
        assertEquals(tracksTest[2].getId(), result.get(0).getId());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void shouldReturnStoredTracksInInputOrder_whenSearchingBySpotifyId() {
        // Given
        SpotifyTrackDTO first = new SpotifyTrackDTO();
        first.setId(tracksTest[1].getSpotifyId());

        SpotifyTrackDTO second = new SpotifyTrackDTO();
        second.setId(tracksTest[2].getSpotifyId());

        // When
        var result = this.service.searchTracks(List.of(first, second));

        // Then
        assertEquals(
            List.of(tracksTest[1].getId(), tracksTest[2].getId()),
            result.stream().map(Track::getId).toList()
        );
    }

    @Test
    void shouldReturnOnlyAlbumTracks_orderedByDiscAndTrackNumber() {
        // When
        Page<TrackDTO> firstPage = this.service.getAlbumTracklist(albumTest, PageRequest.of(0, 2));
        Page<TrackDTO> secondPage = this.service.getAlbumTracklist(albumTest, PageRequest.of(1, 2));

        // Then
        assertEquals(3, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals(2, firstPage.getNumberOfElements());
        assertEquals("First Track", firstPage.getContent().get(0).name());
        assertEquals("Second Track", firstPage.getContent().get(1).name());
        assertEquals(1, secondPage.getNumberOfElements());
        assertEquals("Final Track", secondPage.getContent().get(0).name());
    }

    @Test
    void shouldReturnOnlyTracksListedAsPopularForTheArtist() {
        // When
        Page<TrackDTO> result = this.service.getArtistPopularTracks(artistTest, PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(tracksTest[1].getName(), result.getContent().get(0).name());
    }

    private Track createTrack(String spotifyId, String name, Album album, int discNumber, int number) {
        Track track = new Track();
        track.setSpotifyId(spotifyId);
        track.setName(name);
        track.setAlbum(album);
        track.setDiscNumber(discNumber);
        track.setNumber(number);
        return track;
    }
}

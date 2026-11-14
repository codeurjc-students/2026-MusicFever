package com.musicfever.music_fever.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Pageable;

import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.mapper.model.TrackMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.MusicService;
import com.musicfever.music_fever.service.TrackService;

@ActiveProfiles("unit")
public class TrackServiceTest {
    // Mocks and tools
    TrackService service;
    MusicService musicService;
    TrackMapper mapper;
    TrackRepository repository;

    // Test Objects
    private static final int TEST_OBJECTS = 3;
    Track[] trackTestArray;
    Artist artistTest;
    Album albumTest;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach 
    void setup(){
        // Given
        musicService = mock(MusicService.class);
        repository = mock(TrackRepository.class);
        mapper = Mappers.getMapper(TrackMapper.class);

        service = new TrackService(repository, mapper, musicService);

        trackTestArray = new Track[TEST_OBJECTS];
        for(int i = 0; i < TEST_OBJECTS; i++){
            trackTestArray[i] = new Track();
            trackTestArray[i].setId((long) i);
            trackTestArray[i].setSpotifyId("Spotify " + i);
            trackTestArray[i].setName("Test Track " + i);
        }

        artistTest = new Artist();
        artistTest.setId(2L);

        albumTest = new Album();
        albumTest.setId(1L);
    }

    public static Stream<Arguments> setRepository(){
        Track trackStatic = new Track();
        trackStatic.setId(1L);
        trackStatic.setName("Test Track");
        return Stream.of(
            Arguments.of("Test Track", trackStatic),
            Arguments.of("", null)
        );
    }

    @ParameterizedTest 
    @MethodSource("setRepository")
    @DisplayName("Find tracks containing a search string in its name")
    void givenSearchString_getTracksContainingSearch(String search, Track track){
        // When
        when(repository.findByNameContainingIgnoreCase(search))
            .thenReturn(track == null ? List.of() : List.of(track));
        List<Track> result = this.service.searchName(search);

        // Then
        verify(repository, times(1)).findByNameContainingIgnoreCase(anyString());
        if (track == null){
            assertTrue(result.isEmpty());
        } else {
            assertEquals(1, result.size());
        }
    }

    @Test 
    @DisplayName("Given a external search response, get tracks involved")
    void searchTrack_givenAExternalSearchResult(){
        // Given
        List<SpotifyTrackDTO> dtos = new ArrayList<>();
        for (int i = 0; i < TEST_OBJECTS; i++){
            SpotifyTrackDTO dto = new SpotifyTrackDTO();
            dto.setId(this.trackTestArray[i].getSpotifyId());
            dtos.add(dto);
        }

        // When
        when(repository.findBySpotifyId(anyString())).thenReturn(Optional.empty());
        when(repository.findBySpotifyId(this.trackTestArray[1].getSpotifyId()))
            .thenReturn(Optional.of(this.trackTestArray[1]));

        when(musicService.loadTrack(dtos.get(0))).thenReturn(this.trackTestArray[0]);
        when(musicService.loadTrack(dtos.get(2))).thenReturn(this.trackTestArray[2]);

        List<Track> result = this.service.searchTracks(dtos);

        // Then
        verify(repository, times(3)).findBySpotifyId(anyString());
        assertEquals(TEST_OBJECTS, result.size());

        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.trackTestArray[i].getName(), result.get(i).getName());
        }
    }

    @Test 
    @DisplayName("Get artist's popular tracks from the repository")
    void getArtistPopularTracks(){
        // When
        when(this.repository.findTopTracksByArtist(artistTest.getId(), pageable))
            .thenReturn(new PageImpl<Track>(List.of(trackTestArray)));

        Page<TrackDTO> results = this.service.getArtistPopularTracks(artistTest, pageable);

        // Then
        assertEquals(TEST_OBJECTS, results.getTotalElements());
        
        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(trackTestArray[i].getName(), results.getContent().get(i).name());
        }
    }

    @Test 
    @DisplayName("Get album's tracklist from the repository")
    void getAlbumTracklist(){
        // When
        when(this.repository.findByAlbumIdOrderByDiscNumberAscNumberAsc(albumTest.getId(), pageable))
            .thenReturn(new PageImpl<Track>(List.of(trackTestArray)));

        Page<TrackDTO> results = this.service.getAlbumTracklist(albumTest, pageable);

        // Then
        assertEquals(TEST_OBJECTS, results.getTotalElements());
        
        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(trackTestArray[i].getName(), results.getContent().get(i).name());
        }
    }
}

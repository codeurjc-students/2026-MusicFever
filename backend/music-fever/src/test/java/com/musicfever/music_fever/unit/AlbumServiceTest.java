package com.musicfever.music_fever.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.exception.AlbumNotFoundException;
import com.musicfever.music_fever.mapper.model.AlbumMapper;
import com.musicfever.music_fever.mapper.model.AlbumMapperImpl;
import com.musicfever.music_fever.mapper.model.ArtistMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.repository.AlbumRepository;
import com.musicfever.music_fever.service.AlbumService;
import com.musicfever.music_fever.service.MusicService;
import com.musicfever.music_fever.service.TrackService;

@ActiveProfiles("unit")
public class AlbumServiceTest {
    // Mocks and tools
    AlbumService service;
    TrackService trackService;
    MusicService musicService;
    AlbumMapper mapper;
    AlbumRepository repository;

    // Test Objects
    private static final int TEST_OBJECTS = 3;
    Album[] albumTestArray;
    TrackDTO trackTest;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach 
    void setup(){
        // Given
        trackService = mock(TrackService.class);
        musicService = mock(MusicService.class);
        repository = mock(AlbumRepository.class);
        mapper = new AlbumMapperImpl(Mappers.getMapper(ArtistMapper.class));

        service = new AlbumService(repository, mapper, musicService, trackService);

        albumTestArray = new Album[TEST_OBJECTS];
        for(int i = 0; i < TEST_OBJECTS; i++){
            albumTestArray[i] = new Album();
            albumTestArray[i].setId((long) i);
            albumTestArray[i].setSpotifyId("Spotify " + i);
            albumTestArray[i].setLoaded(i % 2 == 0);
            albumTestArray[i].setName("Test Album " + i);
        }
        
        trackTest = new TrackDTO(1L, "Test Track", null, null, null, null);
    }

    public static Stream<Arguments> setRepository(){
        Album albumStatic = new Album();
        albumStatic.setId(1L);
        albumStatic.setName("Test Album");
        return Stream.of(
            Arguments.of("Test Album", albumStatic),
            Arguments.of("", null)
        );
    }

    @ParameterizedTest 
    @MethodSource("setRepository")
    @DisplayName("Find albums containing a search string in its name")
    void givenSearchString_getAlbumsContainingSearch(String search, Album album){
        // When
        when(repository.findByNameContainingIgnoreCase(search))
            .thenReturn(album == null ? List.of() : List.of(album));
        List<Album> result = this.service.searchName(search);

        // Then
        verify(repository, times(1)).findByNameContainingIgnoreCase(anyString());
        if (album == null){
            assertTrue(result.isEmpty());
        } else {
            assertEquals(1, result.size());
        }

    }

    @Test 
    @DisplayName("Given a external search response, get albums involved")
    void searchAlbum_givenAExternalSearchResult(){
        // Given
        List<SpotifyAlbumDTO> dtos = new ArrayList<>();
        for (int i = 0; i < TEST_OBJECTS; i++){
            SpotifyAlbumDTO dto = new SpotifyAlbumDTO();
            dto.setId(this.albumTestArray[i].getSpotifyId());
            dtos.add(dto);
        }

        // When
        when(repository.findBySpotifyId(anyString())).thenReturn(Optional.empty());
        when(repository.findBySpotifyId(this.albumTestArray[1].getSpotifyId()))
            .thenReturn(Optional.of(this.albumTestArray[1]));

        when(musicService.loadAlbum(dtos.get(0))).thenReturn(this.albumTestArray[0]);
        when(musicService.loadAlbum(dtos.get(2))).thenReturn(this.albumTestArray[2]);

        List<Album> result = this.service.searchAlbums(dtos);

        // Then
        verify(repository, times(3)).findBySpotifyId(anyString());
        assertEquals(TEST_OBJECTS, result.size());

        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.albumTestArray[i].getName(), result.get(i).getName());
        }
    }

    @Test 
    @DisplayName("Get album from the repository given the id")
    void getAlbumByIdTest(){
        // When
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(this.albumTestArray[0].getId()))
            .thenReturn(Optional.of(this.albumTestArray[0]));

        // Then
        assertEquals(this.albumTestArray[0], this.service.findAlbumById((long) 0));
        assertThrows(AlbumNotFoundException.class, () -> {this.service.findAlbumById(4L);});
    }

    @Test 
    @DisplayName("Get album dto from the repository given the id")
    void getAlbumDTOByIdTest(){
        // When
        for (int i = 0; i < TEST_OBJECTS; i++){
            when(repository.findById(this.albumTestArray[i].getId()))
                .thenReturn(Optional.of(this.albumTestArray[i]));
        }

        when(musicService.loadTracklist(this.albumTestArray[1])).thenReturn(this.albumTestArray[1]);

        // Then
        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.albumTestArray[i].getName(), this.service.getAlbumDisplayDTO(this.albumTestArray[i].getId()).name());
        }
    }

    @Test 
    @DisplayName("Get album's tracks from the repository given the id")
    void getTracklistFromAGivenAlbum(){
        // When
        when(repository.findById(anyLong())).thenReturn(Optional.of(this.albumTestArray[0]));
        when(trackService.getAlbumTracklist(this.albumTestArray[0], pageable))
            .thenReturn(new PageImpl<TrackDTO>(List.of(trackTest)));

        Page<TrackDTO> results = this.service.getAlbumTracklist(1L, pageable);

        // Then
        assertEquals(1, results.getTotalElements());
        assertEquals(trackTest.name(), results.getContent().get(0).name());
    }

    @Test 
    @DisplayName("")
    void loadTracklist_givenAnAlbum(){
        // When
        for (int i = 0; i < TEST_OBJECTS; i++){
            when(repository.findById(this.albumTestArray[i].getId()))
                .thenReturn(Optional.of(this.albumTestArray[i]));
        }

        when(musicService.loadTracklist(this.albumTestArray[1]))
            .thenReturn(this.albumTestArray[1]);

        List<Album> response = this.service.loadTracklists(Arrays.asList(albumTestArray));

        // Then
        assertEquals(TEST_OBJECTS, response.size());
        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.albumTestArray[i], response.get(i));
        }
    }
}
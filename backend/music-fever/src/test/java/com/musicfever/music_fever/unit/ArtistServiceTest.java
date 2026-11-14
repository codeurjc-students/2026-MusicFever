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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.dto.album.AlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.track.TrackDTO;
import com.musicfever.music_fever.exception.ArtistNotFoundException;
import com.musicfever.music_fever.mapper.model.ArtistMapper;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.repository.ArtistRepository;
import com.musicfever.music_fever.service.AlbumService;
import com.musicfever.music_fever.service.ArtistService;
import com.musicfever.music_fever.service.MusicService;
import com.musicfever.music_fever.service.TrackService;

@ActiveProfiles("unit")
public class ArtistServiceTest {

    // Mocks and tools
    ArtistService service;
    AlbumService albumService;
    TrackService trackService;
    MusicService musicService;
    ArtistMapper mapper;
    ArtistRepository repository;

    // Test Objects
    private static final int TEST_OBJECTS = 3;
    Artist[] artistTestArray;
    AlbumDTO albumTest;
    TrackDTO trackTest;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach 
    void setup(){
        // Given
        albumService = mock(AlbumService.class);
        trackService = mock(TrackService.class);
        musicService = mock(MusicService.class);
        repository = mock(ArtistRepository.class);
        mapper = Mappers.getMapper(ArtistMapper.class);

        service = new ArtistService(repository, mapper, albumService, trackService, musicService);

        artistTestArray = new Artist[TEST_OBJECTS];
        for(int i = 0; i < TEST_OBJECTS; i++){
            artistTestArray[i] = new Artist();
            artistTestArray[i].setId((long) i);
            artistTestArray[i].setSpotifyId("Spotify " + i);
            artistTestArray[i].setLoaded(i % 2 == 0);
            artistTestArray[i].setName("Test Artist " + i);
        }
        
        albumTest = new AlbumDTO(1L, "Test Album", null, null, null, null, true);
        trackTest = new TrackDTO(1L, "Test Track", null, null, null, null);
    }

    public static Stream<Arguments> setRepository(){
        Artist artistStatic = new Artist();
        artistStatic.setId(1L);
        artistStatic.setName("Test Artist");
        return Stream.of(
            Arguments.of("Test Artist", artistStatic),
            Arguments.of("", null)
        );
    }

    @ParameterizedTest 
    @MethodSource("setRepository")
    @DisplayName("Find artists containing a search string in its name")
    void givenSearchString_getArtistsContainingSearch(String search, Artist artist){
        // When
        when(repository.findByNameContainingIgnoreCase(search))
            .thenReturn(artist == null ? List.of() : List.of(artist));
        List<Artist> result = this.service.searchName(search);

        // Then
        verify(repository, times(1)).findByNameContainingIgnoreCase(anyString());
        if (artist == null){
            assertTrue(result.isEmpty());
        } else {
            assertEquals(1, result.size());
        }

    }

    @Test 
    @DisplayName("Given a external search response, get artists involved")
    void searchArtist_givenAExternalSearchResult(){
        // Given
        List<SpotifyArtistDTO> dtos = new ArrayList<>();
        for (int i = 0; i < TEST_OBJECTS; i++){
            SpotifyArtistDTO dto = new SpotifyArtistDTO();
            dto.setId(this.artistTestArray[i].getSpotifyId());
            dtos.add(dto);
        }

        // When
        when(repository.findBySpotifyId(anyString())).thenReturn(Optional.empty());
        when(repository.findBySpotifyId(this.artistTestArray[1].getSpotifyId()))
            .thenReturn(Optional.of(this.artistTestArray[1]));

        when(musicService.loadArtist(dtos.get(0))).thenReturn(this.artistTestArray[0]);
        when(musicService.loadArtist(dtos.get(2))).thenReturn(this.artistTestArray[2]);

        List<Artist> result = this.service.searchArtists(dtos);

        // Then
        verify(repository, times(3)).findBySpotifyId(anyString());
        assertEquals(TEST_OBJECTS, result.size());

        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.artistTestArray[i].getName(), result.get(i).getName());
        }
    }

    @Test 
    @DisplayName("Get artist from the repository given the id")
    void getArtistByIdTest(){
        // When
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(this.artistTestArray[0].getId()))
            .thenReturn(Optional.of(this.artistTestArray[0]));

        // Then
        assertEquals(this.artistTestArray[0], this.service.getArtistById((long) 0));
        assertThrows(ArtistNotFoundException.class, () -> {this.service.getArtistById(4L);});
    }

    @Test 
    @DisplayName("Get artist dto from the repository given the id")
    void getArtistDTOByIdTest(){
        // When
        for (int i = 0; i < TEST_OBJECTS; i++){
            when(repository.findById(this.artistTestArray[i].getId()))
                .thenReturn(Optional.of(this.artistTestArray[i]));
        }

        when(musicService.loadArtistAlbums(this.artistTestArray[1])).thenReturn(this.artistTestArray[1]);
        when(musicService.loadArtistTopTracks(this.artistTestArray[1])).thenReturn(this.artistTestArray[1]);

        // Then
        for (int i = 0; i < TEST_OBJECTS; i++){
            assertEquals(this.artistTestArray[i].getName(), this.service.getArtistDTOById(this.artistTestArray[i].getId()).name());
        }
    }

    @Test 
    @DisplayName("Get artist's albums' from the repository given the id")
    void getArtistAlbums(){
        // When
        when(repository.findById(anyLong())).thenReturn(Optional.of(this.artistTestArray[0]));
        when(albumService.getArtistAlbums(this.artistTestArray[0], pageable))
            .thenReturn(new PageImpl<AlbumDTO>(List.of(albumTest)));

        Page<AlbumDTO> results = this.service.getArtistAlbums(1L, pageable);

        // Then
        assertEquals(1, results.getTotalElements());
        assertEquals(albumTest.name(), results.getContent().get(0).name());
    }

    @Test 
    @DisplayName("Get artist's popular tracks from the repository given the id")
    void getArtistPopularTracks(){
        // When
        when(repository.findById(anyLong())).thenReturn(Optional.of(this.artistTestArray[0]));
        when(trackService.getArtistPopularTracks(this.artistTestArray[0], pageable))
            .thenReturn(new PageImpl<TrackDTO>(List.of(trackTest)));

        Page<TrackDTO> results = this.service.getArtistPopularTracks(1L, pageable);

        // Then
        assertEquals(1, results.getTotalElements());
        assertEquals(trackTest.name(), results.getContent().get(0).name());
    }
}

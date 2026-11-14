package com.musicfever.music_fever.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.springframework.test.context.ActiveProfiles;

import com.musicfever.music_fever.dto.api.spotify.ItemsPage;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;
import com.musicfever.music_fever.dto.api.spotify.SpotifyAlbumDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyArtistDTO;
import com.musicfever.music_fever.dto.api.spotify.SpotifyTrackDTO;
import com.musicfever.music_fever.dto.search.SearchDTO;
import com.musicfever.music_fever.mapper.model.AlbumMapper;
import com.musicfever.music_fever.mapper.model.AlbumMapperImpl;
import com.musicfever.music_fever.mapper.model.ArtistMapper;
import com.musicfever.music_fever.mapper.model.TrackMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.service.SearchService;
import com.musicfever.music_fever.service.AlbumService;
import com.musicfever.music_fever.service.ArtistService;
import com.musicfever.music_fever.service.TrackService;
import com.musicfever.music_fever.service.api.ApiService;

@ActiveProfiles("unit")
public class SearchServiceTest {

    // Mocks and tools
    SearchService service;
    ArtistService artistService;
    AlbumService albumService;
    TrackService trackService;
    ApiService apiService;
    ArtistMapper artistMapper;
    AlbumMapper albumMapper;
    TrackMapper trackMapper;

    // Test Objects
    private static final String SEARCH = "Test";
    Artist artistTest;
    Album albumTest;
    Track trackTest;

    @BeforeEach
    void setup() {
        // Given
        artistService = mock(ArtistService.class);
        albumService = mock(AlbumService.class);
        trackService = mock(TrackService.class);
        apiService = mock(ApiService.class);

        artistMapper = Mappers.getMapper(ArtistMapper.class);
        albumMapper = new AlbumMapperImpl(artistMapper);
        trackMapper = Mappers.getMapper(TrackMapper.class);

        service = new SearchService(
            artistService, albumService, trackService,
            artistMapper, albumMapper, trackMapper, apiService
        );

        artistTest = new Artist();
        artistTest.setId(1L);
        artistTest.setSpotifyId("artist-1");
        artistTest.setName("Test Artist");

        albumTest = new Album();
        albumTest.setId(2L);
        albumTest.setSpotifyId("album-1");
        albumTest.setName("Test Album");
        albumTest.getArtists().add(artistTest);

        trackTest = new Track();
        trackTest.setId(3L);
        trackTest.setSpotifyId("track-1");
        trackTest.setName("Test Track");
        trackTest.setAlbum(albumTest);
    }

    @ParameterizedTest
    @CsvSource({
        "true,  false, false, 1",
        "false, true,  false, 1",
        "false, false, true,  1",
        "true,  true,  true,  3"
    })
    @DisplayName("Return local results without calling Spotify when any category matches")
    void getSearch_givenLocalResults(
            boolean hasArtists, boolean hasAlbums, boolean hasTracks, int expectedSize) {
        // Given
        List<Artist> artists = hasArtists ? List.of(artistTest) : List.of();
        List<Album> albums = hasAlbums ? List.of(albumTest) : List.of();
        List<Track> tracks = hasTracks ? List.of(trackTest) : List.of();

        // When
        when(artistService.searchName(SEARCH)).thenReturn(artists);
        when(albumService.searchName(SEARCH)).thenReturn(albums);
        when(trackService.searchName(SEARCH)).thenReturn(tracks);

        SearchDTO result = this.service.getSearch(SEARCH);

        // Then
        assertEquals(expectedSize, result.size());
        assertEquals(artistMapper.toDTOs(artists), result.artists());
        assertEquals(albumMapper.toDTOs(albums), result.albums());
        assertEquals(trackMapper.toDTOs(tracks), result.tracks());
        verifyNoInteractions(apiService);
    }

    @ParameterizedTest(name = "Call externalSearch directly: {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("Return Spotify results and load the matching albums' tracklists")
    void shouldReturnSpotifyResults_whenRequestedDirectlyOrNoLocalResultsExist(
            boolean directSearch) {
        // Given
        SpotifyArtistDTO spotifyArtist = new SpotifyArtistDTO();
        spotifyArtist.setId(artistTest.getSpotifyId());
        List<SpotifyArtistDTO> spotifyArtists = List.of(spotifyArtist);

        SpotifyAlbumDTO spotifyAlbum = new SpotifyAlbumDTO();
        spotifyAlbum.setId(albumTest.getSpotifyId());
        List<SpotifyAlbumDTO> spotifyAlbums = List.of(spotifyAlbum);

        SpotifyTrackDTO spotifyTrack = new SpotifyTrackDTO();
        spotifyTrack.setId(trackTest.getSpotifyId());
        List<SpotifyTrackDTO> spotifyTracks = List.of(spotifyTrack);

        SearchResponse response = new SearchResponse();
        response.setArtists(createPage(spotifyArtists));
        response.setAlbums(createPage(spotifyAlbums));
        response.setTracks(createPage(spotifyTracks));

        List<Artist> artists = List.of(artistTest);
        List<Album> albums = List.of(albumTest);
        List<Track> tracks = List.of(trackTest);

        // When
        if (!directSearch) {
            when(artistService.searchName(SEARCH)).thenReturn(List.of());
            when(albumService.searchName(SEARCH)).thenReturn(List.of());
            when(trackService.searchName(SEARCH)).thenReturn(List.of());
        }

        when(apiService.getSearch(SEARCH)).thenReturn(response);
        when(artistService.searchArtists(spotifyArtists)).thenReturn(artists);
        when(albumService.searchAlbums(spotifyAlbums)).thenReturn(albums);
        when(trackService.searchTracks(spotifyTracks)).thenReturn(tracks);

        SearchDTO result = directSearch
            ? this.service.externalSearch(SEARCH)
            : this.service.getSearch(SEARCH);

        // Then
        assertEquals(3, result.size());
        assertEquals(artistMapper.toDTOs(artists), result.artists());
        assertEquals(albumMapper.toDTOs(albums), result.albums());
        assertEquals(trackMapper.toDTOs(tracks), result.tracks());

        verify(apiService).getSearch(SEARCH);
        verify(artistService).searchArtists(spotifyArtists);

        InOrder order = inOrder(albumService, trackService);
        order.verify(albumService).searchAlbums(spotifyAlbums);
        order.verify(albumService).loadTracklists(albums);
        order.verify(trackService).searchTracks(spotifyTracks);
    }

    private <T> ItemsPage<T> createPage(List<T> items) {
        ItemsPage<T> page = new ItemsPage<>();
        page.setItems(items);
        return page;
    }
}
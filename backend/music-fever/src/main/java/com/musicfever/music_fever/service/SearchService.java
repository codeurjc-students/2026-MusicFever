package com.musicfever.music_fever.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.dto.search.SearchDTO;
import com.musicfever.music_fever.mapper.model.AlbumMapper;
import com.musicfever.music_fever.mapper.model.ArtistMapper;
import com.musicfever.music_fever.mapper.model.TrackMapper;
import com.musicfever.music_fever.model.Album;
import com.musicfever.music_fever.model.Artist;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.service.api.ApiService;
import com.musicfever.music_fever.dto.api.spotify.SearchResponse;

@Service 
public class SearchService {
    // Services
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final TrackService trackService;
    private final ApiService apiService;

    // Mappers
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final TrackMapper trackMapper;

    public SearchService(ArtistService artistServ, AlbumService albumServ, TrackService trackServ,
        ArtistMapper artistMap, AlbumMapper albumMap, TrackMapper trackMap, ApiService apiService){
        this.artistService = artistServ;
        this.albumService = albumServ;
        this.trackService = trackServ;
        this.artistMapper = artistMap;
        this.albumMapper = albumMap;
        this.trackMapper = trackMap;
        this.apiService = apiService;
    }

    public SearchDTO getSearch(String searchString) {
        List<Artist> artistsSearch = this.artistService.searchName(searchString);
        List<Album> albumsSearch = this.albumService.searchName(searchString);
        List<Track> tracksSearch = this.trackService.searchName(searchString);

        if (artistsSearch.isEmpty() && albumsSearch.isEmpty() && tracksSearch.isEmpty()){
            return this.externalSearch(searchString);
        }

        return this.mapResponse(artistsSearch, albumsSearch, tracksSearch);
    }

    private SearchDTO mapResponse(List<Artist> artistsSearch, List<Album> albumsSearch, List<Track> tracksSearch) {
        return new SearchDTO(
            artistsSearch.size() + albumsSearch.size() + tracksSearch.size(),
            artistMapper.toDTOs(artistsSearch),
            albumMapper.toDTOs(albumsSearch),
            trackMapper.toDTOs(tracksSearch)
        );
    }

    public SearchDTO externalSearch(String search) {
        SearchResponse response = this.apiService.getSearch(search);
        List<Artist> artistsSearch = this.artistService.loadArtists(response.getArtists().getItems());
        List<Album> albumsSearch = this.albumService.loadAlbums(response.getAlbums().getItems());
        this.albumService.loadTracklists(albumsSearch);
        List<Track> tracksSearch = this.trackService.loadTracks(response.getTracks().getItems());

        return this.mapResponse(artistsSearch, albumsSearch, tracksSearch);
    }
}

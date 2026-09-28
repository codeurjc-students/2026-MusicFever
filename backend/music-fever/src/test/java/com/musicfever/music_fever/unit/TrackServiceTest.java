package com.musicfever.music_fever.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.TrackService;

public class TrackServiceTest {
    TrackRepository repository;
    TrackService trackService;

    @BeforeEach 
    void setup(){
        // Given
        repository = mock(TrackRepository.class);
        trackService = new TrackService(repository);
    }

    private List<Track> getTestTracklist() {
        Track track1 = new Track("Test 1", "Test", 1, true);
        Track track2 = new Track("Test 2", "Test", 2, false);
        Track track3 = new Track("Test 3", "Test", 3, true);
        return List.of(track1, track2, track3);
    }

    @Test
    @DisplayName("Get tracklist successfully")
    void testFindAllTracks_listWithElements() {
        // When
        List<Track> realTracklist = getTestTracklist();
        when(this.repository.findAll()).thenReturn(realTracklist);

        // Then
        List<Track> tracklist = this.trackService.findAll();
        assertEquals(realTracklist.size(), tracklist.size());

        for (int i = 0; i < realTracklist.size(); i++){
            assertEquals(realTracklist.get(i), tracklist.get(i));
        }

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Get empty tracklist successfully")
    void testFindAllTracks_emptyList(){
        when(this.repository.findAll()).thenReturn(new ArrayList<>());

        List<Track> tracklist = this.trackService.findAll();
        assertEquals(0, tracklist.size());
        verify(repository).findAll();
    }
}

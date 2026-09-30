package com.musicfever.music_fever.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.musicfever.music_fever.config.PostgresTestBase;
import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.repository.TrackRepository;
import com.musicfever.music_fever.service.TrackService;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TrackIntegrationTest extends PostgresTestBase{
    private final TrackRepository repository;
    private final TrackService service;

    public TrackIntegrationTest(TrackRepository repository, TrackService service) {
        this.repository = repository;
        this.service = service;
    }

    @BeforeEach 
    void setup(){
        repository.deleteAll();

        repository.save(new Track("Test", "Test 1", 1, true));
        repository.save(new Track("Test", "Test 2", 2, false));
        repository.save(new Track("Test", "Test 3", 3, true));
    }

    @Test 
    void shouldReturnAllElements(){
        List<Track> tracklist = this.service.findAll();

        assertEquals(3, tracklist.size());
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), tracklist.stream().map(Track::getName).sorted().toList());
    }
}

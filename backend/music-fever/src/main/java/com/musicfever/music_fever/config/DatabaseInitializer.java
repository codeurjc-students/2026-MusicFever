package com.musicfever.music_fever.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.service.TrackService;
import com.musicfever.music_fever.service.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    private final TrackService trackService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(TrackService trackService, UserService userService, PasswordEncoder encoder) {
        this.trackService = trackService;
        this.userService = userService;
        this.passwordEncoder = encoder;
    }

    @PostConstruct 
    public void initDatabase(){
        this.trackService.saveTrack(new Track("Taylor Swift", "Patient Zero", 1, false));
        this.trackService.saveTrack(new Track("Tate McRae", "NOBODY'S GIRL", 3, false));
        this.trackService.saveTrack(new Track("Ariana Grande", "petal", 3, false));
        
        this.userService.saveUser(new UserEntity("armiiin", passwordEncoder.encode("eras1325"), "armin@gmail.com"));
    }
}

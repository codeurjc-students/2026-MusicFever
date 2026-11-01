package com.musicfever.music_fever.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.service.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.passwordEncoder = encoder;
    }

    @PostConstruct 
    public void initDatabase(){
        this.userService.saveUser(new UserEntity("armiiin", passwordEncoder.encode("eras1325"), "armin@gmail.com"));
    }
}

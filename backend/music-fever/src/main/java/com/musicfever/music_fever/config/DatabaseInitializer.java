package com.musicfever.music_fever.config;

import java.time.LocalDate;

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
        UserEntity user = new UserEntity("armiiin", passwordEncoder.encode("eras1325"), "armin@gmail.com");
        user.setAccountCreatedDate(LocalDate.of(2026, 7, 13));
        this.userService.saveUser(user);
    }
}

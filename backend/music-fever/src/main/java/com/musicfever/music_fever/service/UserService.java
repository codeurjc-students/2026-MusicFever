package com.musicfever.music_fever.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.repository.UserRepository;

@Service 
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public void saveUser(UserEntity user){
        this.repository.save(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return this.repository.findByEmail(email);
    }
}

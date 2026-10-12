package com.musicfever.music_fever.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.config.jwt.LoginRequest;
import com.musicfever.music_fever.exception.EmailAlreadyExistsException;
import com.musicfever.music_fever.exception.UsernameAlreadyExistsException;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.repository.UserRepository;

@Service 
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder encoder){
        this.repository = repository;
        this.passwordEncoder = encoder;
    }

    public void saveUser(UserEntity user){
        this.repository.save(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return this.repository.findByEmail(email);
    }

    public void register(LoginRequest request) throws UsernameAlreadyExistsException, EmailAlreadyExistsException{
        // Search username unique
        if (this.repository.findByUsername(request.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException();
        }

        // Search email unique
        if (this.repository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException();
        }

        this.repository.save(new UserEntity(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail()));
    }
}

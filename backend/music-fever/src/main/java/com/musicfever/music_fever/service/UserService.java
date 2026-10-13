package com.musicfever.music_fever.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.config.jwt.LoginRequest;
import com.musicfever.music_fever.exception.UserAlreadyExistsException;
import com.musicfever.music_fever.exception.UserNotFoundException;
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

    public Optional<UserEntity> findByUsername(String username) {
        return this.repository.findByUsername(username);
    }

    public LoginRequest resolveLoginIdentifier(LoginRequest request) throws UserNotFoundException{
        Optional<UserEntity> userOptional;
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            userOptional = this.repository.findByEmail(request.getEmail());
        } else {
            userOptional = this.repository.findByUsername(request.getUsername());
        }
        if (userOptional.isEmpty()){
            throw new UserNotFoundException("Invalid username or email");
        } else {
            request.setEmail(userOptional.get().getEmail());
            request.setUsername(userOptional.get().getUsername());
        }
        return request;
    }

    public void registerUser(LoginRequest request) throws UserAlreadyExistsException{
        // Search username unique
        if (this.repository.findByUsername(request.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("This username already exists");
        }

        // Search email unique
        if (this.repository.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("This email already exists");
        }

        this.repository.save(new UserEntity(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail()));
    }
}

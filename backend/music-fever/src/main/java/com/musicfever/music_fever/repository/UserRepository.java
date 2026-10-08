package com.musicfever.music_fever.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musicfever.music_fever.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUsername(String username);
}

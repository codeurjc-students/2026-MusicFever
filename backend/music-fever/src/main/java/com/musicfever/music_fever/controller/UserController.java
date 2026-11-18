package com.musicfever.music_fever.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicfever.music_fever.dto.user.ActiveUserDTO;
import com.musicfever.music_fever.dto.user.UserDTO;
import com.musicfever.music_fever.mapper.model.UserMapper;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@Tag(name = "Users", description = "Endpoints for managing the active user")
@RequestMapping("/api/v1/me")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("")
    public ResponseEntity<ActiveUserDTO> getActiveUser(Authentication auth) {
        String username = auth.getName();
        Optional<UserEntity> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ActiveUserDTO dto = mapper.toPrincipalDTO(user.get());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication auth) {
        String username = auth.getName();
        Optional<UserEntity> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO dto = mapper.toDTO(user.get());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("")
    public ResponseEntity<UserDTO> updateProfile(Authentication auth, @RequestBody UpdateProfileRequest changes) {
        String username = auth.getName();
        UserDTO dto = this.userService.updateProfile(username, changes.email(), changes.profilePicture());

        if (dto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    public record UpdateProfileRequest(
        String email,
        @JsonProperty("profilePicture") String profilePicture
    ) {}
}

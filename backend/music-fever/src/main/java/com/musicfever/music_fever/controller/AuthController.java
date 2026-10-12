package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletResponse;

import com.musicfever.music_fever.config.jwt.AuthResponse;
import com.musicfever.music_fever.config.jwt.LoginRequest;
import com.musicfever.music_fever.config.jwt.UserLoginService;
import com.musicfever.music_fever.dto.user.ActiveUserDTO;
import com.musicfever.music_fever.dto.user.UserMapper;
import com.musicfever.music_fever.exception.EmailAlreadyExistsException;
import com.musicfever.music_fever.exception.UsernameAlreadyExistsException;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;



@RestController 
@Tag(name = "Tracks", description = "Endpoints for managing authorization")
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserLoginService userLoginService;
    private final UserService userService;
    private final UserMapper mapper;

    public AuthController(UserLoginService userLoginService, UserService userService, UserMapper mapper) {
        this.userLoginService = userLoginService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userLoginService.login(response, request);
    }

    @GetMapping("/me")
    public ResponseEntity<ActiveUserDTO> getActiveUser(Authentication auth) {
        String email = auth.getName();
        Optional<UserEntity> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ActiveUserDTO dto = mapper.toPrincipalDTO(user.get());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            this.userService.register(request);
            return userLoginService.login(response, request);
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AuthResponse(AuthResponse.Status.FAILURE, "Username already exists"));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AuthResponse(AuthResponse.Status.FAILURE, "Email already exists"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        this.userLoginService.logout(response);
        return ResponseEntity.noContent().build();
    }
    
}

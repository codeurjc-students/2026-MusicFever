package com.musicfever.music_fever.dto.user;

import java.time.LocalDate;

import com.musicfever.music_fever.enums.MusicProvider;
import com.musicfever.music_fever.model.Image;

public record UserDTO(String username, String email, LocalDate accountCreatedDate, 
    MusicProvider musicProvider, Image profilePicture){}

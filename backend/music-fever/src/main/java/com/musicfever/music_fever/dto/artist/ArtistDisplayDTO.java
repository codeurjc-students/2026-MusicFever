package com.musicfever.music_fever.dto.artist;

import java.util.Set;

import com.musicfever.music_fever.model.Image;

public record ArtistDisplayDTO(Long id, String name, Long listeners, Image bigImage, Set<String> genres){}
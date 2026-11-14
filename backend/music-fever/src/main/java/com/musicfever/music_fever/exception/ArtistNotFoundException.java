package com.musicfever.music_fever.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ArtistNotFoundException extends RuntimeException{
    public ArtistNotFoundException(String message){
        super(message);
    }
}

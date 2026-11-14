package com.musicfever.music_fever.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(String message){
        super(message);
    }
}

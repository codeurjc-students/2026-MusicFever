package com.musicfever.music_fever.dto.api.spotify;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ItemsPage<T> {
    private String href;
    private int limit;
    private String next;
    private String previous;
    private List<T> items;
}

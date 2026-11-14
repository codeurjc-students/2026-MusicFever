package com.musicfever.music_fever.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicfever.music_fever.dto.search.SearchDTO;
import com.musicfever.music_fever.service.SearchService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@Tag(name = "Tracks", description = "Endpoints for managing authorization")
@RequestMapping("api/v1/search")
public class SearchController {
    private final SearchService service;

    public SearchController(SearchService service){
        this.service = service;
    }

    @GetMapping("")
    public SearchDTO getSearch(@RequestParam String search) {
        return this.service.getSearch(search);
    }

    @GetMapping("/external-search")
    public SearchDTO getExternalSearch(@RequestParam String search) {
        return this.service.externalSearch(search);
    }
    
}

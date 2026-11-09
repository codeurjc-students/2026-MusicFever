import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ArtistDisplay } from '../model/artist.model';
import { AlbumPage } from '../model/album.model';
import { TrackPage } from '../model/track.model';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {
    private http = inject(HttpClient);
    private readonly apiUrl = 'https://localhost:8443/api/v1/artists';

    getArtist(id: string) {
        return this.http.get<ArtistDisplay>(`${this.apiUrl}/${id}`);
    }

    getArtistAlbums(id: string, page: number = 0, size: number = 10) {
        return this.http.get<AlbumPage>(
            `${this.apiUrl}/${id}/albums`,
            {
            params: { page, size }
            }
        );
    }

    getArtistTopTracks(id: string, page: number = 0, size: number = 10) {
        return this.http.get<TrackPage>(
            `${this.apiUrl}/${id}/popular-tracks`,
            { params: { page, size } }
        );
    }
}
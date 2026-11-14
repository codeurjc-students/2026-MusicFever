import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { AlbumDisplay } from '../model/album.model';
import { TrackPage } from '../model/track.model';

@Injectable({
  providedIn: 'root'
})
export class AlbumService {
    private http = inject(HttpClient);
    private readonly apiUrl = 'https://localhost:8443/api/v1/albums';

    getAlbum(id: number) {
        return this.http.get<AlbumDisplay>(`${this.apiUrl}/${id}`);
    }

    getAlbumTracks(id: number, page: number = 0, size: number = 10) {
        return this.http.get<TrackPage>(
            `${this.apiUrl}/${id}/tracks`,
            {
                params: { page, size }
            }
        );
    }
}
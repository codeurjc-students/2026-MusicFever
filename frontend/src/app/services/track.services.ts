import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Track } from '../model/track.model';

@Injectable({
  providedIn: 'root'
})
export class TrackService {

  private url = 'https://localhost:8443/api/v1/tracks/';

  constructor(private http: HttpClient) {}

  getTracks(): Observable<Track[]> {
    return this.http.get<Track[]>(this.url);
  }
}
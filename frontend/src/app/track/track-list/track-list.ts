import { Component, OnInit, signal } from '@angular/core';
import { Track } from '../../model/track.model';
import { TrackComponent } from '../../track/track';
import { TrackService } from '../../services/track.services';

@Component({
  selector: 'app-track-list',
  imports: [TrackComponent],
  templateUrl: './track-list.html',
  styleUrl: './track-list.css'
})
export class TrackList implements OnInit {
  tracks = signal<Track[]>([]);

  constructor(private trackService: TrackService) {}

  ngOnInit(): void {
    this.trackService.getTracks().subscribe({
      next: tracks => this.tracks.set(tracks),
      error: err => console.error('ERROR:', err)
    });
  }
}
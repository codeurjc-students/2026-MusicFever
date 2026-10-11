import { Component, OnInit, signal, inject } from '@angular/core';

import { Track } from '../../model/track.model';
import { TrackComponent } from '../../track/track';
import { TrackService } from '../../services/track.services';
import { AuthService } from '../../services/auth.services';

@Component({
  selector: 'app-track-list',
  imports: [TrackComponent],
  templateUrl: './track-list.html',
  styleUrl: './track-list.css'
})
export class TrackList implements OnInit {
  tracks = signal<Track[]>([]);
  authService = inject(AuthService);

  constructor(private trackService: TrackService) {}

  ngOnInit(): void {
    this.trackService.getTracks().subscribe({
      next: tracks => this.tracks.set(tracks),
      error: err => console.error('ERROR:', err)
    });

    this.authService.getCurrentUser().subscribe({
      next: user => {
        this.authService.setCurrentUser(user);
      },
      error: () => {
        this.authService.setCurrentUser(null);
      }
    });
  }
}
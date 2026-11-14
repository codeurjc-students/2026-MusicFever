import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

import { Track } from '../../../model/track.model';

@Component({
  imports: [MatIconModule],
  selector: 'app-track-card',
  styleUrl: '../cards.css',
  templateUrl: './track-card.html',
})
export class TrackCard {
  track = input.required<Track>();

  play(){
    // play action
  }

  playNext(){
    // play next action
  }

  onQueue(){
    // add song to queue action
  }

  addToPlaylist(){
    // add to playlist action
  }
}

import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';

@Component({
  imports: [MatIconModule],
  selector: 'app-mini-player',
  styleUrl: './mini-player.css',
  templateUrl: './mini-player.html',
})
export class MiniPlayer {
  isPlaying = false;

  togglePlay(): void {
    this.isPlaying = !this.isPlaying;
  }
}

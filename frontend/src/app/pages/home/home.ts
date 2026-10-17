import { Component } from '@angular/core';
import { TrackList } from '../../track/track-list/track-list';

@Component({
  imports: [TrackList],
  selector: 'app-home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {}

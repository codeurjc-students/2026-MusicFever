import { Component, input } from '@angular/core';
import { Track } from '../model/track.model';

@Component({
    selector: 'app-track',
    templateUrl: './track.html'
})
export class TrackComponent {
    track = input.required<Track>();
}
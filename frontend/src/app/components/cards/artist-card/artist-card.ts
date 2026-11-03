import { Component, input } from '@angular/core';
import { Router } from '@angular/router';

import { Artist } from '../../../model/artist.model';

@Component({
  imports: [],
  selector: 'app-artist-card',
  styleUrl: '../cards.css',
  templateUrl: './artist-card.html',
})
export class ArtistCard {
  artist = input.required<Artist>();

  artistPage(){
    // moves to artist page
  }
}

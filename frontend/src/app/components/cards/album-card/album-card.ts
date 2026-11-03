import { Component, input } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

import { Album } from '../../../model/album.model';

@Component({
  imports: [MatIconModule],
  selector: 'app-album-card',
  styleUrl: '../cards.css',
  templateUrl: './album-card.html',
})
export class AlbumCard {
  album = input.required<Album>();

  albumPage(){
    // moves to album page
  }
}

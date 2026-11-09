import { Component, inject, input } from '@angular/core';
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
  private router = inject(Router);

  albumPage(){
    this.router.navigate(['/album', this.album().id]);
  }
}

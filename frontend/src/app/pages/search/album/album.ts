import { Component, inject, signal } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import { AlbumDisplay } from '../../../model/album.model';
import { TrackCard } from '../../../components/cards/track-card/track-card'
import { Track } from '../../../model/track.model';
import { AlbumService } from '../../../services/album.services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  imports: [MatProgressSpinnerModule, MatIconModule, MatPaginatorModule, TrackCard],
  selector: 'app-album',
  styleUrl: './album.css',
  templateUrl: './album.html',
})
export class AlbumPage {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  readonly albumId = Number(this.route.snapshot.paramMap.get('id'));

  album = signal<AlbumDisplay | null>(null);
  loading = signal(true);

  private albumService = inject(AlbumService);

  tracklist = signal<Track[]>([]);
  totalTracks = signal<number>(0);

  pageSize = 10;
  pageIndex = 0;

  ngOnInit(): void {
    this.loadAlbum();
  }

  loadAlbum(): void {
    this.albumService.getAlbum(this.albumId).subscribe({
      next: (album) => {
        this.album.set(album);
        this.loading.set(false);
        this.loadTracks();
      },
      error: (error) => {
        console.error(error);
        this.loading.set(false);
      }
    });
  }

  loadTracks(): void {
    this.albumService.getAlbumTracks(
      this.albumId,
      this.pageIndex,
      this.pageSize
    ).subscribe({
      next: (page) => {
        this.tracklist.set(page.content);
        this.totalTracks.set(page.totalElements);
      },
      error: (error) => console.error(error)
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTracks();
  }

  artistPage(artistId: number): void{
    this.router.navigate(['/artist', artistId]);
  }
}

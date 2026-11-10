import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { ArtistDisplay } from '../../../model/artist.model';
import { ArtistService } from '../../../services/artist.services';
import { Album } from '../../../model/album.model';
import { AlbumCard } from '../../../components/cards/album-card/album-card';
import { Track } from '../../../model/track.model';
import { TrackCard } from '../../../components/cards/track-card/track-card';

@Component({
  imports: [MatTabsModule, MatPaginatorModule, MatProgressSpinnerModule, AlbumCard, TrackCard],
  selector: 'app-artist',
  styleUrl: './artist.css',
  templateUrl: './artist.html',
})
export class ArtistPage {
  private route = inject(ActivatedRoute);
  private artistService = inject(ArtistService);

  artist = signal<ArtistDisplay | null>(null);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.artistService.getArtist(id).subscribe({
      next: artist => {
        this.artist.set(artist);
        this.loading.set(false);
        this.loadAlbums();
      },
      error: () => {
        this.loading.set(false);
      }
    });
    }
  }

  onTabChange(index: number) {
    if (index === 0 && this.albums().length === 0) {
      this.loadAlbums();
    }
    if (index === 1 && !this.popularTracksLoaded()) {
      this.loadPopularTracks();
    }
  }

  onPageChange(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.loadAlbums(event.pageIndex);
  }

  albums = signal<Album[]>([]);
  totalAlbums = signal(0);
  pageSize = 10;
  pageIndex = 0;

  loadAlbums(page: number = 0) {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.artistService.getArtistAlbums(id, page, this.pageSize)
      .subscribe(response => {
        this.albums.set(response.content);
        this.totalAlbums.set(response.totalElements);
        this.pageIndex = page;
      });
  }

  popularTracks = signal<Track[]>([]);
  totalTracks = signal(0);

  tracksPageSize = 10;
  tracksPageIndex = 0;
  popularTracksLoaded = signal(false);

  loadPopularTracks(page: number = 0) {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.artistService.getArtistTopTracks(id, page, this.tracksPageSize)
      .subscribe(response => {
        this.popularTracks.set(response.content);
        this.totalTracks.set(response.totalElements);
        this.tracksPageIndex = page;
        this.popularTracksLoaded.set(true);
      });
  }

  onTracksPageChange(event: PageEvent) {
    this.tracksPageSize = event.pageSize;
    this.loadPopularTracks(event.pageIndex);
  }
}

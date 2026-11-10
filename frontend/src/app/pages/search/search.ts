import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SearchService } from '../../services/search.services';
import { SearchResponse } from '../../model/search.model';
import { ArtistCard } from '../../components/cards/artist-card/artist-card';
import { AlbumCard } from '../../components/cards/album-card/album-card';
import { TrackCard } from '../../components/cards/track-card/track-card';

@Component({
  imports: [ArtistCard, AlbumCard, TrackCard],
  selector: 'app-search',
  styleUrl: './search.css',
  templateUrl: './search.html',
})
export class Search implements OnInit {
  private route = inject(ActivatedRoute);
  private searchService = inject(SearchService);
  searchResult = signal<SearchResponse | null>(null);
  loading = signal(false);

  ngOnInit() {
    this.route.queryParamMap.subscribe(params => {
      const query = params.get('search') ?? '';

      this.searchService.searchQuery.set(query);

      if (query.trim()) {
        this.loading.set(true);
        this.searchResult.set(null);

        this.searchService.search(query).subscribe({
          next: result => {
            this.searchResult.set(result);
            this.loading.set(false);
          },
          error: err => {
            console.error(err);
            this.loading.set(false);
          }
        });
      }
    });
  }

  forceExternalSearch(): void {
    const query = this.searchService.searchQuery();

    this.loading.set(true);

    this.searchService.externalSearch(query).subscribe({
      next: (result) => {
        this.searchResult.set(result);
        this.loading.set(false);
      },
      error: (error) => {
        console.error(error);
        this.loading.set(false);
      }
    });
  }
}

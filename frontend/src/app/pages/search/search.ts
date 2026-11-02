import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SearchService } from '../../services/search.services';

@Component({
  imports: [],
  selector: 'app-search',
  styleUrl: './search.css',
  templateUrl: './search.html',
})
export class Search implements OnInit {
  private route = inject(ActivatedRoute);
  private searchService = inject(SearchService);

  ngOnInit() {
    this.route.queryParamMap.subscribe(params => {
      const query = params.get('q') ?? '';
      this.searchService.searchQuery.set(query);

      if (query.trim()) {
        this.searchService.search(query);
      }
    });
  }
}

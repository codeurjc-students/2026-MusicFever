import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

import { SearchService } from '../../services/search.services';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule, MatIconModule],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css'
})
export class SearchBar {
  private router = inject(Router);
  searchService = inject(SearchService);

  searchText = '';

  search(): void {
    const query = this.searchText.trim();

    if (!query) return;

    this.searchService.searchQuery.set(query);

    this.router.navigate(['/search'], {
      queryParams: { search: query }
    });
  }
}
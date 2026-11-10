import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SearchResponse } from '../model/search.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private http = inject(HttpClient);
  private readonly apiUrl = 'https://localhost:8443/api/v1/search';

  searchQuery = signal('');

  search(query: string) {
    return this.http.get<SearchResponse>(this.apiUrl, {
      params: { search: query },
    });
  }

  externalSearch(query: string) {
    return this.http.get<SearchResponse>(`${this.apiUrl}/external-search`, {
      params: { search: query },
    });
  }
}
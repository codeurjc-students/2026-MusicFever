import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private http = inject(HttpClient);

  searchQuery = signal('');

  search(query: string) {
  return this.http.get<string[]>('/api/search', {
    params: { searchString: query }
  });
}
}
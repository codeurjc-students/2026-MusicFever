
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

import { SearchService } from '../../app/services/search.services';
import { SearchBar } from '../../app/components/search-bar/search-bar';
import { Search } from '../../app/pages/search/search';
import { SearchResponse } from '../../app/model/search.model';

describe('SearchService Integration', () => {
  let searchBarFixture: ComponentFixture<SearchBar>;
  let searchBar: SearchBar;

  let searchFixture: ComponentFixture<Search>;
  let searchComponent: Search;

  let searchService: SearchService;
  let router: Router;
  let httpMock: HttpTestingController;

  const queryParams$ = new BehaviorSubject(
    convertToParamMap({})
  );

  // Test search results
  const searchResult: SearchResponse = {
    size: 3,
    artists: [{
      id: 1,
      name: 'Test artist',
      smallImage: {
        width: 100,
        height: 100,
        url: 'test-artist.jpg'
      }
    }],
    albums: [{
      id: 2,
      name: 'Test album',
      artists: [],
      totalTracks: 12,
      releaseDate: '2026-01-01',
      smallImage: {
        width: 200,
        height: 200,
        url: 'test-album.jpg'
      }
    }],
    tracks: [{
      name: 'Test song',
      number: 1,
      explicit: false,
      album: {
        id: 2,
        name: 'Test album',
        artists: [],
        totalTracks: 12,
        releaseDate: '2026-01-01',
        smallImage: {
          width: 200,
          height: 200,
          url: 'test-album.jpg'
        }
      }
    }]
  };

  beforeEach(async () => {
    queryParams$.next(convertToParamMap({}));

    await TestBed.configureTestingModule({
      imports: [SearchBar, Search],
      providers: [
        SearchService,
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParamMap: queryParams$.asObservable()
          }
        }
      ]
    }).compileComponents();

    searchService = TestBed.inject(SearchService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);

    searchBarFixture = TestBed.createComponent(SearchBar);
    searchBar = searchBarFixture.componentInstance;
    searchBarFixture.detectChanges();

    searchFixture = TestBed.createComponent(Search);
    searchComponent = searchFixture.componentInstance;
    searchFixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    vi.restoreAllMocks();
  });

  it('should create the search service', () => {
    expect(searchService).toBeTruthy();
  });

  it('should initialize with an empty search query', () => {
    expect(searchService.searchQuery()).toBe('');
  });

  it('should update the search service when submitting a search', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate')
      .mockResolvedValue(true);

    const input = searchBarFixture.nativeElement.querySelector('input');

    input.value = 'Taylor Swift';
    input.dispatchEvent(new Event('input'));

    await searchBarFixture.whenStable();

    const form = searchBarFixture.nativeElement.querySelector('form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    searchBarFixture.detectChanges();

    expect(searchService.searchQuery()).toBe('Taylor Swift');

    expect(navigateSpy).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Taylor Swift' }
    });
  });

  it('should trim the query before updating the service', () => {
    const navigateSpy = vi.spyOn(router, 'navigate')
      .mockResolvedValue(true);

    searchBar.searchText = '  Coldplay  ';
    searchBar.search();

    expect(searchService.searchQuery()).toBe('Coldplay');

    expect(navigateSpy).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Coldplay' }
    });
  });

  it('should not update the service when the search is empty', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');

    searchBar.searchText = '   ';
    searchBar.search();

    expect(searchService.searchQuery()).toBe('');
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should share the search query across service instances', () => {
    const anotherInstance = TestBed.inject(SearchService);

    searchService.searchQuery.set('Arctic Monkeys');

    expect(anotherInstance.searchQuery()).toBe('Arctic Monkeys');
    expect(anotherInstance).toBe(searchService);
  });

  it('should update the search query from URL parameters', () => {
    queryParams$.next(
      convertToParamMap({ search: 'Taylor Swift' })
    );

    expect(searchService.searchQuery()).toBe('Taylor Swift');

    const req = httpMock.expectOne(
      request => request.url.endsWith('/search')
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('Taylor Swift');

    req.flush(searchResult);
  });

  it('should display loading while searching', () => {
    queryParams$.next(
      convertToParamMap({ search: 'Taylor Swift' })
    );
    searchFixture.detectChanges();

    expect(searchComponent.loading()).toBe(true);
    expect(searchComponent.searchResult()).toBeNull();

    expect(searchFixture.nativeElement.textContent)
      .toContain('Loading...');

    const req = httpMock.expectOne(
      request => request.url.endsWith('/search')
    );

    req.flush(searchResult);
    searchFixture.detectChanges();

    expect(searchComponent.loading()).toBe(false);
    expect(searchComponent.searchResult()).toEqual(searchResult);
  });

  it('should display search results after the HTTP response', () => {
    queryParams$.next(
      convertToParamMap({ search: 'test' })
    );

    const req = httpMock.expectOne(
      request => request.url.endsWith('/search')
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('test');

    req.flush(searchResult);
    searchFixture.detectChanges();

    const element = searchFixture.nativeElement;

    expect(element.textContent).toContain('3 results');

    expect(element.querySelectorAll('app-artist-card').length)
      .toBe(1);

    expect(element.querySelectorAll('app-album-card').length)
      .toBe(1);

    expect(element.querySelectorAll('app-track-card').length)
      .toBe(1);

    expect(element.textContent).toContain('Test artist');
    expect(element.textContent).toContain('Test album');
    expect(element.textContent).toContain('Test song');
  });

  it('should not perform an HTTP request for an empty query', () => {
    queryParams$.next(
      convertToParamMap({ search: '   ' })
    );

    expect(searchService.searchQuery()).toBe('   ');

    httpMock.expectNone(
      request => request.url.endsWith('/search')
    );
  });

  it('should perform an external search', () => {
    queryParams$.next(
      convertToParamMap({ search: 'Taylor Swift' })
    );

    httpMock.expectOne(
      request => request.url.endsWith('/search')
    ).flush(searchResult);

    searchFixture.detectChanges();

    const link = searchFixture.nativeElement.querySelector('a');
    link.click();

    expect(searchComponent.loading()).toBe(true);

    const req = httpMock.expectOne(
      request => request.url.endsWith('/external-search')
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('Taylor Swift');

    req.flush(searchResult);
    searchFixture.detectChanges();

    expect(searchComponent.searchResult()).toEqual(searchResult);
    expect(searchComponent.loading()).toBe(false);
  });

  it('should handle search HTTP errors', () => {
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    queryParams$.next(
      convertToParamMap({ search: 'Taylor Swift' })
    );

    const req = httpMock.expectOne(
      request => request.url.endsWith('/search')
    );

    req.flush('Search failed', {
      status: 500,
      statusText: 'Internal Server Error'
    });

    searchFixture.detectChanges();

    expect(searchComponent.loading()).toBe(false);
    expect(searchComponent.searchResult()).toBeNull();
    expect(consoleSpy).toHaveBeenCalled();
  });

  it('should handle external search HTTP errors', () => {
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    searchService.searchQuery.set('Taylor Swift');

    searchComponent.forceExternalSearch();

    const req = httpMock.expectOne(
      request => request.url.endsWith('/external-search')
    );

    req.flush('External search failed', {
      status: 500,
      statusText: 'Internal Server Error'
    });

    searchFixture.detectChanges();

    expect(searchComponent.loading()).toBe(false);
    expect(consoleSpy).toHaveBeenCalled();
  });

  it('should share the query between SearchBar and Search', () => {
    const navigateSpy = vi.spyOn(router, 'navigate')
      .mockResolvedValue(true);

    searchBar.searchText = 'Coldplay';
    searchBar.search();

    expect(searchService.searchQuery()).toBe('Coldplay');

    queryParams$.next(
      convertToParamMap({ search: 'Coldplay' })
    );

    const req = httpMock.expectOne(
      request => request.url.endsWith('/search')
    );

    expect(req.request.params.get('search')).toBe('Coldplay');

    req.flush(searchResult);
    searchFixture.detectChanges();

    expect(searchComponent.searchResult()).toEqual(searchResult);

    expect(navigateSpy).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Coldplay' }
    });
  });
});
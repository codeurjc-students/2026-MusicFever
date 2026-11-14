
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { convertToParamMap } from '@angular/router';
import { vi } from 'vitest';
import { of, Subject, throwError } from 'rxjs';

import { Search } from '../../../app/pages/search/search';
import { SearchService } from '../../../app/services/search.services';
import { SearchResponse } from '../../../app/model/search.model';
import { signal } from '@angular/core';

describe('Search', () => {
  let fixture: ComponentFixture<Search>;
  let component: Search;

  // Test search results to display
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

  // SearchServiceMock
  const searchServiceMock = {
    searchQuery: signal(''),
    search: vi.fn(),
    externalSearch: vi.fn()
  };

  // RouterMock for child cards
  const routerMock = {
    navigate: vi.fn()
  };

  const queryParams$ = new Subject<ReturnType<typeof convertToParamMap>>();

  beforeEach(async () => {
    vi.clearAllMocks();

    searchServiceMock.searchQuery.set('');

    searchServiceMock.search.mockReturnValue(of(searchResult));
    searchServiceMock.externalSearch.mockReturnValue(of(searchResult));

    await TestBed.configureTestingModule({
      imports: [Search],
      providers: [
        {
          provide: SearchService,
          useValue: searchServiceMock
        },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParamMap: queryParams$.asObservable()
          }
        },
        {
          provide: Router,
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Search);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should search when the query parameter changes', () => {
    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    expect(searchServiceMock.searchQuery()).toBe('test');

    expect(searchServiceMock.search)
      .toHaveBeenCalledWith('test');

    expect(searchServiceMock.search)
      .toHaveBeenCalledTimes(1);
  });

  it('should display the search results', () => {
    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    const element = fixture.nativeElement;

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

  it('should display loading while searching', () => {
    const response$ = new Subject<SearchResponse>();
    searchServiceMock.search.mockReturnValue(response$.asObservable());

    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    expect(component.loading()).toBe(true);
    expect(component.searchResult()).toBeNull();
    expect(fixture.nativeElement.textContent)
      .toContain('Loading...');

    response$.next(searchResult);
    response$.complete();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.searchResult()).toEqual(searchResult);
    expect(fixture.nativeElement.textContent)
      .toContain('3 results');
  });

  it('should not search when the query is empty', () => {
    queryParams$.next(convertToParamMap({ search: '' }));
    fixture.detectChanges();

    expect(searchServiceMock.searchQuery()).toBe('');

    expect(searchServiceMock.search)
      .not.toHaveBeenCalled();
  });

  it('should not search when the query contains only spaces', () => {
    queryParams$.next(convertToParamMap({ search: '   ' }));
    fixture.detectChanges();

    expect(searchServiceMock.search)
      .not.toHaveBeenCalled();
  });

  it('should handle search errors', () => {
    const error = new Error('Search failed');
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    searchServiceMock.search.mockReturnValue(
      throwError(() => error)
    );

    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.searchResult()).toBeNull();
    expect(consoleSpy).toHaveBeenCalledWith(error);

    consoleSpy.mockRestore();
  });

  it('should perform an external search when the link is clicked', () => {
    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('a');
    link.click();

    expect(searchServiceMock.externalSearch)
      .toHaveBeenCalledWith('test');

    expect(searchServiceMock.externalSearch)
      .toHaveBeenCalledTimes(1);

    expect(component.searchResult()).toEqual(searchResult);
    expect(component.loading()).toBe(false);
  });

  it('should display loading while performing an external search', () => {
    const response$ = new Subject<SearchResponse>();
    searchServiceMock.externalSearch.mockReturnValue(
      response$.asObservable()
    );

    queryParams$.next(convertToParamMap({ search: 'test' }));
    fixture.detectChanges();

    component.forceExternalSearch();
    fixture.detectChanges();

    expect(component.loading()).toBe(true);
    expect(fixture.nativeElement.textContent)
      .toContain('Loading...');

    response$.next(searchResult);
    response$.complete();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.searchResult()).toEqual(searchResult);
  });

  it('should handle external search errors', () => {
    const error = new Error('External search failed');
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    searchServiceMock.externalSearch.mockReturnValue(
      throwError(() => error)
    );

    component.forceExternalSearch();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(consoleSpy).toHaveBeenCalledWith(error);

    consoleSpy.mockRestore();
  });
});

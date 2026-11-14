import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

import { AlbumPage } from '../../app/pages/search/album/album';
import { AlbumService } from '../../app/services/album.services';
import { AlbumDisplay } from '../../app/model/album.model';
import { TrackPage } from '../../app/model/track.model';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('AlbumService Integration', () => {
  let fixture: ComponentFixture<AlbumPage>;
  let component: AlbumPage;
  let albumService: AlbumService;
  let httpMock: HttpTestingController;
  let router: Router;

  const album: AlbumDisplay = {
    id: 1,
    name: 'Test album',
    artists: [{
      id: 2,
      name: 'Test artist',
      smallImage: {
        width: 100,
        height: 100,
        url: 'test-artist.jpg'
      }
    }],
    totalTracks: 12,
    genre: 'Pop',
    releaseDate: '2026-01-01',
    bigImage: {
      width: 300,
      height: 300,
      url: 'test-album.jpg'
    }
  };

  const trackPage: TrackPage = {
    content: [{
      name: 'Test song',
      number: 1,
      explicit: false,
      album: {
        id: 1,
        name: 'Test album',
        artists: album.artists,
        totalTracks: 12,
        releaseDate: '2026-01-01',
        smallImage: {
          width: 200,
          height: 200,
          url: 'test-album-small.jpg'
        }
      }
    }],
    totalElements: 12,
    totalPages: 2,
    number: 0,
    size: 10
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumPage],
      providers: [
        provideNoopAnimations(),
        AlbumService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '1' })
            }
          }
        },
        {
          provide: Router,
          useValue: { navigate: vi.fn() }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumPage);
    component = fixture.componentInstance;
    albumService = TestBed.inject(AlbumService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the album service', () => {
    expect(albumService).toBeTruthy();

    httpMock.expectOne(
      'https://localhost:8443/api/v1/albums/1'
    ).flush(album);

    httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    ).flush(trackPage);
  });

  it('should load and display the album', () => {
    const req = httpMock.expectOne(
      'https://localhost:8443/api/v1/albums/1'
    );

    expect(req.request.method).toBe('GET');

    req.flush(album);

    httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    ).flush(trackPage);

    fixture.detectChanges();

    expect(component.album()).toEqual(album);
    expect(component.loading()).toBe(false);

    expect(fixture.nativeElement.textContent)
      .toContain('Test album');

    expect(fixture.nativeElement.textContent)
      .toContain('Test artist');

    expect(fixture.nativeElement.textContent)
      .toContain('12 tracks');
  });

  it('should load and display the album tracks', () => {
    httpMock.expectOne(
      request => request.url.endsWith('/albums/1')
    ).flush(album);

    const req = httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');

    req.flush(trackPage);
    fixture.detectChanges();

    expect(component.tracklist()).toEqual(trackPage.content);
    expect(component.totalTracks()).toBe(12);

    expect(fixture.nativeElement.textContent)
      .toContain('Test song');
  });

  it('should update track pagination', () => {
    httpMock.expectOne(
      request => request.url.endsWith('/albums/1')
    ).flush(album);

    httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    ).flush(trackPage);

    component.onPageChange({
      pageIndex: 1,
      pageSize: 5,
      length: 12
    } as PageEvent);

    const req = httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    );

    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('5');

    req.flush({
      ...trackPage,
      number: 1,
      size: 5
    });

    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(5);
  });

  it('should navigate to the artist page', () => {
    httpMock.expectOne(
      request => request.url.endsWith('/albums/1')
    ).flush(album);

    httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    ).flush(trackPage);

    fixture.detectChanges();

    const navigateSpy = vi.spyOn(router, 'navigate');

    const link = fixture.nativeElement.querySelector('a');
    link.click();

    expect(navigateSpy).toHaveBeenCalledWith(['/artist', 2]);
  });

  it('should handle an album not found error', () => {
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    const req = httpMock.expectOne(
      request => request.url.endsWith('/albums/1')
    );

    req.flush('Not found', {
      status: 404,
      statusText: 'Not Found'
    });

    fixture.detectChanges();

    expect(component.album()).toBeNull();
    expect(component.loading()).toBe(false);

    expect(fixture.nativeElement.textContent)
      .toContain('Album No Encontrado (prov)');

    httpMock.expectNone(
      request => request.url.endsWith('/1/tracks')
    );

    consoleSpy.mockRestore();
  });

  it('should handle errors when loading tracks', () => {
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    httpMock.expectOne(
      request => request.url.endsWith('/albums/1')
    ).flush(album);

    const req = httpMock.expectOne(
      request => request.url.endsWith('/1/tracks')
    );

    req.flush('Tracks not found', {
      status: 500,
      statusText: 'Internal Server Error'
    });

    expect(component.tracklist()).toEqual([]);
    expect(component.totalTracks()).toBe(0);
    expect(consoleSpy).toHaveBeenCalled();

    consoleSpy.mockRestore();
  });
});
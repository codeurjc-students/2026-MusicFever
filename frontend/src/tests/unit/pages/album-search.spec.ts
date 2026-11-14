
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { vi } from 'vitest';
import { of, Subject, throwError } from 'rxjs';

import { AlbumPage } from '../../../app/pages/search/album/album';
import { AlbumService } from '../../../app/services/album.services';
import { AlbumDisplay } from '../../../app/model/album.model';
import { Track, TrackPage } from '../../../app/model/track.model';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('AlbumPage', () => {
  let fixture: ComponentFixture<AlbumPage>;
  let component: AlbumPage;

  // Test album to display
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

  // Test track
  const track: Track = {
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
  };

  // Test track page
  const trackPage: TrackPage = {
    content: [track],
    totalElements: 12,
    totalPages: 2,
    number: 0,
    size: 10
  };

  // AlbumServiceMock
  const albumServiceMock = {
    getAlbum: vi.fn(),
    getAlbumTracks: vi.fn()
  };

  // RouterMock
  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    albumServiceMock.getAlbum.mockReturnValue(of(album));
    albumServiceMock.getAlbumTracks.mockReturnValue(of(trackPage));

    await TestBed.configureTestingModule({
      imports: [AlbumPage],
      providers: [
        provideNoopAnimations(),
        {
          provide: AlbumService,
          useValue: albumServiceMock
        },
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
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumPage);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should load the album and tracks on initialization', () => {
    expect(albumServiceMock.getAlbum)
      .toHaveBeenCalledWith(1);

    expect(albumServiceMock.getAlbumTracks)
      .toHaveBeenCalledWith(1, 0, 10);

    expect(component.album()).toEqual(album);
    expect(component.tracklist()).toEqual([track]);
    expect(component.totalTracks()).toBe(12);
    expect(component.loading()).toBe(false);
  });

  it('should display the album information', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('h3').textContent)
      .toContain('Test album');

    expect(element.querySelector('img').getAttribute('src'))
      .toBe('test-album.jpg');

    expect(element.textContent).toContain('Test artist');
    expect(element.textContent).toContain('2026-01-01');
    expect(element.textContent).toContain('12 tracks');
    expect(element.querySelector('#tab-name').textContent)
      .toContain('Pop');
  });

  it('should display the album tracklist', () => {
    const element = fixture.nativeElement;

    expect(element.querySelectorAll('app-track-card').length)
      .toBe(1);

    expect(element.textContent).toContain('Test song');
    expect(component.totalTracks()).toBe(12);
  });

  it('should display loading while fetching the album', () => {
    const response$ = new Subject<AlbumDisplay>();

    albumServiceMock.getAlbum.mockReturnValue(response$.asObservable());

    const newFixture = TestBed.createComponent(AlbumPage);
    const newComponent = newFixture.componentInstance;

    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(true);
    expect(newFixture.nativeElement.querySelector('mat-spinner'))
      .not.toBeNull();

    response$.next(album);
    response$.complete();
    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(false);
    expect(newComponent.album()).toEqual(album);
  });

  it('should display not found when loading the album fails', () => {
    const error = new Error('Album not found');
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    albumServiceMock.getAlbum.mockReturnValue(
      throwError(() => error)
    );

    const newFixture = TestBed.createComponent(AlbumPage);
    const newComponent = newFixture.componentInstance;

    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(false);
    expect(newComponent.album()).toBeNull();

    expect(newFixture.nativeElement.textContent)
      .toContain('Album No Encontrado (prov)');

    expect(consoleSpy).toHaveBeenCalledWith(error);

    consoleSpy.mockRestore();
  });

  it('should not display the genre when it is null', () => {
    albumServiceMock.getAlbum.mockReturnValue(
      of({ ...album, genre: null })
    );

    const newFixture = TestBed.createComponent(AlbumPage);
    newFixture.detectChanges();

    const genreElements = newFixture.nativeElement
        .querySelectorAll('.container > .row-display #tab-name');

    expect(genreElements.length).toBe(0);
  });

  it('should update track pagination', () => {
    const event: PageEvent = {
      pageIndex: 1,
      pageSize: 5,
      length: 12
    };

    component.onPageChange(event);

    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(5);

    expect(albumServiceMock.getAlbumTracks)
      .toHaveBeenCalledWith(1, 1, 5);

    expect(albumServiceMock.getAlbumTracks)
      .toHaveBeenCalledTimes(2);
  });

  it('should navigate to the artist page when clicked', () => {
    const link = fixture.nativeElement.querySelector(
      'a'
    );

    link.click();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/artist', 2]);

    expect(routerMock.navigate)
      .toHaveBeenCalledTimes(1);
  });

  it('should handle errors when loading tracks', () => {
    const error = new Error('Tracks not found');
    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    albumServiceMock.getAlbumTracks.mockReturnValue(
      throwError(() => error)
    );

    component.loadTracks();

    expect(consoleSpy).toHaveBeenCalledWith(error);
    expect(component.tracklist()).toEqual([track]);

    consoleSpy.mockRestore();
  });

  it('should not load tracks when album loading fails', () => {
    albumServiceMock.getAlbum.mockReturnValue(
      throwError(() => new Error('Album not found'))
    );

    const consoleSpy = vi.spyOn(console, 'error')
      .mockImplementation(() => {});

    const newFixture = TestBed.createComponent(AlbumPage);
    newFixture.detectChanges();

    // Only the first component loaded tracks successfully
    expect(albumServiceMock.getAlbumTracks)
      .toHaveBeenCalledTimes(1);

    consoleSpy.mockRestore();
  });
});
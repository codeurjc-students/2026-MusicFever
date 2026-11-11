
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { vi } from 'vitest';
import { of, Subject, throwError } from 'rxjs';

import { ArtistPage } from '../../../app/pages/search/artist/artist';
import { ArtistService } from '../../../app/services/artist.services';
import { ArtistDisplay } from '../../../app/model/artist.model';
import { Album, AlbumPage } from '../../../app/model/album.model';
import { Track, TrackPage } from '../../../app/model/track.model';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ArtistPage', () => {
  let fixture: ComponentFixture<ArtistPage>;
  let component: ArtistPage;

  // Test artist to display
  const artist: ArtistDisplay = {
    id: 1,
    name: 'Test artist',
    bigImage: {
      width: 300,
      height: 300,
      url: 'test-artist.jpg'
    },
    genres: ['Pop', 'Rock'],
    listeners: 1000
  };

  // Test album
  const album: Album = {
    id: 2,
    name: 'Test album',
    artists: [{
      id: 1,
      name: 'Test artist',
      smallImage: {
        width: 100,
        height: 100,
        url: 'test-artist-small.jpg'
      }
    }],
    totalTracks: 12,
    releaseDate: '2026-01-01',
    smallImage: {
      width: 200,
      height: 200,
      url: 'test-album.jpg'
    }
  };

  // Test track
  const track: Track = {
    name: 'Test song',
    number: 1,
    explicit: false,
    album
  };

  // Test album page
  const albumPage: AlbumPage = {
    content: [album],
    totalElements: 15,
    totalPages: 2,
    number: 0,
    size: 10
  };

  // Test track page
  const trackPage: TrackPage = {
    content: [track],
    totalElements: 20,
    totalPages: 2,
    number: 0,
    size: 10
  };

  // ArtistServiceMock
  const artistServiceMock = {
    getArtist: vi.fn(),
    getArtistAlbums: vi.fn(),
    getArtistTopTracks: vi.fn()
  };

  // RouterMock for child cards
  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    artistServiceMock.getArtist.mockReturnValue(of(artist));
    artistServiceMock.getArtistAlbums.mockReturnValue(of(albumPage));
    artistServiceMock.getArtistTopTracks.mockReturnValue(of(trackPage));

    await TestBed.configureTestingModule({
      imports: [ArtistPage],
      providers: [
        provideNoopAnimations(),
        {
          provide: ArtistService,
          useValue: artistServiceMock
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

    fixture = TestBed.createComponent(ArtistPage);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should load the artist and albums on initialization', () => {
    expect(artistServiceMock.getArtist)
      .toHaveBeenCalledWith('1');

    expect(artistServiceMock.getArtistAlbums)
      .toHaveBeenCalledWith('1', 0, 10);

    expect(component.artist()).toEqual(artist);
    expect(component.albums()).toEqual([album]);
    expect(component.loading()).toBe(false);
  });

  it('should display the artist information', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('h3').textContent)
      .toContain('Test artist');

    expect(element.querySelector('img').getAttribute('src'))
      .toBe('test-artist.jpg');

    expect(element.textContent).toContain('Listeners: 1000');
    expect(element.textContent).toContain('Pop');
    expect(element.textContent).toContain('Rock');
  });

  it('should display the artist albums', () => {
    const element = fixture.nativeElement;

    expect(element.querySelectorAll('app-album-card').length)
      .toBe(1);

    expect(element.textContent).toContain('Test album');

    expect(component.totalAlbums()).toBe(15);
  });

  it('should display loading while fetching the artist', () => {
    const response$ = new Subject<ArtistDisplay>();

    artistServiceMock.getArtist.mockReturnValue(response$.asObservable());

    const newFixture = TestBed.createComponent(ArtistPage);
    const newComponent = newFixture.componentInstance;

    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(true);
    expect(newFixture.nativeElement.querySelector('mat-spinner'))
      .not.toBeNull();

    response$.next(artist);
    response$.complete();
    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(false);
    expect(newComponent.artist()).toEqual(artist);
  });

  it('should display not found when loading the artist fails', () => {
    artistServiceMock.getArtist.mockReturnValue(
      throwError(() => new Error('Artist not found'))
    );

    const newFixture = TestBed.createComponent(ArtistPage);
    const newComponent = newFixture.componentInstance;

    newFixture.detectChanges();

    expect(newComponent.loading()).toBe(false);
    expect(newComponent.artist()).toBeNull();

    expect(newFixture.nativeElement.textContent)
      .toContain('Artista No Encontrado (prov)');
  });

  it('should load popular tracks when switching to the second tab', () => {
    component.onTabChange(1);
    fixture.detectChanges();

    expect(artistServiceMock.getArtistTopTracks)
      .toHaveBeenCalledWith('1', 0, 10);

    expect(component.popularTracks()).toEqual([track]);
    expect(component.totalTracks()).toBe(20);
    expect(component.popularTracksLoaded()).toBe(true);
  });

  it('should not reload popular tracks when switching back to the tab', () => {
    component.onTabChange(1);
    component.onTabChange(0);
    component.onTabChange(1);

    expect(artistServiceMock.getArtistTopTracks)
      .toHaveBeenCalledTimes(1);
  });

  it('should reload albums when the albums list is empty', () => {
    component.albums.set([]);

    component.onTabChange(0);

    expect(artistServiceMock.getArtistAlbums)
      .toHaveBeenCalledTimes(2);
  });

  it('should not reload albums when they are already loaded', () => {
    component.onTabChange(0);

    expect(artistServiceMock.getArtistAlbums)
      .toHaveBeenCalledTimes(1);
  });

  it('should update album pagination', () => {
    const event: PageEvent = {
      pageIndex: 1,
      pageSize: 5,
      length: 15
    };

    component.onPageChange(event);

    expect(component.pageSize).toBe(5);
    expect(component.pageIndex).toBe(1);

    expect(artistServiceMock.getArtistAlbums)
      .toHaveBeenCalledWith('1', 1, 5);
  });

  it('should update popular tracks pagination', () => {
    const event: PageEvent = {
      pageIndex: 1,
      pageSize: 5,
      length: 20
    };

    component.onTracksPageChange(event);

    expect(component.tracksPageSize).toBe(5);
    expect(component.tracksPageIndex).toBe(1);

    expect(artistServiceMock.getArtistTopTracks)
      .toHaveBeenCalledWith('1', 1, 5);
  });

  it('should not load popular tracks before selecting their tab', () => {
    expect(artistServiceMock.getArtistTopTracks)
      .not.toHaveBeenCalled();
  });

  it('should not load albums or tracks without an artist ID', () => {
    const route = TestBed.inject(ActivatedRoute);

    vi.spyOn(route.snapshot.paramMap, 'get')
      .mockReturnValue(null);

    component.loadAlbums();
    component.loadPopularTracks();

    expect(artistServiceMock.getArtistAlbums)
      .toHaveBeenCalledTimes(1);

    expect(artistServiceMock.getArtistTopTracks)
      .not.toHaveBeenCalled();
  });
});

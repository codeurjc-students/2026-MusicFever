
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';

import { AlbumCard } from '../../../app/components/cards/album-card/album-card'
import { Album } from '../../../app/model/album.model';

describe('AlbumCard', () => {
  let fixture: ComponentFixture<AlbumCard>;
  let component: AlbumCard;

  // Test album to display
  const album: Album = {
    id: 1,
    name: 'Test album',
    artists: [
      {
        id: 1,
        name: 'Test artist 1',
        smallImage: {
          width: 100,
          height: 100,
          url: 'test-artist-1.jpg'
        }
      },
      {
        id: 2,
        name: 'Test artist 2',
        smallImage: {
          width: 100,
          height: 100,
          url: 'test-artist-2.jpg'
        }
      }
    ],
    totalTracks: 12,
    releaseDate: '2026-01-01',
    smallImage: {
      width: 200,
      height: 200,
      url: 'test-album.jpg'
    }
  };

  // RouterMock
  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [AlbumCard],
      providers: [{
        provide: Router,
        useValue: routerMock
      }]
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumCard);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('album', album);
    fixture.detectChanges();
  });

  it('should display the album information', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('#main-name').textContent)
      .toContain('Test album');

    expect(element.querySelector('img').getAttribute('src'))
      .toBe('test-album.jpg');

    expect(element.textContent.replace(/\s+/g, ' '))
      .toContain('Test artist 1, Test artist 2');

    expect(element.textContent)
      .toContain('2026-01-01');

    expect(element.textContent)
      .toContain('12 tracks');

    expect(element.querySelector('#tab-name').textContent)
      .toContain('Album');
  });

  it('should display the default release date when missing', () => {
    fixture.componentRef.setInput('album', {
      ...album,
      releaseDate: null
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent)
      .toContain('XX-XX-XXXX');
  });

  it('should navigate to the album page when clicked', () => {
    const button = fixture.nativeElement.querySelector('button');

    button.click();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/album', 1]);

    expect(routerMock.navigate)
      .toHaveBeenCalledTimes(1);
  });
});

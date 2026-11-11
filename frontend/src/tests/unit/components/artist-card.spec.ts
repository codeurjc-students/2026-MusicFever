
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';

import { ArtistCard } from '../../../app/components/cards/artist-card/artist-card';
import { Artist } from '../../../app/model/artist.model'

describe('ArtistCard', () => {
  let fixture: ComponentFixture<ArtistCard>;
  let component: ArtistCard;

  // Test artist to display
  const artist: Artist = {
    id: 1,
    name: 'Test artist',
    smallImage: {
      width: 100,
      height: 100,
      url: 'test-image.jpg'
    }
  };

  // RouterMock
  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [ArtistCard],
      providers: [{
        provide: Router,
        useValue: routerMock
      }]
    }).compileComponents();

    fixture = TestBed.createComponent(ArtistCard);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('artist', artist);
    fixture.detectChanges();
  });

  it('should display the artist information', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('#main-name').textContent)
      .toContain('Test artist');

    expect(element.querySelector('img').getAttribute('src'))
      .toBe('test-image.jpg');

    expect(element.querySelector('#tab-name').textContent)
      .toContain('Artist');
  });

  it('should navigate to the artist page when clicked', () => {
    const button = fixture.nativeElement.querySelector('button');

    button.click();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/artist', 1]);

    expect(routerMock.navigate)
      .toHaveBeenCalledTimes(1);
  });
});

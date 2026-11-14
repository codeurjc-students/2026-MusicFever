
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';

import { TrackCard } from '../../../app/components/cards/track-card/track-card'
import { Track } from '../../../app/model/track.model';

describe('TrackCard', () => {
  let fixture: ComponentFixture<TrackCard>;
  let component: TrackCard;

  // Test track to display
  const track: Track = {
    name: 'Test song',
    number: 1,
    explicit: false,
    album: {
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
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackCard]
    }).compileComponents();

    fixture = TestBed.createComponent(TrackCard);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('track', track);
    fixture.detectChanges();
  });

  it('should display the track information', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('#main-name').textContent)
      .toContain('1. Test song');

    expect(element.querySelector('img').getAttribute('src'))
      .toBe('test-album.jpg');

    expect(element.textContent)
      .toContain('Test album');

    expect(element.textContent.replace(/\s+/g, ' '))
      .toContain('Test artist 1, Test artist 2');

    expect(element.querySelector('#tab-name').textContent)
      .toContain('Track');
  });

  it('should call play when the play button is clicked', () => {
    const spy = vi.spyOn(component, 'play');
    const button = fixture.nativeElement.querySelectorAll('button')[0];

    button.click();

    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('should call playNext when the play next button is clicked', () => {
    const spy = vi.spyOn(component, 'playNext');
    const button = fixture.nativeElement.querySelectorAll('button')[1];

    button.click();

    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('should call onQueue when the queue button is clicked', () => {
    const spy = vi.spyOn(component, 'onQueue');
    const button = fixture.nativeElement.querySelectorAll('button')[2];

    button.click();

    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('should call addToPlaylist when the playlist button is clicked', () => {
    const spy = vi.spyOn(component, 'addToPlaylist');
    const button = fixture.nativeElement.querySelectorAll('button')[3];

    button.click();

    expect(spy).toHaveBeenCalledTimes(1);
  });
});

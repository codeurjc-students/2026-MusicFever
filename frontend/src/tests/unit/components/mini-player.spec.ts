import { ComponentFixture, TestBed } from '@angular/core/testing';

import { describe, it, expect, beforeEach } from 'vitest';

import { MiniPlayer } from '../../../app/components/mini-player/mini-player';

describe('MiniPlayer', () => {
  let fixture: ComponentFixture<MiniPlayer>;
  let component: MiniPlayer;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiniPlayer]
    }).compileComponents();

    fixture = TestBed.createComponent(MiniPlayer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the album image', () => {
    const image = fixture.nativeElement.querySelector('#album-photo');

    expect(image).not.toBeNull();
    expect(image.getAttribute('src')).toBe('/image_not_found.png');
  });

  it('should display the song name', () => {
    const songName = fixture.nativeElement.querySelector('#song-name');

    expect(songName.textContent).toBe('Song Name');
  });

  it('should display the artist name', () => {
    const artistName = fixture.nativeElement.querySelector('#artist-name');

    expect(artistName.textContent).toBe('Artist Name');
  });

  it('should be paused by default', () => {
    expect(component.isPlaying).toBe(false);

    const playIcon = fixture.nativeElement.querySelector('#actions-layout mat-icon');

    expect(playIcon.getAttribute('fonticon')).toBe('play_arrow');
  });

  it('should toggle playback state when togglePlay is called', () => {
    component.togglePlay();

    expect(component.isPlaying).toBe(true);

    component.togglePlay();

    expect(component.isPlaying).toBe(false);
  });

  it('should change the icon to pause when the play button is clicked', () => {
    const playIcon = fixture.nativeElement.querySelector('#actions-layout mat-icon');

    playIcon.click();
    fixture.detectChanges();

    expect(component.isPlaying).toBe(true);
    expect(playIcon.getAttribute('fonticon')).toBe('pause');
  });

  it('should change the icon back to play when clicked again', () => {
    const playIcon = fixture.nativeElement.querySelector('#actions-layout mat-icon');

    playIcon.click();
    fixture.detectChanges();

    playIcon.click();
    fixture.detectChanges();

    expect(component.isPlaying).toBe(false);
    expect(playIcon.getAttribute('fonticon')).toBe('play_arrow');
  });

  it('should display the skip next icon', () => {
    const icons = fixture.nativeElement.querySelectorAll('#actions-layout mat-icon');

    expect(icons.length).toBe(2);
    expect(icons[1].getAttribute('fonticon')).toBe('skip_next');
  });
});
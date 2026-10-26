
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Info } from '../../../app/pages/info/info';

describe('Info', () => {
  let fixture: ComponentFixture<Info>;
  let component: Info;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Info],
      providers: [
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Info);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the rooms banner', () => {
    const banner = fixture.nativeElement.querySelector('.banner');

    expect(banner).not.toBeNull();
    expect(banner.textContent).toContain('Rooms');
    expect(banner.textContent).toContain('Your party. Your queue. Everyone gets a say.');
  });

  it('should display the room information', () => {
    const info = fixture.nativeElement.querySelector('#info-layout');

    expect(info).not.toBeNull();
    expect(info.textContent).toContain('What is a Room?');
    expect(info.textContent).toContain('Rooms are private');
    expect(info.textContent).toContain('Everyone contributes to the queue');
    expect(info.textContent).toContain('No one takes over');
    expect(info.textContent).toContain('Playback stays with the host');
    expect(info.textContent).toContain('One room at a time');
  });

  it('should display all five room features', () => {
    const features = fixture.nativeElement.querySelectorAll(
      '#info-layout .info-strip'
    );

    expect(features.length).toBe(5);
  });

  it('should display the five steps', () => {
    const steps = fixture.nativeElement.querySelectorAll(
      '#step-1, #step-2, #step-3, #step-4, #step-5'
    );

    expect(steps.length).toBe(5);
  });

  it('should display the Spotify Premium requirements', () => {
    const requirements = fixture.nativeElement.querySelector('#requirements');

    expect(requirements).not.toBeNull();
    expect(requirements.textContent).toContain('Spotify Premium required');
    expect(requirements.textContent).toContain('linked Spotify Premium account');
  });

  it('should display the FAQ and contact links', () => {
    const requirements = fixture.nativeElement.querySelector('#requirements');
    const links = requirements.querySelectorAll('a');

    expect(links.length).toBe(2);
    expect(links[0].textContent).toContain('FAQ');
    expect(links[1].textContent).toContain('contact us');
  });

  it('should display the three use cases', () => {
    const party = fixture.nativeElement.querySelector('#party');
    const dinner = fixture.nativeElement.querySelector('#dinner');
    const getTogether = fixture.nativeElement.querySelector('#get-together');

    expect(party).not.toBeNull();
    expect(dinner).not.toBeNull();
    expect(getTogether).not.toBeNull();
  });

  it('should display the additional features', () => {
    const search = fixture.nativeElement.querySelector('#search');
    const play = fixture.nativeElement.querySelector('#play');
    const playlist = fixture.nativeElement.querySelector('#playlist');
    const stats = fixture.nativeElement.querySelector('#stats');

    expect(search).not.toBeNull();
    expect(play).not.toBeNull();
    expect(playlist).not.toBeNull();
    expect(stats).not.toBeNull();
  });

  it('should display the create and join room buttons', () => {
    const createButton = fixture.nativeElement.querySelector('#create-room');
    const joinButton = fixture.nativeElement.querySelector('#join-room');

    expect(createButton).not.toBeNull();
    expect(joinButton).not.toBeNull();
    expect(createButton.textContent).toContain('Create Room');
    expect(joinButton.textContent).toContain('Join with Code');
  });

  it('should navigate to rooms when clicking Create Room', () => {
    const button = fixture.nativeElement.querySelector('#create-room');

    button.click();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should navigate to rooms when clicking Join with Code', () => {
    const button = fixture.nativeElement.querySelector('#join-room');

    button.click();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should navigate to rooms when calling createRoom', () => {
    component.createRoom();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should navigate to rooms when calling joinRoom', () => {
    component.joinRoom();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });
});

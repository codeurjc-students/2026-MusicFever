import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { of } from 'rxjs';

import { App } from '../../app/app';
import { TrackService } from '../../app/services/track.services';
import { Track } from '../../app/model/track.model';

describe('App', () => {
  let fixture: ComponentFixture<App>;
  let component: App;

  // Test tracks to display
  const tracks: Track[] = [{
      name: 'Test song 1',
      artistName: 'Test artist 1',
      number: 1,
      explicit: false
    },
    {
      name: 'Test song 2',
      artistName: 'Test artist 2',
      number: 2,
      explicit: true
    }];

  // TrackServiceMock
  const trackServiceMock = {
    getTracks: vi.fn().mockReturnValue(of(tracks))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [{
        provide: TrackService,
        useValue: trackServiceMock
      }]
    }).compileComponents();

    fixture = TestBed.createComponent(App);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should display the list of tracks', () => {
    const elements = fixture.nativeElement.querySelectorAll('app-track');
    expect(elements.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('Test song 1');
    expect(fixture.nativeElement.textContent).toContain('Test song 2');
    expect(trackServiceMock.getTracks).toHaveBeenCalledTimes(1);
  });
});
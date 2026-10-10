import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { TrackList } from '../../app/track/track-list/track-list';
import { Track } from '../../app/model/track.model';
import { firstValueFrom } from 'rxjs';
import { TrackService } from '../../app/services/track.services';

describe('App integration test', () => {

  let fixture: ComponentFixture<TrackList>;
  let expectedTracks: Track[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackList],
      providers: [
        provideHttpClient()
      ]
    }).compileComponents();

    const trackService = TestBed.inject(TrackService);
    try {
      expectedTracks = await firstValueFrom(trackService.getTracks());
    } catch (error: any) {
      console.error('HTTP STATUS:', error.status);
      console.error('HTTP ERROR:', error.error);
      console.error('FULL ERROR:', error);
      throw error;
    }
    fixture = TestBed.createComponent(TrackList);
  });

  it('should display tracks obtained from the real API', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const trackElements = fixture.nativeElement.querySelectorAll('app-track');
    expect(trackElements.length).toBe(expectedTracks.length);

    const pageText = fixture.nativeElement.textContent;
    for (let i: number = 0; i < expectedTracks.length; i++){
      expect(pageText).toContain(expectedTracks[i].name);
    }
  });
});
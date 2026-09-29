import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { App } from '../../app/app';
import { Track } from '../../app/model/track.model';
import { firstValueFrom } from 'rxjs';
import { TrackService } from '../../app/services/track.services';

describe('App integration test', () => {

  let fixture: ComponentFixture<App>;
  let expectedTracks: Track[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient()
      ]
    }).compileComponents();

    const trackService = TestBed.inject(TrackService);
    expectedTracks = await firstValueFrom(trackService.getTracks());
    fixture = TestBed.createComponent(App);
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
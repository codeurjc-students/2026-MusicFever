
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  provideHttpClient,
  withInterceptors,
  HttpResponse,
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { Router } from '@angular/router';

import { vi } from 'vitest';
import { firstValueFrom, from, of, switchMap } from 'rxjs';

import { TrackList } from '../../app/track/track-list/track-list';
import { Track } from '../../app/model/track.model';
import { TrackService } from '../../app/services/track.services';
import { AuthService } from '../../app/services/auth.services';

describe('App integration test', () => {
  let fixture: ComponentFixture<TrackList>;
  let expectedTracks: Track[];

  // Cookies returned by the real API
  const cookies = new Map<string, string>();

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
  };

  // Preserve authentication cookies between real HTTP requests
  const authInterceptor: HttpInterceptorFn = (req, next) => {
    if (!/\/api\/v1\/auth\/(login|me|logout)$/.test(req.url)) {
      return next(req);
    }

    const headers: Record<string, string> = {};

    if (req.body !== null) {
      headers['Content-Type'] = 'application/json';
    }

    if (cookies.size > 0) {
      headers['Cookie'] = Array.from(cookies)
        .map(([name, value]) => `${name}=${value}`)
        .join('; ');
    }

    const request = fetch(req.url, {
      method: req.method,
      headers,
      body: req.body !== null
        ? JSON.stringify(req.body)
        : undefined
    });

    return from(request).pipe(
      switchMap(async response => {
        // Store or remove cookies sent by Spring Boot
        for (const header of response.headers.getSetCookie()) {
          const firstPart = header.split(';')[0];
          const separator = firstPart.indexOf('=');

          if (separator === -1) continue;

          const name = firstPart.substring(0, separator);
          const value = firstPart.substring(separator + 1);

          if (value === '' || /max-age=0/i.test(header)) {
            cookies.delete(name);
          } else {
            cookies.set(name, value);
          }
        }

        const text = await response.text();
        const body = text ? JSON.parse(text) : null;

        if (!response.ok) {
          throw new HttpErrorResponse({
            error: body,
            status: response.status,
            statusText: response.statusText,
            url: req.url
          });
        }

        return new HttpResponse({
          body,
          status: response.status,
          url: req.url
        });
      })
    );
  };

  beforeEach(async () => {
    cookies.clear();
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [TrackList],
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    const trackService = TestBed.inject(TrackService);

    expectedTracks = await firstValueFrom(
      trackService.getTracks()
    );

    fixture = TestBed.createComponent(TrackList);
  });

  it('should display tracks obtained from the real API', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const trackElements =
      fixture.nativeElement.querySelectorAll('app-track');

    expect(trackElements.length).toBe(expectedTracks.length);

    const pageText = fixture.nativeElement.textContent;

    for (let i = 0; i < expectedTracks.length; i++) {
      expect(pageText).toContain(expectedTracks[i].name);
    }
  });

  it('should logout using the real API', async () => {
    const authService = TestBed.inject(AuthService);

    // Authenticate using the real API
    const loginResponse = await firstValueFrom(
      authService.login(
        null,
        'armin@gmail.com',
        'eras1325'
      )
    );

    expect(loginResponse.status).toBe('SUCCESS');
    expect(cookies.has('AuthToken')).toBe(true);

    // Verify that the session is authenticated
    const user = await firstValueFrom(
      authService.getCurrentUser()
    );

    expect(user.username).toBe('armiiin');

    // Initialize the authenticated user
    authService.setCurrentUser(user);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(authService.currentUser()).not.toBeNull();

    // Check the authenticated UI
    const logoutLink =
      fixture.nativeElement.querySelector('a');

    expect(logoutLink).not.toBeNull();
    expect(logoutLink.textContent).toContain('Logout');

    // Logout through the DOM
    logoutLink.click();

    // Wait for the real logout request to finish
    await vi.waitFor(() => {
      fixture.detectChanges();

      expect(routerMock.navigate)
        .toHaveBeenCalledWith(['/']);
    });

    fixture.detectChanges();

    // Verify the component state and DOM
    expect(authService.currentUser()).toBeNull();

    expect(fixture.nativeElement.textContent)
      .toContain('Welcome to Music Fever!');

    expect(fixture.nativeElement.querySelector('a'))
      .toBeNull();

    // Verify that the API rejects the expired session
    await expect(
      firstValueFrom(authService.getCurrentUser())
    ).rejects.toMatchObject({
      status: 401
    });
  });
});

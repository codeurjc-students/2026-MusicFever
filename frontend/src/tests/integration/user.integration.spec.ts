import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  provideHttpClient,
  withInterceptors,
  HttpInterceptorFn,
  HttpResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { firstValueFrom, from, filter, take, timeout } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Profile } from '../../app/pages/profile/profile';
import { AuthService } from '../../app/services/auth.services';

describe('Profile integration test', () => {
  let authService: AuthService;

  const authApiUrl = 'https://localhost:8443/api/v1/auth';
  const profileUrl = 'https://localhost:8443/api/v1/me/profile';

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
  };

  // Cookie used only by the integration test
  let authCookie: string | null = null;

  // Forward the authenticated profile request to the real API
  const authInterceptor: HttpInterceptorFn = (req, next) => {
    if (req.method === 'GET' && req.url === profileUrl && authCookie) {
      const cookie = authCookie;

      return from(
        (async () => {
          const response = await fetch(req.url, {
            method: 'GET',
            headers: {
              Cookie: cookie
            }
          });

          if (!response.ok) {
            throw new Error(`GET /me/profile failed: ${response.status}`);
          }

          return new HttpResponse({
            body: await response.json(),
            status: response.status,
            url: req.url
          });
        })()
      );
    }

    return next(req);
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    authCookie = null;

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService);
  });

  it('should load and display the user profile using the real API', async () => {
    // Register a unique user through the real AuthService
    const uniqueId = crypto.randomUUID().replace(/-/g, '').slice(0, 20);
    const newUser = {
      username: `test_${uniqueId}`,
      email: `test_${uniqueId}@example.com`,
      password: 'TestPassword123!'
    };

    const registration = await firstValueFrom(
      authService.register(
        newUser.username,
        newUser.email,
        newUser.password,
        newUser.password
      )
    );

    expect(registration.status).toBe('SUCCESS');

    // Login using Node.js fetch to capture the authentication cookie
    const loginResponse = await fetch(`${authApiUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: null,
        email: newUser.email,
        password: newUser.password
      })
    });

    expect(loginResponse.ok).toBe(true);
    expect((await loginResponse.json()).status).toBe('SUCCESS');

    const tokenCookie = loginResponse.headers.getSetCookie().find(cookie =>
      cookie.startsWith('AuthToken=')
    );

    expect(tokenCookie).toBeDefined();
    authCookie = tokenCookie!.split(';')[0];

    // Observe the profile before ngOnInit requests it through UserService
    const fixture: ComponentFixture<Profile> = TestBed.createComponent(Profile);

    try {
      const profileObservable = TestBed.runInInjectionContext(() =>
        toObservable(fixture.componentInstance.userProfile)
      );

      const profilePromise = firstValueFrom(
        profileObservable.pipe(
          filter(profile => profile !== null),
          take(1),
          timeout(5000)
        )
      );

      fixture.detectChanges();

      const profile = await profilePromise;

      expect(profile?.username).toBe(newUser.username);
      expect(profile?.email).toBe(newUser.email);
      expect(profile?.accountCreatedDate).toBeTruthy();

      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();

      // Verify that the data returned by the API is rendered in the DOM
      const element: HTMLElement = fixture.nativeElement;
      const fields = element.querySelectorAll('.profile-info dd');

      expect(fields).toHaveLength(3);
      expect(fields[0].textContent?.trim()).toBe(newUser.username);
      expect(fields[1].textContent?.trim()).toBe(newUser.email);
      expect(fields[2].textContent?.trim()).toBe(profile?.accountCreatedDate);

      expect(element.querySelector('#profile-picture')?.getAttribute('src'))
        .toBe(profile?.profilePicture?.url ?? '/image_not_found.png');
    } finally {
      fixture.destroy();
    }
  }, 15_000);
});
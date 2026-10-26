
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  provideHttpClient,
  withInterceptors,
  HttpInterceptorFn,
  HttpResponse
} from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom, of, from, filter, take, timeout } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Login } from '../../app/auth/login/login';
import { Home } from '../../app/pages/home/home';
import { AuthService } from '../../app/services/auth.services';

describe('Login integration test', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;
  let authService: AuthService;

  const apiUrl = 'https://localhost:8443/api/v1/auth';

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
  };

  // Existing user in the real database
  const credentials = {
    username: 'armiiin',
    email: 'armin@gmail.com',
    password: 'eras1325'
  };

  // Cookie used only by the integration test
  let authCookie: string | null = null;

  // Forward authenticated requests to the real API
  const authInterceptor: HttpInterceptorFn = (req, next) => {
    if (req.url === `${apiUrl}/me` && authCookie) {
      return from(
        (async () => {
          const response = await fetch(req.url, {
            method: 'GET',
            headers: {
              Cookie: authCookie
            }
          });

          if (!response.ok) {
            throw new Error(`GET /me failed: ${response.status}`);
          }

          const user = await response.json();

          return new HttpResponse({
            body: user,
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
      imports: [Login, Home],
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {},
            queryParams: of({}),
            params: of({}),
            fragment: of(null),
            data: of({})
          }
        }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService);

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should login with email using the real API', async () => {
    // First, verify the credentials against the real API
    const response = await firstValueFrom(
      authService.login(null, credentials.email, credentials.password)
    );

    expect(response.status).toBe('SUCCESS');

    // Enter credentials through the DOM
    const email = fixture.nativeElement.querySelector('input[type="email"]');
    const password = fixture.nativeElement.querySelector('input[type="password"]');

    email.value = credentials.email;
    email.dispatchEvent(new Event('input', { bubbles: true }));

    password.value = credentials.password;
    password.dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    // Submit the real form
    const form = fixture.nativeElement.querySelector('#login-form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toBe('');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
    expect(fixture.nativeElement.querySelector('.login-error')).toBeNull();
  });

  it('should login with username using the real API', async () => {
    // Switch to username mode
    fixture.nativeElement.querySelector('.toggle').click();
    fixture.detectChanges();

    const username = fixture.nativeElement.querySelector(
      'input[autocomplete="username"]'
    );
    const password = fixture.nativeElement.querySelector(
      'input[type="password"]'
    );

    username.value = credentials.username;
    username.dispatchEvent(new Event('input', { bubbles: true }));

    password.value = credentials.password;
    password.dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toBe('');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should display the authenticated user in Home after login', async () => {
    // Login using Node.js fetch to capture the authentication cookie
    const loginResponse = await fetch(`${apiUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: null,
        email: credentials.email,
        password: credentials.password
      })
    });

    expect(loginResponse.ok).toBe(true);

    const response = await loginResponse.json();

    expect(response.status).toBe('SUCCESS');

    // Extract AuthToken from the Set-Cookie headers
    const cookies = loginResponse.headers.getSetCookie();

    const tokenCookie = cookies.find(cookie =>
      cookie.startsWith('AuthToken=')
    );

    expect(tokenCookie).toBeDefined();

    authCookie = tokenCookie!.split(';')[0];

    // Verify that the authenticated user can be retrieved
    const user = await firstValueFrom(authService.getCurrentUser());

    expect(user).not.toBeNull();
    expect(user.username).toBe(credentials.username);

    // Create Home after authentication
    const homeFixture = TestBed.createComponent(Home);

    try {
      // Observe changes to the authenticated user
      const userObservable = TestBed.runInInjectionContext(() =>
        toObservable(authService.currentUser)
      );

      const userPromise = firstValueFrom(
        userObservable.pipe(
          filter(user => user?.username === credentials.username),
          take(1),
          timeout(5000)
        )
      );

      // Initialize Home and retrieve the current user
      homeFixture.detectChanges();

      const homeUser = await userPromise;

      homeFixture.detectChanges();

      // Verify the authenticated user
      expect(homeUser).not.toBeNull();
      expect(homeUser?.username).toBe(credentials.username);
      expect(authService.currentUser()?.username).toBe(credentials.username);

      // Verify the welcome message in the DOM
      const title = homeFixture.nativeElement.querySelector('#title');

      expect(title).not.toBeNull();
      expect(title.textContent).toContain('Welcome back,');
      expect(title.textContent).toContain(credentials.username);
    } finally {
      homeFixture.destroy();
    }
  });
});

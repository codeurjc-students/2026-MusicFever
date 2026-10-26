
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  provideHttpClient,
  withInterceptors,
  HttpInterceptorFn,
  HttpResponse
} from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { toObservable } from '@angular/core/rxjs-interop';
import { firstValueFrom, of, from, filter, take, timeout } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Register } from '../../app/auth/register/register';
import { Home } from '../../app/pages/home/home';
import { AuthService } from '../../app/services/auth.services';

describe('Register integration test', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;
  let authService: AuthService;

  const apiUrl = 'https://localhost:8443/api/v1/auth';

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
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
      imports: [Register, Home],
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

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should register a new user and display it in Home using the real API', async () => {
    // Generate unique credentials for every execution
    const uniqueId = crypto.randomUUID().replace(/-/g, '').slice(0, 20);

    const newUser = {
      username: `test_${uniqueId}`,
      email: `test_${uniqueId}@example.com`,
      password: 'TestPassword123!',
      confirm_password: 'TestPassword123!'
    };

    // Enter data through the virtual DOM
    const username = fixture.nativeElement.querySelector(
      'input[autocomplete="username"]'
    );
    const email = fixture.nativeElement.querySelector(
      'input[type="email"]'
    );
    const passwords = fixture.nativeElement.querySelectorAll(
      'input[type="password"]'
    );

    username.value = newUser.username;
    username.dispatchEvent(new Event('input', { bubbles: true }));

    email.value = newUser.email;
    email.dispatchEvent(new Event('input', { bubbles: true }));

    passwords[0].value = newUser.password;
    passwords[0].dispatchEvent(new Event('input', { bubbles: true }));

    passwords[1].value = newUser.confirm_password;
    passwords[1].dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    expect(component.registerForm.valid).toBe(true);

    // Submit the form to the real backend
    const form = fixture.nativeElement.querySelector('#register-form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    await fixture.whenStable();
    fixture.detectChanges();

    // Verify successful registration
    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toBe('');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
    expect(fixture.nativeElement.querySelector('.login-error')).toBeNull();

    // Login using Node.js fetch to capture the authentication cookie
    const loginResponse = await fetch(`${apiUrl}/login`, {
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

    const response = await loginResponse.json();

    expect(response.status).toBe('SUCCESS');

    // Extract AuthToken from the Set-Cookie headers
    const cookies = loginResponse.headers.getSetCookie();

    const tokenCookie = cookies.find(cookie =>
      cookie.startsWith('AuthToken=')
    );

    expect(tokenCookie).toBeDefined();

    authCookie = tokenCookie!.split(';')[0];

    // Verify that the new user can be retrieved from the real API
    const user = await firstValueFrom(authService.getCurrentUser());

    expect(user).not.toBeNull();
    expect(user.username).toBe(newUser.username);

    // Create Home after authentication
    const homeFixture = TestBed.createComponent(Home);

    try {
      // Observe the authenticated user before initializing Home
      const userObservable = TestBed.runInInjectionContext(() =>
        toObservable(authService.currentUser)
      );

      const userPromise = firstValueFrom(
        userObservable.pipe(
          filter(user => user?.username === newUser.username),
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
      expect(homeUser?.username).toBe(newUser.username);
      expect(authService.currentUser()?.username).toBe(newUser.username);

      // Verify the welcome message in the DOM
      const title = homeFixture.nativeElement.querySelector('#title');

      expect(title).not.toBeNull();
      expect(title.textContent).toContain('Welcome back,');
      expect(title.textContent).toContain(newUser.username);
    } finally {
      homeFixture.destroy();
    }
  });

  it('should reject registration when passwords do not match', async () => {
    await expect(
      firstValueFrom(
        authService.register(
          'TestUser',
          'test@example.com',
          'TestPassword123!',
          'DifferentPassword123!'
        )
      )
    ).rejects.toThrow('Passwords do not match');
  });
});
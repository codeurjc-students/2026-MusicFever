
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Login } from '../../app/auth/login/login';
import { Home } from '../../app/pages/home/home';
import { AuthService } from '../../app/services/auth.services';

describe('Login integration test', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;
  let authService: AuthService;

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
  };

  // Existing user in the real database
  const credentials = {
    username: 'armiiin',
    email: 'armin@gmail.com',
    password: 'eras1325'
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [Login, Home],
      providers: [
        provideHttpClient(),
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
    // Login using the real API
    const response = await firstValueFrom(
      authService.login(null, credentials.email, credentials.password)
    );

    expect(response.status).toBe('SUCCESS');

    // Retrieve the authenticated user from the real API
    const user = await firstValueFrom(authService.getCurrentUser());

    expect(user).not.toBeNull();
    expect(user.username).toBe(credentials.username);

    // Initialize Home with the authenticated user
    authService.setCurrentUser(user);

    const homeFixture = TestBed.createComponent(Home);

    try {
      homeFixture.detectChanges();

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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Login } from '../../app/auth/login/login';
import { AuthService } from '../../app/services/auth.services';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;

  const authServiceMock = {
    login: vi.fn()
  };

  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    authServiceMock.login.mockReturnValue(of({
      status: 'SUCCESS',
      message: 'Login successful',
      error: null
    }));

    routerMock.navigate.mockResolvedValue(true);

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
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

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display the login form', () => {
    const form = fixture.nativeElement.querySelector('#login-form');
    const email = fixture.nativeElement.querySelector('email-input');
    const password = fixture.nativeElement.querySelector('password-input');
    const button = fixture.nativeElement.querySelector('button[type="submit"]');

    expect(form).not.toBeNull();
    expect(email).not.toBeNull();
    expect(password).not.toBeNull();
    expect(button.textContent).toContain('Sign in');
  });

  it('should switch between email and username login', () => {
    const toggle = fixture.nativeElement.querySelector('.toggle');

    expect(fixture.nativeElement.querySelector('email-input')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('username-input')).toBeNull();

    toggle.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('username-input')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('email-input')).toBeNull();
    expect(toggle.textContent).toContain('Log in with email');

    toggle.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('email-input')).not.toBeNull();
    expect(toggle.textContent).toContain('Log in with username');
  });

  it('should not call the service when the form is invalid', () => {
    const form = fixture.nativeElement.querySelector('#login-form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();

    expect(authServiceMock.login).not.toHaveBeenCalled();
    expect(component.loginForm.controls.email.touched).toBe(true);
    expect(component.loginForm.controls.password.touched).toBe(true);
  });

  it('should login with email and navigate on success', () => {
    const email = fixture.nativeElement.querySelector('input[type="email"]');
    const password = fixture.nativeElement.querySelector('input[type="password"]');

    email.value = 'test@example.com';
    email.dispatchEvent(new Event('input', { bubbles: true }));

    password.value = 'TestPassword123';
    password.dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');
    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();

    expect(authServiceMock.login).toHaveBeenCalledWith(
      null,
      'test@example.com',
      'TestPassword123'
    );

    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
    expect(component.loading()).toBe(false);
  });

  it('should login with username when username mode is selected', () => {
    const toggle = fixture.nativeElement.querySelector('.toggle');

    toggle.click();
    fixture.detectChanges();

    const username = fixture.nativeElement.querySelector('input[autocomplete="username"]');
    const password = fixture.nativeElement.querySelector('input[type="password"]');

    username.value = 'TestUser';
    username.dispatchEvent(new Event('input', { bubbles: true }));

    password.value = 'TestPassword123';
    password.dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');
    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    expect(authServiceMock.login).toHaveBeenCalledWith(
      'TestUser',
      null,
      'TestPassword123'
    );
  });

  it('should display an error when login fails', () => {
    authServiceMock.login.mockReturnValue(of({
      status: 'FAILURE',
      message: 'Login failed',
      error: 'Invalid credentials'
    }));

    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'WrongPassword'
    });

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');
    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();

    const error = fixture.nativeElement.querySelector('.login-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain('Invalid email or password');
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should display the server error message', () => {
    authServiceMock.login.mockReturnValue(
      throwError(() => ({
        error: { message: 'Server unavailable' }
      }))
    );

    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'TestPassword123'
    });

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');
    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();

    const error = fixture.nativeElement.querySelector('.login-error');

    expect(error.textContent).toContain('Server unavailable');
    expect(component.loading()).toBe(false);
  });

  it('should disable the submit button while loading', () => {
    const response$ = new Subject<any>();
    authServiceMock.login.mockReturnValue(response$.asObservable());

    component.loginForm.patchValue({
      email: 'test@example.com',
      password: 'TestPassword123'
    });

    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('#login-form');
    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button[type="submit"]');

    expect(component.loading()).toBe(true);
    expect(button.disabled).toBe(true);

    response$.next({
      status: 'SUCCESS',
      message: 'Login successful',
      error: null
    });
    response$.complete();

    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(button.disabled).toBe(false);
  });

  it('should clear the error message when switching login mode', () => {
    component.errorMessage.set('Previous error');
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.login-error')).not.toBeNull();

    fixture.nativeElement.querySelector('.toggle').click();
    fixture.detectChanges();

    expect(component.errorMessage()).toBe('');
    expect(fixture.nativeElement.querySelector('.login-error')).toBeNull();
  });
});

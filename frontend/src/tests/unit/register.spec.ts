import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Register } from '../../app/auth/register/register';
import { AuthService } from '../../app/services/auth.services';

describe('Register', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;

  const authServiceMock = {
    register: vi.fn()
  };

  const routerMock = {
    navigate: vi.fn()
  };

  const validUser = {
    username: 'TestUser',
    email: 'test@example.com',
    password: 'TestPassword123',
    confirm_password: 'TestPassword123'
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    authServiceMock.register.mockReturnValue(of({
      status: 'SUCCESS',
      message: 'Registration successful',
      error: null
    }));

    routerMock.navigate.mockResolvedValue(true);

    await TestBed.configureTestingModule({
      imports: [Register],
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

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function submitForm() {
    const form = fixture.nativeElement.querySelector('#register-form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    fixture.detectChanges();
  }

  it('should display the registration form', () => {
    const form = fixture.nativeElement.querySelector('#register-form');
    const username = fixture.nativeElement.querySelector('username-input');
    const email = fixture.nativeElement.querySelector('email-input');
    const passwords = fixture.nativeElement.querySelectorAll('password-input');
    const button = fixture.nativeElement.querySelector('button[type="submit"]');

    expect(form).not.toBeNull();
    expect(username).not.toBeNull();
    expect(email).not.toBeNull();
    expect(passwords.length).toBe(2);
    expect(button.textContent).toContain('Create Account');
  });

  it('should update the form controls when the user enters data', () => {
    const username = fixture.nativeElement.querySelector(
      'input[autocomplete="username"]'
    );
    const email = fixture.nativeElement.querySelector('input[type="email"]');
    const passwords = fixture.nativeElement.querySelectorAll(
      'input[type="password"]'
    );

    username.value = validUser.username;
    username.dispatchEvent(new Event('input', { bubbles: true }));

    email.value = validUser.email;
    email.dispatchEvent(new Event('input', { bubbles: true }));

    passwords[0].value = validUser.password;
    passwords[0].dispatchEvent(new Event('input', { bubbles: true }));

    passwords[1].value = validUser.confirm_password;
    passwords[1].dispatchEvent(new Event('input', { bubbles: true }));

    fixture.detectChanges();

    expect(component.registerForm.getRawValue()).toEqual(validUser);
    expect(component.registerForm.valid).toBe(true);
  });

  it('should not call the service when the form is empty', () => {
    submitForm();

    expect(authServiceMock.register).not.toHaveBeenCalled();
    expect(component.registerForm.invalid).toBe(true);
    expect(component.registerForm.controls.username.touched).toBe(true);
    expect(component.registerForm.controls.email.touched).toBe(true);
    expect(component.registerForm.controls.password.touched).toBe(true);
    expect(component.registerForm.controls.confirm_password.touched).toBe(true);
  });

  it('should not register with an invalid username', () => {
    component.registerForm.patchValue({
      ...validUser,
      username: 'ab'
    });

    submitForm();

    expect(component.registerForm.controls.username.hasError('minlength'))
      .toBe(true);
    expect(authServiceMock.register).not.toHaveBeenCalled();
  });

  it('should not register with an invalid email', () => {
    component.registerForm.patchValue({
      ...validUser,
      email: 'invalid-email'
    });

    submitForm();

    expect(component.registerForm.controls.email.hasError('email')).toBe(true);
    expect(authServiceMock.register).not.toHaveBeenCalled();
  });

  it('should register and navigate on success', () => {
    component.registerForm.patchValue(validUser);
    fixture.detectChanges();

    submitForm();

    expect(authServiceMock.register).toHaveBeenCalledWith(
      'TestUser',
      'test@example.com',
      'TestPassword123',
      'TestPassword123'
    );

    expect(authServiceMock.register).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
    expect(component.loading()).toBe(false);
  });

  it('should display an error when registration fails', () => {
    authServiceMock.register.mockReturnValue(of({
      status: 'FAILURE',
      message: 'Registration failed',
      error: 'Invalid credentials'
    }));

    component.registerForm.patchValue(validUser);
    fixture.detectChanges();

    submitForm();

    const error = fixture.nativeElement.querySelector('.login-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain(
      'Invalid username, email or password'
    );
    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(component.loading()).toBe(false);
  });

  it('should display the server error message', () => {
    authServiceMock.register.mockReturnValue(
      throwError(() => ({
        error: { message: 'Username already exists' }
      }))
    );

    component.registerForm.patchValue(validUser);
    fixture.detectChanges();

    submitForm();

    const error = fixture.nativeElement.querySelector('.login-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain('Username already exists');
    expect(component.loading()).toBe(false);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should display an error when passwords do not match', () => {
    authServiceMock.register.mockReturnValue(
      throwError(() => new Error('Passwords do not match'))
    );

    component.registerForm.patchValue({
      ...validUser,
      confirm_password: 'DifferentPassword'
    });

    fixture.detectChanges();
    submitForm();

    const error = fixture.nativeElement.querySelector('.login-error');

    expect(authServiceMock.register).toHaveBeenCalledWith(
      'TestUser',
      'test@example.com',
      'TestPassword123',
      'DifferentPassword'
    );

    expect(error).not.toBeNull();
    expect(error.textContent).toContain('Passwords do not match');
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should disable the submit button while loading', () => {
    const response$ = new Subject<any>();
    authServiceMock.register.mockReturnValue(response$.asObservable());

    component.registerForm.patchValue(validUser);
    fixture.detectChanges();

    submitForm();

    const button = fixture.nativeElement.querySelector('button[type="submit"]');

    expect(component.loading()).toBe(true);
    expect(button.disabled).toBe(true);

    response$.next({
      status: 'SUCCESS',
      message: 'Registration successful',
      error: null
    });
    response$.complete();

    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(button.disabled).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should clear previous errors when submitting again', () => {
    component.errorMessage.set('Previous error');
    component.registerForm.patchValue(validUser);

    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.login-error')).not.toBeNull();

    submitForm();

    expect(component.errorMessage()).toBe('');
    expect(fixture.nativeElement.querySelector('.login-error')).toBeNull();
  });
});

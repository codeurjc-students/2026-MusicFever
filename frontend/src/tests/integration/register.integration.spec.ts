import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Register } from '../../app/auth/register/register';
import { AuthService } from '../../app/services/auth.services';

describe('Register integration test', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;
  let authService: AuthService;

  const routerMock = {
    navigate: vi.fn().mockResolvedValue(true)
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [Register],
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

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should register a new user using the real API', async () => {
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

    // Verify that the new user can authenticate through the real API
    const response = await firstValueFrom(
      authService.login(null, newUser.email, newUser.password)
    );

    expect(response.status).toBe('SUCCESS');
  });
});

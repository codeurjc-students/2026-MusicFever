import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, Validators } from '@angular/forms';
import { describe, it, expect, beforeEach } from 'vitest';

import { PasswordInput } from '../../../app/auth/components/password-input/password-input';

describe('PasswordInput', () => {
  let fixture: ComponentFixture<PasswordInput>;
  let component: PasswordInput;
  let passwordControl: FormControl<string>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswordInput]
    }).compileComponents();

    fixture = TestBed.createComponent(PasswordInput);
    component = fixture.componentInstance;

    passwordControl = new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    });

    fixture.componentRef.setInput('passwordFormControl', passwordControl);
    fixture.detectChanges();
  });

  it('should display the password input', () => {
    const input = fixture.nativeElement.querySelector('input');

    expect(input).not.toBeNull();
    expect(input.placeholder).toBe('Enter your password');
    expect(input.type).toBe('password');
  });

  it('should update the form control when the user enters a password', () => {
    const input = fixture.nativeElement.querySelector('input');

    input.value = 'TestPassword123';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(passwordControl.value).toBe('TestPassword123');
    expect(passwordControl.valid).toBe(true);
  });

  it('should display an error when the password is required', async () => {
    passwordControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent.replace(/\s+/g, ' ').trim())
      .toBe('Password is required');
  });

  it('should not display errors when the password is valid', async () => {
    passwordControl.setValue('TestPassword123');
    passwordControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).toBeNull();
  });

  it('should toggle password visibility when clicking the button', () => {
    const input = fixture.nativeElement.querySelector('input');
    const button = fixture.nativeElement.querySelector('button');

    expect(input.type).toBe('password');
    expect(button.getAttribute('aria-label')).toBe('Show password');

    button.click();
    fixture.detectChanges();

    expect(input.type).toBe('text');
    expect(button.getAttribute('aria-label')).toBe('Hide password');

    button.click();
    fixture.detectChanges();

    expect(input.type).toBe('password');
    expect(button.getAttribute('aria-label')).toBe('Show password');
  });

  it('should display the custom label', () => {
    fixture.componentRef.setInput('label', 'Confirm password');
    fixture.detectChanges();

    const label = fixture.nativeElement.querySelector('mat-label');

    expect(label.textContent).toContain('Confirm password');
  });
});

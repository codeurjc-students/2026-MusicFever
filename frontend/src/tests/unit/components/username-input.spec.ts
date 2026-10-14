import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, Validators } from '@angular/forms';
import { describe, it, expect, beforeEach } from 'vitest';

import { UsernameInput } from '../../../app/auth/components/username-input/username-input';

describe('UsernameInput', () => {
  let fixture: ComponentFixture<UsernameInput>;
  let component: UsernameInput;
  let usernameControl: FormControl<string>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsernameInput]
    }).compileComponents();

    fixture = TestBed.createComponent(UsernameInput);
    component = fixture.componentInstance;

    usernameControl = new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30)
      ]
    });

    fixture.componentRef.setInput('usernameFormControl', usernameControl);
    fixture.detectChanges();
  });

  it('should display the username input', () => {
    const input = fixture.nativeElement.querySelector('input');

    expect(input).not.toBeNull();
    expect(input.placeholder).toBe('Enter username');
    expect(input.getAttribute('autocomplete')).toBe('username');
  });

  it('should update the form control when the user enters a username', () => {
    const input = fixture.nativeElement.querySelector('input');

    input.value = 'TestUser';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(usernameControl.value).toBe('TestUser');
    expect(usernameControl.valid).toBe(true);
  });

  it('should display an error when the username is required', async () => {
    usernameControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent.replace(/\s+/g, ' ').trim())
      .toBe('Username is required');
  });

  it('should display an error when the username has fewer than 3 characters', async () => {
    usernameControl.setValue('ab');
    usernameControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain(
      'Username must have at least 3 characters'
    );
  });

  it('should display an error when the username exceeds 30 characters', async () => {
    usernameControl.setValue('a'.repeat(31));
    usernameControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain(
      'Username cannot exceed 30 characters'
    );
  });

  it('should not display errors when the username is valid', async () => {
    usernameControl.setValue('TestUser');
    usernameControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const errors = fixture.nativeElement.querySelectorAll('mat-error');

    expect(errors.length).toBe(0);
  });

  it('should accept a username with exactly 3 characters', () => {
    usernameControl.setValue('abc');
    fixture.detectChanges();

    expect(usernameControl.valid).toBe(true);
  });

  it('should accept a username with exactly 30 characters', () => {
    usernameControl.setValue('a'.repeat(30));
    fixture.detectChanges();

    expect(usernameControl.valid).toBe(true);
  });
});

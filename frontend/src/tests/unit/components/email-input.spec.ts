import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, Validators } from '@angular/forms';

import { describe, it, expect, beforeEach } from 'vitest';

import { EmailInput } from '../../../app/auth/components/email-input/email-input';

describe('EmailInput', () => {
  let fixture: ComponentFixture<EmailInput>;
  let component: EmailInput;
  let emailControl: FormControl<string>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailInput]
    }).compileComponents();

    fixture = TestBed.createComponent(EmailInput);
    component = fixture.componentInstance;

    emailControl = new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    });

    fixture.componentRef.setInput('emailFormControl', emailControl);
    fixture.detectChanges();
  });

  it('should display the email input', () => {
    const input = fixture.nativeElement.querySelector('input[type="email"]');

    expect(input).not.toBeNull();
    expect(input.placeholder).toBe('Ex. pat@example.com');
  });

  it('should update the form control when the user enters an email', () => {
    const input = fixture.nativeElement.querySelector('input');

    input.value = 'test@example.com';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(emailControl.value).toBe('test@example.com');
    expect(emailControl.valid).toBe(true);
  });

  it('should display an error when the email is required', async () => {
    emailControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain('Email is required');
  });

  it('should display an error when the email is invalid', async () => {
    emailControl.setValue('invalid-email');
    emailControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).not.toBeNull();
    expect(error.textContent).toContain('Please enter a valid email address');
  });

  it('should not display errors when the email is valid', async () => {
    emailControl.setValue('test@example.com');
    emailControl.markAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();

    const error = fixture.nativeElement.querySelector('mat-error');

    expect(error).toBeNull();
  });
});

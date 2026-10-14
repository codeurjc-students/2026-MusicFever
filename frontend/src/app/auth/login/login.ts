import { Component, signal, inject } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';

import { AuthService } from '../../services/auth.services';
import { EmailInput } from '../components/email-input/email-input';
import { PasswordInput } from '../components/password-input/password-input';
import { UsernameInput } from '../components/username-input/username-input';

@Component({
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
    UsernameInput,
    EmailInput,
    PasswordInput,
    ReactiveFormsModule,
  ],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private readonly router = inject(Router);

  loginForm = new FormGroup({
      username: new FormControl('', {
      nonNullable: true
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  loginWithUsername = signal(false);

  toggleLoginMode() {
    this.loginWithUsername.update(value => !value);

    const usernameControl = this.loginForm.controls.username;
    const emailControl = this.loginForm.controls.email;

    if (this.loginWithUsername()) {
      usernameControl.setValidators([
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30)
      ]);
      emailControl.clearValidators();
    } else {
      emailControl.setValidators([
        Validators.required,
        Validators.email
      ]);
      usernameControl.clearValidators();
    }

    usernameControl.updateValueAndValidity();
    emailControl.updateValueAndValidity();

    this.errorMessage.set('');
  }

  private readonly authService = inject(AuthService);

  loading = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    const { username, email, password } = this.loginForm.getRawValue();

    this.authService.login(this.loginWithUsername() ? username : null, this.loginWithUsername() ? null : email, password).subscribe({
      next: response => {
        this.loading.set(false);

        if (response.status === 'SUCCESS') {
          void this.router.navigate(['/']);
        } else {
          this.errorMessage.set('Invalid email or password');
        }
      },
      error: err => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? err.message);
      }
    });
  }
}
import { Component, signal, inject } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, Router } from '@angular/router';
import { FormsModule, FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';

import { AuthService } from '../../services/auth.services';
import { EmailInput } from '../components/email-input/email-input';
import { PasswordInput } from '../components/password-input/password-input';

@Component({
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
    EmailInput,
    PasswordInput,
    ReactiveFormsModule,
  ],
  selector: 'app-login',
  styleUrl: './register.css',
  templateUrl: './register.html',
})
export class Register {
  private router = inject(Router);

  registerForm = new FormGroup({
    username: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30)
      ]
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),
    confirm_password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  private authService = inject(AuthService);

  loading = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    const { username, email, password, confirm_password } = this.registerForm.getRawValue();

    this.authService.register(username, email, password, confirm_password).subscribe({
      next: response => {
        this.loading.set(false);

        if (response.status === 'SUCCESS') {
          this.router.navigate(['/']);
        } else {
          this.errorMessage.set('Invalid username, email or password');
        }
      },
      error: err => {
        this.loading.set(false);
        this.errorMessage.set(err.message);
      }
    });
  }
}
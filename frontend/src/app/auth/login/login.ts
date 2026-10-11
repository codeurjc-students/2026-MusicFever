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

@Component({
  imports: [
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
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private router = inject(Router);

  loginForm = new FormGroup({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  private authService = inject(AuthService);

  loading = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    const { email, password } = this.loginForm.getRawValue();

    this.authService.login(email, password).subscribe({
      next: response => {
        this.loading.set(false);

        if (response.status === 'SUCCESS') {
          this.router.navigate(['/']);
        } else {
          this.errorMessage.set('Invalid email or password');
        }
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Invalid email or password');
      }
    });
  }
}

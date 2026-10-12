import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { ActiveUser } from '../model/activeuser.model'
import { throwError } from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

interface RegisterRequest{
  username: string,
  email: string,
  password: string,
}

interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message: string;
  error: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'https://localhost:8443/api/v1/auth';

  private _currentUser = signal<ActiveUser | null>(null);
  currentUser = this._currentUser.asReadonly();

  router = inject(Router);

  login(email: string, password: string) {
    const credentials: LoginRequest = { email, password };

    return this.http.post<AuthResponse>(
      `${this.apiUrl}/login`,
      credentials,
      { withCredentials: true }
    );
  }

  getCurrentUser() {
    return this.http.get<ActiveUser>(
      `${this.apiUrl}/me`,
      { withCredentials: true }
    );
  }

  setCurrentUser(user: ActiveUser | null) {
    this._currentUser.set(user);
  }

  register(username: string, email: string, password: string, confirm_password: string) {
    if (password !== confirm_password) {
      return throwError(() => new Error('Passwords do not match'));
    }

    const credentials: RegisterRequest = { username, email, password };
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/register`,
      credentials,
      { withCredentials: true }
    )
  }

  logout(){
    this.http.post<AuthResponse>(
      `${this.apiUrl}/logout`,
      {},
      { withCredentials: true }
    ).subscribe({
    next: () => {
      this._currentUser.set(null);
      this.router.navigate(['/']);
    }
    })
  }
}
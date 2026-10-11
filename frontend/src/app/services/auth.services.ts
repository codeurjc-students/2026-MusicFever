import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ActiveUser } from '../model/activeuser.model'

interface LoginRequest {
  email: string;
  password: string;
}

interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message: string;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);
  private apiUrl = 'https://localhost:8443/api/v1/auth';

  private _currentUser = signal<ActiveUser | null>(null);
  currentUser = this._currentUser.asReadonly();

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
}
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UserDisplay } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'https://localhost:8443/api/v1/me';

  getProfile(): Observable<UserDisplay> {
    return this.http.get<UserDisplay>(`${this.apiUrl}/profile`, {
      withCredentials: true,
    });
  }
}
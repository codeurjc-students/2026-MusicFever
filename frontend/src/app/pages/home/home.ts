import { Component, inject } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';

import { AuthService } from '../../services/auth.services';

@Component({
  imports: [MatIconModule, MatButtonModule],
  selector: 'app-home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {
  authService = inject(AuthService);

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe({
      next: user => {
        this.authService.setCurrentUser(user);
      },
      error: () => {
        this.authService.setCurrentUser(null);
      }
    });
  }
}

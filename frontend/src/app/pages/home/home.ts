import { Component, inject } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.services';

@Component({
  imports: [MatIconModule, MatButtonModule],
  selector: 'app-home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {
  authService = inject(AuthService);
  private router = inject(Router);

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

  player(): void {
    this.router.navigate(['/player']);
  }

  stats(): void {
    this.router.navigate(['/stats']);
  }

  rooms(): void {
    this.router.navigate(['/rooms']);
  }

  createRoom(): void {
    this.router.navigate(['/rooms']);
  }

  joinRoom(): void {
    this.router.navigate(['/rooms']);
  }
}

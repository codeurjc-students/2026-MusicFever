import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import {MatSlideToggle} from '@angular/material/slide-toggle';

import { MenuItem } from '../../components/menu-item/menu-item';
import { AuthService } from '../../services/auth.services';

@Component({
  selector: 'app-side-header',
  standalone: true,
  imports: [MenuItem, MatSlideToggle],
  templateUrl: './side-header.html',
  styleUrl: './side-header.css'
})
export class SideHeader {
  private router = inject(Router);
  authService = inject(AuthService);

  login() {
    this.router.navigate(['/login']);
  }

  register() {
    this.router.navigate(['/register']);
  }

  howItWorks() {
    // Pending implementation
  }

  contactUs() {
    // Pending implementation
  }

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
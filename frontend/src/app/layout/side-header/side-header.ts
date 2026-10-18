import { Component, inject } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';

import {MatSlideToggle} from '@angular/material/slide-toggle';

import { MenuItem } from '../../components/menu-item/menu-item';
import { AuthService } from '../../services/auth.services';
import { SearchService } from '../../services/search.services';

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
  searchService = inject(SearchService);
  readonly encodeURIComponent = encodeURIComponent;

  isSearchPage = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => this.router.url.split('?')[0] === '/search'),
      startWith(this.router.url.split('?')[0] === '/search')
    ),
    { initialValue: false }
  );

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
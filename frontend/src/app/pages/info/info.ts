import { Component, inject } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  imports: [MatIconModule, MatButtonModule],
  selector: 'app-info',
  styleUrl: './info.css',
  templateUrl: './info.html',
})
export class Info {
  private router = inject(Router);

  createRoom(): void {
    this.router.navigate(['/rooms']);
  }

  joinRoom(): void {
    this.router.navigate(['/rooms']);
  }
}

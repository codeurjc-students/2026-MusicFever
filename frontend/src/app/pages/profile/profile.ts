import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../services/user.services';
import { UserDisplay } from '../../model/user.model';

@Component({
  imports: [],
  selector: 'app-profile',
  styleUrl: './profile.css',
  templateUrl: './profile.html',
})
export class Profile {
  private readonly userService = inject(UserService);
  readonly userProfile = signal<UserDisplay | null>(null);

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => this.userProfile.set(profile),
    });
  }
}

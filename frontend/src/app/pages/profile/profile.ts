import { Component, inject, signal } from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { FormsModule } from '@angular/forms';

import { UserService } from '../../services/user.services';
import { UserDisplay } from '../../model/user.model';
import { AuthService } from '../../services/auth.services';

@Component({
  imports: [MatTabsModule, MatIconModule, MatButtonModule, FormsModule],
  selector: 'app-profile',
  styleUrl: './profile.css',
  templateUrl: './profile.html',
})
export class Profile {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  readonly userProfile = signal<UserDisplay | null>(null);

  isEditing = false;

  draftImage = signal<string | null>(null);
  draftEmail = signal<string | null>(null);

  startEditing() {
    this.draftEmail.set(this.userProfile()?.email ?? null);
    this.isEditing = true;
  }

  discard(){
    this.isEditing = false;
    this.draftImage.set(null);
  }

  saveChanges() {
    const user = this.userProfile();
    if (!user) return;

    const changes: { email?: string; image?: string } = {};
    const email = this.draftEmail()?.trim();
    const image = this.draftImage();

    if (email != null && email !== user.email) changes.email = email;
    if (image !== null) changes.image = image;

    if (Object.keys(changes).length === 0) {
      this.discard();
      return;
    }

    this.userService.updateProfile(changes).subscribe({
      next: (updatedUser) => {
        this.userProfile.set(updatedUser);

        const activeUser = this.authService.currentUser();
        if (activeUser && updatedUser.profilePicture) {
          this.authService.setCurrentUser({
            ...activeUser,
            profilePicture: updatedUser.profilePicture,
          });
        }

        this.discard();
      },
      error: (error) => {
        console.error('Profile could not be saved', error);
      },
    });
  }

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => this.userProfile.set(profile),
    });
  }

  onPhotoSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => this.draftImage.set(reader.result as string);
    reader.readAsDataURL(file);
  }

  logout(){
    this.authService.logout();
  }
}

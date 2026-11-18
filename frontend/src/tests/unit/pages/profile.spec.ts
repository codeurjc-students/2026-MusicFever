import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Profile } from '../../../app/pages/profile/profile';
import { ActiveUser, UserDisplay } from '../../../app/model/user.model';
import { AuthService } from '../../../app/services/auth.services';
import { UserService } from '../../../app/services/user.services';

describe('Profile', () => {
  let fixture: ComponentFixture<Profile>;
  let component: Profile;

  // Test user to display
  const user: UserDisplay = {
    username: 'Test user',
    profilePicture: {
      width: 100,
      height: 100,
      url: 'test-user.jpg'
    },
    email: 'test@example.com',
    accountCreatedDate: '2026-01-01',
    musicProvider: 'SPOTIFY'
  };

  const activeUser: ActiveUser = {
    username: user.username,
    profilePicture: user.profilePicture
  };

  // UserServiceMock
  const userServiceMock = {
    getProfile: vi.fn(),
    updateProfile: vi.fn()
  };

  // AuthServiceMock
  const authServiceMock = {
    currentUser: vi.fn(),
    setCurrentUser: vi.fn(),
    logout: vi.fn()
  };

  // RouterMock
  const routerMock = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    userServiceMock.getProfile.mockReturnValue(of(user));
    authServiceMock.currentUser.mockReturnValue(activeUser);

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock
        },
        {
          provide: AuthService,
          useValue: authServiceMock
        },
        {
          provide: Router,
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should load and display the user profile', () => {
    const element = fixture.nativeElement;

    expect(userServiceMock.getProfile)
      .toHaveBeenCalledTimes(1);

    expect(element.querySelector('.profile-info').textContent)
      .toContain('Test user');

    expect(element.querySelector('.profile-info').textContent)
      .toContain('test@example.com');

    expect(element.querySelector('.profile-info').textContent)
      .toContain('2026-01-01');

    expect(element.querySelector('#profile-picture').getAttribute('src'))
      .toBe('test-user.jpg');
  });

  it('should display Spotify as connected', () => {
    const account = fixture.nativeElement.querySelector('.app-container');

    expect(account.textContent)
      .toContain('Spotify');

    expect(account.textContent)
      .toContain('Connected');
  });

  it('should display a message when no music account is connected', () => {
    component.userProfile.set({
      ...user,
      musicProvider: 'NONE'
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.app-container').textContent)
      .toContain('No music account connected');
  });

  it('should enter edit mode with the current email', async () => {
    const element = fixture.nativeElement;

    element.querySelector('.profile-container .purple-btn').click();
    fixture.detectChanges();
    await fixture.whenStable();

    expect(element.querySelector('.profile-container h4').textContent)
      .toContain('Edit Profile');

    expect(element.querySelector('input[type="email"]').value)
      .toBe('test@example.com');

    expect(element.querySelector('.photo-btn').textContent)
      .toContain('Change photo');
  });

  it('should preview the selected photo without saving it', async () => {
    const element = fixture.nativeElement;
    const file = new File(['test'], 'test-photo.png', { type: 'image/png' });

    element.querySelector('.profile-container .purple-btn').click();
    fixture.detectChanges();

    const photoInput: HTMLInputElement = element.querySelector('input[type="file"]');

    Object.defineProperty(photoInput, 'files', {
      value: [file]
    });
    photoInput.dispatchEvent(new Event('change'));

    await vi.waitFor(() => {
      fixture.detectChanges();

      expect(element.querySelector('#profile-picture').getAttribute('src'))
        .toBe('data:image/png;base64,dGVzdA==');
    });

    expect(userServiceMock.updateProfile)
      .not.toHaveBeenCalled();
  });

  it('should discard changes and restore the original profile picture', () => {
    const element = fixture.nativeElement;

    element.querySelector('.profile-container .purple-btn').click();
    component.draftEmail.set('changed@example.com');
    component.draftImage.set('data:image/png;base64,dGVzdA==');
    fixture.detectChanges();

    element.querySelector('.discard-btn').click();
    fixture.detectChanges();

    expect(element.querySelector('input[type="email"]'))
      .toBeNull();

    expect(element.querySelector('.profile-info').textContent)
      .toContain('test@example.com');

    expect(element.querySelector('#profile-picture').getAttribute('src'))
      .toBe('test-user.jpg');

    expect(userServiceMock.updateProfile)
      .not.toHaveBeenCalled();
  });

  it('should exit edit mode without updating the profile when nothing changed', () => {
    const element = fixture.nativeElement;

    element.querySelector('.profile-container .purple-btn').click();
    fixture.detectChanges();

    element.querySelector('.save-btn').click();
    fixture.detectChanges();

    expect(userServiceMock.updateProfile)
      .not.toHaveBeenCalled();

    expect(element.querySelector('.profile-container h4').textContent)
      .toContain('Your Profile');
  });

  it('should save the new email and photo and update the active user', async () => {
    const element = fixture.nativeElement;
    const draftImage = 'data:image/png;base64,dGVzdA==';
    const updatedUser: UserDisplay = {
      ...user,
      email: 'changed@example.com',
      profilePicture: {
        ...user.profilePicture,
        url: 'updated-user.jpg'
      }
    };

    userServiceMock.updateProfile.mockReturnValue(of(updatedUser));

    element.querySelector('.profile-container .purple-btn').click();
    fixture.detectChanges();
    await fixture.whenStable();

    const emailInput: HTMLInputElement = element.querySelector('input[type="email"]');

    emailInput.value = 'changed@example.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));
    component.draftImage.set(draftImage);
    fixture.detectChanges();

    element.querySelector('.save-btn').click();
    fixture.detectChanges();

    expect(userServiceMock.updateProfile)
      .toHaveBeenCalledWith({
        email: 'changed@example.com',
        image: draftImage
      });

    expect(userServiceMock.updateProfile)
      .toHaveBeenCalledTimes(1);

    expect(authServiceMock.setCurrentUser)
      .toHaveBeenCalledWith({
        ...activeUser,
        profilePicture: updatedUser.profilePicture
      });

    expect(element.querySelector('.profile-info').textContent)
      .toContain('changed@example.com');

    expect(element.querySelector('#profile-picture').getAttribute('src'))
      .toBe('updated-user.jpg');

    expect(element.querySelector('input[type="email"]'))
      .toBeNull();
  });

  it('should keep edit mode open when saving fails', () => {
    const element = fixture.nativeElement;
    const error = new Error('Update failed');
    const consoleErrorMock = vi.spyOn(console, 'error').mockImplementation(() => {});

    userServiceMock.updateProfile.mockReturnValue(throwError(() => error));

    try {
      element.querySelector('.profile-container .purple-btn').click();
      component.draftEmail.set('changed@example.com');
      fixture.detectChanges();

      element.querySelector('.save-btn').click();
      fixture.detectChanges();

      expect(consoleErrorMock)
        .toHaveBeenCalledWith('Profile could not be saved', error);

      expect(element.querySelector('input[type="email"]'))
        .not.toBeNull();

      expect(component.userProfile())
        .toEqual(user);

      expect(authServiceMock.setCurrentUser)
        .not.toHaveBeenCalled();
    } finally {
      consoleErrorMock.mockRestore();
    }
  });

  it('should log out when the logout button is clicked', () => {
    const button = fixture.nativeElement.querySelector('.logout-btn');

    button.click();

    expect(authServiceMock.logout)
      .toHaveBeenCalledTimes(1);
  });

  it('should navigate to the analytics page when clicked', () => {
    const button = fixture.nativeElement.querySelector('.container .purple-btn');

    button.click();

    expect(routerMock.navigate)
      .toHaveBeenCalledWith(['/stats']);

    expect(routerMock.navigate)
      .toHaveBeenCalledTimes(1);
  });
});
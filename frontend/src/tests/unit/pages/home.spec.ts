import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { signal } from '@angular/core';
import { of, throwError } from 'rxjs';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { Home } from '../../../app/pages/home/home';
import { AuthService } from '../../../app/services/auth.services';

describe('Home', () => {
  let fixture: ComponentFixture<Home>;
  let component: Home;
  let router: Router;

  const currentUser = signal<any>(null);

  const authServiceMock = {
    currentUser,
    getCurrentUser: vi.fn(),
    setCurrentUser: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    currentUser.set(null);
    authServiceMock.getCurrentUser.mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the welcome message when user is not authenticated', () => {
    const title = fixture.nativeElement.querySelector('#title');

    expect(title.textContent).toContain('Welcome to');
    expect(title.textContent).toContain('Music Fever!');
  });

  it('should display the username when user is authenticated', () => {
    currentUser.set({ username: 'TestUser' });
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('#title');

    expect(title.textContent).toContain('Welcome back,');
    expect(title.textContent).toContain('TestUser!');
  });

  it('should display the main actions', () => {
    const actions = fixture.nativeElement.querySelector('#actions-layout');

    expect(actions).not.toBeNull();
    expect(actions.textContent).toContain('Rooms');
    expect(actions.textContent).toContain('Search');
    expect(actions.textContent).toContain('Player');
    expect(actions.textContent).toContain('Stats');
  });

  it('should display the rooms section', () => {
    const rooms = fixture.nativeElement.querySelector('#info-layout');

    expect(rooms).not.toBeNull();
    expect(rooms.textContent).toContain('Create Room');
    expect(rooms.textContent).toContain('Join with Code');
  });

  it('should navigate to rooms when clicking Go to Rooms', () => {
    const button = fixture.nativeElement.querySelector('#main-action-button');

    button.click();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should navigate to player when clicking Open Player', () => {
    const buttons = fixture.nativeElement.querySelectorAll('#action-button');

    buttons[1].click();

    expect(router.navigate).toHaveBeenCalledWith(['/player']);
  });

  it('should navigate to stats when clicking View Stats', () => {
    const buttons = fixture.nativeElement.querySelectorAll('#action-button');

    buttons[2].click();

    expect(router.navigate).toHaveBeenCalledWith(['/stats']);
  });

  it('should navigate to rooms when clicking Create Room', () => {
    const button = fixture.nativeElement.querySelector('#create-room');

    button.click();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should navigate to rooms when clicking Join with Code', () => {
    const button = fixture.nativeElement.querySelector('#join-room');

    button.click();

    expect(router.navigate).toHaveBeenCalledWith(['/rooms']);
  });

  it('should get and set the current user on initialization', () => {
    expect(authServiceMock.getCurrentUser).toHaveBeenCalled();
    expect(authServiceMock.setCurrentUser).toHaveBeenCalledWith(null);
  });

  it('should set the current user when authentication succeeds', () => {
    const user = { username: 'TestUser' };

    authServiceMock.getCurrentUser.mockReturnValue(of(user));

    component.ngOnInit();

    expect(authServiceMock.setCurrentUser).toHaveBeenCalledWith(user);
  });

  it('should set the current user to null when authentication fails', () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      throwError(() => new Error('Authentication failed'))
    );

    component.ngOnInit();

    expect(authServiceMock.setCurrentUser).toHaveBeenCalledWith(null);
  });
});

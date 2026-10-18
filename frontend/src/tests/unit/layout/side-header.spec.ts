import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, signal } from '@angular/core';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { SideHeader } from '../../../app/layout/side-header/side-header';
import { AuthService } from '../../../app/services/auth.services';
import { SearchService } from '../../../app/services/search.services';

@Component({
  template: ''
})
class DummyComponent {}

describe('SideHeader', () => {
  let fixture: ComponentFixture<SideHeader>;
  let component: SideHeader;
  let router: Router;
  let authService: {
    currentUser: ReturnType<typeof signal<any>>;
    getCurrentUser: ReturnType<typeof vi.fn>;
    setCurrentUser: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authService = {
      currentUser: signal(null),
      getCurrentUser: vi.fn().mockReturnValue(of(null)),
      setCurrentUser: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [SideHeader],
      providers: [
        provideRouter([
          { path: 'home', component: DummyComponent },
          { path: 'search', component: DummyComponent },
          { path: 'player', component: DummyComponent },
          { path: 'rooms', component: DummyComponent },
          { path: 'stats', component: DummyComponent },
          { path: 'login', component: DummyComponent },
          { path: 'register', component: DummyComponent }
        ]),
        { provide: AuthService, useValue: authService },
        { provide: SearchService, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SideHeader);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the application logo', () => {
    const logo = fixture.nativeElement.querySelector('.logo');

    expect(logo).not.toBeNull();
    expect(logo.getAttribute('src')).toBe('/logo.png');
    expect(logo.getAttribute('alt')).toBe('Music Fever logo');
  });

  it('should display login and register links when the user is not authenticated', () => {
    const authLinks = fixture.nativeElement.querySelector('#auth-links');

    expect(authLinks).not.toBeNull();
    expect(authLinks.textContent).toContain('Log In');
    expect(authLinks.textContent).toContain('Register');
  });

  it('should display the username when the user is authenticated', () => {
    authService.currentUser.set({ username: 'testuser' });
    fixture.detectChanges();

    const username = fixture.nativeElement.querySelector('#username');

    expect(username).not.toBeNull();
    expect(username.textContent.trim()).toBe('testuser');
    expect(fixture.nativeElement.querySelector('#auth-links')).toBeNull();
  });

  it('should display the profile picture', () => {
    const picture = fixture.nativeElement.querySelector('#profile-picture');

    expect(picture).not.toBeNull();
    expect(picture.getAttribute('src')).toBe('/image_not_found.png');
  });

  it('should navigate to login when the login link is clicked', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    const loginLink = fixture.nativeElement.querySelector('#auth-links a:first-child');

    loginLink.click();

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to register when the register link is clicked', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    const registerLink = fixture.nativeElement.querySelector('#auth-links a:last-child');

    registerLink.click();

    expect(navigateSpy).toHaveBeenCalledWith(['/register']);
  });

  it('should display the main navigation items', () => {
    const menuItems = fixture.nativeElement.querySelectorAll('#header-elements app-menu-item');

    expect(menuItems.length).toBe(4);
    expect(fixture.nativeElement.textContent).toContain('Home');
    expect(fixture.nativeElement.textContent).toContain('Player');
    expect(fixture.nativeElement.textContent).toContain('Rooms');
    expect(fixture.nativeElement.textContent).toContain('Stats');
  });

  it('should not display the search menu item by default', () => {
    expect(component.isSearchPage()).toBe(false);

    const menuItems = fixture.nativeElement.querySelectorAll('#header-elements app-menu-item');

    expect(menuItems.length).toBe(4);
  });

  it('should display the search menu item when navigating to the search page', async () => {
    await router.navigateByUrl('/search');
    fixture.detectChanges();

    expect(component.isSearchPage()).toBe(true);

    const menuItems = fixture.nativeElement.querySelectorAll('#header-elements app-menu-item');

    expect(menuItems.length).toBe(5);
    expect(fixture.nativeElement.textContent).toContain('Search');
  });

  it('should display the search menu item when the URL contains query parameters', async () => {
    await router.navigateByUrl('/search?search=Coldplay');
    fixture.detectChanges();

    expect(component.isSearchPage()).toBe(true);

    const menuItems = fixture.nativeElement.querySelectorAll('#header-elements app-menu-item');

    expect(menuItems.length).toBe(5);
  });

  it('should hide the search menu item when leaving the search page', async () => {
    await router.navigateByUrl('/search');
    fixture.detectChanges();

    expect(component.isSearchPage()).toBe(true);

    await router.navigateByUrl('/home');
    fixture.detectChanges();

    expect(component.isSearchPage()).toBe(false);

    const menuItems = fixture.nativeElement.querySelectorAll('#header-elements app-menu-item');

    expect(menuItems.length).toBe(4);
  });

  it('should display the theme toggle', () => {
    const toggle = fixture.nativeElement.querySelector('mat-slide-toggle');

    expect(toggle).not.toBeNull();
  });

  it('should display the How It Works and Contact Us links', () => {
    const howItWorks = fixture.nativeElement.querySelector('#how-it-works-link');
    const contactUs = fixture.nativeElement.querySelector('#contact-us-link');

    expect(howItWorks).not.toBeNull();
    expect(howItWorks.textContent.trim()).toBe('How It Works');

    expect(contactUs).not.toBeNull();
    expect(contactUs.textContent.trim()).toBe('Contact Us');
  });

  it('should call howItWorks when its link is clicked', () => {
    const spy = vi.spyOn(component, 'howItWorks');
    const link = fixture.nativeElement.querySelector('#how-it-works-link');

    link.click();

    expect(spy).toHaveBeenCalledOnce();
  });

  it('should call contactUs when its link is clicked', () => {
    const spy = vi.spyOn(component, 'contactUs');
    const link = fixture.nativeElement.querySelector('#contact-us-link');

    link.click();

    expect(spy).toHaveBeenCalledOnce();
  });

  it('should get the current user on initialization', () => {
    expect(authService.getCurrentUser).toHaveBeenCalledOnce();
  });

  it('should set the current user when authentication succeeds', () => {
    const user = { username: 'testuser' };

    authService.getCurrentUser.mockReturnValue(of(user));
    authService.setCurrentUser.mockClear();

    component.ngOnInit();

    expect(authService.setCurrentUser).toHaveBeenCalledWith(user);
  });

  it('should set the current user to null when authentication fails', () => {
    authService.getCurrentUser.mockReturnValue(
      throwError(() => new Error('Authentication failed'))
    );

    authService.setCurrentUser.mockClear();

    component.ngOnInit();

    expect(authService.setCurrentUser).toHaveBeenCalledWith(null);
  });
});
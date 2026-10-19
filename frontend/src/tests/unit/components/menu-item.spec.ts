import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { provideRouter, Router } from '@angular/router';

import { describe, it, expect, beforeEach } from 'vitest';

import { MenuItem } from '../../../app/components/menu-item/menu-item';

@Component({
  template: ''
})
class DummyComponent {}

describe('MenuItem', () => {
  let fixture: ComponentFixture<MenuItem>;
  let component: MenuItem;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuItem],
      providers: [
        provideRouter([
          { path: 'home', component: DummyComponent },
          { path: 'profile', component: DummyComponent }
        ])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MenuItem);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    fixture.componentRef.setInput('label', 'Home');
    fixture.componentRef.setInput('icon', 'home');
    fixture.componentRef.setInput('route', '/home');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct label', () => {
    const span = fixture.nativeElement.querySelector('span');

    expect(span.textContent).toBe('Home');
  });

  it('should display the correct icon', () => {
    const icon = fixture.nativeElement.querySelector('mat-icon');

    expect(icon).not.toBeNull();
    expect(icon.textContent.trim()).toBe('home');
  });

  it('should have the correct router link', () => {
    const link = fixture.nativeElement.querySelector('a');

    expect(link.getAttribute('href')).toBe('/home');
  });

  it('should update the label when the input changes', () => {
    fixture.componentRef.setInput('label', 'Profile');
    fixture.detectChanges();

    const span = fixture.nativeElement.querySelector('span');

    expect(span.textContent).toBe('Profile');
  });

  it('should update the icon when the input changes', () => {
    fixture.componentRef.setInput('icon', 'person');
    fixture.detectChanges();

    const icon = fixture.nativeElement.querySelector('mat-icon');

    expect(icon.textContent.trim()).toBe('person');
  });

  it('should update the router link when the input changes', () => {
    fixture.componentRef.setInput('route', '/profile');
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('a');

    expect(link.getAttribute('href')).toBe('/profile');
  });

  it('should apply the active class when the route is active', async () => {
    await router.navigateByUrl('/home');
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('a');

    expect(link.classList.contains('active')).toBe(true);
  });

  it('should not apply the active class when the route is inactive', async () => {
    await router.navigateByUrl('/profile');
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('a');

    expect(link.classList.contains('active')).toBe(false);
  });
});
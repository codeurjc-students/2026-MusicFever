import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { describe, it, expect, beforeEach } from 'vitest';

import { Footer } from '../../../app/layout/footer/footer';

describe('Footer', () => {
  let fixture: ComponentFixture<Footer>;
  let component: Footer;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Footer],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(Footer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the application name and copyright', () => {
    const appName = fixture.nativeElement.querySelector('#app-name');

    expect(appName).not.toBeNull();
    expect(appName.textContent).toContain('© 2026 Music Fever');
  });

  it('should display the blog link', () => {
    const link = fixture.nativeElement.querySelector('a[href="https://medium.com/@armingarc"]');

    expect(link).not.toBeNull();
    expect(link.textContent.trim()).toBe('Blog');
  });

  it('should display the correct internal navigation links', () => {
    const links = fixture.nativeElement.querySelectorAll('#center-layout a');

    expect(links.length).toBe(4);

    expect(links[1].textContent.trim()).toBe('FAQ');
    expect(links[1].getAttribute('href')).toBe('/faq');

    expect(links[2].textContent.trim()).toBe('Privacy');
    expect(links[2].getAttribute('href')).toBe('/privacy');

    expect(links[3].textContent.trim()).toBe('Terms');
    expect(links[3].getAttribute('href')).toBe('/terms');
  });

  it('should display the powered by text', () => {
    const text = fixture.nativeElement.querySelector('#apis-text');

    expect(text).not.toBeNull();
    expect(text.textContent).toBe('Powered by:');
  });

  it('should display the Spotify and Apple logos', () => {
    const spotifyLogo = fixture.nativeElement.querySelector('#spotify_logo');
    const appleLogo = fixture.nativeElement.querySelector('#apple_logo');

    expect(spotifyLogo).not.toBeNull();
    expect(appleLogo).not.toBeNull();
  });

  it('should display the GitHub link and icon', () => {
    const link = fixture.nativeElement.querySelector(
      'a[href="https://github.com/codeurjc-students/2026-MusicFever"]'
    );

    expect(link).not.toBeNull();

    const image = link.querySelector('img');

    expect(image).not.toBeNull();
    expect(image.id).toBe('github');
    expect(image.getAttribute('src')).toBe('/github_logo.png');
  });

  it('should display the Instagram link and icon', () => {
    const link = fixture.nativeElement.querySelector(
      'a[href="https://www.instagram.com/musicfever_2026/"]'
    );

    expect(link).not.toBeNull();

    const image = link.querySelector('img');

    expect(image).not.toBeNull();
    expect(image.id).toBe('ig');
    expect(image.getAttribute('src')).toBe('/instagram_logo.png');
  });
});
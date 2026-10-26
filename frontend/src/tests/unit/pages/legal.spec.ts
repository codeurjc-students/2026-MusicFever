import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, it, expect, beforeEach } from 'vitest';

import { Privacy } from '../../../app/pages/legal/privacy/privacy';
import { Terms } from '../../../app/pages/legal/terms/terms';

describe('Privacy', () => {
  let fixture: ComponentFixture<Privacy>;
  let component: Privacy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Privacy]
    }).compileComponents();

    fixture = TestBed.createComponent(Privacy);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the privacy policy title', () => {
    const title = fixture.nativeElement.querySelector('.title-frame h1');

    expect(title.textContent).toContain('Privacy Policy');
  });

  it('should display all privacy policy sections', () => {
    const sections = fixture.nativeElement.querySelectorAll('.section h4');

    expect(sections.length).toBe(18);
  });

  it('should display the privacy contact email', () => {
    expect(fixture.nativeElement.textContent).toContain('privacy@musicfever.com');
  });
});

describe('Terms', () => {
  let fixture: ComponentFixture<Terms>;
  let component: Terms;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Terms]
    }).compileComponents();

    fixture = TestBed.createComponent(Terms);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the terms of service title', () => {
    const title = fixture.nativeElement.querySelector('.title-frame h1');

    expect(title.textContent).toContain('Terms of Service');
  });

  it('should display all terms of service sections', () => {
    const sections = fixture.nativeElement.querySelectorAll('.section h4');

    expect(sections.length).toBe(23);
  });

  it('should display the support contact email', () => {
    expect(fixture.nativeElement.textContent).toContain('support@musicfever.com');
  });
});
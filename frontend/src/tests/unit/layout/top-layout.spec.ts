import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { TopHeader } from '../../../app/layout/top-header/top-header';
import { SearchService } from '../../../app/services/search.services';

describe('TopHeader', () => {
  let fixture: ComponentFixture<TopHeader>;
  let component: TopHeader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopHeader],
      providers: [
        provideRouter([]),
        {
          provide: SearchService,
          useValue: {
            searchQuery: signal('')
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TopHeader);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the mini player', () => {
    const miniPlayer = fixture.nativeElement.querySelector('app-mini-player');

    expect(miniPlayer).not.toBeNull();
  });

  it('should display the search bar', () => {
    const searchBar = fixture.nativeElement.querySelector('app-search-bar');

    expect(searchBar).not.toBeNull();
  });

  it('should display both components inside the container', () => {
    const container = fixture.nativeElement.querySelector('.container');

    expect(container).not.toBeNull();
    expect(container.querySelector('app-mini-player')).not.toBeNull();
    expect(container.querySelector('app-search-bar')).not.toBeNull();
  });
});
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { signal } from '@angular/core';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { SearchBar } from '../../../app/components/search-bar/search-bar';
import { SearchService } from '../../../app/services/search.services';

describe('SearchBar', () => {
  let fixture: ComponentFixture<SearchBar>;
  let component: SearchBar;
  let router: { navigate: ReturnType<typeof vi.fn> };
  let searchService: { searchQuery: ReturnType<typeof signal<string>> };

  beforeEach(async () => {
    router = {
      navigate: vi.fn()
    };

    searchService = {
      searchQuery: signal('')
    };

    await TestBed.configureTestingModule({
      imports: [SearchBar],
      providers: [
        { provide: Router, useValue: router },
        { provide: SearchService, useValue: searchService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchBar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the search input', () => {
    const input = fixture.nativeElement.querySelector('input[type="search"]');

    expect(input).not.toBeNull();
    expect(input.placeholder).toBe('Search artists, albums, songs...');
  });

  it('should have an empty search text by default', () => {
    expect(component.searchText).toBe('');
  });

  it('should update searchText when the user types', async () => {
    const input = fixture.nativeElement.querySelector('input');

    input.value = 'Taylor Swift';
    input.dispatchEvent(new Event('input'));

    await fixture.whenStable();

    expect(component.searchText).toBe('Taylor Swift');
  });

  it('should display the search button', () => {
    const button = fixture.nativeElement.querySelector('button[type="submit"]');

    expect(button).not.toBeNull();
    expect(button.getAttribute('aria-label')).toBe('Search');
    expect(button.querySelector('mat-icon').textContent.trim()).toBe('search');
  });

  it('should update the search service when searching', () => {
    component.searchText = 'Taylor Swift';

    component.search();

    expect(searchService.searchQuery()).toBe('Taylor Swift');
  });

  it('should navigate to the search page with the correct query', () => {
    component.searchText = 'Taylor Swift';

    component.search();

    expect(router.navigate).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Taylor Swift' }
    });
  });

  it('should trim whitespace from the search query', () => {
    component.searchText = '  Taylor Swift  ';

    component.search();

    expect(searchService.searchQuery()).toBe('Taylor Swift');
    expect(router.navigate).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Taylor Swift' }
    });
  });

  it('should not search when the input is empty', () => {
    component.searchText = '';

    component.search();

    expect(searchService.searchQuery()).toBe('');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not search when the input contains only whitespace', () => {
    component.searchText = '   ';

    component.search();

    expect(searchService.searchQuery()).toBe('');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should submit the search when the form is submitted', async () => {
    const input = fixture.nativeElement.querySelector('input');
    const form = fixture.nativeElement.querySelector('form');

    input.value = 'Coldplay';
    input.dispatchEvent(new Event('input'));

    await fixture.whenStable();

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    fixture.detectChanges();

    expect(searchService.searchQuery()).toBe('Coldplay');
    expect(router.navigate).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Coldplay' }
    });
  });
});
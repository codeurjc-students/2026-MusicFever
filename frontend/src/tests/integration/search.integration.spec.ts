import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { describe, it, expect, beforeEach, vi } from 'vitest';

import { SearchService } from '../../app/services/search.services';
import { SearchBar } from '../../app/components/search-bar/search-bar';

describe('SearchService Integration', () => {
  let fixture: ComponentFixture<SearchBar>;
  let component: SearchBar;
  let searchService: SearchService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchBar],
      providers: [
        SearchService,
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchBar);
    component = fixture.componentInstance;
    searchService = TestBed.inject(SearchService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create the search service', () => {
    expect(searchService).toBeTruthy();
  });

  it('should initialize with an empty search query', () => {
    expect(searchService.searchQuery()).toBe('');
  });

  it('should update the search service when submitting a search', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    const input = fixture.nativeElement.querySelector('input');

    input.value = 'Taylor Swift';
    input.dispatchEvent(new Event('input'));

    await fixture.whenStable();

    const form = fixture.nativeElement.querySelector('form');

    form.dispatchEvent(new Event('submit', {
      bubbles: true,
      cancelable: true
    }));

    fixture.detectChanges();

    expect(searchService.searchQuery()).toBe('Taylor Swift');
    expect(navigateSpy).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Taylor Swift' }
    });
  });

  it('should trim the query before updating the service', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.searchText = '  Coldplay  ';
    component.search();

    expect(searchService.searchQuery()).toBe('Coldplay');
    expect(navigateSpy).toHaveBeenCalledWith(['/search'], {
      queryParams: { search: 'Coldplay' }
    });
  });

  it('should not update the service when the search is empty', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');

    component.searchText = '   ';
    component.search();

    expect(searchService.searchQuery()).toBe('');
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should share the search query across service instances', () => {
    const anotherInstance = TestBed.inject(SearchService);

    searchService.searchQuery.set('Arctic Monkeys');

    expect(anotherInstance.searchQuery()).toBe('Arctic Monkeys');
    expect(anotherInstance).toBe(searchService);
  });
});
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatTabGroupHarness } from '@angular/material/tabs/testing';
import { MatPaginatorHarness } from '@angular/material/paginator/testing';

import { ArtistPage } from '../../app/pages/search/artist/artist';
import { ArtistService } from '../../app/services/artist.services';
import { ArtistDisplay } from '../../app/model/artist.model';
import { AlbumPage } from '../../app/model/album.model';
import { TrackPage } from '../../app/model/track.model';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ArtistService Integration', () => {
    let fixture: ComponentFixture<ArtistPage>;
    let component: ArtistPage;
    let artistService: ArtistService;
    let httpMock: HttpTestingController;

    const artist: ArtistDisplay = {
        id: 1,
        name: 'Test artist',
        bigImage: {
            width: 300,
            height: 300,
            url: 'test-artist.jpg'
        },
        genres: ['Pop', 'Rock'],
        listeners: 1000
    };

    const albumPage: AlbumPage = {
        content: [{
            id: 2,
            name: 'Test album',
            artists: [],
            totalTracks: 12,
            releaseDate: '2026-01-01',
            smallImage: {
                width: 200,
                height: 200,
                url: 'test-album.jpg'
            }
        }],
        totalElements: 15,
        totalPages: 2,
        number: 0,
        size: 10
    };

    const trackPage: TrackPage = {
        content: [{
            name: 'Test song',
            number: 1,
            explicit: false,
            album: albumPage.content[0]
        }],
        totalElements: 20,
        totalPages: 2,
        number: 0,
        size: 10
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArtistPage],
            providers: [
                ArtistService,
                provideNoopAnimations(),
                provideHttpClient(),
                provideHttpClientTesting(),
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            paramMap: convertToParamMap({ id: '1' })
                        }
                    }
                },
                {
                    provide: Router,
                    useValue: { navigate: () => Promise.resolve(true) }
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ArtistPage);
        component = fixture.componentInstance;
        artistService = TestBed.inject(ArtistService);
        httpMock = TestBed.inject(HttpTestingController);

        fixture.detectChanges();
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should create the artist service', () => {
        expect(artistService).toBeTruthy();

        const req = httpMock.expectOne(
            'https://localhost:8443/api/v1/artists/1'
        );
        req.flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);
    });

    it('should load and display the artist', () => {
        const req = httpMock.expectOne(
            'https://localhost:8443/api/v1/artists/1'
        );

        expect(req.request.method).toBe('GET');

        req.flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);

        fixture.detectChanges();

        expect(component.artist()).toEqual(artist);
        expect(component.loading()).toBe(false);

        expect(fixture.nativeElement.textContent)
            .toContain('Test artist');

        expect(fixture.nativeElement.textContent)
            .toContain('Listeners: 1000');

        expect(fixture.nativeElement.textContent)
            .toContain('Pop');
    });

    it('should load the artist albums', () => {
        httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        ).flush(artist);

        const req = httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        );

        expect(req.request.method).toBe('GET');
        expect(req.request.params.get('page')).toBe('0');
        expect(req.request.params.get('size')).toBe('10');

        req.flush(albumPage);
        fixture.detectChanges();

        expect(component.albums()).toEqual(albumPage.content);
        expect(component.totalAlbums()).toBe(15);

        expect(fixture.nativeElement.textContent)
            .toContain('Test album');
    });

    it('should load popular tracks when selecting their tab', () => {
        httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        ).flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);

        fixture.detectChanges();

        component.onTabChange(1);

        const req = httpMock.expectOne(
            request => request.url.endsWith('/1/popular-tracks')
        );

        expect(req.request.method).toBe('GET');
        expect(req.request.params.get('page')).toBe('0');
        expect(req.request.params.get('size')).toBe('10');

        req.flush(trackPage);
        fixture.detectChanges();

        expect(component.popularTracks()).toEqual(trackPage.content);
        expect(component.totalTracks()).toBe(20);
        expect(component.popularTracksLoaded()).toBe(true);
    });

    it('should update album pagination', () => {
        httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        ).flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);

        component.onPageChange({
            pageIndex: 1,
            pageSize: 5,
            length: 15
        } as PageEvent);

        const req = httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        );

        expect(req.request.params.get('page')).toBe('1');
        expect(req.request.params.get('size')).toBe('5');

        req.flush({
            ...albumPage,
            number: 1,
            size: 5
        });

        expect(component.pageIndex).toBe(1);
        expect(component.pageSize).toBe(5);
    });

    it('should handle an artist not found error', () => {
        const req = httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        );

        req.flush('Not found', {
            status: 404,
            statusText: 'Not Found'
        });

        fixture.detectChanges();

        expect(component.artist()).toBeNull();
        expect(component.loading()).toBe(false);

        expect(fixture.nativeElement.textContent)
            .toContain('Artista No Encontrado (prov)');

        httpMock.expectNone(
            request => request.url.endsWith('/1/albums')
        );
    });


    it('should load popular tracks when clicking the popular tracks tab', async () => {
        // Load artist and albums
        httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        ).flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);

        fixture.detectChanges();

        // Select the popular tracks tab using Angular Material Harness
        const loader = TestbedHarnessEnvironment.loader(fixture);
        const tabGroup = await loader.getHarness(MatTabGroupHarness);

        const tabs = await tabGroup.getTabs();
        await tabs[1].select();

        // Verify the HTTP request
        const req = httpMock.expectOne(
            request => request.url.endsWith('/1/popular-tracks')
        );

        expect(req.request.method).toBe('GET');
        expect(req.request.params.get('page')).toBe('0');
        expect(req.request.params.get('size')).toBe('10');

        req.flush(trackPage);
        fixture.detectChanges();

        expect(component.popularTracks()).toEqual(trackPage.content);
        expect(component.totalTracks()).toBe(20);
        expect(component.popularTracksLoaded()).toBe(true);

        expect(fixture.nativeElement.querySelectorAll('app-track-card').length)
            .toBe(1);
    });


    it('should change popular tracks pages using MatPaginator', async () => {
        // Load artist and albums
        httpMock.expectOne(
            request => request.url.endsWith('/artists/1')
        ).flush(artist);

        httpMock.expectOne(
            request => request.url.endsWith('/1/albums')
        ).flush(albumPage);

        fixture.detectChanges();

        // Select popular tracks tab
        const loader = TestbedHarnessEnvironment.loader(fixture);
        const tabGroup = await loader.getHarness(MatTabGroupHarness);

        const tabs = await tabGroup.getTabs();
        await tabs[1].select();

        // Load initial popular tracks
        const initialReq = httpMock.expectOne(
            request => request.url.endsWith('/1/popular-tracks')
        );

        initialReq.flush(trackPage);
        fixture.detectChanges();

        // Get the paginator from the active tab
        const paginator = await loader.getHarness(MatPaginatorHarness);

        // Change page size to 5
        await paginator.setPageSize(5);

        const sizeReq = httpMock.expectOne(
            request => request.url.endsWith('/1/popular-tracks')
        );

        expect(sizeReq.request.params.get('page')).toBe('0');
        expect(sizeReq.request.params.get('size')).toBe('5');

        sizeReq.flush({
            ...trackPage,
            number: 0,
            size: 5
        });

        fixture.detectChanges();

        // Navigate to the next page
        await paginator.goToNextPage();

        const pageReq = httpMock.expectOne(
            request => request.url.endsWith('/1/popular-tracks')
        );

        expect(pageReq.request.method).toBe('GET');
        expect(pageReq.request.params.get('page')).toBe('1');
        expect(pageReq.request.params.get('size')).toBe('5');

        pageReq.flush({
            ...trackPage,
            number: 1,
            size: 5
        });

        fixture.detectChanges();

        expect(component.tracksPageIndex).toBe(1);
        expect(component.tracksPageSize).toBe(5);
        expect(component.totalTracks()).toBe(20);
    });
});
import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { TrackList } from './track/track-list/track-list';
import { Register } from './auth/register/register';

export const routes: Routes = [
    { path: '', component: TrackList },
    { path: 'login', component: Login },
    { path: 'register', component: Register }
];

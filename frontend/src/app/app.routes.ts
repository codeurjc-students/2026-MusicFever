import { Routes } from '@angular/router';

import { MainLayout } from './main-layout/main-layout';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';

import { Home } from './pages/home/home';
import { Player } from './pages/player/player';
import { Rooms } from './pages/rooms/rooms';
import { Stats } from './pages/stats/stats';
import { Search } from './pages/search/search';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: Home },
      { path: 'search', component: Search },
      { path: 'player', component: Player },
      { path: 'rooms', component: Rooms },
      { path: 'stats', component: Stats }
    ]
  },
  { path: 'login', component: Login },
  { path: 'register', component: Register }
];
import { Component } from '@angular/core';

import { MiniPlayer } from '../../components/mini-player/mini-player';
import { SearchBar } from '../../components/search-bar/search-bar';
@Component({
  imports: [MiniPlayer, SearchBar],
  selector: 'app-top-header',
  styleUrl: './top-header.css',
  templateUrl: './top-header.html',
})
export class TopHeader {}

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { SideHeader } from './side-header/side-header';
import { TopHeader } from './top-header/top-header';
import { Footer } from './footer/footer';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, SideHeader, TopHeader, Footer],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css'
})
export class MainLayout {}
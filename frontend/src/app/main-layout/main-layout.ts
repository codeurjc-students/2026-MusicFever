import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SideHeader } from './side-header/side-header';
import { TopHeader } from './top-header/top-header';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, SideHeader, TopHeader],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css'
})
export class MainLayout {}
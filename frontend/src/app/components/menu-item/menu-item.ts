import { Component, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-menu-item',
  imports: [RouterLink, RouterLinkActive, MatIconModule],
  templateUrl: './menu-item.html',
  styleUrl: './menu-item.css'
})
export class MenuItem {
  label = input.required<string>();
  icon = input.required<string>();
  route = input.required<string>();
}
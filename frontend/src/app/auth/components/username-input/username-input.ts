import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'username-input',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './username-input.html',
  styleUrl: './username-input.css'
})
export class UsernameInput {
  usernameFormControl = input.required<FormControl<string>>();
}
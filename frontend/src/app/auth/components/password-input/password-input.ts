import { Component, signal, input } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'password-input',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule
  ],
  templateUrl: './password-input.html',
  styleUrl: './password-input.css'
})
export class PasswordInput {
  label = input('Password')
  hidePassword = signal(true);
  passwordFormControl = input.required<FormControl<string>>();
}
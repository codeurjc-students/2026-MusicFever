import { Component, input } from '@angular/core';
import {
  FormControl,
  FormGroupDirective,
  NgForm,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form?.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

/** @title Input with a custom ErrorStateMatcher */
@Component({
  selector: 'email-input',
  templateUrl: './email-input.html',
  styleUrl: './email-input.css',
  imports: [FormsModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
})
export class EmailInput {
  emailFormControl = input.required<FormControl<string>>();

  matcher = new MyErrorStateMatcher();
}
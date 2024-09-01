import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {RegisterService} from "../shared/service/requests/register.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-register',
  standalone: true,
    imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  username: string = "";
  password = "";
  pin = "";
  email? = "";
  firstName? = "";
  lastName? = "";

  constructor(private registerService: RegisterService, private router: Router, private toastr: ToastrService) {}

  submitRegister() {
    if (this.username === "" || this.password === "" || this.pin === "") {
      this.toastr.error('Please enter a username, password, and pin');
      return;
    }

    this.registerService
      .register(this.username, this.password, this.pin, this.email, this.firstName, this.lastName)
      .subscribe({
        next: (data) => {
          this.toastr.success('Register successful', 'Success');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.toastr.error('Username is already taken or pin was incorrect', 'Error');
        },
      });
  }
}

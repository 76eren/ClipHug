import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {LoginService} from "../shared/service/requests/login.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username: string = "";
  password: string = "";

  constructor(
    private loginService: LoginService, private toastr: ToastrService) {
  }

  public submitLogin() {
    if (this.username === "" || this.password === "") {
      this.toastr.error('Please enter a username and password');
      return;
    }

    console.log("Username is: " + this.username + " Password is: " + this.password)

    this.loginService.login(this.username, this.password);
  }



}

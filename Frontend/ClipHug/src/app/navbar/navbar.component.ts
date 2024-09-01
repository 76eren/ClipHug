import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import {AuthenticationService} from "../shared/service/requests/authentication.service";


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {

  constructor(private authService: AuthenticationService) {}

  signout() {
    this.authService.signout();
  }
}

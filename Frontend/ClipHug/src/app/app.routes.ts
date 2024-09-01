import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AppLayoutComponent} from "./app-layout/app-layout.component";
import {LoginComponent} from "./login/login.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
      }
    ],
  },
  {
    path: '**',
    redirectTo: 'dashboard' +
      '',
  },
];

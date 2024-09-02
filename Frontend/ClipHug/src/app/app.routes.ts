import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AppLayoutComponent} from "./app-layout/app-layout.component";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {LoginGuard} from "./shared/guard/login.guard";
import {AuthGuard} from "./shared/guard/auth.guard";
import {UploadComponent} from "./upload/upload.component";
import {ManagerComponent} from "./manager/manager.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [LoginGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [LoginGuard],
  },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
      },
      {
        path: 'upload',
        component: UploadComponent
      },
      {
        path: 'manager',
        component: ManagerComponent
      },
      {
        path: '',
        redirectTo: 'dashboard',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'dashboard' +
      '',
  },
];

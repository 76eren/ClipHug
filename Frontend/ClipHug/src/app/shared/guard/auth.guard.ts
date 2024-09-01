import { inject } from '@angular/core';
import {map, take} from 'rxjs/operators';
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../service/requests/authentication.service";

export const AuthGuard: () => Observable<boolean> = () => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  return authService.isAuthenticated().pipe(
    take(1),
    map(isAuthenticated => {
      if (isAuthenticated) {
        return true;
      }
      else {
        router.navigate(['/login']);
        return false;
      }
    })
  );
};

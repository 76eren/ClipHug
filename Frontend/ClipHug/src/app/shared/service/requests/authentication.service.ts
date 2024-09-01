import {Injectable} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import { map, catchError } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import {ApiService} from "../api.service";

@Injectable()
export class AuthenticationService {
  constructor(private toastr: ToastrService, private router: Router, private http: HttpClient, private apiService: ApiService) {}


  public isAuthenticated(): Observable<boolean> {
    let endpoint = '/auth/authenticated';

    return this.apiService.get<any>(endpoint)
      .pipe(
        map(response => {
          return !!(response.payload && response.payload.authenticated);
        }),
        catchError(error => {
          return of(false);
        })
      );
  }

  public isAdmin(): Observable<boolean> {
    let endpoint = '/auth/isAdmin';

    return this.apiService.get<any>(endpoint)
      .pipe(
        map(response => {
          if (response.payload && response.payload.admin) {
            return true;
          }
          else {
            return false;
          }
        }),
        catchError(error => {
          return of(false);
        })
      );
  }

  public signout(): void {
    this.apiService.post<any>('/auth/logout')
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.toastr.error();
        }
      });
  }
}

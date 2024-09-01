import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root',
})export class LoginService {
  constructor(private apiService: ApiService, private router: Router, private toastr: ToastrService) {
  }

  login(username: string, password: string) {
    const loginData = {
      "username" : username,
      "password" : password
    };

    this.apiService.post<any>('/auth/login', { body: loginData })
      .subscribe({
        next: () => {
          this.toastr.success("Login successful");
          this.router.navigate(['/home']);
        },
        error: () => {
          this.toastr.error("Invalid username or password");
        }
      });

  }
}

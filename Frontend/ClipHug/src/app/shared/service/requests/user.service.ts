import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {ApiResponse} from "../../models/ApiResponse";
import {UserModel} from "../../models/Login/user.model";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  constructor(private apiService: ApiService, private toastr: ToastrService) {
  }

  public getCurrentSignedInUser(): Observable<ApiResponse<UserModel>> {
    return this.apiService.get<ApiResponse<UserModel>>('/user/me').pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

}

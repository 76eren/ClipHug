import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ApiResponse} from "../../models/ApiResponse";
import {RegisterModel} from "../../models/Login/register.model";

@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  constructor(private apiService: ApiService) {
  }

  register(username: string, password: string, pin: string, email?: string, firstName?: string, lastName?: string) {
    const payload = {
      "username" : username,
      "password" : password,
      "pin" : pin,
      "email" : email,
      "firstName" : firstName,
      "lastName" : lastName
    };

    return this.apiService.post<ApiResponse<RegisterModel>>('/auth/register', { body: payload })

  }
}

import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable, OperatorFunction } from 'rxjs';

@Injectable()
export class ApiService {
  public static API_URL = 'http://localhost:8080/api/v1'; // TODO: Make this a config variable

  constructor(private http: HttpClient) {
  }

  public get<T>(path: string, options?: { headers?: HttpHeaders, params?: HttpParams }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();
    let requestParams: HttpParams = options?.params ?? new HttpParams();

    return this.http.get<T>(`${ApiService.API_URL}${path}`, { headers: requestHeaders, params: requestParams, withCredentials: true });
  }

  public post<T>(path: string, options?: { headers?: HttpHeaders, body?: object }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.post<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true })
  }

  public put<T>(path: string, options?: {headers?: HttpHeaders, body?: object}): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.put<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true })
  }

  public patch<T>(path: string, options?: { headers?: HttpHeaders, body?: object }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.patch<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true });
  }

  public delete<T>(path: string, options?: { headers?: HttpHeaders }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.delete<T>(`${ApiService.API_URL}${path}`, { headers: requestHeaders, withCredentials: true })
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private _http: HttpClient) { }

  registerUser(appUser: any): Observable<any> {
    return this._http.post<Observable<any>>(environment.api+'users', appUser);
  }
}

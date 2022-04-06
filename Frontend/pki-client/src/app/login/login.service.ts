import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class LoginService {

  isLoggedIn = false;

  constructor(private _http: HttpClient) { }

  loginUser(appUser: any): Observable<any> {
    return this._http.post<Observable<any>>(environment.api+'users/login', appUser)
    .pipe(
      tap(data => console.log("data: ", data))
    )
  };
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class LoginService {

  isLoggedIn = false;

  constructor(private _http: HttpClient) { }

  loginUser(appUser: any): Observable<any> {
    return this._http.post<Observable<any>>('http://localhost:8080/users/login', appUser)
    .pipe(
      tap(data => console.log("data: ", data))
    )
  };
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(public http: HttpClient) { }

  public getCurrentUser(): Observable<User> {
    return this.http.get<User>(environment.api + 'users/whoami');
  }

  public getClients(): Observable<User[]> {
    return this.http.get<User[]>(environment.api + 'users/find-all-clients');
  }
}

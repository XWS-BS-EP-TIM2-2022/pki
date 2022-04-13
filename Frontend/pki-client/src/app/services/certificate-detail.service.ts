import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Certificate } from '../model/certificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateDetailService {

  constructor(public http: HttpClient) { }

  public getCertificate(serialNumber: String): Observable<Certificate> {
    return this.http.get<Certificate>(environment.api + 'api/certificates/get-certificate' + serialNumber);
  }
}

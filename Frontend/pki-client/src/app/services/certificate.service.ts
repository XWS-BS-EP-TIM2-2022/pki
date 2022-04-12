import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Certificate } from '../model/certificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  constructor(public http: HttpClient) { }

  public getCertificatesForUser(): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(environment.api + 'api/certificates/');
  }

  public issueRootCertificate() {
    return this.http.post(environment.api + 'api/certificates/createRoot', {});
  }
}

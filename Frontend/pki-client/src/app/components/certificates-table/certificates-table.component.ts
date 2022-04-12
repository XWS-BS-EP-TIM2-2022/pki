import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { CertificateService } from 'src/app/services/certificate.service';
import { Certificate, CertificateViewModel } from 'src/app/model/certificate';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-certificates-table',
  templateUrl: './certificates-table.component.html',
  styleUrls: ['./certificates-table.component.css'],
  providers: [DatePipe]
})
export class CertificatesTableComponent implements OnInit {
  displayedColumns: string[] = ['serialNum', 'subject', 'issuer', 'validPeriod', 'viewCert','download'];
  isAdmin=true;
  userCertificates!: CertificateViewModel[];

  constructor(public certificateService: CertificateService, private _snackBar: MatSnackBar) { }

  ngOnInit(): void {
    if(this.isAdmin)this.displayedColumns.push("withdraw")
    this.certificateService.getAllCertificatesForUser().subscribe( res => this.userCertificates = res );
  }

  revokeCertificate(certificate: CertificateViewModel) {
    this.certificateService.revokeCertificate(certificate.serialNumber).subscribe(
      (data) => {
        this.certificateService.getAllCertificatesForUser().subscribe( res => this.userCertificates = res );
        this._snackBar.open('Certificate successfully revoked', 'Dissmiss', {
          duration: 3000
        });


        setTimeout(() => {
        }, 1000);
      },
      (error) => {
        this._snackBar.open('Certificate could not be revoked', 'Dissmiss', {
          duration: 3000
        });
      });;;
  }

}

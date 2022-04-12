import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { CertificateService } from 'src/app/services/certificate.service';
import { CertificateViewModel } from 'src/app/model/certificate';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/model/user';

@Component({
  selector: 'app-certificates-table',
  templateUrl: './certificates-table.component.html',
  styleUrls: ['./certificates-table.component.css'],
  providers: [DatePipe]
})
export class CertificatesTableComponent implements OnInit {
  displayedColumns: string[] = ['subject', 'issuer', 'validPeriod', 'viewCert','download', "withdraw"];
  currentUser!: User;
  isAdmin=true;
  userCertificates!: CertificateViewModel[];

  constructor(public certificateService: CertificateService, private _snackBar: MatSnackBar, public userService: UserService) { }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(data => {
      this.currentUser = data;
      this.isAdmin = this.currentUser.role === 'Admin';
    });
    
    this.certificateService.getAllCertificatesForUser().subscribe( res => this.userCertificates = res );
  }

  revokeCertificate(certificate: CertificateViewModel) {
    this.certificateService.revokeCertificate(certificate.serialNumber).subscribe(
      (data) => {
        this.certificateService.getAllCertificatesForUser().subscribe( res => this.userCertificates = res );
        this._snackBar.open('Certificate successfully withdrawn', 'Dissmiss', {
          duration: 3000
        });


        setTimeout(() => {
        }, 1000);
      },
      (error) => {
        this._snackBar.open('Certificate could not be withdrawn', 'Dissmiss', {
          duration: 3000
        });
      });;;
  }

}

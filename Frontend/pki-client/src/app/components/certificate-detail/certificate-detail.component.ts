import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Certificate } from 'src/app/model/certificate';
import { User } from 'src/app/model/user';
import { CertificateDetailService } from 'src/app/services/certificate-detail.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-certificate-detail',
  templateUrl: './certificate-detail.component.html',
  styleUrls: ['./certificate-detail.component.css']
})
export class CertificateDetailComponent implements OnInit {

  //currentUser! : User;
  currentCertificate! : Certificate
  serialNumber = ""
  certificateName = ""
  //commonName = "" 
  issuer = ""
  subject = ""
  signatureAlgorithm = ""
  publicKey = ""
  dateFrom = ""
  dateTo = ""
  version = ""


  constructor(private userService: UserService,
              private route: Router,
              private certificateDetailService: CertificateDetailService) { }

  ngOnInit(): void {
    //this.getCurrentUser();
    //this.getCertificate();
  }

  // public getCurrentUser() {
  //   this.userService.getCurrentUser().subscribe(data => {
  //     this.currentUser = data;
  //     this.commonName = this.currentUser.commonName;
  //   });
  // }

  getCertificate(serialNumber: String): void {
    this.certificateDetailService.getCertificate(serialNumber).subscribe(data => {
      this.currentCertificate = data;
      this.serialNumber = this.currentCertificate.serialNumber;
      this.certificateName = this.currentCertificate.certificateName;
      this.issuer = this.currentCertificate.issuerEmail;
      this.subject = this.currentCertificate.subjectEmail;
      this.signatureAlgorithm = this.currentCertificate.signatureAlgorithm;
      this.publicKey = this.currentCertificate.publicKey;
      this.dateFrom = this.currentCertificate.dateFrom;
      this.dateTo = this.currentCertificate.dateTo;
      this.version = this.currentCertificate.version;
    });
  }

}

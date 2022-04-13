import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Certificate, CertificateViewModel } from 'src/app/model/certificate';

@Component({
  selector: 'app-certificate-detail',
  templateUrl: './certificate-detail.component.html',
  styleUrls: ['./certificate-detail.component.css']
})
export class CertificateDetailComponent implements OnInit {
  @Input()
  currentCertificate! : CertificateViewModel

  serialNumber = ""
  certificateName = ""
  issuer = ""
  subject = ""
  signatureAlgorithm = ""
  publicKey = ""
  dateFrom!: string
  dateTo!: string
  version = ""

  constructor() { }

  ngOnInit(): void {
      this.serialNumber = this.currentCertificate.serialNumber;
      this.certificateName = this.currentCertificate.certificateName;
      this.issuer = this.currentCertificate.issuer;
      this.subject = this.currentCertificate.subject;
      this.signatureAlgorithm = this.currentCertificate.signatureAlgorithm;
      this.publicKey = this.currentCertificate.publicKey;
      this.dateFrom = new Date(this.currentCertificate.validFrom).toLocaleDateString();
      this.dateTo = new Date(this.currentCertificate.validTo).toLocaleDateString();
      this.version = this.currentCertificate.version;
    
  }

}

import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Certificate } from 'src/app/model/certificate';
import { User } from 'src/app/model/user';
import { CertificateService } from 'src/app/services/certificate.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-new-certificate-form',
  templateUrl: './new-certificate-form.component.html',
  styleUrls: ['./new-certificate-form.component.css']
})
export class NewCertificateFormComponent implements OnInit {

  currentUser!: User
  clients!: User[]
  issuers!: Certificate[]

  selectedSubject!: number //id
  selectedIssuer!: string //serialnum
  constructor(public userService: UserService, public certificateService: CertificateService, public snackBar: MatSnackBar,
    private route: Router) { }

  ngOnInit(): void {
    this.getCurrentUser();
    this.getAllClients();
    this.getMyCertificates();
  }

  public getCurrentUser() {
    this.userService.getCurrentUser().subscribe(data => this.currentUser = data);
  }

  public getAllClients() {
    this.userService.getClients().subscribe(data => this.clients = data);
  }

  public getMyCertificates() {
    this.certificateService.getCertificatesForUser().subscribe(data => this.issuers = data);
  }

}

import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Certificate, CertificateDto } from 'src/app/model/certificate';
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
  selectedType!: number // 0 for intermediate and 1 for end
  startDate!: Date
  endDate!: Date

  commonName = ""
  errorMessage = ""

  constructor(public userService: UserService, public certificateService: CertificateService, public snackBar: MatSnackBar,
    private route: Router) { }

  ngOnInit(): void {
    this.getCurrentUser();
    this.getAllClients();
    this.getMyCertificates();
  }

  public getCurrentUser() {
    this.userService.getCurrentUser().subscribe(data => {
      this.currentUser = data;
      this.commonName = this.currentUser.commonName;
    });
  }

  public getAllClients() {
    this.userService.getClients().subscribe(data => this.clients = data);
  }

  public getMyCertificates() {
    this.certificateService.getCertificatesForUser().subscribe(data => this.issuers = data);
  }

  public createNewCertificate() {
    let isCA;
    if (this.selectedType === 0)
      isCA = true;
    else
      isCA = false;

    let dto: CertificateDto = {
      issuerSerialNumber: this.selectedIssuer,
      validFrom: this.startDate,
      validTo: this.endDate,
      subjectId: this.selectedSubject,
      issuerId: this.currentUser.id,
      isCA: isCA,
    }

    this.certificateService.createNewCertificate(dto).subscribe((data) => {
      this.snackBar.open("New certificate successfully created!", "Dismiss", { duration: 3000 });
      this.route.navigate(['/certificates'])
    },
      (error) => {
        this.errorMessage = error.error
        //this.snackBar.open(error.error, "Dismiss", { duration: 3000 });
      });
  }

  isButtonDisabled() {
    if (this.selectedIssuer === undefined || this.selectedIssuer === null ||
      this.selectedSubject === undefined || this.selectedSubject === null ||
      this.selectedType === undefined || this.startDate === undefined || this.startDate === null ||
      this.endDate === null || this.endDate === undefined)
      return true;

    return false;
  }
}

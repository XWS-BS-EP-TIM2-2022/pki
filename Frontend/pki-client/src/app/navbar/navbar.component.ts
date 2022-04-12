import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { LoginService } from '../login/login.service';
import { User } from '../model/user';
import { CertificateService } from '../services/certificate.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  currentUser!: User;

  constructor(public readonly loginService: LoginService, public certificateService: CertificateService, public snackBar: MatSnackBar,
    private route: Router, public userService: UserService) { }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  logout(): void {
    localStorage.removeItem('userId');
    localStorage.removeItem('userType');
    localStorage.removeItem('jwt');
    this.loginService.isLoggedIn = false;
  }

  public getCurrentUser() {
    this.userService.getCurrentUser().subscribe(data => this.currentUser = data);
  }

  public createRoot() {
    this.certificateService.issueRootCertificate().subscribe(
      (data) => {
        this.snackBar.open("New root certificate successfully created!", "Dismiss", { duration: 3000 });
        this.route.navigate(['/certificates'])
      },
      (error) => {
        this.snackBar.open("Root certificate already exists!", "Dismiss", { duration: 3000 });
      })
  }
}

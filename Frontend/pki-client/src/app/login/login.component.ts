import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public readonly myFormGroup: FormGroup;

  constructor(private loginService: LoginService,
    private readonly formBuilder: FormBuilder,
    private router: Router) {
    this.myFormGroup = this.formBuilder.group({
      email: ['', Validators.compose([Validators.required, Validators.email])],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
  }

  public onClickSubmit(): void {
    if (this.myFormGroup.invalid) {
      alert('Invalid input');
      return;
    }
    this.loginService.loginUser(this.myFormGroup.getRawValue()).subscribe({
      next: (data) => {
        localStorage.setItem('userId', data.id);
        localStorage.setItem('userType', data.appUserType)
        localStorage.setItem('jwt', data.userTokenState.accessToken)
        this.loginService.isLoggedIn = true;
        window.location.href = '/certificates'
      },
      error: (err) => { alert("Invalid username/password!") }
    });
  }

}

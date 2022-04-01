import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterService } from './register.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  public readonly myFormGroup: FormGroup;

  constructor(private registerService: RegisterService,
    private readonly formBuilder: FormBuilder) { 
      this.myFormGroup = this.formBuilder.group({
        email: ['', Validators.compose([Validators.required, Validators.email])],
        password: ['', Validators.required],
        rePassword: ['', Validators.required],
        name: [],
        surname: [],
        address: [],
        appUserType: []
    });
  }

  ngOnInit(): void {
  }

  public onClickSubmit(): void {
    if (this.myFormGroup.invalid) {
        // stop here if it's invalid
        alert('Invalid input');
        return;
    }
    var rePassword = this.myFormGroup.get('rePassword')?.value;
    var password = this.myFormGroup.get('password')?.value;
    if(password != rePassword) {
      alert("Password doesn't match!");
      return;
    }
    this.registerService.registerUser(this.myFormGroup.getRawValue()).subscribe({
      next: (data) => {alert("Succesfully registered!")},
      error: (err) => {alert("Email already in use!")}
    });
  }
}

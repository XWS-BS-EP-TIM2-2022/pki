import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CertificatesTableComponent } from './components/certificates-table/certificates-table.component';
import { NewCertificateFormComponent } from './components/new-certificate-form/new-certificate-form.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';


const routes: Routes = [
  { path: 'certificates', component: CertificatesTableComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'new-certificate', component: NewCertificateFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

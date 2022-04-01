import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CertificatesTableComponent } from './components/certificates-table/certificates-table.component';

const routes: Routes = [
  {
    path:'certificates',
    component: CertificatesTableComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

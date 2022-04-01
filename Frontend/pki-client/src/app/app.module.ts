import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CertificatesTableComponent } from './components/certificates-table/certificates-table.component';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule }from '@angular/material/button';
@NgModule({
  declarations: [
    AppComponent,
    CertificatesTableComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatButtonModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';

export interface Certificate {
  serialId: number,
  subjectName:string,
  issuerName: string,
  valid:{
    notBefore: Date,
    notAfter: Date,
  }
}
const ELEMENT_DATA: Certificate[] = [
  {serialId: 1, subjectName: 'Hydrogen', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 2, subjectName: 'Helium', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 3, subjectName: 'Lithium', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 4, subjectName: 'Beryllium', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 5, subjectName: 'Boron', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 6, subjectName: 'Carbon', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 7, subjectName: 'Nitrogen', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 8, subjectName: 'Oxygen', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 9, subjectName: 'Fluorine', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
  {serialId: 10, subjectName: 'Neon', issuerName: "Ftn",valid:{notAfter: new Date(),notBefore:new Date()}},
];
@Component({
  selector: 'app-certificates-table',
  templateUrl: './certificates-table.component.html',
  styleUrls: ['./certificates-table.component.css'],
  providers: [DatePipe]
})
export class CertificatesTableComponent implements OnInit {
  displayedColumns: string[] = ['serialNum', 'subject', 'issuer', 'validPeriod', 'viewCert','download'];
  dataSource = ELEMENT_DATA;
  isAdmin=true;
  constructor() { }

  ngOnInit(): void {
    if(this.isAdmin)this.displayedColumns.push("withdraw")
  }

}

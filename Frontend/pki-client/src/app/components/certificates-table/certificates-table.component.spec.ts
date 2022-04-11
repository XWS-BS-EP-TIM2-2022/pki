import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificatesTableComponent } from './certificates-table.component';

describe('CertificatesTableComponent', () => {
  let component: CertificatesTableComponent;
  let fixture: ComponentFixture<CertificatesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CertificatesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificatesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewCertificateFormComponent } from './new-certificate-form.component';

describe('NewCertificateFormComponent', () => {
  let component: NewCertificateFormComponent;
  let fixture: ComponentFixture<NewCertificateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewCertificateFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewCertificateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

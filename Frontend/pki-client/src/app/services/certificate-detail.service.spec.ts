import { TestBed } from '@angular/core/testing';

import { CertificateDetailService } from './certificate-detail.service';

describe('CertificateDetailService', () => {
  let service: CertificateDetailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CertificateDetailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

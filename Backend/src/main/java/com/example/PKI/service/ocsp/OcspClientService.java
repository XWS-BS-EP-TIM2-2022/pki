package com.example.PKI.service.ocsp;
import java.security.cert.X509Certificate;

public interface OcspClientService {
    public void validateCertificate(X509Certificate certificate);
    public void revokeCertificate(X509Certificate certificate);
    public void revokeCertificate(String serialNumber);
}

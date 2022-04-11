package com.example.PKI.repository;

import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertificateRepository extends JpaRepository<CertificateData, String> {
    @Query("select cert from CertificateData cert where cert.issuerEmail=?1")
    public List<CertificateData> findAllCertificatesByIssuer(String issuerEmail);

    @Query("select cert from CertificateData cert where cert.issuerCertificateSerialNum=?1")
    public List<CertificateData> findAllCertificatesByIssuerCertificateSerialNum(String serialNumber);

    @Query("select cert from CertificateData cert where cert.subjectEmail=?1")
    public List<CertificateData> findCertificateBySubject(String subjectEmail);
}

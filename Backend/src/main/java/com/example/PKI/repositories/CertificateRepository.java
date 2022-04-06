package com.example.PKI.repositories;

import com.example.PKI.model.CertificateData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<CertificateData, Integer> {
}

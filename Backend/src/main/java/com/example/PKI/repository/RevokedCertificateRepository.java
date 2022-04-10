package com.example.PKI.repository;

import com.example.PKI.model.RevokedCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedCertificateRepository extends JpaRepository<RevokedCertificate,String> {
}

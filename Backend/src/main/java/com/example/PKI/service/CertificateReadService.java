package com.example.PKI.service;

import com.example.PKI.model.Certificate;

import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateReadService {
    public List<X509Certificate> findAll();
    public List<Certificate> findAllByUser(Integer id);
}

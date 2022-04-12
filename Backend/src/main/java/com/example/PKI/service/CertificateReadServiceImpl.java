package com.example.PKI.service;

import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.Role;
import com.example.PKI.repository.CertificateRepository;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CertificateReadServiceImpl implements CertificateReadService {

    @Autowired
    private CertificateRepository repository;
    @Autowired
    private KeyStoreReader keyStoreReader;
    @Autowired
    private KeyStoreConfig config;
    @Override
    public List<CertificateData> findAll() {
        return repository.findAll();
    }

    @Override
    public List<CertificateData> findAllByUserRole(Role role, String email) {
        return role.equals(Role.Admin) ? this.findAll() : role.equals(Role.Intermediate) ? findCertificatesForIntermidiet(email) : repository.findCertificateBySubject(email);
    }

    private List<CertificateData> findCertificatesForIntermidiet(String intermEmail) {
        List<CertificateData> issued = repository.findAllCertificatesByIssuer(intermEmail);
        List<CertificateData> subject = repository.findCertificateBySubject(intermEmail);
        issued.addAll(subject);
        return issued;
    }

    public Collection<CertificateData> findCertificatesByUser(User user) {
        return repository.findAllCertificatesByIssuer(user.getEmail());
    }
}

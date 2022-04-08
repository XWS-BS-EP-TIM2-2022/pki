package com.example.PKI.service;

import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.model.Certificate;
import com.example.PKI.repositories.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
@Service
public class CertificateReadServiceImpl implements CertificateReadService{

    @Autowired
    private CertificateRepository repository;

    @Autowired
    private KeyStoreReader keyStoreReader;
    @Autowired
    private KeyStoreConfig config;

    @Override
    public List<X509Certificate> findAll() {
        List<X509Certificate> list=new ArrayList<>();
        for (Certificate cert:repository.findAll())
            list.add((X509Certificate) keyStoreReader.readCertificate(config.getRootCertKeystore(), config.getRootCertPassword(), cert.getUser().getUsername() + cert.getSerialNumber()));
        return list;
    }

    @Override
    public List<Certificate> findAllByUser(Integer id) {
        return null;
    }
}

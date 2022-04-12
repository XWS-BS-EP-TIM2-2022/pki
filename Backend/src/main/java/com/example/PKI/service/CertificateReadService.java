package com.example.PKI.service;

import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.Role;

import java.security.KeyStoreException;
import java.util.Collection;
import java.util.List;

public interface CertificateReadService {
    public List<CertificateData> findAll();
    public List<CertificateData> findAllByUserRole(Role role,String email);
    public Collection<CertificateData> findCertificatesByUser(User user) throws KeyStoreException;
}

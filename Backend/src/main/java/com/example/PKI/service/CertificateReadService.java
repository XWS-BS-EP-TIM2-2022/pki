package com.example.PKI.service;

import com.example.PKI.model.CertificateData;
import com.example.PKI.model.enumerations.Role;
import java.util.List;

public interface CertificateReadService {
    public List<CertificateData> findAll();
    public List<CertificateData> findAllByUserRole(Role role,String email);
}

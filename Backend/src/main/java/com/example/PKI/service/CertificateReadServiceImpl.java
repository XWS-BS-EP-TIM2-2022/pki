package com.example.PKI.service;

import com.example.PKI.exception.CustomCertificateRevokedException;
import com.example.PKI.dtos.CertificateDTO;
import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.model.CertificateData;
import com.example.PKI.model.RevokedCertificate;
import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.CertificateLevel;
import com.example.PKI.model.enumerations.Role;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.service.ocsp.OcspClientService;
import com.example.PKI.repository.RevokedCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateReadServiceImpl implements CertificateReadService {
    @Autowired
    private CertificateRepository repository;
    @Autowired
    private OcspClientService ocspClientService;
    @Autowired
    private KeyStoreReader keyStoreReader;
    @Autowired
    private KeyStoreConfig config;
    @Autowired
    private RevokedCertificateRepository revokedCertificateRepository;

    @Override
    public List<CertificateData> findAll() {
        return repository.findAll();
    }

    @Override
    public List<CertificateData> findAllByUserRole(Role role, String email) {
        return role.equals(Role.Admin) ? this.findAll() : role.equals(Role.Intermediate) ? findCertificatesForIntermediate(email) : repository.findCertificateBySubject(email);
    }

    private List<CertificateData> findCertificatesForIntermediate(String intermEmail) {
        List<CertificateData> issued = repository.findAllCertificatesByIssuer(intermEmail);
        List<CertificateData> subject = repository.findCertificateBySubject(intermEmail);
        issued.addAll(subject);
        return issued;
    }

    public Collection<CertificateData> findCertificatesByUser(User user) throws KeyStoreException {
        var usersCerts = repository.findAllCertificatesByIssuer(user.getEmail())
                .stream()
                .filter(cert -> cert.getLevel() != CertificateLevel.End)
                .collect(Collectors.toList());
        var notRevoked = new ArrayList<CertificateData>();
        for (var cert : usersCerts) {
            if (!isCertificateRevoked(cert))
                notRevoked.add(cert);
        }

        return notRevoked;
    }

    private boolean isCertificateRevoked(CertificateData data) throws KeyStoreException {
        X509Certificate cert = readCertificate(data);
        try {
            ocspClientService.validateCertificate(cert);
        }catch (CustomCertificateRevokedException exception) {
            return true;
        }
        return false;
    }

    private X509Certificate readCertificate(CertificateData cert) {
        if (cert.getLevel() == CertificateLevel.Root)
            return (X509Certificate) keyStoreReader.readCertificate(config.getRootCertKeystore(), config.getRootCertPassword(), cert.getSerialNumber());
        else if (cert.getLevel() == CertificateLevel.Intermediate)
            return (X509Certificate) keyStoreReader.readCertificate(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword(), cert.getSerialNumber());
        else
            return (X509Certificate) keyStoreReader.readCertificate(config.getEndCertKeystore(), config.getEndCertPassword(), cert.getSerialNumber());
    }

    @Override
    public Collection<CertificateDTO> findAllCertificatesByUser(User user) {
        List<CertificateData> certs = repository.findAllCertificatesByIssuer(user.getEmail());
        List<CertificateDTO> allCertificates = new ArrayList<>();
        String keyStoreName = "";
        String keyStorePass = "";
        for (CertificateData cert: certs) {
            boolean isCertRevoked = isCertificateRevoked(cert.getSerialNumber());
            try {
                keyStoreName = keyStoreReader.getKeyStoreNameByAlias(cert.getSerialNumber());
                keyStorePass = keyStoreReader.getKeyStorePasswordByAlias(cert.getSerialNumber());
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            X509Certificate certFromKS = (X509Certificate) keyStoreReader.readCertificate(keyStoreName, keyStorePass, cert.getSerialNumber());
            Date validFrom = certFromKS.getNotBefore();
            Date validTo = certFromKS.getNotAfter();
            CertificateDTO createdCert = new CertificateDTO(cert.getSerialNumber(), cert.subjectEmail,
                                                                cert.issuerEmail, validFrom, validTo, isCertRevoked);
            allCertificates.add(createdCert);
        }
        return allCertificates;
    }

    private boolean isCertificateRevoked(String serialNumber) {
        List<RevokedCertificate> revokedCerts = revokedCertificateRepository.findAll();
        for (RevokedCertificate cert: revokedCerts) {
            if(cert.getSerialNumber().equals(serialNumber) && cert.getRevoked())
                return true;
        }
        return false;
    }
}

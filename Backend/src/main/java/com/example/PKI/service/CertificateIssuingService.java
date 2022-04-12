package com.example.PKI.service;

import com.example.PKI.certificates.CertificateGenerator;
import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;
import com.example.PKI.dto.NewCertificateDTO;
import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.keystores.KeyStoreWriter;
import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.CertificateLevel;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.UserRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class CertificateIssuingService {
    private CertificateGenerator certificateGenerator = new CertificateGenerator();
    @Autowired
    private KeyStoreWriter keyStoreWriter;
    @Autowired
    private KeyStoreReader keystoreReader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificateRepository certificateRepository;

    public CertificateIssuingService() {}

    public CertificateData issueRootCertificate() {
        var admin = getAdmin();
        BigInteger serialNumber = generateSerialNumber();

        if (keystoreReader.rootAlreadyExists(String.valueOf(serialNumber)))
            return null;
        KeyPair keyPair = generateKeys();
        IssuerData issuer = new IssuerData(keyPair.getPrivate(), getDataForSelfSigned(admin).build());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -5);
        Date validFrom = calendar.getTime();
        calendar.add(Calendar.YEAR, 20);
        Date validTo = calendar.getTime();

        SubjectData subject = new SubjectData(keyPair.getPublic(),
                issuer.getX500name(),
                serialNumber,
                validFrom,
                validTo);
        X509Certificate rootCertificate = certificateGenerator.generateCertificate(subject, issuer, initRootKeyUsages(), true);
        this.verifyCertificate(keyPair.getPublic(), rootCertificate);
        keyStoreWriter.saveToRootKeyStore(String.valueOf(serialNumber), rootCertificate, keyPair.getPrivate());
        return saveToDatabase(new CertificateData(rootCertificate.getSerialNumber().toString(),admin.getEmail(),null, admin.getEmail(), CertificateLevel.Root,admin));
    }

    public CertificateData issueNewCertificate(NewCertificateDTO newCertificateDTO) throws KeyStoreException {
        IssuerData issuerData = keystoreReader.getIssuerData(newCertificateDTO.getIssuerSerialNumber());
        KeyPair keyPair = this.generateKeys();
        BigInteger serialNumber = generateSerialNumber();
        User subject = userRepository.findById(newCertificateDTO.getSubjectId()).get();
        SubjectData subjectData = new SubjectData(keyPair.getPublic(),
                this.getX500NameForUser(subject),
                serialNumber,
                newCertificateDTO.getValidFrom(),
                newCertificateDTO.getValidTo());
        String issuerEmail = String.valueOf(issuerData.getX500name().getRDNs(BCStyle.E)[0].getFirst().getValue());
        X509Certificate cert = certificateGenerator.generateCertificate(subjectData, issuerData, newCertificateDTO.getKeyUsages(),
                newCertificateDTO.getIsCA());
        this.verifySignedCertificateSigne(newCertificateDTO, cert);
        keyStoreWriter.saveToKeyStore(cert.getSerialNumber().toString(), newCertificateDTO.getIsCA(), cert, keyPair.getPrivate());
        CertificateData certificateData=new CertificateData(cert.getSerialNumber().toString(),issuerEmail,getIssuerCertificate(newCertificateDTO.getIssuerSerialNumber()).getSerialNumber().toString(),
                subject.getEmail(),
                newCertificateDTO.getIsCA() ? CertificateLevel.Intermediate : CertificateLevel.End,subject);
        return saveToDatabase(certificateData);
    }

    private CertificateData saveToDatabase(CertificateData certificateData) {
        return certificateRepository.save(certificateData);
    }

    private ArrayList<Integer> initRootKeyUsages() {
        var keyUsages = new ArrayList<Integer>();
        keyUsages.add(KeyUsage.digitalSignature); //can be used to verify digital signatures
        keyUsages.add(KeyUsage.keyCertSign); //can be used to sign other certificates
        keyUsages.add(KeyUsage.keyEncipherment); //public key can be used for enciphering private key
        keyUsages.add(KeyUsage.cRLSign); //public key can used for verifying signatures on certificate revocation list
        return keyUsages;
    }

    private BigInteger generateSerialNumber() {
        return new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16);
    }

    private void verifySignedCertificateSigne(NewCertificateDTO newCertificateDTO, X509Certificate cert) throws KeyStoreException {
        X509Certificate issuerCert = getIssuerCertificate(newCertificateDTO.getIssuerSerialNumber());
        this.verifyCertificate(issuerCert.getPublicKey(), cert);
    }

    private X509Certificate getIssuerCertificate(String serialNumber) throws KeyStoreException {
        String keyStoreName = keystoreReader.getKeyStoreNameByAlias(serialNumber);
        String keyStorePass = keystoreReader.getKeyStorePasswordByAlias(serialNumber);
        return (X509Certificate) keystoreReader.readCertificate(keyStoreName, keyStorePass, serialNumber);
    }

    private User getAdmin() {
        return userRepository.getByUsername("admin");
    }

    private void verifyCertificate(PublicKey publicKey, X509Certificate certificate) {
        try {
            certificate.verify(publicKey);
            System.out.println("Ispravan potpis");
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            e.printStackTrace();
        }
    }

    private X500NameBuilder getDataForSelfSigned(User admin) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "XWS Root Cert");
        builder.addRDN(BCStyle.SURNAME, admin.getSurname());
        builder.addRDN(BCStyle.NAME, admin.getName());
        builder.addRDN(BCStyle.O, admin.getOrganizationName());
        builder.addRDN(BCStyle.E, admin.getEmail());
        return builder;
    }

    private KeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
            SecureRandom random = SecureRandom.getInstanceStrong();
            generator.initialize(2048, random);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X500Name getX500NameForUser(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getCommonName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getName());
        builder.addRDN(BCStyle.O, user.getOrganizationName());
        builder.addRDN(BCStyle.E, user.getEmail());
        return builder.build();
    }
}

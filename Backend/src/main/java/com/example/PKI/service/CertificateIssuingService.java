package com.example.PKI.service;

import com.example.PKI.certificates.CertificateGenerator;
import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;
import com.example.PKI.dtos.NewCertificateDTO;
import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.keystores.KeyStoreWriter;
import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import com.example.PKI.repositories.CertificateRepository;
import com.example.PKI.repositories.UserRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateIssuingService {
    private CertificateGenerator certificateGenerator = new CertificateGenerator();
    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
    private KeyStoreReader keystoreReader = new KeyStoreReader();
    private KeyStoreConfig config;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificateRepository certificateRepository;

    public CertificateIssuingService(KeyStoreConfig config) {
        this.config = config;
    }

    public CertificateData issueCertificate() {
        var admin = userRepository.findAll().stream().filter(user -> user.getUsername().equals("admin"))
                .findFirst()
                .orElse(null);

        X500NameBuilder builder = getDataForSelfSigned(admin);

        var keyPair = generateKeys();
        var issuer = new IssuerData(keyPair.getPrivate(), builder.build());

        var serialNumber = System.currentTimeMillis();
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -5);
        var validFrom = calendar.getTime();
        calendar.add(Calendar.YEAR, 20);
        var validTo = calendar.getTime();

        var subject = new SubjectData(keyPair.getPublic(),
                issuer.getX500name(),
                Long.toString(serialNumber),
                validFrom,
                validTo);

        var keyUsages = new ArrayList<Integer>();
        keyUsages.add(KeyUsage.digitalSignature); //can be used to verify digital signatures
        keyUsages.add(KeyUsage.keyCertSign); //can be used to sign other certificates
        keyUsages.add(KeyUsage.keyEncipherment); //public key can be used for enciphering private key
        keyUsages.add(KeyUsage.cRLSign); //public key can used for verifying signatures on certificate revocation list

        var rootAlias = admin.getUsername() + " root certificate";

        if (rootAlreadyExists(rootAlias))
            return null;

        var rootCertificate = certificateGenerator.generateCertificate(subject, issuer, keyUsages, true);
        saveToKeyStore(issuer, rootAlias, rootCertificate);

        return certificateRepository.save(new CertificateData(rootCertificate.getSerialNumber().toString(),
                rootCertificate.getSigAlgName(), rootCertificate.getSerialNumber().toString(), rootCertificate.getNotBefore(),
                rootCertificate.getNotAfter(), rootCertificate.getSerialNumber().toString(), false, admin));
    }

    private void saveToKeyStore(IssuerData issuer, String rootAlias, X509Certificate rootCertificate) {
        keyStoreWriter.loadKeyStore(null, config.getRootCertPassword().toCharArray());
        keyStoreWriter.write(rootAlias, issuer.getPrivateKey(), config.getRootCertPassword().toCharArray(), rootCertificate);
        keyStoreWriter.saveKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
    }

    private boolean rootAlreadyExists(String alias) {
        var c = keystoreReader.readCertificate(config.getRootCertKeystore(), config.getRootCertPassword(), alias);
        return c != null;
    }

    private X500NameBuilder getDataForSelfSigned(User admin) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "XWS Root Cert");
        builder.addRDN(BCStyle.SURNAME, admin.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, admin.getGivenName());
        builder.addRDN(BCStyle.O, admin.getOrganizationName());
        builder.addRDN(BCStyle.E, admin.getEmail());
        return builder;
    }

    private KeyPair generateKeys() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);

            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CertificateData issueNewCertificate(NewCertificateDTO newCertificateDTO) throws KeyStoreException {

        if (isDateValid(newCertificateDTO.getValidFrom(), newCertificateDTO.getValidTo()))
            return null;

        User subjectUser = userRepository.getById(newCertificateDTO.getSubjectId());
        User issuerUser = userRepository.getById(newCertificateDTO.getIssuerId());

        //get certificate chain and validate
        String issuerAlias = issuerUser.getEmail() + newCertificateDTO.getIssuerSerialNumber();


        //generating new certificate
        KeyPair keyPair = generateKeys();
        var serialNumber = System.currentTimeMillis();
        SubjectData subjectData = new SubjectData(keyPair.getPublic(), getX500Name(subjectUser).build(),
                Long.toString(serialNumber), newCertificateDTO.getValidFrom(), newCertificateDTO.getValidTo());
        IssuerData issuerData = keystoreReader.findCAbySerialNumber(newCertificateDTO.getIssuerSerialNumber(),
                getKeyStoreNameByAlias(issuerAlias), getKeyStorePasswordByAlias(issuerAlias).toCharArray());

        var newCertificate = certificateGenerator.generateCertificate(subjectData, issuerData, newCertificateDTO.getKeyUsages(), newCertificateDTO.isCA());

        //saving new certificate to keystore
        String keyStorePassword = "";
        String filePath = "";
        String alias = subjectUser.getEmail() + Long.toString(serialNumber);
        if(newCertificateDTO.isCA()){
            filePath = config.getIntermediateCertKeystore();
            keyStorePassword = config.getIntermediateCertPassword();
        }else{
            filePath = config.getEndCertKeystore();
            keyStorePassword = config.getEndCertPassword();
        }
        Certificate[] certificateChain = getCertificateChain(Long.toString(serialNumber), newCertificateDTO.isCA());
        keyStoreWriter.loadKeyStore(filePath, keyStorePassword.toCharArray());
        keyStoreWriter.write(alias, issuerData.getPrivateKey(), keyStorePassword.toCharArray(), certificateChain);

        //adding new certificate do database

        return null;
    }

    private String getKeyStoreNameByAlias(String issuerAlias) throws KeyStoreException {
        var rootKS = keystoreReader.getKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
        if (rootKS.containsAlias(issuerAlias))
            return config.getRootCertKeystore();

        var intermediateKS = keystoreReader.getKeyStore(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword().toCharArray());
        if (intermediateKS.containsAlias(issuerAlias))
            return config.getIntermediateCertKeystore();

        var endKS = keystoreReader.getKeyStore(config.getEndCertKeystore(), config.getEndCertPassword().toCharArray());
        if (endKS.containsAlias(issuerAlias))
            return config.getEndCertKeystore();

        return null;
    }

    private String getKeyStorePasswordByAlias(String issuerAlias) throws KeyStoreException {
        var rootKS = keystoreReader.getKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
        if (rootKS.containsAlias(issuerAlias))
            return config.getRootCertPassword();

        var intermediateKS = keystoreReader.getKeyStore(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword().toCharArray());
        if (intermediateKS.containsAlias(issuerAlias))
            return config.getIntermediateCertPassword();

        var endKS = keystoreReader.getKeyStore(config.getEndCertKeystore(), config.getEndCertPassword().toCharArray());
        if (endKS.containsAlias(issuerAlias))
            return config.getEndCertPassword();

        return null;
    }

    private boolean isDateValid(Date validFrom, Date validTo) {
        if (validFrom.compareTo(validTo) > 0)
            return false;
        return true;
    }

    private X500NameBuilder getX500Name(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getCommonName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getGivenName());
        builder.addRDN(BCStyle.O, user.getOrganizationName());
        builder.addRDN(BCStyle.E, user.getEmail());
        return builder;
    }

    public Certificate[] getCertificateChain(String serialNumber, boolean isCA) {
        try {
            KeyStore keyStore;
            List<Certificate> ALChain = new ArrayList<>();
            if (!isCA) {
                keyStore = keystoreReader.getKeyStore(config.getEndCertKeystore(), config.getEndCertPassword().toCharArray());
                var EECertificate = (X509Certificate)keyStore.getCertificate(serialNumber);
                ALChain.add(EECertificate);
                serialNumber = EECertificate.getIssuerX500Principal().toString();
            }
            keyStore = keystoreReader.getKeyStore(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword().toCharArray());
            ALChain.addAll(Arrays.asList(keyStore.getCertificateChain(serialNumber)));
            Certificate[] VChain = new Certificate[ALChain.size()];
            return ALChain.toArray(VChain);
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }


}

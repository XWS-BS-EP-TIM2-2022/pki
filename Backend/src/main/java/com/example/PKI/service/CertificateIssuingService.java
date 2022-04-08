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

    public CertificateData issueRootCertificate() {
        var admin = getAdmin();
        BigInteger serialNumber = generateSerialNumber();

        if (rootAlreadyExists(String.valueOf(serialNumber)))
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
        this.saveToRootKeyStore(String.valueOf(serialNumber), rootCertificate, keyPair.getPrivate());
        return certificateRepository.save(new CertificateData(rootCertificate.getSerialNumber().toString(),
                rootCertificate.getSigAlgName(), rootCertificate.getSerialNumber().toString(), rootCertificate.getNotBefore(),
                rootCertificate.getNotAfter(), rootCertificate.getSerialNumber().toString(), false, admin));
    }

    public CertificateData issueNewCertificate(NewCertificateDTO newCertificateDTO) throws KeyStoreException {
        IssuerData issuerData = getIssuerData(newCertificateDTO.getIssuerSerialNumber());
        KeyPair keyPair = this.generateKeys();
        BigInteger serialNumber = generateSerialNumber();
        User subject = userRepository.findById(newCertificateDTO.getSubjectId()).get();
        SubjectData subjectData = new SubjectData(keyPair.getPublic(),
                this.getX500NameForUser(subject),
                serialNumber,
                newCertificateDTO.getValidFrom(),
                newCertificateDTO.getValidTo());

        X509Certificate cert = certificateGenerator.generateCertificate(subjectData, issuerData, newCertificateDTO.getKeyUsages(),
                newCertificateDTO.getIsCA());
        this.verifySignedCertificateSigne(newCertificateDTO, cert);
        this.saveToKeyStore(cert.getSerialNumber().toString(), newCertificateDTO.getIsCA(), cert, keyPair.getPrivate());
        return certificateRepository.save(new CertificateData(cert.getSerialNumber().toString(), false));
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
        String issuerAlias = newCertificateDTO.getIssuerSerialNumber();
        String keyStoreName = this.getKeyStoreNameByAlias(issuerAlias);
        String keyStorePass = this.getKeyStorePasswordByAlias(issuerAlias);
        X509Certificate issuerCert = (X509Certificate) keystoreReader.readCertificate(keyStoreName, keyStorePass, issuerAlias);
        this.verifyCertificate(issuerCert.getPublicKey(), cert);
    }

    private User getAdmin() {
        return userRepository.findAll().stream().filter(user -> user.getUsername().equals("admin"))
                .findFirst()
                .orElse(null);
    }

    private void verifyCertificate(PublicKey publicKey, X509Certificate certificate) {
        try {
            certificate.verify(publicKey);
            System.out.println("Ispravan potpis");
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    private void saveToRootKeyStore(String rootAlias, X509Certificate rootCertificate, PrivateKey privateKey) {
        String keyPass = config.getRootCertPassword() + rootCertificate.getSerialNumber();
        keyStoreWriter.loadKeyStore(null, config.getRootCertPassword().toCharArray());
        keyStoreWriter.write(rootAlias, privateKey, keyPass.toCharArray(), rootCertificate);
        keyStoreWriter.saveKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
    }

    private void saveToKeyStore(String alias, boolean isCa, X509Certificate certificate, PrivateKey privateKey) {
        String subjectPassword = "";
        String keyStorePassword = "";
        String filePath = "";

        if (isCa) {
            filePath = config.getIntermediateCertKeystore();
            keyStorePassword = config.getIntermediateCertPassword();
        } else {
            filePath = config.getEndCertKeystore();
            keyStorePassword = config.getEndCertPassword();
        }

        subjectPassword = keyStorePassword + certificate.getSerialNumber();

        // creating chain

        File file = new File(filePath);
        if (!file.exists()) {
            keyStoreWriter.loadKeyStore(null, keyStorePassword.toCharArray());
        } else
            keyStoreWriter.loadKeyStore(filePath, keyStorePassword.toCharArray());

        keyStoreWriter.write(alias, privateKey, subjectPassword.toCharArray(), certificate);
        keyStoreWriter.saveKeyStore(filePath, keyStorePassword.toCharArray());
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
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            generator.initialize(2048, random);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IssuerData getIssuerData(String serialNumber) throws KeyStoreException {
        String keyStoreName = this.getKeyStoreNameByAlias(serialNumber);
        String keyStorePass = this.getKeyStorePasswordByAlias(serialNumber);
        String issuerPassword = getKeyStorePasswordByAlias(serialNumber) + serialNumber;
        IssuerData issuerData = keystoreReader.readIssuerFromStore(keyStoreName, serialNumber, keyStorePass.toCharArray(), issuerPassword.toCharArray());
        return issuerData;
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
        //return config.getIntermediateCertPassword();
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

    private X500Name getX500NameForUser(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getCommonName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getGivenName());
        builder.addRDN(BCStyle.O, user.getOrganizationName());
        builder.addRDN(BCStyle.E, user.getEmail());
        return builder.build();
    }
}

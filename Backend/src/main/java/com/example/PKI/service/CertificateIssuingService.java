package com.example.PKI.service;

import com.example.PKI.certificates.CertificateGenerator;
import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;
import com.example.PKI.keystores.KeyStoreConfig;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.keystores.KeyStoreWriter;
import com.example.PKI.model.Certificate;
import com.example.PKI.model.User;
import com.example.PKI.repositories.CertificateRepository;
import com.example.PKI.repositories.UserRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public Certificate issueCertificate() {
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

        var rootCertificate = certificateGenerator.generateCertificate(subject, issuer, keyUsages, true);
        var rootAlias = admin.getUsername() + rootCertificate.getSerialNumber().toString();

        if(rootAlreadyExists(rootAlias))
           return null;


       saveToKeyStore(issuer, rootAlias, rootCertificate);

        return certificateRepository.save(new Certificate(rootCertificate.getSerialNumber().toString(),
                rootCertificate.getIssuerDN().toString(), rootCertificate.getSubjectDN().toString(), false, admin));
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
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return null;
    }
}

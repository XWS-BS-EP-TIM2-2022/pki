package com.example.PKI.services;

import com.example.PKI.certificates.CertificateGenerator;
import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;
import com.example.PKI.dtos.NewCertificateDTO;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.keystores.KeyStoreWriter;
import com.example.PKI.model.Certificate;
import com.example.PKI.repositories.CertificateRepository;
import com.example.PKI.repositories.UserRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.security.*;
import java.util.ArrayList;

@Service
public class RootCertificateService {
    private CertificateGenerator certificateGenerator = new CertificateGenerator();
    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
    private KeyStoreReader keystoreReader = new KeyStoreReader();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificateRepository certificateRepository;

   public Certificate issueCertificate(NewCertificateDTO certificateRequest) {
       var admin = userRepository.findAll().stream().filter(user -> user.getUsername().equals("admin"))
               .findFirst()
               .orElse(null);

       X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
       builder.addRDN(BCStyle.CN, admin.getCommonName());
       builder.addRDN(BCStyle.SURNAME, admin.getSurname());
       builder.addRDN(BCStyle.GIVENNAME, admin.getGivenName());
       builder.addRDN(BCStyle.O, admin.getOrgName());
       builder.addRDN(BCStyle.E, admin.getEmail());

       var keyPair = generateKeys();
       var issuer = new IssuerData(keyPair.getPrivate(), builder.build());

       var serialNumber = System.currentTimeMillis();
       var subject = new SubjectData(keyPair.getPublic(),
               issuer.getX500name(),
               Long.toString(serialNumber),
               certificateRequest.getValidFrom(),
               certificateRequest.getValidTo());

       var keyUsages = new ArrayList<Integer>();
       keyUsages.add(KeyUsage.digitalSignature);
       keyUsages.add(KeyUsage.keyCertSign);
       keyUsages.add(KeyUsage.keyEncipherment);
       keyUsages.add(KeyUsage.cRLSign);

       var rootAlias = admin.getUsername() + serialNumber;

       var rootCertificate = certificateGenerator.generateCertificate(subject, issuer, keyUsages);
       keyStoreWriter.loadKeyStore(null, new char[]{'p', 'a', 's', 's'} );
       keyStoreWriter.write(rootAlias, issuer.getPrivateKey(), new char[]{'p', 'a', 's', 's'}, rootCertificate);
       keyStoreWriter.saveKeyStore("novi.jks", new char[]{'p', 'a', 's', 's'});

       var c = keystoreReader.readCertificate("novi.jks", "pass", rootAlias);
       System.out.println(c);
       return certificateRepository.save(new Certificate(rootCertificate.getSerialNumber().toString(),
               rootCertificate.getSigAlgName(), rootCertificate.getSerialNumber().toString(), rootCertificate.getNotBefore(),
               rootCertificate.getNotAfter(), rootCertificate.getSerialNumber().toString(), false, admin));
   }

    private KeyPair generateKeys() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            var random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            generator.initialize(2048, random);

            return generator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e){
            e.printStackTrace();
        }

        return null;
    }
}

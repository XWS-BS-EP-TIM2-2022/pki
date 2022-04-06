package com.example.PKI.keystores;

import org.apache.tomcat.jni.Directory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class KeyStoreConfig {
    @Value("${root-cert.keystore}")
    private String rootCertKeystore;

    @Value("${root-cert.password}")
    private String rootCertPassword;

    @Value("${intermediate-cert.keystore}")
    private String intermediateCertKeystore;

    @Value("${intermediate-cert.password}")
    private String intermediateCertPassword;

    @Value("${end-cert.keystore}")
    private String endCertKeystore;

    @Value("${end-cert.password}")
    private String endCertPassword;

    private String certificatesFolder = "certificates/";

    public KeyStoreConfig() {
        try {
            var path = Path.of(certificatesFolder);
            if (Files.notExists(path))
                Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRootCertKeystore() {
        return certificatesFolder + rootCertKeystore;
    }

    public void setRootCertKeystore(String rootCertKeystore) {
        this.rootCertKeystore = rootCertKeystore;
    }

    public String getRootCertPassword() {
        return rootCertPassword;
    }

    public void setRootCertPassword(String rootCertPassword) {
        this.rootCertPassword = rootCertPassword;
    }

    public String getIntermediateCertKeystore() {
        return certificatesFolder + intermediateCertKeystore;
    }

    public void setIntermediateCertKeystore(String intermediateCertKeystore) {
        this.intermediateCertKeystore = intermediateCertKeystore;
    }

    public String getIntermediateCertPassword() {
        return intermediateCertPassword;
    }

    public void setIntermediateCertPassword(String intermediateCertPassword) {
        this.intermediateCertPassword = intermediateCertPassword;
    }

    public String getEndCertKeystore() {
        return certificatesFolder + endCertKeystore;
    }

    public void setEndCertKeystore(String endCertKeystore) {
        this.endCertKeystore = endCertKeystore;
    }

    public String getEndCertPassword() {
        return endCertPassword;
    }

    public void setEndCertPassword(String endCertPassword) {
        this.endCertPassword = endCertPassword;
    }

    public String getCertificatesFolder() {
        return certificatesFolder;
    }

    public void setCertificatesFolder(String certificatesFolder) {
        this.certificatesFolder = certificatesFolder;
    }
}

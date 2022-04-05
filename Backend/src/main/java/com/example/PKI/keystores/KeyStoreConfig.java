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

    public String getRootCertKeystore() {
        return rootCertKeystore;
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
}

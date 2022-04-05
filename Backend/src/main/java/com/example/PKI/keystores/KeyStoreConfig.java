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
}

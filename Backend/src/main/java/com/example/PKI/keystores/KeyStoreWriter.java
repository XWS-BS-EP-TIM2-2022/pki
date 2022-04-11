package com.example.PKI.keystores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
public class KeyStoreWriter {
	private KeyStore keyStore;
	@Autowired
	private KeyStoreConfig config;
	public KeyStoreWriter() {
		try {
			keyStore = KeyStore.getInstance("PKCS12", "SunJSSE");

		} catch (KeyStoreException | NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	public void saveToRootKeyStore(String rootAlias, X509Certificate rootCertificate, PrivateKey privateKey) {
		String keyPass = config.getRootCertPassword() + rootCertificate.getSerialNumber();
		this.loadKeyStore(null, config.getRootCertPassword().toCharArray());
		this.write(rootAlias, privateKey, keyPass.toCharArray(), rootCertificate);
		this.saveKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
	}

	public void saveToKeyStore(String alias, boolean isCa, X509Certificate certificate, PrivateKey privateKey) {
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
			this.loadKeyStore(null, keyStorePassword.toCharArray());
		} else
			this.loadKeyStore(filePath, keyStorePassword.toCharArray());

		this.write(alias, privateKey, subjectPassword.toCharArray(), certificate);
		this.saveKeyStore(filePath, keyStorePassword.toCharArray());
	}

	public void loadKeyStore(String fileName, char[] password) {
		try {
			if(fileName != null) {
				keyStore.load(new FileInputStream(fileName), password);
			} else {
				//Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
				keyStore.load(null, password);
			}
		} catch (NoSuchAlgorithmException | CertificateException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveKeyStore(String fileName, char[] password) {
		try {
			keyStore.store(new FileOutputStream(fileName), password);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	public void write(String alias, PrivateKey privateKey, char[] password, Certificate[] certificates) {
		try {
			keyStore.setKeyEntry(alias, privateKey, password, certificates);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
}

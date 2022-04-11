package com.example.PKI.keystores;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.example.PKI.data.IssuerData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeyStoreReader {
	//KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
	//Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
	// - Sertifikati koji ukljucuju javni kljuc
	// - Privatni kljucevi
	// - Tajni kljucevi, koji se koriste u simetricnima siframa
	private KeyStore keyStore;
	@Autowired
	private KeyStoreConfig config;
	public KeyStoreReader() {
		try {
			keyStore = KeyStore.getInstance("PKCS12", "SunJSSE");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
	 * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
	 *
	 * @param keyStoreFile - datoteka odakle se citaju podaci
	 * @param alias        - alias putem kog se identifikuje sertifikat izdavaoca
	 * @param password     - lozinka koja je neophodna da se otvori key store
	 * @param keyPass      - lozinka koja je neophodna da se izvuce privatni kljuc
	 * @return - podatke o izdavaocu i odgovarajuci privatni kljuc
	 */
	public IssuerData readIssuerFromStore(String keyStoreFile, String alias, char[] password, char[] keyPass) {
		try {
			//Datoteka se ucitava
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			keyStore.load(in, password);
			//Iscitava se sertifikat koji ima dati alias
			Certificate cert = keyStore.getCertificate(alias);
			//Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
			PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

			X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
			return new IssuerData(privKey, issuerName);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ucitava sertifikat is KS fajla
	 */
	public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
			//ucitavamo podatke
			var file = new File(keyStoreFile);
			if (!file.exists())
				return null;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			ks.load(in, keyStorePass.toCharArray());

			if (ks.isKeyEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				return cert;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ucitava privatni kljuc is KS fajla
	 */
	public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			ks.load(in, keyStorePass.toCharArray());

			if (ks.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
				return pk;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	public KeyStore getKeyStore(String fileName, char[] password) {
		try {
			if (fileName != null) {
				keyStore.load(new FileInputStream(fileName), password);
			}
			return keyStore;
		} catch (NoSuchAlgorithmException | CertificateException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getKeyStoreNameByAlias(String issuerAlias) throws KeyStoreException {
		var rootKS = this.getKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
		if (rootKS.containsAlias(issuerAlias))
			return config.getRootCertKeystore();

		var intermediateKS = this.getKeyStore(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword().toCharArray());
		if (intermediateKS.containsAlias(issuerAlias))
			return config.getIntermediateCertKeystore();

		var endKS = this.getKeyStore(config.getEndCertKeystore(), config.getEndCertPassword().toCharArray());
		if (endKS.containsAlias(issuerAlias))
			return config.getEndCertKeystore();

		return null;
	}

	public IssuerData getIssuerData(String serialNumber) throws KeyStoreException {
		String keyStoreName = this.getKeyStoreNameByAlias(serialNumber);
		String keyStorePass = this.getKeyStorePasswordByAlias(serialNumber);
		String issuerPassword = this.getKeyStorePasswordByAlias(serialNumber) + serialNumber;
		IssuerData issuerData = this.readIssuerFromStore(keyStoreName, serialNumber, keyStorePass.toCharArray(), issuerPassword.toCharArray());
		return issuerData;
	}

	public boolean rootAlreadyExists(String alias) {
		var c = this.readCertificate(config.getRootCertKeystore(), config.getRootCertPassword(), alias);
		return c != null;
	}

	public String getKeyStorePasswordByAlias(String issuerAlias) throws KeyStoreException {
		//return config.getIntermediateCertPassword();
		var rootKS = this.getKeyStore(config.getRootCertKeystore(), config.getRootCertPassword().toCharArray());
		if (rootKS.containsAlias(issuerAlias))
			return config.getRootCertPassword();

		var intermediateKS = this.getKeyStore(config.getIntermediateCertKeystore(), config.getIntermediateCertPassword().toCharArray());
		if (intermediateKS.containsAlias(issuerAlias))
			return config.getIntermediateCertPassword();

		var endKS = this.getKeyStore(config.getEndCertKeystore(), config.getEndCertPassword().toCharArray());
		if (endKS.containsAlias(issuerAlias))
			return config.getEndCertPassword();

		return null;
	}
}
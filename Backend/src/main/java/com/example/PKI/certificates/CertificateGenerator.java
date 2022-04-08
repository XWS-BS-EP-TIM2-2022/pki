package com.example.PKI.certificates;

import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;


public class CertificateGenerator {
	public CertificateGenerator() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public X509Certificate generateCustom(SubjectData subjectData, IssuerData issuerData){
		return null;
	}

	public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, List<Integer> keyUsageValues,
											   boolean isCA) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			builder = builder.setProvider("BC");

			ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					subjectData.getSerialNumber(),
					subjectData.getStartDate(),
					subjectData.getEndDate(),
					subjectData.getX500name(),
					subjectData.getPublicKey());

			certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
			certGen.addExtension(Extension.keyUsage, true, new KeyUsage(setKeyUsage(keyUsageValues)));
			certGen.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.dNSName, "localhost")));

			X509CertificateHolder certHolder = certGen.build(contentSigner);
			JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			certConverter = certConverter.setProvider("BC");

			return certConverter.getCertificate(certHolder);
		} catch (CertificateEncodingException | IllegalArgumentException | IllegalStateException | OperatorCreationException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (CertIOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int setKeyUsage(List<Integer> keyUsageValues) {
		var retVal = 0;
		for(var i : keyUsageValues) {
			retVal = retVal | i;
		}

		return retVal;
	}
}

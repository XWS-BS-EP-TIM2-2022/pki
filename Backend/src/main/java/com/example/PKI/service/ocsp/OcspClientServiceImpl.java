package com.example.PKI.service.ocsp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.PKI.exception.CertificatesRevokingException;
import com.example.PKI.exception.CustomCertificateRevokedException;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.model.CertificateData;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.service.CertificateReadService;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class OcspClientServiceImpl implements OcspClientService {

    @Autowired
    private KeyStoreReader reader;

    @Autowired
    private CertificateRepository certificateRepository;

    @Override
    public void validateCertificate(X509Certificate certificate) {
        this.validateOcspRespStatus(this.sendOCSPRequest(certificate));
    }

    @Override
    public void revokeCertificate(String serialNumber) {
        CertificateData certificateData = certificateRepository.findById(serialNumber).get();
        String url = getCABaseUrl(certificateData);
        List<String> serialNumbersToRevoke = getCertificateToRevokeSerialNumbers(certificateData);
        int statusCode = this.sendRevokeRequest(url, serialNumbersToRevoke);
        if (statusCode != 200) throw new CertificatesRevokingException();
    }

    private String getCABaseUrl(CertificateData certificateData) {
        X509Certificate x509Certificate = reader.getCertificateByCertificateData(certificateData);
        try {
            return getOCSPUrl(x509Certificate) + "/api/ocsp/revoke";
        } catch (IOException e) {
            e.printStackTrace();
            throw new CertificatesRevokingException();
        }
    }

    private List<String> getCertificateToRevokeSerialNumbers(CertificateData certificateData) {
        List<String> serialNumbersToRevoke=new ArrayList<>(){{add(certificateData.getSerialNumber());}};
        if (certificateData.isEndUser()) return serialNumbersToRevoke;
        serialNumbersToRevoke.addAll(this.findCertificateIssuedBy(certificateData).stream().map(CertificateData::getSerialNumber).collect(Collectors.toList()));
        return serialNumbersToRevoke;
    }

    private List<CertificateData> findCertificateIssuedBy(CertificateData certificate) {
        if (certificate.isEndUser()) return List.of(certificate);
        List<CertificateData> certificatesIssuedBy = certificateRepository.findAllCertificatesByIssuerCertificateSerialNum(certificate.getSerialNumber());
        List<CertificateData> subCertificates = new ArrayList<>();
        for (CertificateData subCertificate : certificatesIssuedBy) {
            subCertificates.addAll(this.findCertificateIssuedBy(subCertificate));
        }
        certificatesIssuedBy.addAll(subCertificates);
        return certificatesIssuedBy;
    }


    public int sendRevokeRequest(String url, List<String> serialNumbers) {
        HttpURLConnection conn = null;
        try {
            HttpURLConnection con = this.createConnection(url);
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeUTF(this.parseStringArray(serialNumbers));
            dos.flush();
            return con.getResponseCode();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return -1;
    }

    private String parseStringArray(List<String> serialNumbers) {
        StringBuilder builder = new StringBuilder();
        for (String s : serialNumbers)
            builder.append(s + ";");
        return builder.toString();
    }

    private void validateOcspRespStatus(BasicOCSPResp resp) {
        if (resp.getResponses()[0].getCertStatus() != null)
            throw new CustomCertificateRevokedException();
    }

    private HttpURLConnection createConnection(String connectionUrl) throws IOException {
        URL url = new URL(connectionUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        //con.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJwa2kiLCJzdWIiOiJhZG1pbjRAZ21haWwuY29tIiwiYXVkIjoid2ViIiwiaWF0IjoxNjQ5NTk1NDAwLCJleHAiOjE2NDk2MTM0MDB9.A65Rr5g2c3pGdmjdhiQILD0T1X66oQuPzmwAKKlW-AMRN6GPRNRjwE0E9SmAhaT88tgzoybKVl8YtJfc0UGwKA");
        con.setRequestMethod("POST");
        return con;
    }

    private BasicOCSPResp requestOCSPResponse(String ocspUrl, OCSPReq ocspReq) {
        HttpURLConnection con = null;
        try {
            con = this.createConnection(ocspUrl);
            con.setRequestProperty("Content-Type", "application/ocsp-request");
            OutputStream outputStream = con.getOutputStream();
            BufferedOutputStream writer = new BufferedOutputStream(outputStream);
            writer.write(ocspReq.getEncoded());
            writer.flush();
            ASN1InputStream asn1InputStream = new ASN1InputStream(con.getInputStream());
            ASN1Primitive obj = asn1InputStream.readObject();
            BasicOCSPResponse re = BasicOCSPResponse.getInstance(obj);
            BasicOCSPResp basicOCSPResp = new BasicOCSPResp(re);
            return basicOCSPResp;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
        return null;
    }

    private String getOCSPUrl(X509Certificate certificate) throws IOException {
        ASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, Extension.authorityInfoAccess.getId());
        } catch (IOException ex) {
            return null;
        }
        if (obj == null) return null;
        AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(obj);
        DERIA5String derStr = DERIA5String.getInstance((ASN1TaggedObject) authorityInformationAccess.getAccessDescriptions()[0].getAccessLocation().toASN1Primitive(), false);
        return derStr.getString();
    }

    private static ASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = certificate.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }

    private OCSPReq generateOCSPRequest(X509Certificate certificate) throws CertificateEncodingException, OperatorCreationException, OCSPException, IOException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        OCSPReqBuilder gen = new OCSPReqBuilder();
        CertificateID certId = getCertificateID(certificate);
        gen.addRequest(certId);
        return gen.build();
    }

    private CertificateID getCertificateID(X509Certificate certificate) throws OperatorCreationException, IOException, CertificateEncodingException, OCSPException {
        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
        X509CertificateHolder certHolder = new X509CertificateHolder(certificate.getEncoded());
        return new CertificateID(
                digestCalculatorProvider.get(CertificateID.HASH_SHA1), certHolder, certificate.getSerialNumber());
    }

    private BasicOCSPResp sendOCSPRequest(X509Certificate certificate) {
        try {
            OCSPReq request = this.generateOCSPRequest(certificate);
            String caUrl = getOCSPUrl(certificate)+"/api/ocsp/validate";
            return this.requestOCSPResponse(caUrl, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        OcspClientServiceImpl ocspClientService = new OcspClientServiceImpl();
        KeyStoreReader keyStoreReader = new KeyStoreReader();
        X509Certificate cert = (X509Certificate) keyStoreReader.readCertificate("certificates/root-cert.pfx", "password", "185199222108429939121308040734458774561");
        try {
            ocspClientService.getOCSPUrl(cert);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ocspClientService.revokeCertificate(issuerCert);
    }
}

package com.example.PKI.service.ocsp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.PKI.exception.CustomCertificateRevokedException;
import com.example.PKI.keystores.KeyStoreReader;
import com.example.PKI.model.CertificateData;
import com.example.PKI.repository.CertificateRepository;
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
import org.springframework.stereotype.Service;

@Service
public class OcspClientServiceImpl  implements  OcspClientService{

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
        CertificateData certificateData=certificateRepository.findById(serialNumber).get();
        int statusCode;
        String url="http://localhost:8080/api/ocsp/revoke";
        if(certificateData.isEndUser()){
             statusCode=this.sendRevokeRequest(url,List.of(serialNumber));
        }else{
            List<CertificateData> certificatesIssuedBy=certificateRepository.findAllCertificatesByIssuerCertificateSerialNum(serialNumber);
            certificatesIssuedBy.add(certificateData);
            statusCode=this.sendRevokeRequest(url,certificatesIssuedBy.stream().map(CertificateData::getSerialNumber).collect(Collectors.toList()));
        }
        if(statusCode==200) System.out.println("USPIJESNO");
        else
            System.out.println("NIJE USPIJESNO");
    }

    public int sendRevokeRequest(String url, List<String> serialNumbers) {
        HttpURLConnection conn=null;
        try {
            HttpURLConnection con=this.createConnection(url);
            DataOutputStream dos=new DataOutputStream(con.getOutputStream());
            dos.writeUTF(this.parseStringArray(serialNumbers));
            dos.flush();
            return con.getResponseCode();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(conn!=null) conn.disconnect();
        }
        return -1;
    }

    private String parseStringArray(List<String> serialNumbers) {
        StringBuilder builder=new StringBuilder();
        for (String s:serialNumbers)
            builder.append(s+";");
        return builder.toString();
    }

    private void validateOcspRespStatus(BasicOCSPResp resp){
        if(resp.getResponses()[0].getCertStatus()!=null)
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
        try{
            con=this.createConnection(ocspUrl);
            con.setRequestProperty("Content-Type", "application/ocsp-request");
            OutputStream outputStream=con.getOutputStream();
            BufferedOutputStream writer=new BufferedOutputStream(outputStream);
            writer.write(ocspReq.getEncoded());
            writer.flush();
            ASN1InputStream asn1InputStream=new ASN1InputStream(con.getInputStream());
            ASN1Primitive obj = asn1InputStream.readObject();
            BasicOCSPResponse re=BasicOCSPResponse.getInstance(obj);
            BasicOCSPResp basicOCSPResp=new BasicOCSPResp(re);
            return basicOCSPResp;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(con!=null)
                con.disconnect();
        }
        return null;
    }

    private String getOCSPUrl(X509Certificate certificate) throws IOException {
        return "http://localhost:8080/api/ocsp/validate";

        //TODO: Izvuci iz authorityinfoa url
        /*ASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, Extension.authorityInfoAccess.getId());
        } catch (IOException ex) {
            return null;
        }

        if (obj == null) {
            return null;
        }
        AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess.getInstance(obj);
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {
            boolean correctAccessMethod = accessDescription.getAccessMethod().equals(X509ObjectIdentifiers.ocspAccessMethod);
            if (!correctAccessMethod) {
                continue;
            }

            GeneralName name = accessDescription.getAccessLocation();
            if (name.getTagNo() != GeneralName.uniformResourceIdentifier) {
                continue;
            }

            DERIA5String derStr = DERIA5String.getInstance((ASN1TaggedObject) name.toASN1Primitive(), false);
            return derStr.getString();
        }
        return null;*/
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
            String caUrl=getOCSPUrl(null);
            return this.requestOCSPResponse(caUrl,request);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        OcspClientServiceImpl ocspClientService=new OcspClientServiceImpl();
        KeyStoreReader keyStoreReader =new KeyStoreReader();
        X509Certificate cert=(X509Certificate) keyStoreReader.readCertificate("certificates/root-cert.pfx","password","44588022343601368361979429911528518916");
        ocspClientService.validateCertificate(cert);
        String url="http://localhost:8080/api/ocsp/revoke";
        List<String> lit=new ArrayList<>();
        lit.add("1230");
        lit.add("1555");
        ocspClientService.sendRevokeRequest(url,lit);
        //ocspClientService.revokeCertificate(issuerCert);
    }
}

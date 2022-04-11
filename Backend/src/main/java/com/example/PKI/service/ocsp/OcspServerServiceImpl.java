package com.example.PKI.service.ocsp;

import com.example.PKI.model.RevokedCertificate;
import com.example.PKI.repository.RevokedCertificateRepository;
import com.example.PKI.service.ocsp.OcspServerService;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.cert.ocsp.jcajce.JcaBasicOCSPRespBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.util.Date;

@Service
public class OcspServerServiceImpl implements OcspServerService {
    @Autowired
    private RevokedCertificateRepository repository;
    @Override
    public BasicOCSPResp generateOCSPResponse(OCSPReq request) throws OperatorCreationException, OCSPException {
        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
        BigInteger serialNum=request.getRequestList()[0].getCertID().getSerialNumber();
        KeyPair keyPair = generateOCSPKeyPair();
        BasicOCSPRespBuilder response = new JcaBasicOCSPRespBuilder(keyPair.getPublic(), digestCalculatorProvider.get(RespID.HASH_SHA1));
        ContentSigner contentSigner=new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider("BC")
                .build(keyPair.getPrivate());
        RevokedCertificate revoked = this.isRevoked(serialNum.toString());
        if(revoked.getRevoked())
            response.addResponse(request.getRequestList()[0].getCertID(),new RevokedStatus(revoked.getTimestamp(),0));
        else response.addResponse(request.getRequestList()[0].getCertID(),CertificateStatus.GOOD);
        return response.build(contentSigner,request.getCerts(),new Date());
    }

    private RevokedCertificate isRevoked(String serialNumber){
        return repository.findById(serialNumber).orElse(new RevokedCertificate(false, new Date()));
    }

    private KeyPair generateOCSPKeyPair() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            generator.initialize(2048, random);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void revokeCertificate(String serialNumber){
        repository.save(new RevokedCertificate(serialNumber,true,new Date()));
    }
}

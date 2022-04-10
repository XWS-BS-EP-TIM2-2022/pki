package com.example.PKI.service.ocsp;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.operator.OperatorCreationException;

public interface OcspServerService {
    public BasicOCSPResp generateOCSPResponse(OCSPReq request) throws OperatorCreationException, OCSPException;
    public void revokeCertificate(String serialNumber);
}

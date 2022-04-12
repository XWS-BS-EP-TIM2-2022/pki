package com.example.PKI.controller;

import com.example.PKI.service.ocsp.OcspServerService;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequestMapping(value = "api/ocsp")
public class OcspController {

    @Autowired
    private OcspServerService ocspServerService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateRequest(HttpServletRequest request) throws IOException {
        BufferedInputStream inputStream=new BufferedInputStream(request.getInputStream());
        OCSPReq req=new OCSPReq(inputStream.readAllBytes());
        System.out.println(req.getRequestList()[0].getCertID().getSerialNumber());
        try{
            BasicOCSPResp resp = ocspServerService.generateOCSPResponse(req);
            return ResponseEntity.ok(resp.getEncoded());
        }catch (Exception e){
            return null;
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revoke(HttpServletRequest request) throws IOException {
        DataInputStream inputStream=new DataInputStream(request.getInputStream());
        String serialNums=inputStream.readUTF();
        List<String> list=this.parseBody(serialNums);
        for (String num:list) {
            ocspServerService.revokeCertificate(num);
        }
        return ResponseEntity.ok().build();
    }

    private List<String> parseBody(String serialNums) {
       return Arrays.stream(serialNums.split(";")).collect(Collectors.toList());

    }
}

package com.example.PKI.controller;

import com.example.PKI.dtos.NewCertificateDTO;
import com.example.PKI.model.CertificateData;
import com.example.PKI.service.CertificateIssuingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyStoreException;

@RestController
@RequestMapping(value = "api/certificates")
public class CertificateController {
    @Autowired
    CertificateIssuingService certificateIssuingService;

    @PostMapping(value = "/createRoot")
    public ResponseEntity<String> createRootCert() {
        var root = certificateIssuingService.issueCertificate();

        if (root == null)
            return new ResponseEntity<>("Root certificate already exists!", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("Root certificate successfully created!", HttpStatus.OK);
    }

    @PostMapping(value = "/createNewCertificate")
    public ResponseEntity<String> createNewCertificate(@RequestBody NewCertificateDTO newCertificateDTO){

        CertificateData createdCert = null;
        try {
            createdCert = certificateIssuingService.issueNewCertificate(newCertificateDTO);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Certificate failed to create ex!", HttpStatus.BAD_REQUEST);
        }
        if (createdCert == null)
            return new ResponseEntity<>("Certificate failed to create!", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("New certificate successfully created!", HttpStatus.OK);
    }
}

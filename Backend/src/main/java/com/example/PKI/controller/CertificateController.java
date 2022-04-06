package com.example.PKI.controller;

import com.example.PKI.service.CertificateIssuingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

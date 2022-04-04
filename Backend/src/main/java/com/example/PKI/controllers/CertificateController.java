package com.example.PKI.controllers;

import com.example.PKI.dtos.NewCertificateDTO;
import com.example.PKI.model.Certificate;
import com.example.PKI.services.RootCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/certificates")
public class CertificateController {
    @Autowired
    RootCertificateService rootCertificateService;

    @PostMapping(value = "/createRoot", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Certificate createRootCert(@RequestBody NewCertificateDTO dto) {
        return rootCertificateService.issueCertificate(dto);
    }
}

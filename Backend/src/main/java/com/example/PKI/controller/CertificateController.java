package com.example.PKI.controller;

import com.example.PKI.dto.NewCertificateDTO;
import com.example.PKI.model.CertificateData;
import com.example.PKI.model.User;
import com.example.PKI.service.CertificateIssuingService;
import com.example.PKI.service.CertificateReadService;
import com.example.PKI.service.ocsp.OcspClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;

@RestController
@RequestMapping(value = "api/certificates")
public class CertificateController {
    @Autowired
    CertificateIssuingService certificateIssuingService;

    @Autowired
    private CertificateReadService certificateReadService;

    @Autowired
    private OcspClientService ocspClientService;

    @PostMapping(value = "/createRoot")
    public ResponseEntity<String> createRootCert() {
        var root = certificateIssuingService.issueRootCertificate();
        if (root == null)
            return new ResponseEntity<>("Root certificate already exists!", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Root certificate successfully created!", HttpStatus.OK);
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeCertificate(@RequestBody String serialNumber){
        ocspClientService.revokeCertificate(serialNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<?> findAll() throws IOException {
        User user=UserController.getLoggedinUser();
        return ResponseEntity.ok(certificateReadService.findAllByUserRole(user.getRole(),user.getEmail()));
    }

    @PostMapping(value = "/createNewCertificate")
    public ResponseEntity<String> createNewCertificate(@RequestBody NewCertificateDTO newCertificateDTO){
        CertificateData createdCert = null;
        try {
            createdCert = certificateIssuingService.issueNewCertificate(newCertificateDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (createdCert == null)
            return new ResponseEntity<>("Certificate failed to create!", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("New certificate successfully created!", HttpStatus.OK);
    }
}

package com.example.PKI.dtos;

import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;

public class CertificateDTO {
    private SubjectData subject;
    private IssuerData issuer;
    private String serialNumber;
}

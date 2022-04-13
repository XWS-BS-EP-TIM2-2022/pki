package com.example.PKI.dto;

import com.example.PKI.data.IssuerData;
import com.example.PKI.data.SubjectData;

import java.util.Date;

public class CertificateDTO {
    private String serialNumber;
    private String subject;
    private String issuer;
    private Date validFrom;
    private Date validTo;
    private boolean isRevoked;
    private String version;
    private String name;
    private String signatureAlgorithm;
    private String publicKey;


    public CertificateDTO(String serialNumber, String subject, String issuer, Date validFrom, Date validTo, boolean isRevoked, String version, String name, String signatureAlgorithm, String publicKey) {
        this.serialNumber = serialNumber;
        this.subject = subject;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isRevoked = isRevoked;
        this.version = version;
        this.name = name;
        this.signatureAlgorithm = signatureAlgorithm;
        this.publicKey = publicKey;
    }



    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}

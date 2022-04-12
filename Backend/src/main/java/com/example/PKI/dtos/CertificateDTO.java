package com.example.PKI.dtos;

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

    public CertificateDTO(String serialNumber, String subject, String issuer, Date validFrom, Date validTo, boolean isRevoked) {
        this.serialNumber = serialNumber;
        this.subject = subject;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isRevoked = isRevoked;
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
}

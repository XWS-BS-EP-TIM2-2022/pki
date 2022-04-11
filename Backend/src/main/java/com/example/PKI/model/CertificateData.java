package com.example.PKI.model;

import com.example.PKI.model.enumerations.CertificateLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "certificates")
public class CertificateData {
    @Id
    private String serialNumber;
    public String issuerEmail;
    public String issuerCertificateSerialNum;
    public String subjectEmail;
    private CertificateLevel level;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    public CertificateData() {}

    public CertificateData(String serialNumber, String issuerEmail, String issuerCertificateSerialNum, String subjectEmail, CertificateLevel level, User user) {
        this.serialNumber = serialNumber;
        this.issuerEmail = issuerEmail;
        this.issuerCertificateSerialNum = issuerCertificateSerialNum;
        this.subjectEmail = subjectEmail;
        this.level = level;
        this.user = user;
    }

    public String getIssuerCertificateSerialNum() {
        return issuerCertificateSerialNum;
    }

    public void setIssuerCertificateSerialNum(String issuerCertificateSerialNum) {
        this.issuerCertificateSerialNum = issuerCertificateSerialNum;
    }

    public CertificateData(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuerEmail() {
        return issuerEmail;
    }

    public void setIssuerEmail(String issuerEmail) {
        this.issuerEmail = issuerEmail;
    }

    public String getSubjectEmail() {
        return subjectEmail;
    }

    public void setSubjectEmail(String subjectEmail) {
        this.subjectEmail = subjectEmail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CertificateLevel getLevel() {
        return level;
    }

    public CertificateData setLevel(CertificateLevel level) {
        this.level = level;
        return this;
    }

    @JsonIgnore
    public boolean isRoot(){
        return level.equals(CertificateLevel.Root);
    }

    @JsonIgnore
    public boolean isEndUser(){
        return level.equals(CertificateLevel.End);
    }
}

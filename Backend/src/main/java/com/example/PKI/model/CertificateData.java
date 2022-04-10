package com.example.PKI.model;

import com.example.PKI.model.enumerations.CertificateLevel;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "certificates")
public class CertificateData {
    @Id
    private String serialNumber;
    public String issuer;
    public String subject;
    private CertificateLevel level;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    public CertificateData() {}

    public CertificateData(String serialNumber, String issuer,String subject, User user) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.user = user;
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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public void setLevel(CertificateLevel level) {
        this.level = level;
    }
}

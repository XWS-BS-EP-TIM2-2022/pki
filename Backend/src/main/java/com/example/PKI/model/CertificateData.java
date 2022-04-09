package com.example.PKI.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "certificates")
public class CertificateData {
    @Id
    private String serialNumber;
    @Column(nullable = true)
    public String signatureAlgorithm;
    @Column(nullable = true)
    public String issuer;
    @Column(nullable = true)
    public Date validFrom;
    @Column(nullable = true)
    public Date validTo;
    @Column(nullable = true)
    public String subject;
    @Column(nullable = true)
    private boolean isWithdrawn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    public CertificateData() {}

    public CertificateData(String serialNumber, String signatureAlgorithm, String issuer,
                           Date validFrom, Date validTo, String subject, boolean isWithdrawn, User user) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.isWithdrawn = isWithdrawn;
        this.user = user;
    }

    public CertificateData(String serialNumber, boolean isWithdrawn) {
        this.serialNumber = serialNumber;
        this.isWithdrawn = isWithdrawn;
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
}

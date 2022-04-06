package com.example.PKI.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "certificates")
public class CertificateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String serialNumber;
    @Column(nullable = false)
    public String signatureAlgorithm;
    @Column(nullable = false)
    public String issuer;
    @Column(nullable = false)
    public Date validFrom;
    @Column(nullable = false)
    public Date validTo;
    @Column(nullable = false)
    public String subject;
    @Column(nullable = false)
    private boolean isWithdrawn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User userId;

    public CertificateData() {}

    public CertificateData(String serialNumber, String signatureAlgorithm, String issuer,
                           Date validFrom, Date validTo, String subject, boolean isWithdrawn, User userId) {
        this.serialNumber = serialNumber;
        this.signatureAlgorithm = signatureAlgorithm;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.subject = subject;
        this.isWithdrawn = isWithdrawn;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isWithdrawn() {
        return isWithdrawn;
    }

    public void setWithdrawn(boolean withdrawn) {
        isWithdrawn = withdrawn;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}

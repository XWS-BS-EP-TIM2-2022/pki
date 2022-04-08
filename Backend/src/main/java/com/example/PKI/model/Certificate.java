package com.example.PKI.model;

import javax.persistence.*;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    private String serialNumber;
    @Column(nullable = false)
    public String issuer;
    @Column(nullable = false)
    public String subject;
    @Column(nullable = false)
    private boolean isRevoked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User user;

    public Certificate() {}

    public Certificate(String serialNumber, String issuer, String subject, boolean isRevoked, User user) {
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.subject = subject;
        this.isRevoked = isRevoked;
        this.user = user;
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

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.example.PKI.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class RevokedCertificate {
    @Id
    private String serialNumber;
    private Boolean revoked;
    private Date timestamp;

    public RevokedCertificate() {
    }

    public RevokedCertificate(Boolean revoked, Date timestamp) {
        this.revoked = revoked;
        this.timestamp = timestamp;
    }

    public RevokedCertificate(String serialNumber, Boolean revoked, Date timestamp) {
        this.serialNumber = serialNumber;
        this.revoked = revoked;
        this.timestamp = timestamp;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

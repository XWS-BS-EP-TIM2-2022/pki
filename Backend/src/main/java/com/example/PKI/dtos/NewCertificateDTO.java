package com.example.PKI.dtos;

import java.util.Date;
import java.util.List;

public class NewCertificateDTO {
    private String issuerSerialNumber;
    private Date validFrom;
    private Date validTo;
    private List<Integer> keyUsages;
    private int subjectId;
    private int issuerId;
    private boolean isCA;
    public List<Integer> addKeyUsage(Integer i){
         keyUsages.add(i);
         return keyUsages;
    }
    public String getIssuerSerialNumber() {
        return issuerSerialNumber;
    }

    public void setIssuerSerialNumber(String issuerSerialNumber) {
        this.issuerSerialNumber = issuerSerialNumber;
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

    public List<Integer> getKeyUsages() {
        return keyUsages;
    }

    public void setKeyUsages(List<Integer> keyUsages) {
        this.keyUsages = keyUsages;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(int issuerId) {
        this.issuerId = issuerId;
    }

    public boolean getIsCA() {
        return isCA;
    }

    public void setIsCA(boolean CA) {
        isCA = CA;
    }
}

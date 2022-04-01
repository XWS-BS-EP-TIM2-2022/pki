package com.example.PKI.model;

public class User {

    private long id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private boolean admin;
    private boolean endEntity;
    private boolean isCA;

    public User() {
    }

    public User(long id, String email, String password, String name, String surname, String address, boolean admin, boolean endEntity, boolean isCA) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.admin = admin;
        this.endEntity = endEntity;
        this.isCA = isCA;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEndEntity() {
        return endEntity;
    }

    public void setEndEntity(boolean endEntity) {
        this.endEntity = endEntity;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }
}
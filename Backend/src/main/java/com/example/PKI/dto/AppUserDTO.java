package com.example.PKI.dto;

import com.example.PKI.model.AppUser;

public class AppUserDTO {

    private long id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private boolean admin;
    private boolean endEntity;
    private boolean isCA;

    public AppUserDTO() {
    }

    public AppUserDTO(long id, String email, String password, String name, String surname, String address, boolean admin, boolean endEntity, boolean isCA) {
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

    public AppUserDTO(AppUser appUser) {
        this(appUser.getId(),
            appUser.getEmail(),
            appUser.getPassword(),
            appUser.getName(),
            appUser.getSurname(),
            appUser.getAddress(),
            appUser.isAdmin(),
            appUser.isEndEntity(),
            appUser.isCA());
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isEndEntity() {
        return endEntity;
    }

    public boolean isCA() {
        return isCA;
    }
}

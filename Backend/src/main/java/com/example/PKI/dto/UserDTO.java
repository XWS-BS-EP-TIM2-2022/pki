package com.example.PKI.dto;

import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.Role;

public class UserDTO {

    private long id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private Role role;
    private String commonName;
    private String organizationName;

    public UserDTO() {
    }

    public UserDTO(long id, String email, String password, String name, String surname, String address, Role role, String commonName, String organizationName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.role = role;
        this.commonName = commonName;
        this.organizationName = organizationName;
    }

    public UserDTO(User appUser) {
        this(appUser.getId(),
            appUser.getEmail(),
            appUser.getPassword(),
            appUser.getName(),
            appUser.getSurname(),
            appUser.getAddress(),
            appUser.getRole(),
            appUser.getCommonName(),
            appUser.getOrganizationName());
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

    public Role getRole() {
        return role;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getOrganizationName() {
        return organizationName;
    }
}

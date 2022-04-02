package com.example.PKI.dto;

public class LoginDTO {
    private String email;
    private String password;
    private Long id;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public LoginDTO() {}

    public LoginDTO(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
}

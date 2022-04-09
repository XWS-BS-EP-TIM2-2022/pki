package com.example.PKI.dto;

import com.example.PKI.model.User;

public class LoginResponseDTO {

    private long id;
    private UserTokenState userTokenState;

    public LoginResponseDTO(long id, UserTokenState userTokenState) {
        this.id = id;
        this.userTokenState = userTokenState;
    }
    public LoginResponseDTO(User appUser, UserTokenState userTokenState) {
        this.id = appUser.getId();
        this.userTokenState = userTokenState;
    }

    public LoginResponseDTO() {
    }

    public long getId() {
        return id;
    }

    public UserTokenState getUserTokenState() {
        return userTokenState;
    }
}

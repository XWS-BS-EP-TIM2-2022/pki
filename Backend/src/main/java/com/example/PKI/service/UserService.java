package com.example.PKI.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.PKI.model.User;

public interface UserService extends Service<User>, UserDetailsService {

}

package com.example.PKI.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.PKI.model.AppUser;

public interface AppUserService extends Service<AppUser>, UserDetailsService {

}

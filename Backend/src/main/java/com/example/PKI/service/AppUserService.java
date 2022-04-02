package com.example.PKI.service;

import com.example.PKI.model.AppUser;
import com.example.PKI.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser findOne(long id) { return appUserRepository.findById(id).orElse(null); }

    public List<AppUser> findAll() { return appUserRepository.findAll(); }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public void remove(long id) { appUserRepository.deleteById(id); }

    public AppUser findByEmail(String email) { return appUserRepository.findByEmail(email); }
}

package com.example.PKI.service;

import com.example.PKI.model.AppUser;
import com.example.PKI.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserServiceImpl implements AppUserService{

    @Autowired
    private AppUserRepository appUserRepository;
    @Override
    public AppUser findOne(long id) { return appUserRepository.findById(id).orElse(null); }
    @Override
    public List<AppUser> findAll() { return appUserRepository.findAll(); }
    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }
    @Override
    public void remove(long id) { appUserRepository.deleteById(id); }
    @Override
    public AppUser findByEmail(String email) { return appUserRepository.findByEmail(email); }
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByEmail(username);
	}
}

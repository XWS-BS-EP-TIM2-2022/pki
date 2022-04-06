package com.example.PKI.service;

import com.example.PKI.model.User;
import com.example.PKI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository appUserRepository;
    @Override
    public User findOne(long id) { return appUserRepository.findById(id).orElse(null); }
    @Override
    public List<User> findAll() { return appUserRepository.findAll(); }
    @Override
    public User save(User appUser) {
        return appUserRepository.save(appUser);
    }
    @Override
    public void remove(long id) { appUserRepository.deleteById(id); }
    @Override
    public User findByEmail(String email) { return appUserRepository.findByEmail(email); }
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByEmail(username);
	}
}

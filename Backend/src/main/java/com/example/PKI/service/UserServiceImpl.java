package com.example.PKI.service;

import com.example.PKI.exception.PasswordNotSecuredException;
import com.example.PKI.model.User;
import com.example.PKI.repository.RoleRepository;
import com.example.PKI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository appUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public UserServiceImpl(){}

    @Autowired
    public UserServiceImpl(UserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public User findOne(long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return appUserRepository.findAll();
    }

    @Override
    public User save(User appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(roleRepository.findByName(appUser.getRole().getName()));
        return appUserRepository.save(appUser);
    }

    @Override
    public void remove(long id) {
        appUserRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = appUserRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", email));
        } else {
            return user;
        }
    }

    @Override
    public User getLoggedInUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(principal);
    }
}

package com.example.PKI.controller;

import com.example.PKI.dto.UserDTO;
import com.example.PKI.dto.LoginDTO;
import com.example.PKI.dto.LoginResponseDTO;
import com.example.PKI.dto.UserTokenState;
import com.example.PKI.model.User;
import com.example.PKI.service.UserServiceImpl;
import com.example.PKI.util.TokenUtils;
import com.example.PKI.verification.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl appUserService;
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Lazy
    private VerificationTokenService verificationTokenService;
    @Lazy
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> save(@RequestBody UserDTO appUserDTO) {

        if (appUserDTO.getEmail() == null || appUserDTO.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(appUserService.findByEmail(appUserDTO.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //AppUser appUser = new AppUser(appUserDTO.getId(), appUserDTO.getEmail(), passwordEncoder.encode(appUserDTO.getPassword()), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), appUserDTO.isAdmin(), appUserDTO.isEndEntity(), appUserDTO.isCA());
        User appUser = new User(appUserDTO.getId(), appUserDTO.getEmail(), appUserDTO.getPassword(), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), appUserDTO.getRole(), appUserDTO.getCommonName(), appUserDTO.getOrganizationName());
        appUser = appUserService.save(appUser);
        return new ResponseEntity<>(new UserDTO(appUser), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(appUser.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();
        UserTokenState userTokenState = new UserTokenState(jwt, expiresIn);

        return new ResponseEntity<>(new LoginResponseDTO(appUser, userTokenState), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> findAll() {
        Collection<User> appUsers = appUserService.findAll();
        Collection<UserDTO> appUserDTOS = new ArrayList<>();
        for (User appUser : appUsers) {
            appUserDTOS.add(new UserDTO(appUser));
        }

        return new ResponseEntity<>(appUserDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> findOne(@PathVariable("id") long id) {
        User appUser = appUserService.findOne(id);

        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new UserDTO(appUser), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") long id) {
        User appUser = appUserService.findOne(id);
        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        appUserService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/update")
    public ResponseEntity<UserDTO> update(@RequestBody UserDTO appUserDTO) {
        User appUser = appUserService.findOne(appUserDTO.getId());

        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        appUser.setName(appUserDTO.getName());
        appUser.setSurname(appUserDTO.getSurname());
        appUser.setAddress(appUserDTO.getAddress());
        appUser.setRole(appUserDTO.getRole());
        appUser.setCommonName(appUserDTO.getCommonName());
        appUser.setOrganizationName((appUserDTO.getOrganizationName()));

        appUser = appUserService.save(appUser);
        return new ResponseEntity<>(new UserDTO(appUser), HttpStatus.OK);
    }
}

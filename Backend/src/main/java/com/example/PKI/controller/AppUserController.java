package com.example.PKI.controller;

import com.example.PKI.dto.AppUserDTO;
import com.example.PKI.dto.LoginDTO;
import com.example.PKI.dto.LoginResponseDTO;
import com.example.PKI.dto.UserTokenState;
import com.example.PKI.model.AppUser;
import com.example.PKI.service.AppUserService;
import com.example.PKI.util.TokenUtils;
import com.example.PKI.verification.VerificationToken;
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
public class AppUserController {

    @Autowired
    private AppUserService appUserService;
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
    public ResponseEntity<AppUserDTO> save(@RequestBody AppUserDTO appUserDTO) {

        if (appUserDTO.getEmail() == null || appUserDTO.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(appUserService.findByEmail(appUserDTO.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //AppUser appUser = new AppUser(appUserDTO.getId(), appUserDTO.getEmail(), passwordEncoder.encode(appUserDTO.getPassword()), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), appUserDTO.isAdmin(), appUserDTO.isEndEntity(), appUserDTO.isCA());
        AppUser appUser = new AppUser(appUserDTO.getId(), appUserDTO.getEmail(), appUserDTO.getPassword(), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), appUserDTO.isAdmin(), appUserDTO.isEndEntity(), appUserDTO.isCA());
        appUser = appUserService.save(appUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(null, token, appUser, new Date());
        verificationTokenService.save(verificationToken);

        return new ResponseEntity<>(new AppUserDTO(appUser), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(appUser.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();
        UserTokenState userTokenState = new UserTokenState(jwt, expiresIn);

        return new ResponseEntity<>(new LoginResponseDTO(appUser, userTokenState), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AppUserDTO>> findAll() {
        Collection<AppUser> appUsers = appUserService.findAll();
        Collection<AppUserDTO> appUserDTOS = new ArrayList<>();
        for (AppUser appUser : appUsers) {
            appUserDTOS.add(new AppUserDTO(appUser));
        }

        return new ResponseEntity<>(appUserDTOS, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserDTO> findOne(@PathVariable("id") long id) {
        AppUser appUser = appUserService.findOne(id);

        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new AppUserDTO(appUser), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") long id) {
        AppUser appUser = appUserService.findOne(id);
        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        appUserService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/update")
    public ResponseEntity<AppUserDTO> update(@RequestBody AppUserDTO appUserDTO) {
        AppUser appUser = appUserService.findOne(appUserDTO.getId());

        if(appUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        appUser.setName(appUserDTO.getName());
        appUser.setSurname(appUserDTO.getSurname());
        appUser.setAddress(appUserDTO.getAddress());
        appUser.setAdmin(appUserDTO.isAdmin());
        appUser.setEndEntity(appUserDTO.isEndEntity());
        appUser.setCA(appUserDTO.isCA());

        appUser = appUserService.save(appUser);
        return new ResponseEntity<>(new AppUserDTO(appUser), HttpStatus.OK);
    }
}

package com.example.PKI.controller;

import com.example.PKI.dto.LoginDTO;
import com.example.PKI.dto.LoginResponseDTO;
import com.example.PKI.dto.UserDTO;
import com.example.PKI.dto.UserTokenState;
import com.example.PKI.model.User;
import com.example.PKI.model.enumerations.Role;
import com.example.PKI.service.UserServiceImpl;
import com.example.PKI.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl appUserService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> save(@RequestBody UserDTO appUserDTO) {

        if (appUserDTO.getEmail() == null || appUserDTO.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(appUserService.findByEmail(appUserDTO.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(appUserDTO.getRole() == Role.Intermediate) {
            User appUser = new User(appUserDTO.getId(), appUserDTO.getEmail(), appUserDTO.getPassword(), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), Role.Intermediate, appUserDTO.getCommonName(), appUserDTO.getOrganizationName());
            appUser = appUserService.save(appUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } else if(appUserDTO.getRole() == Role.EndUser) {
            User appUser = new User(appUserDTO.getId(), appUserDTO.getEmail(),appUserDTO.getPassword(), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), Role.EndUser, appUserDTO.getCommonName(), appUserDTO.getOrganizationName());
            appUser = appUserService.save(appUser);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(appUser.getEmail());
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

    @GetMapping("/whoami")
    public User getCurrentUser() {
        return appUserService.getLoggedInUser();
    }

    @GetMapping(value="/find-all-clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> findClients() {
        var users = appUserService.findAll();
        var clients = users.stream().filter(user -> user.getRole() == Role.Intermediate || user.getRole() == Role.EndUser)
                .collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity(clients, HttpStatus.OK);
    }
}

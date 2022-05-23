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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO appUserDTO) {
        User appUser = new User(appUserDTO.getId(), appUserDTO.getEmail(), appUserDTO.getPassword(), appUserDTO.getName(), appUserDTO.getSurname(), appUserDTO.getAddress(), appUserDTO.getRole(), appUserDTO.getCommonName(), appUserDTO.getOrganizationName());
        if(appUser.getRole().getName().equals(Role.ADMIN_ROLE) || appUserService.findByEmail(appUser.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        appUserService.save(appUser);
        return new ResponseEntity<>(HttpStatus.OK);
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
    @PreAuthorize("hasRole('ADMIN')")
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
        var currentUser = appUserService.getLoggedInUser();
        var users = appUserService.findAll();
        var clients = users.stream()
                .filter(user -> user.getEmail() != currentUser.getEmail() &&
                        (user.getRole().getName().equals(Role.INTERMEDIATE_ROLE) || user.getRole().getName().equals(Role.END_USER_ROLE)))
                .collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity(clients, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}

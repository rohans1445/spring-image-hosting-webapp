package com.example.imagehosting.controller;

import com.example.imagehosting.dto.LoginRequest;
import com.example.imagehosting.dto.LoginResponse;
import com.example.imagehosting.dto.RegistrationRequest;
import com.example.imagehosting.dto.RegistrationResponse;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.services.UserService;
import com.example.imagehosting.utility.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationProvider authenticationProvider;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationProvider authenticationProvider, UserService userService, JwtUtil jwtUtil) {
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(authentication);
        return new ResponseEntity<>(new LoginResponse(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest){
//        if(userService.isUsernameTaken(registrationRequest.getUsername())){
//            throw new InvalidInputException("That username has been taken.");
//        }
//        if(userService.isEmailTaken(registrationRequest.getEmail())){
//            throw new InvalidInputException("An account already exists with that email.");
//        }
        User user = userService.processRegistrationRequest(registrationRequest);
        RegistrationResponse response = new RegistrationResponse(user.getId(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

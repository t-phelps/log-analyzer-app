package com.loganalyzer.backend.controller;

import com.loganalyzer.backend.config.UsernamePwdAuthenticationProvider;
import com.loganalyzer.backend.dto.CreatAccountRequest;
import com.loganalyzer.backend.dto.LoginRequest;
import com.loganalyzer.backend.jwt.JwtTokenGenerator;
import com.loganalyzer.backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UsernamePwdAuthenticationProvider usernamePwdAuthenticationProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UsernamePwdAuthenticationProvider usernamePwdAuthenticationProvider, AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator) {
        this.authenticationService = authenticationService;
        this.usernamePwdAuthenticationProvider = usernamePwdAuthenticationProvider;
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Endpoint for logging a user in
     * @param loginRequest - login request containing credentials of the user
     * @return - response containing success or failure and their signed jwt if passed
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if(loginRequest.username().isEmpty() || loginRequest.password().isEmpty()) {
            return ResponseEntity.badRequest().body("Failed Login: A field within the request is empty");
        }

        try{
            Authentication  authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );
            ResponseCookie cookie = authenticationService.authenticateUser(authentication.getPrincipal());
            return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("User login successful");
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreatAccountRequest request){
        if(request.email().isEmpty() || request.username().isEmpty() || request.password().isEmpty()) {
            return ResponseEntity.badRequest().body("Failed To Create Account: A field within the request is empty");
        }

        try{
            ResponseCookie cookie = authenticationService.createUser(request);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Account created successfully");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed To Create Account");
        }
    }
}

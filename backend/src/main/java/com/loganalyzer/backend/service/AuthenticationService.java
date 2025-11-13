package com.loganalyzer.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;


import com.loganalyzer.backend.dto.CreateAccountRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.generated.tables.pojos.Users;
import com.loganalyzer.backend.jwt.JwtTokenGenerator;
import com.loganalyzer.backend.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository, JwtTokenGenerator jwtTokenGenerator,
    PasswordEncoder passwordEncoder,  UserDetailsService userDetailsService) {
        this.authenticationRepository = authenticationRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Authenticate user against the db
     * @param principal - the username
     * @return - a response cookie for user, else null
     */
    public ResponseCookie generateUserCookie(Object principal) {
        String username = principal.toString();

        String jws = jwtTokenGenerator.getJwt(username);

        // return a response cookie to be stored in the front end
        return ResponseCookie.from("jwt", jws)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();
    }


    /**
     * TODO most of this could be moved to userDetailsService
     *
     * Create a user in the database
     * @param request - a {@link CreateAccountRequest} with user details
     * @return a response cookie for user
     */
    public ResponseCookie createUser(CreateAccountRequest request) {
        String username = request.username();
        String email  = request.email();
        String password = request.password(); // is this unsafe to do this here? notice in Authentication object, Credentials: [Protected]
        String role = "USER";

        // hash the password using jBCrypt
        String hashedPassword = passwordEncoder.encode(password);

        try {
            // save the user to the database
            authenticationRepository.createUser(new Users(null, email, username, hashedPassword, role, LocalDateTime.now()));
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return generateUserCookie(username);
    }

    private ResponseCookie generateUserCookie(String username){
        String jws = jwtTokenGenerator.getJwt(username);

        return ResponseCookie.from("jwt", jws)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();
    }
}

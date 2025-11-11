package com.loganalyzer.backend.service;

import com.loganalyzer.backend.dto.CreatAccountRequest;
import com.loganalyzer.backend.dto.LoginRequest;
import test.generated.tables.pojos.Users;
import com.loganalyzer.backend.jwt.JwtTokenGenerator;
import com.loganalyzer.backend.repository.AuthenticationRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository, JwtTokenGenerator jwtTokenGenerator) {
        this.authenticationRepository = authenticationRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Authenticate a user against the DB
     * @param loginRequest - the credentials provided in the login request
     */
    public ResponseCookie authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        // get the user stored in the db
        Users user = authenticationRepository.getUser(username);

        if (user != null) {
            String storedHashedPassword = user.getPassword();

            // verify the raw password against the stored hash
            if (BCrypt.checkpw(password, storedHashedPassword)) {
                String jws = jwtTokenGenerator.getJwt(username);

                // return a response cookie to be stored in the front end
                return ResponseCookie.from("jwt", jws)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(3600)
                        .build();
            }
        }

        return null;
    }


    public ResponseCookie createUser(CreatAccountRequest request) {
        String username = request.username();
        String email  = request.email();
        String password = request.password();
        String role = "USER";

        // hash the password using jBCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // save the user to the database
        authenticationRepository.createUser(new Users(null, email, username, hashedPassword, role, LocalDateTime.now()));

        // generate JWT
        String jws = jwtTokenGenerator.getJwt(username);

        // return a response cookie to be stored in the frontend
        return ResponseCookie.from("jwt", jws)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();
    }
}

package service;

import dto.LoginRequest;
import dto.User;
import dto.CreatAccountRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import repository.AuthenticationRepository;
import jwt.JwtTokenGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository,  JwtTokenGenerator jwtTokenGenerator) {
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

        // hash password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        // get the user stored in the db
        Optional<User> user = authenticationRepository.getUser(new User(username, hashedPassword, null));

        if(user.isPresent()){
            if(passwordEncoder.matches(hashedPassword, user.get().getPassword())){
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

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        authenticationRepository.createUser(new User(email, username, hashedPassword, role));
        String jws = jwtTokenGenerator.getJwt(username);

        // return a response cookie to be stored in the front end
        return ResponseCookie.from("jwt", jws)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();
    }
}

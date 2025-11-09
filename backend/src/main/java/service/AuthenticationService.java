package service;

import dto.LoginRequest;
import dto.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String username = loginRequest.username();
        String password = loginRequest.password();

        // hash password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        // get the user stored in the db
        Optional<User> user = authenticationRepository.getUser(new User(email, username, hashedPassword, null));
        if(user.isPresent()){
            String jws = jwtTokenGenerator.getJwt(username);
        }




    }
}

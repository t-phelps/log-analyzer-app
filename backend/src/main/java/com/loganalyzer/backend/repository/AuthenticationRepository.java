package com.loganalyzer.backend.repository;

import com.loganalyzer.backend.dto.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthenticationRepository {



    public Optional<User> getUser(String username) {
        return Optional.empty();
    }

    public void createUser(User user){

    }
}

package com.loganalyzer.backend.config;

import com.loganalyzer.backend.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import test.generated.tables.pojos.Users;

import java.util.List;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public UserDetailsService(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Calls repository to find a user by username
     *
     * @param username the username identifying the user whose data is required.
     * @return - a {@link UserDetails} object
     * @throws UsernameNotFoundException - on user not found in database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users customer = authenticationRepository.getUser(username);
        if(customer == null) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole()));

        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }
}

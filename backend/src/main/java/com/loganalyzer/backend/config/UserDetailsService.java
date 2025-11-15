package com.loganalyzer.backend.config;

import com.loganalyzer.backend.repository.AccountRepository;
import com.loganalyzer.backend.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import test.generated.tables.pojos.Users;

import java.util.List;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsService(AuthenticationRepository authenticationRepository,
                              PasswordEncoder passwordEncoder,
                              AccountRepository accountRepository) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
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

        return new User(customer.getUsername(), customer.getPassword(), authorities);
    }


    /**
     * Service method for changing a users password
     * @param principal - the UserDetails principal object to get the useranme
     * @param oldPassword - users old password
     * @param newPassword - users new password
     */
    public void changePassword(UserDetails principal, String oldPassword, String newPassword) {
        String username = principal.getUsername();

        // hitting this again feels redundant but spring deletes user credentials after authentication, so I don't see another way to access their old password from the db here
        UserDetails userDetails = loadUserByUsername(username);

        if(!passwordEncoder.matches(oldPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        String encodedPassword =  passwordEncoder.encode(newPassword);

        accountRepository.changePassword(username, encodedPassword);

        // fetch the updated userDetails
        UserDetails updatedUserDetails = loadUserByUsername(username);

        // get the new authentication object
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                null,
                updatedUserDetails.getAuthorities());

        // reset the security context with the new authentication object
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}

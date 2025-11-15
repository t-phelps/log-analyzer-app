package com.loganalyzer.backend.controller;

import com.loganalyzer.backend.config.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private UserDetailsService userDetailsService;

    @Autowired
    public AccountController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Allow a user to change their password while logged in
     * @param newPassword - the updated password
     * @return - ok on success, else unauthorized
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam("newPassword") String newPassword,
    @RequestParam("oldPassword") String oldPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()){
            try {
                userDetailsService.changePassword(
                        (UserDetails) authentication.getPrincipal(),
                        oldPassword,
                        newPassword);

                return ResponseEntity.ok("Password Changed Successfully");

            }catch(IllegalArgumentException e){
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

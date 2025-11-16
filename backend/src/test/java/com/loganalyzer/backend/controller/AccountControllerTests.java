package com.loganalyzer.backend.controller;

import com.loganalyzer.backend.service.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTests {

    @Mock
    private CustomUserDetailsService mockCustomUserDetailsService;

    @Mock
    private Authentication mockAuthentication;

    private UserDetails mockUser;

    private AccountController uut;
    @BeforeEach
    public void setUp() {
        this.uut = new AccountController(mockCustomUserDetailsService);

        mockUser = new User(
                "testUser",
                "testPassword",
                List.of(new SimpleGrantedAuthority("USER")));
    }

    @Test
    public void changePassword_callsUserDetailsService() throws Exception {
        // ARRANGE
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

        // ACT
        uut.changePassword("oldPassword", "newPassword");

        // ASSERT

        verify(mockCustomUserDetailsService, times(1))
                .changePassword(mockUser, "oldPassword",  "newPassword");
    }

}

package com.loganalyzer.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    @Bean
    public AuthenticationManager authenticationManager(UsernamePwdAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    /**
     * SecurityFilterChain method to define which api endpoints we should require authorization on
     * @param http - is this the http request coming in from the authorization manager?
     * @return - the built http response
     * @throws Exception - on error
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, UsernamePwdAuthenticationProvider usernamePwdAuthenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.setAllowedOrigins(List.of("http://localhost:3000")); // cors config to allow front end communication
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/auth/login", "/auth/create").permitAll() //
                                .requestMatchers("/upload/file").authenticated() // protected endpoints
                                .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // disable spring custom login
                .sessionManagement(session -> // create stateless session requiring JWT auth
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http.authenticationProvider(usernamePwdAuthenticationProvider); // use our own auth provider in the chaining
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // jwt filter before every request
        return http.build();
    }

    /**
     * Bean for utilizing bcrypt functionalities
     * @return - a Bcrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }

    /**
     * Bean for jwt authentication functionalities
     * @return our Jwt auth filter
     * @throws Exception
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter();
    }


}

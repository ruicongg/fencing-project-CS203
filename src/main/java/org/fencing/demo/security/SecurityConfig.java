package org.fencing.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF protection
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()) // Allow all requests without authentication
            .formLogin().disable(); // Disable the login form
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Might need to change later but auto-generate a random salt internally 
        return new BCryptPasswordEncoder();
    }
}

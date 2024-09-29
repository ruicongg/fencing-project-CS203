package org.fencing.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder encoder() {
        // Might need to change later but auto-generate a random salt internally 
        return new BCryptPasswordEncoder();
    }
}

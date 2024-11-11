package org.fencing.demo.security;

import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminPassword"))
                    .role(Role.ADMIN)
                    .build();
            
            userRepository.save(adminUser);
            
            // Generate and print the JWT token
            String token = jwtService.generateToken(adminUser);
            System.out.println("Admin JWT Token: " + token);
            System.out.println("This token will expire in 24 hours. Use it to authenticate as admin.");
        }
    }
}

package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.fencing.demo.events.*;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.security.*;
import org.fencing.demo.security.auth.*;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private Player player;
    private User user;

    @BeforeEach
    public void setUp() {
        registerRequest = new RegisterRequest("testUser", "password", "test@example.com", "USER", "MALE");
        authenticationRequest = new AuthenticationRequest("testUser", "password");
        player = new Player("testUser", "encodedPassword", "test@example.com", Role.USER, Gender.MALE);
        user = new User("testUser", "encodedPassword", "test@example.com", Role.USER);
    }

    @Test
    public void testRegister() {
        // Arrange
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(Player.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // Assert
        verify(playerRepository).save(any(Player.class));
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    public void testAuthenticate() {
        // Arrange
        when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    public void testAuthenticateUserNotFound() {
        // Arrange
        when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });
    }
}

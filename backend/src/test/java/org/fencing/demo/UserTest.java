package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserNotFoundException;
import org.fencing.demo.user.UserRepository;
import org.fencing.demo.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        MockitoAnnotations.openMocks(this);
    }

    // Test for Post user
    // @Test
    // public void testCreateUser() {
    //     User user = new User("testUser", "password123", "test@example.com", Role.USER);
    //     // Mock the save method to return the same user when called
    //     when(userRepository.save(any(User.class))).thenReturn(user);

    //     User createdUser = userService.addUser(user);

    //     assertNotNull(createdUser);
    //     assertEquals("testUser", createdUser.getUsername());
    //     verify(userRepository, times(1)).save(user);
    // }

    @Test
    public void testGetUser_Success() {
        User user = new User("testUser", "password123", "test@example.com", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(1L);

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUser_Failure() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User foundUser = userService.getUser(1L);

        assertNull(foundUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateUser() {
        User user = new User("testUser", "password123", "test@example.com", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        user.setEmail("updated@example.com");
        User updatedUser = userService.updateUser(1L, user);

        assertNotNull(updatedUser);
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        User user = new User("testUser", "password123", "test@example.com", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_Failure() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testLogin_Success() {
        User user = new User("testUser", "password123", "test@example.com", Role.USER);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        User loggedInUser = null;
        try {
            loggedInUser = userService.login("testUser", "password123");
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
        }

        assertNotNull(loggedInUser);
        assertEquals("testUser", loggedInUser.getUsername());
    }

    @Test
    public void testLogin_Failure() { // Need to change, wrong password case
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login("testUser", "wrongPassword");
        });
    }
}

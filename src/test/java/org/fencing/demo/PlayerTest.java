package org.fencing.demo;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.player.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerTest {

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePlayer() {
        Player player = new Player("testUser", "password123", "test@example.com");
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player createdPlayer = playerService.addPlayer(player);

        assertNotNull(createdPlayer);
        assertEquals("testUser", createdPlayer.getUsername());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    public void testGetPlayer_Success() {
        Player player = new Player("testUser", "password123", "test@example.com");
        player.setId(1L);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Player foundPlayer = playerService.getPlayer(1L);

        assertNotNull(foundPlayer);
        assertEquals("testUser", foundPlayer.getUsername());
        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPlayer_Failure() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        Player foundPlayer = playerService.getPlayer(1L);

        assertNull(foundPlayer);
        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdatePlayer() {
        Player player = new Player("testUser", "password123", "test@example.com");
        player.setId(1L);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        player.setEmail("updated@example.com");
        Player updatedPlayer = playerService.updatePlayer(1L, player);

        assertNotNull(updatedPlayer);
        assertEquals("updated@example.com", updatedPlayer.getEmail());
        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    public void testDeletePlayer_Success() {
        // Arrange
        Player player = new Player("testUser", "password123", "test@example.com");
        player.setId(1L);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        doNothing().when(playerRepository).delete(player);

        // Act
        playerService.deletePlayer(1L);

        // Assert
        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).delete(player);
    }

    @Test
    public void testDeletePlayer_Failure() {

        when(playerRepository.findById(1L)).thenReturn(Optional.empty());


        assertThrows(IllegalArgumentException.class, () -> playerService.deletePlayer(1L));
        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    public void testLogin_Success() {
        Player player = new Player("testUser", "password123", "test@example.com");
        when(playerRepository.findByUsername("testUser")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("password123", player.getPassword())).thenReturn(true);

        Player loggedInPlayer = null;
        try {
            loggedInPlayer = playerService.login("testUser", "password123");
        } catch (InvalidCredentialsException e) {

            e.printStackTrace();
        }

        assertNotNull(loggedInPlayer);
        assertEquals("testUser", loggedInPlayer.getUsername());
    }

    @Test
    public void testLogin_Failure() { // Need to change, wrong password case
        when(playerRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> {
            playerService.login("testUser", "wrongPassword");
        });
    }
}
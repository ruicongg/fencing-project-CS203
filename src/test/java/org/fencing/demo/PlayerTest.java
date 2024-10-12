package org.fencing.demo;

import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.player.PlayerServiceImpl;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.fencing.demo.player.PlayerNotFoundException;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePlayer() {
        Player player = new Player("testUser", "password123", "test@example.com", Role.USER);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player createdPlayer = playerService.addPlayer(player);

        assertNotNull(createdPlayer);
        assertEquals("testUser", createdPlayer.getUsername());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    public void testGetPlayer_Success() {
        Player player = new Player("testUser", "password123", "test@example.com", Role.USER);
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
        Player player = new Player("testUser", "password123", "test@example.com", Role.USER);
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
        Player player = new Player("testUser", "password123", "test@example.com", Role.USER);
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

        assertThrows(PlayerNotFoundException.class, () -> playerService.deletePlayer(1L));
        verify(playerRepository, times(1)).findById(1L);
    }
}
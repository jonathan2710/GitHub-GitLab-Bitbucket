package com.example.vbote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.vbote.entity.User;
import com.example.vbote.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .password("pwd")
                .role("USER")
                .blocked(false)
                .build();
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User created = userService.createUser(user);
        assertNotNull(created);
        assertEquals(1L, created.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_duplicateUsername_throws() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertTrue(ex.getMessage().contains("Username exist"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.getUserById(1L);
        assertEquals("alice", found.getUsername());
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(2L));
    }

    @Test
    void updatUser_changesPasswordWhenProvided() {
        User update = User.builder().username("alice2").password("newpwd").role("ADMIN").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("alice2")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updatUser(1L, update);
        assertEquals("alice2", result.getUsername());
        assertEquals("newpwd", result.getPassword());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void blockAndUnblockUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User blocked = userService.blockUser(1L);
        assertTrue(blocked.getBlocked());

        when(userRepository.findById(1L)).thenReturn(Optional.of(blocked));
        User unblocked = userService.unblockUser(1L);
        assertFalse(unblocked.getBlocked());
    }

    @Test
    void validateCredentials_success_and_failure() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        assertTrue(userService.validateCredentials("alice", "pwd"));
        assertFalse(userService.validateCredentials("alice", "wrong"));
    }

}

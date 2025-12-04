package com.example.vbote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.vbote.entity.Session;
import com.example.vbote.entity.User;
import com.example.vbote.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionService sessionService;

    private User user;
    private Session session;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("alice").password("pwd").role("USER").blocked(false).build();
        session = Session.builder().id(10L).user(user).token("tok").ipAddress("127.0.0.1").active(true).build();
    }

    @Test
    void creatSession_success() {
        when(userService.validateCredentials("alice", "pwd")).thenReturn(true);
        when(userService.getUserByUsername("alice")).thenReturn(Optional.of(user));
        // token uniqueness check - always empty
        when(sessionRepository.findByToken(anyString())).thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> {
            Session s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        Session created = sessionService.creatSession("alice", "pwd", "127.0.0.1");
        assertNotNull(created);
        assertEquals(100L, created.getId());
        assertEquals(user, created.getUser());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void creatSession_invalidCredentials_throws() {
        when(userService.validateCredentials("alice", "pwd")).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> sessionService.creatSession("alice", "pwd", "127.0.0.1"));
        assertTrue(ex.getMessage().contains("Invalid credentials"));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getActiveSessions_delegatesToRepository() {
        when(sessionRepository.findByActiveTrue()).thenReturn(List.of(session));
        List<Session> list = sessionService.getActiveSessions();
        assertEquals(1, list.size());
        assertEquals(session, list.get(0));
    }

    @Test
    void closeSession_byToken_deactivatesAndSaves() {
        when(sessionRepository.findByTokenAndActiveTrue("tok")).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        sessionService.closeSession("tok");

        ArgumentCaptor<Session> cap = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(cap.capture());
        Session saved = cap.getValue();
        assertFalse(saved.getActive());
    }

    @Test
    void closeSessionById_deactivatesAndSaves() {
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        sessionService.closeSessionById(10L);

        ArgumentCaptor<Session> cap = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(cap.capture());
        Session saved = cap.getValue();
        assertFalse(saved.getActive());
    }

    @Test
    void isValidActiveSession_returnsRepositoryValue() {
        when(sessionRepository.existsByTokenAndActiveTrue("tok")).thenReturn(true);
        assertTrue(sessionService.isValidActiveSession("tok"));
        when(sessionRepository.existsByTokenAndActiveTrue("tok")).thenReturn(false);
        assertFalse(sessionService.isValidActiveSession("tok"));
    }

    @Test
    void getUserByToken_returnsUser_whenActive() {
        when(sessionRepository.findByTokenAndActiveTrue("tok")).thenReturn(Optional.of(session));
        User got = sessionService.getUserByToken("tok");
        assertEquals(user, got);
    }

    @Test
    void getUserByToken_noActiveSession_throws() {
        when(sessionRepository.findByTokenAndActiveTrue("tok")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> sessionService.getUserByToken("tok"));
    }

}

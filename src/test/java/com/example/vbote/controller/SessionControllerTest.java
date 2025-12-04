package com.example.vbote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.vbote.entity.Session;
import com.example.vbote.entity.User;
import com.example.vbote.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

class SessionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Session session;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();

        user = User.builder().id(1L).username("alice").role("USER").blocked(false).build();
        session = Session.builder().id(10L).user(user).token("tok-123").ipAddress("127.0.0.1").active(true).build();
    }

    @Test
    void postLogin_success_returnsCreated() throws Exception {
        when(sessionService.creatSession("alice", "pwd", "127.0.0.1")).thenReturn(session);

        Map<String,String> payload = new HashMap<>();
        payload.put("username", "alice");
        payload.put("password", "pwd");

        mockMvc.perform(post("/api/sessions/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("tok-123"))
            .andExpect(jsonPath("$.user.username").value("alice"));
    }

    @Test
    void postLogin_missingPassword_returnsBadRequest() throws Exception {
        Map<String,String> payload = new HashMap<>();
        payload.put("username", "alice");

        mockMvc.perform(post("/api/sessions/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void postLogin_invalidCredentials_returnsUnauthorized() throws Exception {
        when(sessionService.creatSession("alice", "wrong", "127.0.0.1")).thenThrow(new RuntimeException("Invalid credentials"));

        Map<String,String> payload = new HashMap<>();
        payload.put("username", "alice");
        payload.put("password", "wrong");

        mockMvc.perform(post("/api/sessions/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getActiveSessions_returnsList() throws Exception {
        when(sessionService.getActiveSessions()).thenReturn(List.of(session));

        mockMvc.perform(get("/api/sessions/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].token").value("tok-123"))
            .andExpect(jsonPath("$[0].user.username").value("alice"));
    }

}

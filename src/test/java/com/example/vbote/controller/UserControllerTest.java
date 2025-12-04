package com.example.vbote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.vbote.entity.User;
import com.example.vbote.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void postCreateUser_returnsCreated() throws Exception {
        User u = User.builder().id(10L).username("bob").role("USER").blocked(false).build();
        when(userService.createUser(any(User.class))).thenReturn(u);

        User payload = User.builder().username("bob").password("secret").role("USER").build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        User u1 = User.builder().id(1L).username("a").role("USER").blocked(false).build();
        User u2 = User.builder().id(2L).username("b").role("ADMIN").blocked(false).build();
        when(userService.getAllUsers()).thenReturn(Arrays.asList(u1,u2));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("a"))
            .andExpect(jsonPath("$[1].username").value("b"));
    }

}

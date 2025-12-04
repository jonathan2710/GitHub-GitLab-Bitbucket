package com.example.vbote.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vbote.entity.Session;
import com.example.vbote.entity.User;
import com.example.vbote.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    
    @PostMapping("/login")
    public ResponseEntity<Session> createSession(@RequestBody Map <String,String> credential,
                                                HttpServletRequest request) {
        String username = null;
        try {
            username = credential.get("username");
            String password = credential.get("password");
            String ipAddress = getClientIp(request);

            if (username == null || password == null) {
                return ResponseEntity.badRequest().build();
            }

            Session session = sessionService.creatSession(username, password, ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(session);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
 
    @GetMapping("/active")
    public ResponseEntity<List<Session>> getActiveSessions() {
        List<Session> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Session>> getActiveSessionsByUser(@PathVariable Long userId) {
        try {
            List<Session> sessions = sessionService.getActiveSessionsByUser(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<Session>> getAllSessionsByUser(@PathVariable Long userId) {
        try {
            List<Session> sessions = sessionService.getAllSessionsByUser(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Session> getSessionByToken(@PathVariable String token) {
        Optional<Session> sessionOpt = sessionService.getSessionByToken(token);
        return sessionOpt.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/token/{token}/active")
    public Optional<Session> getActiveSessionByToken(@PathVariable String token) {
        return sessionService.getActiveSessionByToken(token);
    }

    @GetMapping("/token/{token}/validate")
    public ResponseEntity<Boolean> isValidActiveSession(@PathVariable String token) {
        boolean isValid = sessionService.isValidActiveSession(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/token/{token}/user")
    public ResponseEntity<User> getUserByToken(@PathVariable String token) {
        try {
            User user = sessionService.getUserByToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        try {
            Session session = sessionService.getSessionById(id);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countActiveSessionByUser(@PathVariable Long userId) {
        try {
            long count = sessionService.countActiveSessionByUser(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/close/{sessionId}")
    public ResponseEntity<Void> closeSessionById(@PathVariable Long sessionId) {
        try {
            sessionService.closeSessionById(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/logout/user/{userId}")
    public ResponseEntity<Void> logoutAllUserSessions(@PathVariable Long userId) {
        try {
            sessionService.closeAllUserSessions(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity<Void> logoutSessionByToken(@PathVariable String token) {
        try {
            sessionService.closeSession(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}

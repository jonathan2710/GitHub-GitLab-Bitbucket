package com.example.vbote.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vbote.entity.Session;
import com.example.vbote.entity.User;
import com.example.vbote.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserService userService;

    public Session creatSession(String username, String password, String ipAddress){
        //validate credential
        if (!userService.validateCredentials(username, password)){
            throw new RuntimeException("Invalid credentials");
        }

        User user = userService.getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBlocked()){
            throw new RuntimeException("user blocked");
        }

        String token = generateUniqueToken();

        Session session = Session.builder()
            .user(user)
            .token(token)
            .ipAddress(ipAddress)
            .active(true)
            .build();

        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<Session> getActiveSessions(){
        return sessionRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Session> getActiveSessionsByUser(Long userId){
        User user = userService.getUserById(userId);
        return sessionRepository.findByUserAndActiveTrue(user);
    }

    @Transactional(readOnly = true)
    public List<Session> getAllSessionsByUser(Long userId){
        User user = userService.getUserById(userId);
        return sessionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionByToken(String token){
        return sessionRepository.findByToken(token);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getActiveSessionByToken(String token){
        return sessionRepository.findByTokenAndActiveTrue(token);
    }    

    @Transactional(readOnly = true)
    public boolean isValidActiveSession(String token){
        return sessionRepository.existsByTokenAndActiveTrue(token);
    }

    public long countActiveSessionByUser(Long userId){
        User user = userService.getUserById(userId);
        return sessionRepository.countByUserAndActiveTrue(user);
    }

    public void closeAllUserSessions(Long userId){
        User user = userService.getUserById(userId);
        sessionRepository.desactiveAllUserSessions(user);
    }

    public void closeSessionById(Long sessionId){
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(()-> new RuntimeException("Session not found: "+sessionId));
        
            session.setActive(false);
            sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Session getSessionById(Long id){
        return sessionRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Session not found: "+id));
    }

    private String generateUniqueToken() {
        String token;
        do{
            token = UUID.randomUUID().toString() + "-"+ System.currentTimeMillis();
        }while (sessionRepository.findByToken(token).isPresent());
        return token;
    }

    @Transactional(readOnly = true)
    public User getUserByToken(String token){
        return sessionRepository.findByTokenAndActiveTrue(token)
                .map(Session::getUser)
                .orElseThrow(()-> new RuntimeException("No active session found"));
    }

    public void closeSession(String token) {
        Optional<Session> sessionOpt = sessionRepository.findByTokenAndActiveTrue(token);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("No active session found for token: " + token);
        }

        Session session = sessionOpt.get();
        session.setActive(false);
        sessionRepository.save(session);
    }


}

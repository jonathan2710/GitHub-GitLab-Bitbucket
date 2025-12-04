package com.example.vbote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.vbote.entity.Session;
import com.example.vbote.entity.User;

import java.util.List;
import java.util.Optional;


@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    Optional<Session> findByToken(String token); 

    Optional<Session> findByTokenAndActiveTrue(String token);

    List<Session> findByUserAndActiveTrue(User user);

    List<Session> findByActiveTrue();

    List<Session> findByUser(User user);

    boolean existsByTokenAndActiveTrue(String token);

    @Modifying
    @Query("UPDATE Session s SET s.active = false WHERE s.user = :user AND s.active = true")
    void desactiveAllUserSessions(User user);

    @Modifying
    @Query("UPDATE Session s SET s.active = false WHERE s.token = :token")
    void desactiveSessionsByToken(String token);

    long countByUserAndActiveTrue(User user);
}

package com.example.vbote.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.vbote.entity.User;

@Repository
public interface UserRepository extends JpaRepository <User,Long>{

    Optional<User> findByUsername(String username);

       boolean existsByUsername(String username);

    List<User> findByBlockedFalse();

    List<User> findByRole(String role);

    List<User> findByRoleAndBlockedFalse(String role);

    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username = :username) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:blocked IS NULL OR u.blocked = :blocked)")
    List<User> findUsersWithFilters(@Param("username") String username, 
                                    @Param("role") String role, 
                                    @Param("blocked") Boolean blocked);


}

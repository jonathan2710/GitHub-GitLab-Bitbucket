package com.example.vbote.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vbote.entity.User;
import com.example.vbote.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user){
        if (userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username exist: "+user.getUsername());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found: "+id));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsers(){
        return userRepository.findByBlockedFalse();
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role){
        return userRepository.findByRole(role);
    }

    public User updatUser(Long id, User userUpdate){
        User existingUser = getUserById(id);

        if(!existingUser.getUsername().equals(userUpdate.getUsername()) && 
            userRepository.existsByUsername(userUpdate.getUsername())){
                throw new RuntimeException("username exists: "+userUpdate.getUsername());
        }

        existingUser.setUsername(userUpdate.getUsername());
        existingUser.setRole(userUpdate.getRole());

        if (userUpdate.getPassword() != null && !userUpdate.getPassword().trim().isEmpty()){
            existingUser.setPassword(userUpdate.getPassword());
        }

        return userRepository.save(existingUser);
    }

    public User blockUser(Long id){
        User user = getUserById(id);
        user.setBlocked(true);
        return userRepository.save(user);
    }

    public User unblockUser(Long id){
        User user = getUserById(id);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("User not found: "+id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean validateCredentials (String username, String password){
        Optional<User> userOpt = userRepository.findByUsername(username);
        if ( (userOpt.isEmpty())) {
            return false;
        }

        User user = userOpt.get();
        if (user.getBlocked()){
            throw new RuntimeException("User blocked");
        }

        boolean matches = user.getPassword().equals(password);
        return matches;
    }

    @Transactional(readOnly = true)
    public List<User> getUserWithFilters(String username, String role, Boolean blocked) {
        return userRepository.findUsersWithFilters(username, role, blocked);
    }
}

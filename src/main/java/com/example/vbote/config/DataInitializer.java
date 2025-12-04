package com.example.vbote.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.vbote.entity.User;
import com.example.vbote.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder().username("admin").password("123456").role("ADMIN").blocked(false).build());
            userRepository.save(User.builder().username("user1").password("123456").role("USER").blocked(false).build());
            userRepository.save(User.builder().username("user2").password("123456").role("USER").blocked(false).build());
            userRepository.save(User.builder().username("user_block").password("123456").role("USER").blocked(true).build());
        }
    }
}

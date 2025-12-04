package com.example.vbote.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vbote.entity.User;
import com.example.vbote.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) Boolean blocked) {
        
        List<User> users;
            if (username != null || role != null || blocked != null) {
                users = userService.getUserWithFilters(username,role,blocked); 
            } else {
                users = userService.getAllUsers();
            }
     
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
	public ResponseEntity<List<User>> getUserByRole(@PathVariable String role) {
		List<User> users = userService.getUsersByRole(role);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
	}

    @GetMapping("/username/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		return userService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		try {
            User update = userService.updatUser(id, user);
            return ResponseEntity.ok(update);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
	}

	@PutMapping("/{id}/block")
	public ResponseEntity<User> blockUser(@PathVariable Long id) {
		try {
            User blocked = userService.blockUser(id);
            return ResponseEntity.ok(blocked);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
	}

	@PutMapping("/{id}/unblock")
	public ResponseEntity<User> unblockUser(@PathVariable Long id) {
		try {
            User unblocked = userService.unblockUser(id);
            return ResponseEntity.ok(unblocked);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
	}

}

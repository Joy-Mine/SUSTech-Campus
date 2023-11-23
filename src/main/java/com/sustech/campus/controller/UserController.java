package com.sustech.campus.controller;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByName(@PathVariable String username) {
        User user = userService.getUserByName(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        Long userId = userService.registerUser(user.getName(), user.getPassword(), user.getType());
        if (userId != null) {
            return ResponseEntity.ok("User registered successfully.");
        } else {
            return ResponseEntity.badRequest().body("Registration failed: User already exists.");
        }
    }

    @PutMapping("/changeType/{id}")
    public ResponseEntity<String> changeUserType(@PathVariable Long id, @RequestBody UserType newType) {
        boolean success = userService.changeUserType(id, newType);
        if (success) {
            return ResponseEntity.ok("User type updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
